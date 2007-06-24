package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.Collection;
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
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.model.TriStateBoolean;
import com.sourcedimensions.client.model.TriStateMask;
import com.sourcedimensions.client.model.TypeCategory;
import com.sourcedimensions.client.model.TypeFilter;
import com.sourcedimensions.client.model.SnapshotNode.Reference;
import com.sourcedimensions.client.model.SnapshotNode.Type;
import com.sourcedimensions.server.ast.AstNode;
import com.sourcedimensions.server.ast.Modifier;
import com.sourcedimensions.server.ast.SimpleType;
import com.sourcedimensions.server.ast.TypeDeclaration;
import com.sourcedimensions.server.ast.TypeDeclarationMember;
import com.sourcedimensions.server.ast.UserDefinedType;
import com.sourcedimensions.server.ast.TypeDeclaration.TypeDeclKind;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.utils.DatabaseHelper;


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
		if (root == null)
		{
			Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);
			SortedMap<String, namespaceNode> nodeMap = new TreeMap<String, namespaceNode>();
			List<String> namespaceFilter = new ArrayList<String>();
			
			if (symQuery.getAllNamespaces())
				namespaceFilter.add("**");
			else
				namespaceFilter.addAll(symQuery.getNamespaceFilter());			
		
			if (namespaceFilter.size() == 0)
				return null;
			
			Query query = session.createQuery("SELECT td, f.m_id, td.m_parent FROM TypeDeclaration td " +
				"INNER JOIN td.m_file f WHERE td.m_kind = :kind AND td.m_project IN (:projects)");
			
			query.setInteger("kind", TypeDeclKind.NAMESPACE.value());
			query.setParameterList("projects", prjSpace);
			
			List list = query.list();
					
			for (Object o : list)
			{
				for (String filter : namespaceFilter)
				{
					String[] fltr = filter.split(Folder.DIVIDER);
					Object[] row = (Object[])o;
					TypeDeclaration decl = (TypeDeclaration)row[0];
					String fileId = (String)row[1];			
					String name = decl.m_name;
					AstNode parent = (AstNode)row[2];
					
					if (parent instanceof TypeDeclarationMember)
					{
						Query q = session.createQuery("SELECT d, d.m_parent FROM TypeDeclaration d, TypeDeclarationMember m "+
							"WHERE m.m_parent.id = d.m_id AND m.m_id = :id");
	
						q.setString("id", parent.getID());
						
						for (List l = q.list(); l.size() > 0; l = q.list())
						{						
							Object[] r = (Object[])l.get(0);
							TypeDeclaration d  = (TypeDeclaration)r[0];
							AstNode p = (AstNode)r[1];
							
							name = d.m_name + "." + name;
							
							if (p instanceof TypeDeclarationMember)
								q.setString("id", p.getID());
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
										addNamespace(decl, name, fileId, nodeMap);
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
												addNamespace(decl, name, fileId, nodeMap);
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
									addNamespace(decl, name, fileId, nodeMap);
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
				
			return output;			
		}
		else if (root.getKind() == TypeDeclKind.NAMESPACE)
		{
		}
		
		return null;
	}

	protected List<SnapshotNode> executeTypeFilter(Session session, TypeDeclaration root, SymbolQuery symQuery)
	{
		List<TypeFilter> typeFilter = new ArrayList<TypeFilter>();
		List<SnapshotNode> output = new ArrayList<SnapshotNode>();
		
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
		
		Query query = session.createQuery("SELECT d FROM TypeDeclarationMember m, TypeDeclaration d " +
				"WHERE d.m_parent.id = m.m_id AND m.m_parent = :id");
		
		query.setString("id", root.getID());
	
		List list = query.list();
		
		for (TypeFilter filter : typeFilter)
		{
			for (Object o : list)
			{
				TypeDeclaration decl = (TypeDeclaration)o;
				
				if (!Pattern.matches(filter.getName(), decl.m_name))
					continue;

				boolean skip = true;
				
				//TODO: TypeCategory.ANONYMCLASS
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
				
				skip = false;
				
				if (!filter.getAllBaseTypes())
				{
					for (BaseType base : filter.getBaseTypes())
					{			
						Set<com.sourcedimensions.server.ast.Type> types = new HashSet<com.sourcedimensions.server.ast.Type>();
						BaseTypeCategory category = BaseTypeCategory.values()[base.getCategory()];
						
						for (com.sourcedimensions.server.ast.Type t : decl.m_baseTypes)
						{
							String name = "";
							
							if (t instanceof UserDefinedType)
							{
								UserDefinedType udt = (UserDefinedType)t;
								name = udt.m_name.get(udt.m_name.size() - 1).m_name;
								
								if (Pattern.matches(base.getName(), name) && (category == BaseTypeCategory.CLASS || 
									category == BaseTypeCategory.CLASSINTF))
								{
									types.add(t);
								}
							}
							else if (t instanceof SimpleType)
							{
								name = ((SimpleType)t).getKind().toString().toLowerCase();
								
								if (Pattern.matches(base.getName(), name) && category == BaseTypeCategory.INTEGRALTYPE)
								{
									types.add(t);
								}									
							}
						}
						
						for (com.sourcedimensions.server.ast.Type t : decl.m_baseInterfaces)
						{
							if (t instanceof UserDefinedType)
							{
								UserDefinedType udt = (UserDefinedType)t;
								String name = udt.m_name.get(udt.m_name.size() - 1).m_name;
							
								if (Pattern.matches(base.getName(), name) && category == BaseTypeCategory.INTERFACE)
								{
									types.add(t);
								}
							}	
						}
						
						if (types.size() == 0)
						{
							skip = true;
							break;
						}
					}
				}

				if (skip)
					continue;
				
				if (!checkModifiers(decl.m_modifiers, filter.getModifiers()))
					continue;

				SnapshotNode s = null;
				
				switch (decl.getKind())
				{
					case CLASS:
						s = new SnapshotNode(Type.CLASS, decl.m_name);
						break;
						
					case INTERFACE:
						s = new SnapshotNode(Type.INTERFACE, decl.m_name);
						break;
						
					case STRUCT:
						s = new SnapshotNode(Type.STRUCT, decl.m_name);
						break;
						
					case ENUM:
						s = new SnapshotNode(Type.ENUM, decl.m_name);
						break;
						
					case NAMESPACE:
						s = new SnapshotNode(Type.NAMESPACE, decl.m_name);
						break;
						
					case ANNOT_TYPE:
						s = new SnapshotNode(Type.ANNOT, decl.m_name);
				}

				s.setRefs(new ArrayList<Reference>());
				s.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(), decl.m_left, decl.m_right));
				
				output.add(s);
				
				if (filter.getInnerTypes())
					s.setChildren(executeTypeFilter(session, decl, symQuery));
			}
		}
		
		return output;
	}
	
	protected boolean checkModifiers(Set<Modifier> value, TriStateMask filter)
	{
		for (Modifier m : value)
		{
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
