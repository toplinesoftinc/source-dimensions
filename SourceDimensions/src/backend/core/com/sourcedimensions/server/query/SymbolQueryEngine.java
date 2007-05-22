package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.hibernate.Query;
import org.hibernate.Session;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.model.SnapshotNode.Reference;
import com.sourcedimensions.client.model.SnapshotNode.Type;
import com.sourcedimensions.server.ast.TypeDeclaration;
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
		
		SnapshotNode root = executeFromRoot(session, projectId, null, query);
		
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

		SnapshotNode root = executeFromRoot(session, projectId, typeDecl, query);		
		
		session.getTransaction().commit();
		
		return root;
	}
	
	protected SnapshotNode executeFromRoot(Session session, String projectId, TypeDeclaration root, SymbolQuery symQuery)
	{
		SortedMap<String, SnapshotNode> nameMap = new TreeMap<String, SnapshotNode>();
		
		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);

		if (root == null || root.getKind() == TypeDeclKind.NAMESPACE)
		{
			List<String> namespaceFilter = new ArrayList<String>();
			
			if (symQuery.getAllNamespaces())
				namespaceFilter.add("**");
			else
				namespaceFilter.addAll(symQuery.getNamespaceFilter());			
			
			Query query = session.createQuery("FROM TypeDeclaration WHERE m_kind = :kind AND " +
					"m_project IN (:projects)");
			
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
					
					Query q = session.createQuery("SELECT p FROM TypeDeclaration d, TypeDeclarationMember m, TypeDeclaration p "+
						"WHERE d.m_parent.class = TypeDeclarationMember AND d.m_parent.id = m.id AND m.m_parent.class = TypeDeclaration " +
						"AND m.m_parent.id = p.id AND p.m_kind = :kind AND d.m_id = :id").setInteger("kind", TypeDeclKind.NAMESPACE.value());

					String id = decl.getID();
					q.setString("id", id);
					
					for (List l = q.list(); l.size() > 0; l = q.list())
					{						
						TypeDeclaration d  = (TypeDeclaration)l.get(0);
						
						id = d.getID();
						name = d.m_name + "." + name;
						q.setString("id", id);
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
										addNamespace(decl, name, nameMap);
								}
								else
								{
									if (parts[j].equals(lookahead))
									{
										wildcard = false;
										i += step;
									}
								}								
								j++;								
							}
							else
							{
								wildcard = true;
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
								{
									addNamespace(decl, name, nameMap);
								}
							}
							else
								break;
							
							i++;
							j++;
						}
					}
				}
			}
		}
		
		Iterator<String> iter = nameMap.keySet().iterator();
		SnapshotNode node = new SnapshotNode();
		node.setChildren(new ArrayList<SnapshotNode>());
		
		while (iter.hasNext())
		{
			node.getChildren().add(new SnapshotNode(Type.NAMESPACE, iter.next()));
		}
			
		return node;
	}
	
	protected void addNamespace(TypeDeclaration decl, String name, SortedMap<String, SnapshotNode> nameMap)
	{
		SnapshotNode node = nameMap.get(name);		
		Reference ref = new Reference(decl.m_id, decl.getSourceFile().getID(), decl.m_left, decl.m_right);
		
		if (node == null)
		{
			node = new SnapshotNode(Type.NAMESPACE, name);
			nameMap.put(name, node);
		}

		node.setRefs(new ArrayList<Reference>());		
		node.getRefs().add(ref);
	}	
}
