package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import com.sourcedimensions.client.model.BaseType;
import com.sourcedimensions.client.model.BaseTypeCategory;
import com.sourcedimensions.client.model.Delegate;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.LocalDeclFilter;
import com.sourcedimensions.client.model.MemberFilter;
import com.sourcedimensions.client.model.Parameter;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.model.TriStateBoolean;
import com.sourcedimensions.client.model.TriStateMask;
import com.sourcedimensions.client.model.TypeCategory;
import com.sourcedimensions.client.model.TypeFilter;
import com.sourcedimensions.client.model.SnapshotNode.Reference;
import com.sourcedimensions.client.model.SnapshotNode.Type;
import com.sourcedimensions.client.model.Type.Property;
import com.sourcedimensions.server.ast.AbstractMember;
import com.sourcedimensions.server.ast.AstNode;
import com.sourcedimensions.server.ast.DataMember;
import com.sourcedimensions.server.ast.Declarator;
import com.sourcedimensions.server.ast.DelegateDeclaration;
import com.sourcedimensions.server.ast.EnumConstMember;
import com.sourcedimensions.server.ast.EventMember;
import com.sourcedimensions.server.ast.FixedSizeBufDeclarator;
import com.sourcedimensions.server.ast.FixedSizeBufMember;
import com.sourcedimensions.server.ast.FunctionalMember;
import com.sourcedimensions.server.ast.IndexerMember;
import com.sourcedimensions.server.ast.InitBlockMember;
import com.sourcedimensions.server.ast.InstanceCreationExpression;
import com.sourcedimensions.server.ast.LocalVariableDeclaration;
import com.sourcedimensions.server.ast.Member;
import com.sourcedimensions.server.ast.Modifier;
import com.sourcedimensions.server.ast.PropertyMember;
import com.sourcedimensions.server.ast.SimpleType;
import com.sourcedimensions.server.ast.TypeDeclaration;
import com.sourcedimensions.server.ast.TypeDeclarationMember;
import com.sourcedimensions.server.ast.UserDefinedType;
import com.sourcedimensions.server.ast.Modifier.ModifierKind;
import com.sourcedimensions.server.ast.TypeDeclaration.TypeDeclKind;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.SourceFile;
import com.sourcedimensions.server.sys.Project.Language;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.utils.DatabaseHelper;
import com.sourcedimensions.server.ast.Parameter.ParamKind;
import com.sourcedimensions.client.model.MemberCategory;
import com.sourcedimensions.client.model.Operator;
import com.sourcedimensions.server.ast.Name;


public class SymbolQueryEngine 
{
	Database m_db;

	protected Set<SourceFile> m_fileSet = new HashSet<SourceFile>();
	protected Map<String, NodeEntry> m_nodeMap = new HashMap<String, NodeEntry>();
	protected List<TypeFilter> m_anonymFilterList = new ArrayList<TypeFilter>();
	
	
	public SymbolQueryEngine(String sessionId)
	{
		m_db = DatabaseHelper.getDbBySessionID(sessionId);
	}
	
	
	public SnapshotNode execute(String projectId, SymbolQuery query)
	{
		Session session = m_db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		SnapshotNode root = new SnapshotNode();
		
		Collection<SnapshotNode> result = executeFromRoot(session, projectId, null, query);

		if (result != null)
		{
			root.setChildren(new ArrayList<SnapshotNode>());
			root.getChildren().addAll(result);
		}		
		
		session.getTransaction().commit();
		
		return root;
	}

	
	public SnapshotNode execute(String projectId, String rootId, SymbolQuery query)
	{
		Session session = m_db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		TypeDeclaration typeDecl = (TypeDeclaration)session.createQuery("FROM TypeDeclaration " +
			" WHERE m_id = :id").setString("id", rootId).uniqueResult();
		
		if (typeDecl == null)
		{
			session.getTransaction().commit();
			return null;
		}

		SnapshotNode root = new SnapshotNode();
		
		m_nodeMap.put(typeDecl.getID(), new NodeEntry(typeDecl, root));
		
		executeFromRoot(session, projectId, typeDecl, query);
		
		session.getTransaction().commit();
		
		return root;
	}

	
	protected Collection<SnapshotNode> executeFromRoot(Session session, String projectId, TypeDeclaration root, SymbolQuery symQuery)
	{
		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);
		
		Collection<SnapshotNode> output = executeNamespaceFilter(session, prjSpace, root, symQuery);		
		
		Project prj = (Project)prjSpace.toArray()[0];
		boolean isCSharp = true;

		switch (prj.getLanguage())
		{
			case JAVA_14:
			case JAVA_15:
				isCSharp = false;
				// TODO: rewrite execAnonymClassFilter function (do not use recursive queries!)
				//execAnonymClassFilter(session);
		}

		execLocalDeclFilter(session, symQuery, isCSharp);
		
		return output;
	}
	
	
	protected Collection<SnapshotNode> executeNamespaceFilter(Session session, Set<Project> prjSpace, TypeDeclaration root, SymbolQuery symQuery)
	{
		Set<TypeDeclaration> rootSet = new HashSet<TypeDeclaration>();

		if (root != null)
			rootSet.add(root);
		
		SortedMap<String, SnapshotNode> nameMap = new TreeMap<String, SnapshotNode>();
		List<String> namespaceFilter = new ArrayList<String>();
		
		if (symQuery.getAllNamespaces())
			namespaceFilter.add("**");
		else
			namespaceFilter.addAll(symQuery.getNamespaceFilter());			
	
		if (namespaceFilter.size() == 0)
			return null;

		Map<String, TypeDeclaration> parentMap = new HashMap<String, TypeDeclaration>();
		
		Query query = session.createQuery("SELECT DISTINCT d1, d2 FROM TypeDeclaration d1 " +
			"INNER JOIN FETCH d1.m_file, TypeDeclarationMember m, TypeDeclaration d2 " +
			"WHERE d1.m_parent = m AND m.m_parent = d2 AND d1.m_kind = :kind AND d1.m_project IN (:projects)");

		query.setInteger("kind", TypeDeclKind.NAMESPACE.value());
		query.setParameterList("projects", prjSpace);		
		
		List list = query.list();		
		
		for (Object o : list)
		{
			Object[] row = (Object[])o;
			TypeDeclaration decl = (TypeDeclaration)row[0];
			TypeDeclaration parent = (TypeDeclaration)row[1];
			
			parentMap.put(decl.getID(), parent);
		}		
		
		query = session.createQuery("SELECT DISTINCT d FROM TypeDeclaration d " +
			"INNER JOIN FETCH d.m_file, CompilationUnit u WHERE d.m_parent = u " + 
			"AND d.m_kind = :kind AND d.m_project IN (:projects)");
		
		query.setInteger("kind", TypeDeclKind.NAMESPACE.value());
		query.setParameterList("projects", prjSpace);
		
		list.addAll(query.list());
		
		for (String filter : namespaceFilter)
		{
			for (Object o : list)
			{
				String[] fltr = filter.split(Folder.DIVIDER);
				TypeDeclaration decl = null;
				
				if (o instanceof TypeDeclaration)
					decl = (TypeDeclaration)o;
				else
					decl = (TypeDeclaration)((Object[])o)[0];

				if (m_nodeMap.containsKey(decl.getID()))
					continue;
				
				String name = null;

				boolean skip = (root == null) ? false : true;
				
				for (TypeDeclaration d = decl; d != null; d = parentMap.get(d.getID()))
				{					
					if (root != null && d.getID().equals(root.getID()))
						skip = false;
					
					if (name == null)
						name = d.m_name;
					else
						name = d.m_name + "." + name;
				}
				
				if (skip)
					continue;

				String[] parts = name.split("\\.");
				
				int i = 0, j = 0;
				boolean wildcard = false;
				String lookahead = null;
				int step = 2;
				
				while (i < fltr.length && j < parts.length)
				{
					if (fltr[i].equals("**"))
					{
						if (wildcard)
						{
							if (lookahead == null)
							{
								if (j == (parts.length - 1))
									addNamespace(decl, name, decl.getSourceFile(), nameMap, rootSet);
							}
							else
							{
								if (parts[j].equals(lookahead))
								{
									wildcard = false;
									i += step;
									
									if (j == (parts.length - 1))
									{
										boolean w = true;
										
										for (int k = i; k < fltr.length; k++)
										{
											if (!fltr[k].equals("**"))
											{
												w = false;
												break;
											}
										}
										
										if (w)
											addNamespace(decl, name, decl.getSourceFile(), nameMap, rootSet);
									}
								}
							}								
							j++;								
						}
						else
						{
							wildcard = true;
							lookahead = null;
							step = 2;
							
							for (int k = i + 1; k < fltr.length; k++,step++)
							{
								if (!fltr[k].equals("**"))
								{
									lookahead = fltr[k];
									break;
								}
							}
						}
					}
					else
					{
						if (Pattern.matches(fltr[i], parts[j]))
						{
							if (i == (fltr.length - 1) && j == (parts.length - 1))
								addNamespace(decl, name, decl.getSourceFile(), nameMap, rootSet);
						}
						else
							break;
						
						i++;
						j++;
					}
				}
			}
		}
			
		List<SnapshotNode> output = new ArrayList<SnapshotNode>();
		
		output.addAll(nameMap.values());
		
		if (rootSet.size() > 0)
			executeTypeFilter(session, prjSpace, rootSet, symQuery);
		
		if (symQuery.getAllNamespaces() || symQuery.getGlobalNamespace())
			output.addAll(executeTypeFilter(session, prjSpace, null, symQuery));

		return output;
	}

	
	protected List<SnapshotNode> executeTypeFilter(Session session, Set<Project> prjSpace, Set<TypeDeclaration> rootSet, SymbolQuery symQuery)
	{
		List<TypeFilter> typeFilter = new ArrayList<TypeFilter>();
		List<SnapshotNode> output = new ArrayList<SnapshotNode>();
		Set<TypeDeclaration> typeRoots = new HashSet<TypeDeclaration>();
		Set<TypeDeclaration> memRoots = new HashSet<TypeDeclaration>();
		
		if (symQuery.getAllTypes())
		{
			TypeFilter filter = new TypeFilter();
			TriStateMask mask = new TriStateMask();
			mask.setAny();
							
			filter.setCategories(~0);
			filter.setModifiers(mask);
			filter.setInnerTypes(true);
			filter.setAllBaseTypes(true);
			filter.setName(".*");
			
			typeFilter.add(filter);
		}
		else
		{
			typeFilter.addAll(symQuery.getTypeFilter());
		}

		if (typeFilter.size() == 0)
			return output;
		
		Project prj = (Project)prjSpace.toArray()[0];
		
		Query query = null;
		
		if (rootSet == null || rootSet.size() == 0)
		{
			query = session.createQuery("SELECT DISTINCT d FROM TypeDeclaration d " +
					"INNER JOIN FETCH d.m_file LEFT JOIN FETCH d.m_modifiers LEFT JOIN FETCH d.m_baseTypes " + 
					"LEFT JOIN FETCH d.m_baseInterfaces, CompilationUnit u WHERE d.m_parent = u " + 
					"AND d.m_kind != :kind AND d.m_project IN (:projects)");
			
			query.setParameterList("projects", prjSpace);
		}
		else
		{	
			query = session.createQuery("SELECT DISTINCT d1, d2 FROM TypeDeclaration d1 " +
					"INNER JOIN FETCH d1.m_file, TypeDeclarationMember m, TypeDeclaration d2 " +
					"LEFT JOIN FETCH d1.m_modifiers LEFT JOIN FETCH d1.m_baseTypes LEFT JOIN FETCH d1.m_baseInterfaces " +
					"WHERE d1.m_parent = m AND m.m_parent = d2 AND d1.m_kind != :kind AND d2 IN (:parents)");

			query.setParameterList("parents", rootSet);		
		}

		query.setInteger("kind", TypeDeclKind.NAMESPACE.value());
		
		List list = query.list();		
		
		for (TypeFilter filter : typeFilter)
		{			
			switch (prj.getLanguage())
			{
				case CSHARP_11:
				case CSHARP_20:
					if (filter.getDelegate() != null)
					{
						Delegate delegate = filter.getDelegate();
						
						if (rootSet == null || rootSet.size() == 0)
						{
							query = session.createQuery("SELECT DISTINCT d FROM DelegateDeclaration d " +
									"INNER JOIN FETCH d.m_file LEFT JOIN FETCH d.m_parameters p " + 
									"INNER JOIN FETCH p.m_type LEFT JOIN FETCH p.m_modifiers, CompilationUnit u WHERE d.m_parent = u " + 
									"AND d.m_project IN (:projects)");

							query.setParameterList("projects", prjSpace);
						}
						else
						{
							query = session.createQuery("SELECT DISTINCT d1, d2 FROM DelegateDeclaration d1 " +
									"INNER JOIN FETCH d1.m_file LEFT JOIN FETCH d1.m_parameters p INNER JOIN p.m_type " +
									"LEFT JOIN p.m_modifiers, TypeDeclarationMember m, TypeDeclaration d2 " +
									"WHERE d1.m_parent = m AND m.m_parent = d2 AND d2 IN (:parents)");

							query.setParameterList("parents", rootSet);
						}
			
						List l = query.list();
							
						for (Object o : l)
						{
							DelegateDeclaration decl = null;
							AstNode parent = null;
							
							if (o instanceof DelegateDeclaration)
							{
								decl = (DelegateDeclaration)o;
								parent = null;
							}
							else
							{
								Object[] row = (Object[])o;							
								decl = (DelegateDeclaration)row[0];
								parent = (AstNode)row[1];
							}
							
							if (m_nodeMap.containsKey(decl.getID()))
								continue;
							
							if (!Pattern.matches(delegate.getName(), decl.m_name))
								continue;
							
							if (!matchType(session, delegate.getType(), decl.getType(), true))
								continue;
							
							if (!delegate.getAnyParams())
							{
								if (!matchParams(session, delegate.getParamList(), decl.m_parameters, true))
									continue;
							}
							
							SnapshotNode snapshot = new SnapshotNode(Type.DELEGATE, decl.m_name);;
							
							if (rootSet != null && rootSet.size() > 0)
							{
								NodeEntry entry = m_nodeMap.get(parent.getID());
								
								if (entry.m_snapshot.getChildren() == null)
									entry.m_snapshot.setChildren(new ArrayList<SnapshotNode>());
								
								entry.m_snapshot.getChildren().add(snapshot);
							}
							else
								output.add(snapshot);

							snapshot.setRefs(new ArrayList<Reference>());
							snapshot.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));													
														
							m_fileSet.add(decl.getSourceFile());
							m_nodeMap.put(decl.getID(), new NodeEntry(decl, snapshot));
						}							
					}
			}
			
			for (Object o : list)
			{
				TypeDeclaration decl = null;
				AstNode parent = null;
				
				if (o instanceof TypeDeclaration)
				{
					decl = (TypeDeclaration)o;
					parent = null;
				}
				else
				{
					Object[] row = (Object[])o;
					decl = (TypeDeclaration)row[0];
					parent = (AstNode)row[1];
				}
				
				if (m_nodeMap.containsKey(decl.getID()))
					continue;
				
				if (!Pattern.matches(filter.getName(), decl.m_name))
					continue;
				
				boolean skip = true;
				
				switch (decl.getKind())
				{
					case CLASS:
						if ((filter.getCategories() & TypeCategory.CLASS.value()) != 0)
							skip = false;
						break;
						
					case INTERFACE:
						if ((filter.getCategories() & TypeCategory.INTERFACE.value()) != 0)
							skip = false;
						break;
						
					case STRUCT:
						if ((filter.getCategories() & TypeCategory.STRUCT.value()) != 0)
							skip = false;			
						break;
						
					case ENUM:
						if ((filter.getCategories() & TypeCategory.ENUM.value()) != 0)
							skip = false;
						break;
													
					case ANNOT_TYPE:
						if ((filter.getCategories() & TypeCategory.ANNOTATION.value()) != 0)
							skip = false;
						break;
				}
				
				if (skip)
					continue;
				
				if (!filter.getAllBaseTypes())
				{		
					if (filter.getBaseTypes().size() == 0)
					{
						skip = (decl.m_baseTypes.size() != 0 || decl.m_baseInterfaces.size() != 0);
					}
					else
					{
						int counter = 0;
				
						for (BaseType base : filter.getBaseTypes())
						{															
							for (com.sourcedimensions.server.ast.Type t : decl.m_baseTypes)
							{							
								if (t instanceof UserDefinedType)
								{
									if (Pattern.matches(base.getName(), t.getName()) && (base.getCategory() == BaseTypeCategory.CLASS.value() || 
										base.getCategory() == BaseTypeCategory.CLASSINTF.value()))
									{
										counter++;
									}
								}
								else if (t instanceof SimpleType)
								{			
									if (Pattern.matches(base.getName(), t.getName()) && base.getCategory() == BaseTypeCategory.INTEGRALTYPE.value())
									{
										counter++;
									}									
								}
							}
							
							for (com.sourcedimensions.server.ast.Type t : decl.m_baseInterfaces)
							{
								if (t instanceof UserDefinedType)
								{
									if (Pattern.matches(base.getName(), t.getName()) && (base.getCategory() == BaseTypeCategory.INTERFACE.value() ||
										base.getCategory() == BaseTypeCategory.CLASSINTF.value()))
									{
										counter++;
									}
								}	
							}
							
							if (counter == 0)
							{
								skip = true;
								break;
							}
						}
					}
				}
				
				if (skip)
					continue;
								
				if (!matchModifiers(decl.m_modifiers, filter.getModifiers()))
					continue;

				SnapshotNode snapshot = null;
				
				switch (decl.getKind())
				{
					case CLASS:
						snapshot = new SnapshotNode(Type.CLASS, decl.m_name);
						break;
						
					case INTERFACE:
						snapshot = new SnapshotNode(Type.INTERFACE, decl.m_name);
						break;
						
					case STRUCT:
						snapshot = new SnapshotNode(Type.STRUCT, decl.m_name);
						break;
						
					case ENUM:
						snapshot = new SnapshotNode(Type.ENUM, decl.m_name);
						break;
						
					case ANNOT_TYPE:
						snapshot = new SnapshotNode(Type.ANNOT, decl.m_name);
						break;
				}
				
				snapshot.setRefs(new ArrayList<Reference>());
				snapshot.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));
					
				if (rootSet != null && rootSet.size() > 0)
				{
					NodeEntry entry = m_nodeMap.get(parent.getID());
					
					if (entry.m_snapshot.getChildren() == null)
						entry.m_snapshot.setChildren(new ArrayList<SnapshotNode>());
					
					entry.m_snapshot.getChildren().add(snapshot);
				}
				else					
					output.add(snapshot);
			
				if (filter.getInnerTypes())
					typeRoots.add(decl);
				
				memRoots.add(decl);

				switch (prj.getLanguage())
				{
					case JAVA_14:
					case JAVA_15:
						if ((filter.getCategories() & TypeCategory.ANONYMCLASS.value()) != 0)	
							m_anonymFilterList.add(filter);
				}
				
				m_fileSet.add(decl.getSourceFile());
				m_nodeMap.put(decl.getID(), new NodeEntry(decl, snapshot));
			}			
		}

		if (typeRoots.size() > 0)
			executeTypeFilter(session, prjSpace, typeRoots, symQuery);	

		if (memRoots.size() > 0)
			executeMemberFilter(session, prjSpace, memRoots, symQuery);
		
		return output;
	}

	
	protected void executeMemberFilter(Session session, Set<Project> prjSpace, Set<TypeDeclaration> rootSet, SymbolQuery symQuery)
	{
		List<MemberFilter> memberFilter = new ArrayList<MemberFilter>();
		Project prj = (Project)prjSpace.toArray()[0];
		boolean isCSharp = (prj.getLanguage() == Language.CSHARP_11 || prj.getLanguage() == Language.CSHARP_20);
		Map<String, Integer> nameCount = new HashMap<String, Integer>();
		Type snapshotType = null;
		
		if (symQuery.getAllMembers())
		{
			MemberFilter filter = new  MemberFilter();
			com.sourcedimensions.client.model.Type type = new com.sourcedimensions.client.model.Type();
			TriStateMask mask = new TriStateMask();
			mask.setAny();
							
			filter.setCategories(~0);
			filter.setModifiers(mask);
			filter.setOperators(~0);
			filter.setAnyParams(true);
			filter.setAnyThrows(true);
			filter.setName(".*");
			
			type.setTypeProps(mask);
			type.setName(".*");
			filter.setType(type);
			
			memberFilter.add(filter);
		}
		else
		{
			memberFilter.addAll(symQuery.getMemberFilter());
		}

		if (memberFilter.size() == 0)
			return;
		
		Query query = session.createQuery("SELECT DISTINCT m, t FROM Member m INNER JOIN FETCH m.m_file LEFT JOIN FETCH m.m_type, " +
			"TypeDeclaration t WHERE m.m_parent = t AND m.m_parent IN (:parents)");
		
		query.setParameterList("parents", rootSet);
		
		List list = query.list();
		
		query = session.createQuery("SELECT DISTINCT m, t FROM AbstractMember m INNER JOIN FETCH m.m_file, " +
			"TypeDeclaration t WHERE m.class IN (EnumConstMember, InitBlockMember) AND m.m_parent = t AND m.m_parent IN (:parents)");

		query.setParameterList("parents", rootSet);

		list.addAll(query.list());
		
		List<MemberEntry> members = new ArrayList<MemberEntry>(); 
		
		for (MemberFilter filter : memberFilter)
		{
			for (Object o : list)
			{				
				Object[] row = (Object[])o;
				
				Hibernate.initialize(row[0]);
				AbstractMember member = (AbstractMember)row[0];
				AstNode parent = (AstNode) row[1];
				
				if (m_nodeMap.containsKey(member.getID()))
					continue;
				
				boolean skip = false;
				
				members.clear();
				
				if (!matchModifiers(member.m_modifiers, filter.getModifiers()))
					continue;

				if (member instanceof DataMember)
				{
					DataMember m = (DataMember)member;
					snapshotType = Type.FIELD;
					
					switch (m.getKind())
					{
						case FIELD:
							if ((filter.getCategories() & MemberCategory.FIELD.value()) == 0)
								skip = true;
							break;
							
						case CONST:
							if ((filter.getCategories() & MemberCategory.CONSTANT.value()) == 0)
								skip = true;
					}
					
					Iterator<Declarator> iter = m.m_declarators.iterator();
					
					while (iter.hasNext())
					{
						Declarator d = iter.next();
						
						if (m_nodeMap.containsKey(d.getID()))
							break;
						
						members.add(new MemberEntry(d.m_name, m.getType(), d, parent));
					}
				}
				else if (member instanceof FunctionalMember)
				{
					FunctionalMember m = (FunctionalMember)member;
					
					switch (m.getKind())
					{
						case CONSTRUCTOR:
							if ((filter.getCategories() & MemberCategory.CONSTRUCTOR.value()) == 0)
								skip = true;
							else
								members.add(new MemberEntry(getQNameStr(m.m_name), null, member, false, parent));
							
							snapshotType = Type.CONSTRUCTOR;
							break;
							
						case DESTRUCTOR:
							if ((filter.getCategories() & MemberCategory.DESTRUCTOR.value()) == 0)
								skip = true;
							else
								members.add(new MemberEntry(getQNameStr(m.m_name), null, member, false, parent));

							snapshotType = Type.DESTRUCTOR;							
							break;  						
							
						case METHOD:
						case ABSTRACT_METHOD:
							if ((filter.getCategories() & MemberCategory.METHOD.value()) == 0)
								skip = true;
							else
								members.add(new MemberEntry(getQNameStr(m.m_name), m.getType(), member, parent));
							
							snapshotType = Type.METHOD;
							break;
							
						case UPLUS_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.UNARYPLUS.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.UNARYPLUS.toString(), m.getType(), member, false, parent));
							
							snapshotType = Type.OPERATOR;							
							break;
							
						case UMINUS_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.UNARYMINUS.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.UNARYMINUS.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case NOT_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.NOT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.NOT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case INV_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.COMPLEMENT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.COMPLEMENT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case INC_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.INCREMENT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.INCREMENT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case DEC_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.DECREMENT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.DECREMENT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case TRUE_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.TRUE.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.TRUE.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case FALSE_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.FALSE.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.FALSE.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case PLUS_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.PLUS.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.PLUS.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case MINUS_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.MINUS.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.MINUS.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case MULT_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.MULT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.MULT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case DIV_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.DIVISION.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.DIVISION.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case REM_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.REMINDER.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.REMINDER.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case AND_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.BITWISEAND.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.BITWISEAND.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case OR_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.BITWISEOR.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.BITWISEOR.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case XOR_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.BITWISEXOR.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.BITWISEXOR.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case LSHIFT_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.LSHIFT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.LSHIFT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case RSHIFT_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.RSHIFT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.RSHIFT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case EQUAL_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.EQ.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.EQ.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case NOT_EQ_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.NOTEQ.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.NOTEQ.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case LESS_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.LESS.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.LESS.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case GT_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.GT.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.GT.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case LESS_EQ_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.LESSEQ.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.LESSEQ.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case GT_EQ_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.GTEQ.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.GTEQ.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case IMP_CONV_OPERATOR:
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.IMPLCONV.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.IMPLCONV.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
							break;
							
						case EXP_CONV_OPERATOR:					
							if ((filter.getCategories() & MemberCategory.OPERATOR.value()) == 0)
								skip = true;
							else
								if ((filter.getOperators() & Operator.EXPLCONV.value()) == 0)
									skip = true;
								else
									members.add(new MemberEntry(Operator.EXPLCONV.toString(), m.getType(), member, false, parent));

							snapshotType = Type.OPERATOR;							
					}
					
					if (!skip)
					{
						if (!isCSharp && !filter.getAnyThrows())
						{
							for (String fltr : filter.getThrowList())
							{
								skip = true;
								
								for (com.sourcedimensions.server.ast.Type t : m.m_throwList)
								{
									if (Pattern.matches(fltr, t.getName()))
									{
										skip = false;
										break;
									}
								}
								
								if (skip)
									break;
							}
						}
						
						if (!skip && !filter.getAnyParams())
						{
							if (!matchParams(session, filter.getParamList(), m.m_parameters, isCSharp))
								skip = true;
						}
					}
				}
				else if (member instanceof EventMember)
				{
					EventMember m = (EventMember)member;					

					if ((filter.getCategories() & MemberCategory.EVENTADD.value()) == 0 &&
							(filter.getCategories() & MemberCategory.EVENTREMOVE.value()) == 0)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.EVENTADD.value()) != 0 && m.getAddAccessor() == null)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.EVENTREMOVE.value()) != 0 && m.getRemoveAccessor() == null)
						skip = true;
					else
					{
						members.add(new MemberEntry(getQNameStr(m.m_name), m.getType(), member, parent));
						
						if (m.getAddAccessor() == null)
							snapshotType = Type.EVENTREMOVE;
						else if (m.getRemoveAccessor() == null)
							snapshotType = Type.EVENTREMOVE;
						else
							snapshotType = Type.EVENT;							
					}
				}
				else if (member instanceof PropertyMember)
				{
					PropertyMember m = (PropertyMember)member;
					
					if ((filter.getCategories() & MemberCategory.PROPERTYGET.value()) == 0 &&
							(filter.getCategories() & MemberCategory.PROPERTYSET.value()) == 0)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.PROPERTYGET.value()) != 0 && 
							 (filter.getCategories() & MemberCategory.PROPERTYSET.value()) == 0 && m.getGetAccessor() == null)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.PROPERTYSET.value()) != 0 && 
							 (filter.getCategories() & MemberCategory.PROPERTYGET.value()) == 0 && m.getSetAccessor() == null)
						skip = true;
					else
					{
						members.add(new MemberEntry(getQNameStr(m.m_name), m.getType(), member, parent));
						
						if (m.getGetAccessor() == null)
							snapshotType = Type.PROPERTYSET;
						else if (m.getSetAccessor() == null)
							snapshotType = Type.PROPERTYGET;
						else
							snapshotType = Type.PROPERTY;
					}
				}
				else if (member instanceof IndexerMember)
				{
					IndexerMember m = (IndexerMember)member;
					
					if ((filter.getCategories() & MemberCategory.INDEXERGET.value()) == 0 &&
							(filter.getCategories() & MemberCategory.INDEXERSET.value()) == 0)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.INDEXERGET.value()) != 0 && m.getGetAccessor() == null)
						skip = true;
					else if ((filter.getCategories() & MemberCategory.INDEXERSET.value()) != 0 && m.getSetAccessor() == null)
						skip = true;
					
					if (!skip)
					{
						if (!matchParams(session, filter.getParamList(), m.m_parameters, isCSharp))
							skip = true;
						else
						{
							String name = getQNameStr(m.m_name);
							
							if (name == "")
								name = "{INDEXER}";
							
							members.add(new MemberEntry(name, m.getType(), member, parent));
						}
						
						if (m.getGetAccessor() == null)
							snapshotType = Type.INDEXERSET;
						else if (m.getSetAccessor() == null)
							snapshotType = Type.INDEXERGET;
						else
							snapshotType = Type.INDEXER;						
					}
				}
				else if (member instanceof FixedSizeBufMember)
				{
					FixedSizeBufMember m = (FixedSizeBufMember)member;
					
					if ((filter.getCategories() & MemberCategory.FIXEDSIZEBUF.value()) == 0)
						skip = true;
					else
					{
						Iterator<FixedSizeBufDeclarator> iter = m.m_declarators.iterator();
						
						while (iter.hasNext())
						{
							FixedSizeBufDeclarator d = iter.next();
							
							if (m_nodeMap.containsKey(d.getID()))
								break;
							
							members.add(new MemberEntry(d.m_name, m.getType(), d, parent));
						}
						
						snapshotType = Type.FIXEDSIZEBUFFER;
					}
				}
				else if (member instanceof EnumConstMember)
				{
					EnumConstMember m = (EnumConstMember)member;
					
					if ((filter.getCategories() & MemberCategory.ENUMCONST.value()) == 0)
						skip = true;
					else
						members.add(new MemberEntry(m.m_name, null, member, parent));
					
					snapshotType = Type.ENUMCONST;
				}
				else if (member instanceof InitBlockMember)
				{
					if ((filter.getCategories() & MemberCategory.INIT_BLOCK.value()) == 0)
						skip = true;
					else
						members.add(new MemberEntry("{INIT.BLOCK}", null, member, false, parent));
					
					snapshotType = Type.INITBLOCK;
				}
				
				if (skip)
					continue;

				List<MemberEntry> validMembers = new ArrayList<MemberEntry>();				
				
				for (MemberEntry m : members)
				{					
					if (filter.getType() != null && m.m_type != null)
					{					
						if (!matchType(session, filter.getType(), m.m_type, isCSharp))
							continue;
					}
					
					boolean valid = true;
					
					if (m.m_checkName)
						valid = Pattern.matches(filter.getName(), m.m_name);
						
					if (valid)
					{
						Integer count = nameCount.get(m.m_name);
						
						if (count == null)
						{
							nameCount.put(m.m_name, 1);
						}
						else
						{
							count++;
							nameCount.put(m.m_name, count);							
							m.m_name += ":" + count.toString();
						}
						
						validMembers.add(m);						
					}
				}
				
				if (validMembers.size() == 0)
					continue;
				
				for (MemberEntry me : validMembers)
				{
					SnapshotNode snapshot = new SnapshotNode(snapshotType, me.m_name);
					
					snapshot.setRefs(new ArrayList<Reference>());
					snapshot.getRefs().add(new Reference(me.m_node.getID(), me.m_node.getSourceFile().getID(), me.m_node.m_left, me.m_node.m_right));

					SnapshotNode parentSnapshot = m_nodeMap.get(parent.getID()).m_snapshot;
					
					if (parentSnapshot.getChildren() == null)
						parentSnapshot.setChildren(new ArrayList<SnapshotNode>());
					
					parentSnapshot.getChildren().add(snapshot);
					
					m_fileSet.add(me.m_node.getSourceFile());
					m_nodeMap.put(me.m_node.getID(), new NodeEntry(me.m_node, snapshot));
				}
			}
		}				
	}
	
	
	protected void execAnonymClassFilter(Session session)
	{
		if (m_anonymFilterList.size() == 0)
			return;
		
		if (m_fileSet.size() == 0)
			return;
		
		Map<String, Integer> nameCount = new HashMap<String, Integer>();
		
		Query query = session.createQuery("SELECT e FROM InstanceCreationExpression e INNER JOIN FETCH e.m_file f " +
			"WHERE f IN (:fileset) AND size(e.m_members) > 0").setParameterList("fileset", m_fileSet);
	
		List list = query.list();
		
		for (Object o : list)
		{
			boolean match = false;

			InstanceCreationExpression expr = (InstanceCreationExpression)o;
			String name = expr.getType().getName();
			
			for (TypeFilter filter : m_anonymFilterList)
			{
				if (!Pattern.matches(filter.getName(), name))
					continue;			
	
				AstNode node = expr;
				
				query = session.createQuery("SELECT p FROM AstNode a INNER JOIN FETCH a.m_parent WHERE a.m_id = :id");

				for (query.setString("id", node.getID()) ; (node = (AstNode)query.uniqueResult()) != null ; query.setString("id", node.getID()))
				{						
					NodeEntry entry = m_nodeMap.get(node.getID());
					
					if (entry != null)
					{
						Integer count = nameCount.get(name);
						
						if (count == null)
							nameCount.put(name, 1);
						else
						{
							count++;
							nameCount.put(name, count);							
							name = name + ":" + Integer.toString(count);
						}
						
						SnapshotNode snapshot = new SnapshotNode(Type.ANONYMCLASS, name);

						snapshot.setRefs(new ArrayList<Reference>());
						snapshot.getRefs().add(new Reference(expr.getID(), expr.getSourceFile().getID(), expr.m_left, expr.m_right));
						
						if (entry.m_snapshot.getChildren() == null)
							entry.m_snapshot.setChildren(new ArrayList<SnapshotNode>());
						
						entry.m_snapshot.getChildren().add(snapshot);
						
						m_nodeMap.put(expr.getID(), new NodeEntry(expr, snapshot));
						
						match = true;
						break;
					}
				}
				
				if (match)
					break;				
			}
		}		
	}	

	
	// TODO: Const/final modifiers match. Change in grammar might be necessary.
	protected void execLocalDeclFilter(Session session, SymbolQuery symQuery, boolean isCSharp)
	{
		if (m_fileSet.size() == 0)
			return;		
		
		List<LocalDeclFilter> localDeclFilter = new ArrayList<LocalDeclFilter>();
		
		if (symQuery.getAllLocalDecls())
		{
			LocalDeclFilter filter = new LocalDeclFilter();
			com.sourcedimensions.client.model.Type type = new com.sourcedimensions.client.model.Type();
			TriStateMask mask = new TriStateMask();

			mask.setAny();
							
			type.setTypeProps(mask);
			type.setName(".*");
			filter.setType(type);
			
			filter.setName(".*");
			filter.setFinal(TriStateBoolean.EITHER);
			
			localDeclFilter.add(filter);
		}
		else
			localDeclFilter.addAll(symQuery.getLocalDeclFilter());			
	
		if (localDeclFilter.size() == 0)
			return;		
		
		Query query = session.createQuery("SELECT l FROM LocalVariableDeclaration l INNER JOIN FETCH l.m_file f " +
			"WHERE f IN (:fileset)").setParameterList("fileset", m_fileSet);
				
		List list = query.list();
		
		for (Object o : list)
		{
			LocalVariableDeclaration decl = (LocalVariableDeclaration)o;
			Set<Declarator> matchSet = new HashSet<Declarator>();
			
			for (LocalDeclFilter filter : localDeclFilter)
			{
				if (!matchType(session, filter.getType(), decl.getType(), isCSharp))
					continue;
				
				for (Declarator dr : decl.m_declarators)
				{
					if (matchSet.contains(dr))
						continue;
					
					if (!Pattern.matches(filter.getName(), dr.m_name))
						continue;
					
					AstNode node = decl;
					
					query = session.createQuery("SELECT p FROM AstNode a INNER JOIN FETCH a.m_parent WHERE a.m_id = :id");

					for (query.setString("id", node.getID()) ; (node = (AstNode)query.uniqueResult()) != null ; query.setString("id", node.getID()))
					{
						NodeEntry entry = m_nodeMap.get(node.getID());
						
						if (entry != null)
						{
							SnapshotNode snapshot = new SnapshotNode(Type.LOCAL, dr.m_name + ":" + decl.getType().getName());

							snapshot.setRefs(new ArrayList<Reference>());
							snapshot.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));
							
							if (entry.m_snapshot.getChildren() == null)
								entry.m_snapshot.setChildren(new ArrayList<SnapshotNode>());
							
							entry.m_snapshot.getChildren().add(snapshot);
			
							matchSet.add(dr);
							
							break;
						}						
					}
				}
			}
		}
	}
	
	
	protected boolean matchType(Session session, com.sourcedimensions.client.model.Type filter, 
		com.sourcedimensions.server.ast.Type type, boolean isCSharp)
	{
		if (!Pattern.matches(filter.getName(), type.getName()))
			return false;
		
		if (filter.getTypeProps().getMask(Property.ARRAY.value()) == (type.m_rank == 0 ? TriStateBoolean.TRUE : TriStateBoolean.FALSE))
			return false;

		if (filter.getTypeProps().getMask(Property.TYPEPARAM.value()) == (type.m_arguments.size() == 0 ? TriStateBoolean.TRUE : TriStateBoolean.FALSE))
			return false;

		if (isCSharp)
		{
			if (filter.getTypeProps().getMask(Property.NULLABLE.value()) == (type.m_nullable ? TriStateBoolean.FALSE : TriStateBoolean.TRUE))
				return false;
			
			if (filter.getTypeProps().getMask(Property.POINTER.value()) == (type.m_ptrIndirection == 0 ? TriStateBoolean.TRUE : TriStateBoolean.FALSE))
				return false;						
		}

		return true;
	}
	
	
	protected boolean matchModifiers(Collection value, TriStateMask filter)
	{
		Set<ModifierKind> enumSet = new HashSet<ModifierKind>();
		
		for (Object o : value)
		{
			Modifier m = (Modifier)o;
			enumSet.add(m.getKind());
			
			switch (m.getKind())
			{
				case PUBLIC:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PUBLIC.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case PROTECTED:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PROTECTED.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case PRIVATE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PRIVATE.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case STATIC:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.STATIC.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case ABSTRACT:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.ABSTRACT.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case FINAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.FINAL.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case NATIVE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.NATIVE.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case SYNCHRONIZED:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.SYNCHRONIZED.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case TRANSIENT:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.TRANSIENT.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case VOLATILE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.VOLATILE.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case STRICTFP:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.STRICTFP.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case UNSAFE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.UNSAFE.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case EXTERN:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.EXTERN.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case INTERNAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.INTERNAL.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case READONLY:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.READONLY.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case VIRTUAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.VIRTUAL.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case OVERRIDE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.OVERRIDE.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case NEW:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.NEW.value()) == TriStateBoolean.FALSE)
						return false;
					break;
					
				case PARTIAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PARTIAL.value()) == TriStateBoolean.FALSE)
						return false;
			}
		}
				
		Set<Modifier> inv = new HashSet<Modifier>();
		
		for (ModifierKind k : ModifierKind.values())
		{
			if (!enumSet.contains(k))
				inv.add(new Modifier(k));
		}
		
		for (Modifier m : inv)
		{
			switch (m.getKind())
			{
				case PUBLIC:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PUBLIC.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case PROTECTED:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PROTECTED.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case PRIVATE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PRIVATE.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case STATIC:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.STATIC.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case ABSTRACT:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.ABSTRACT.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case FINAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.FINAL.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case NATIVE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.NATIVE.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case SYNCHRONIZED:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.SYNCHRONIZED.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case TRANSIENT:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.TRANSIENT.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case VOLATILE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.VOLATILE.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case STRICTFP:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.STRICTFP.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case UNSAFE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.UNSAFE.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case EXTERN:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.EXTERN.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case INTERNAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.INTERNAL.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case READONLY:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.READONLY.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case VIRTUAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.VIRTUAL.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case OVERRIDE:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.OVERRIDE.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case NEW:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.NEW.value()) == TriStateBoolean.TRUE)
						return false;
					break;
					
				case PARTIAL:
					if (filter.getMask(com.sourcedimensions.client.model.Modifier.PARTIAL.value()) == TriStateBoolean.TRUE)
						return false;
			}
		}
				
		return true;
	}

	
	protected boolean matchParams(Session session, List<Parameter> filter, 
		List<com.sourcedimensions.server.ast.Parameter> params, boolean isCSharp)
	{
		if (filter == null)
			return false;
		
		Set<Integer> posSet = new HashSet<Integer>();
		
		for (Parameter par : filter)
		{
			Set<Integer> pos = new HashSet<Integer>();
			
			switch (par.getPosType())
			{
				case ANY:
					for (int i = 0; i < params.size(); i++)
						pos.add(i);
					break;
					
				case EXACT:
					pos.add(par.getPosValue());
					break;
					
				case LESSEQ:
					for (int i = 0; i < par.getPosMax(); i++)
						pos.add(i);
					break;
					
				case GREATEREQ:
					for (int i = par.getPosMin(); i < params.size(); i++)
						pos.add(i);
					break;
					
				case BETWEEN:
					for (int i = par.getPosMin(); i <= par.getPosMax(); i++)
						pos.add(i);
					break;
					
				case LIST:
					pos.addAll(par.getPosList());
					break;
			}

			if (par.getQuantitative())
			{
				return (pos.contains(params.size()));
			}
			else
			{
				boolean match = false;
				int i;
				
				for (i = 0; i < params.size(); i++)
				{
					if (posSet.contains(i))
						continue;
					
					if (pos.contains(i))
					{
						com.sourcedimensions.server.ast.Parameter param = params.get(i);
						
						if (!Pattern.matches(par.getName(), params.get(i).m_name))
							continue;
						
						if (isCSharp)
						{
							if (par.getModifiers().getMask(Parameter.Modifier.OUT.value()) == 
									(param.getKind() == ParamKind.OUT ? TriStateBoolean.FALSE : TriStateBoolean.TRUE))
								continue;
	
							if (par.getModifiers().getMask(Parameter.Modifier.REF.value()) == 
									(param.getKind() == ParamKind.REF ? TriStateBoolean.FALSE : TriStateBoolean.TRUE))
								continue;
						}
						else
						{
							if (par.getModifiers().getMask(Parameter.Modifier.FINAL.value()) == 
									(param.m_finalParam ? TriStateBoolean.FALSE : TriStateBoolean.TRUE))
								continue;
						}
						
						if (par.getModifiers().getMask(Parameter.Modifier.PARAMS.value()) == 
								(param.m_varParam ? TriStateBoolean.FALSE : TriStateBoolean.TRUE))
							continue;
						
						if (!matchType(session, par.getType(), param.getType(), isCSharp))
							continue;
					
						posSet.add(i);
						match = true;
						break;
					}
				}
				
				if (!match)
					return false;				
			}
		}
		
		return true;
	}
	
	
	protected void addNamespace(TypeDeclaration decl, String name, SourceFile file, Map<String, SnapshotNode> nameMap, Set<TypeDeclaration> rootSet)
	{
		SnapshotNode s = nameMap.get(name);		
		Reference ref = new Reference(decl.getID(), file.getID(), decl.m_left, decl.m_right);
		
		if (s == null)
		{
			s = new SnapshotNode(Type.NAMESPACE, name);
			nameMap.put(name, s);
		}

		s.setRefs(new ArrayList<Reference>());		
		s.getRefs().add(ref);
	
		rootSet.add(decl);
		
		m_fileSet.add(file);
		m_nodeMap.put(decl.getID(), new NodeEntry(decl, s));
	}	
	
	
	protected String getQNameStr(List<Name> name)
	{
		String qname = "";
		
		for (int i = 0; i < name.size(); i++)
		{
			if (i > 0)
				qname += ".";
			
			qname += name.get(i).m_name;
		}
		
		return qname;		
	}


	protected class NodeEntry
	{
		public NodeEntry(AstNode node, SnapshotNode snapshot)
		{
			m_node = node;
			m_snapshot = snapshot;
		}
		
		public AstNode m_node;
		public SnapshotNode m_snapshot;
	}
	
	
	protected class MemberEntry
	{
		public MemberEntry(String name, com.sourcedimensions.server.ast.Type type, AstNode node, AstNode parent)
		{
			this(name, type, node, true, parent);
		}
		
		public MemberEntry(String name, com.sourcedimensions.server.ast.Type type, AstNode node, boolean checkName, AstNode parent)
		{
			m_name = name;
			m_type = type;
			m_node = node;
			m_parent = parent;
			m_checkName = checkName;
		}
		
		public String m_name;
		public AstNode m_node;
		public com.sourcedimensions.server.ast.Type m_type;
		public boolean m_checkName;
		public AstNode m_parent;
	}
}
