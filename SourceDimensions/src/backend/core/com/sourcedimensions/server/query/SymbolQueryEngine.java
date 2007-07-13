package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.hibernate.Query;
import org.hibernate.Session;
import com.sourcedimensions.client.model.BaseType;
import com.sourcedimensions.client.model.BaseTypeCategory;
import com.sourcedimensions.client.model.Delegate;
import com.sourcedimensions.client.model.Folder;
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
import com.sourcedimensions.server.ast.AstNode;
import com.sourcedimensions.server.ast.DelegateDeclaration;
import com.sourcedimensions.server.ast.InstanceCreationExpression;
import com.sourcedimensions.server.ast.Modifier;
import com.sourcedimensions.server.ast.SimpleType;
import com.sourcedimensions.server.ast.TypeDeclaration;
import com.sourcedimensions.server.ast.TypeDeclarationMember;
import com.sourcedimensions.server.ast.UserDefinedType;
import com.sourcedimensions.server.ast.Modifier.ModifierKind;
import com.sourcedimensions.server.ast.TypeDeclaration.TypeDeclKind;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.SourceFile;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.utils.DatabaseHelper;
import com.sourcedimensions.server.ast.Parameter.ParamKind;


public class SymbolQueryEngine 
{
	Database m_db;
	
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
		
		Collection<SnapshotNode> result = executeFromRoot(session, projectId, typeDecl, query);
		
		if (result != null)
		{
			root.setChildren(new ArrayList<SnapshotNode>());
			root.getChildren().addAll(result);
		}
		
		session.getTransaction().commit();
		
		return root;
	}
	
	protected Collection<SnapshotNode> executeFromRoot(Session session, String projectId, TypeDeclaration root, SymbolQuery symQuery)
	{
		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);
		
		if (root == null)
		{
			SortedMap<String, namespaceNode> nodeMap = new TreeMap<String, namespaceNode>();
			List<String> namespaceFilter = new ArrayList<String>();
			
			if (symQuery.getAllNamespaces())
				namespaceFilter.add("**");
			else
				namespaceFilter.addAll(symQuery.getNamespaceFilter());			
		
			if (namespaceFilter.size() == 0)
				return null;
			
			Query query = session.createQuery("SELECT td FROM TypeDeclaration td " +
				"INNER JOIN td.m_file INNER JOIN td.m_parent WHERE td.m_kind = :kind AND td.m_project IN (:projects)");
			
			query.setInteger("kind", TypeDeclKind.NAMESPACE.value());
			query.setParameterList("projects", prjSpace);
			
			List list = query.list();
					
			for (Object o : list)
			{
				for (String filter : namespaceFilter)
				{
					String[] fltr = filter.split(Folder.DIVIDER);
					TypeDeclaration decl = (TypeDeclaration)o;
					String name = decl.m_name;
					
					if (decl.getParent() instanceof TypeDeclarationMember)
					{
						Query q = session.createQuery("SELECT d FROM TypeDeclaration d INNER JOIN d.m_parent, TypeDeclarationMember m "+
							"WHERE m.m_parent.id = d.m_id AND m.m_id = :id");
	
						q.setString("id", decl.getParent().getID());
						
						for (List l = q.list(); l.size() > 0; l = q.list())
						{						
							TypeDeclaration d  = (TypeDeclaration)l.get(0);
							
							name = d.m_name + "." + name;
							
							if (d.getParent() instanceof TypeDeclarationMember)
								q.setString("id", d.getParent().getID());
							else
								break;
						}
					}

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
										addNamespace(decl, name, decl.getSourceFile().getID(), nodeMap);
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
												addNamespace(decl, name, decl.getSourceFile().getID(), nodeMap);
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
									addNamespace(decl, name, decl.getSourceFile().getID(), nodeMap);
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
			
			for (namespaceNode n : nodeMap.values())
			{
				output.add(n.m_node);
				
				for (TypeDeclaration decl : n.m_declSet)
				{
					Collection<SnapshotNode> result = executeFromRoot(session, projectId, decl, symQuery);

					if (result != null)
					{
						n.m_node.setChildren(new ArrayList<SnapshotNode>());
						n.m_node.getChildren().addAll(result);
					}
				}
			}
			
			if (symQuery.getGlobalNamespace())
			{
				output.addAll(executeTypeFilter(session, null, prjSpace, symQuery));
			}
				
			return output;			
		}
		else if (root.getKind() == TypeDeclKind.NAMESPACE)
		{
			return executeTypeFilter(session, root, prjSpace, symQuery);
		}
		
		return null;
	}

	protected List<SnapshotNode> executeTypeFilter(Session session, TypeDeclaration root, Set<Project> prjSpace, SymbolQuery symQuery)
	{
		List<TypeFilter> typeFilter = new ArrayList<TypeFilter>();
		List<SnapshotNode> output = new ArrayList<SnapshotNode>();
		Set<String> nameSet = new HashSet<String>();
		
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
		
		Query query = null;
		
		if (root == null)
		{
			query = session.createQuery("SELECT DISTINCT d FROM TypeDeclaration d INNER JOIN d.m_parent dp INNER JOIN d.m_project INNER JOIN d.m_file " +
				"LEFT JOIN d.m_modifiers LEFT JOIN d.m_baseTypes LEFT JOIN d.m_baseInterfaces WHERE dp.m_parent IS NULL AND d.m_project in (:projects)");
			
			query.setParameterList("projects", prjSpace);
		}
		else
		{
			query = session.createQuery("SELECT DISTINCT d FROM TypeDeclarationMember m INNER JOIN m.m_parent mp, " +
				"TypeDeclaration d INNER JOIN d.m_parent dp INNER JOIN d.m_project INNER JOIN d.m_file LEFT JOIN d.m_modifiers " +
				"LEFT JOIN d.m_baseTypes LEFT JOIN d.m_baseInterfaces WHERE dp.m_id = m.m_id AND mp.m_id = :id");

			query.setString("id", root.getID());		
		}
		
		List list = query.list();		
		
		for (TypeFilter filter : typeFilter)
		{
			Project prj = (Project)prjSpace.toArray()[0];
			
			switch (prj.getLanguage())
			{
				case CSHARP_11:
				case CSHARP_20:
					if (filter.getDelegate() != null)
					{
						Delegate delegate = filter.getDelegate();
						
						if (root == null)
						{
							query = session.createQuery("SELECT d FROM DelegateDeclaration d INNER JOIN d.m_parent dp LEFT JOIN d.m_parameters p " + 
								" INNER JOIN p.m_type LEFT JOIN p.m_modifiers WHERE pd.m_parent IS NULL AND d.m_project in (:projects)");
							
							query.setParameterList("projects", prjSpace);
						}
						else
						{
							query = session.createQuery("SELECT d FROM TypeDeclarationMember m, DelegateDeclaration d LEFT JOIN d.m_parameters p " +
								" INNER JOIN p.m_type LEFT JOIN p.m_modifiers WHERE d.m_parent.id = m.m_id AND m.m_parent = :id");
					
							query.setString("id", root.getID());
						}
			
						List l = query.list();
							
						for (Object obj : l)
						{
							DelegateDeclaration decl = (DelegateDeclaration)obj;
							
							if (nameSet.contains(decl.m_name))
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
							
							SnapshotNode snapshot = new SnapshotNode(Type.DELEGATE, decl.m_name);

							snapshot.setRefs(new ArrayList<Reference>());
							snapshot.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));
							
							output.add(snapshot);
							
							nameSet.add(decl.m_name);
						}							
					}
			}
			
			for (Object o : list)
			{
				TypeDeclaration decl = (TypeDeclaration)o;

				if (nameSet.contains(decl.m_name))
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
						
					case NAMESPACE:
						snapshot = new SnapshotNode(Type.NAMESPACE, decl.m_name);
						break;
						
					case ANNOT_TYPE:
						snapshot = new SnapshotNode(Type.ANNOT, decl.m_name);
				}

				
				snapshot.setRefs(new ArrayList<Reference>());
				snapshot.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));
				
				output.add(snapshot);
			
				nameSet.add(decl.m_name);
				
				snapshot.setChildren(new ArrayList<SnapshotNode>());
				
				if (filter.getInnerTypes())
					snapshot.getChildren().addAll(executeTypeFilter(session, decl, prjSpace, symQuery));
				
				switch (prj.getLanguage())
				{
					case JAVA_14:
					case JAVA_15:
						if ((filter.getCategories() & TypeCategory.ANONYMCLASS.value()) != 0)
							snapshot.getChildren().addAll(execAnonymClassFilter(session, decl, decl.getSourceFile(), filter));
				}					
			}
		}
		
		return output;
	}

	protected List<SnapshotNode> execAnonymClassFilter(Session session, TypeDeclaration root, SourceFile file, TypeFilter filter)
	{
		List<SnapshotNode> output = new ArrayList<SnapshotNode>();
		Map<String, Integer> nameCount = new HashMap<String, Integer>();
	
		Query query = session.createQuery("FROM InstanceCreationExpression e WHERE e.m_file = :file AND size(e.m_members) > 0").setEntity("file", file);
		
		List list = query.list();
		
		for (Object o : list)
		{
			InstanceCreationExpression expr = (InstanceCreationExpression)o;
			AstNode node = expr;

			query = session.createQuery("SELECT p FROM AstNode a INNER JOIN a.m_parent p WHERE a.m_id = :id");

			for (query.setString("id", node.getID()) ; (node = (AstNode)query.uniqueResult()) != null ; query.setString("id", node.getID()))
			{
				String name = expr.getType().getName();
				
				if (!Pattern.matches(filter.getName(), name))
					continue;
				
				if (node instanceof TypeDeclaration)
				{
					if (node.getID().equals(root.getID()))
					{
						Integer count = nameCount.get(name);
						
						if (count == null)
							nameCount.put(name, 1);
						else
						{
							count++;
							name = name + ":" + Integer.toString(count);
						}
						
						SnapshotNode snapshot = new SnapshotNode(Type.ANONYMCLASS, name);
	
						snapshot.setRefs(new ArrayList<Reference>());
						snapshot.getRefs().add(new Reference(expr.getID(), file.getID(), expr.m_left, expr.m_right));
						
						output.add(snapshot);
					}
					
					break;						
				}
			}
		}
		
		return output;
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
					
					if (!match)
						return false;
				}
			}
		}
		
		return true;
	}
	
	protected void addNamespace(TypeDeclaration decl, String name, String fileId, Map<String, namespaceNode> nodeMap)
	{
		namespaceNode n = nodeMap.get(name);		
		Reference ref = new Reference(decl.getID(), fileId, decl.m_left, decl.m_right);
		
		if (n == null)
		{
			n = new namespaceNode();
			
			n.m_node = new SnapshotNode(Type.NAMESPACE, name);
			nodeMap.put(name, n);
		}

		n.m_declSet.add(decl);
		n.m_node.setRefs(new ArrayList<Reference>());		
		n.m_node.getRefs().add(ref);
	}	
	
	protected class namespaceNode
	{
		public SnapshotNode m_node;
		public Set<TypeDeclaration> m_declSet = new HashSet<TypeDeclaration>();
	}
}
