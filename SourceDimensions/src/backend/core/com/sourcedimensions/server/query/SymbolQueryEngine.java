package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
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
		return executeFromRoot(projectId, null, query);
	}

	public SnapshotNode execute(String projectId, String rootId, SymbolQuery query)
	{
		Session session = m_db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		TypeDeclaration typeDecl = (TypeDeclaration)session.createQuery("FROM TypeDeclaration " +
			" WHERE m_id = :id").setString("id", rootId).uniqueResult();
		
		if (typeDecl == null)
			return null;
		
		session.getTransaction().commit();
		
		return executeFromRoot(projectId, typeDecl, query);
	}
	
	protected SnapshotNode executeFromRoot(String projectId, TypeDeclaration root, SymbolQuery query)
	{
		SnapshotNode rootNode = new SnapshotNode();
		rootNode.setChildren(new ArrayList<SnapshotNode>());
		HashSet<TypeDeclaration> leafNodes = new HashSet<TypeDeclaration>();
		
		Session session = m_db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();

		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);
		
		if (root == null || root.getKind() == TypeDeclKind.NAMESPACE)
		{
			HashSet<TypeDeclaration> leaves = new HashSet<TypeDeclaration>();	
			List<String> namespaceFilter = new ArrayList<String>();
			
			if (query.getAllNamespaces())
				namespaceFilter.add("**");
			else
				namespaceFilter.addAll(query.getNamespaceFilter());			
			
			for (String filter : namespaceFilter)
			{
				String[] names = filter.split(Folder.DIVIDER);
				
				leaves.add(root);
				
				for (int i = 0; i < names.length; i++)
				{
					if (names[i].equals("**"))
					{
						String lookahead = null;
						
						for (int j = i + 1; j < names.length; j++)
						{
							if (!names[j].equals("**"))
							{
								lookahead = names[j];
								break;
							}
						}

						Pattern pattern = null;
						
						if (lookahead != null)
							pattern = Pattern.compile(lookahead);
						
						HashSet<TypeDeclaration> buf = new HashSet<TypeDeclaration>();
						buf.addAll(leaves);
						
						while (buf.size() > 0)
						{
							Set<TypeDeclaration> copySet = (Set)buf.clone();
							
							for (TypeDeclaration decl : copySet)
							{
								Query q;

								if (decl == null)
								{
									q = session.createQuery("SELECT d FROM TypeDeclaration d INNER JOIN d.m_parent p WHERE p.m_parent IS NULL " +
										" AND d.m_kind = :kind AND d.m_project IN (:projects) ORDER BY d.m_name");
								}
								else
								{
									q = session.createQuery("FROM TypeDeclaration WHERE m_parent = :parent " +
										" AND m_kind = :kind AND m_project IN (:projects) ORDER BY m_name");
									
									q.setEntity("parent", decl);
								}
								
								q.setInteger("kind", TypeDeclKind.NAMESPACE.value());
								q.setParameterList("projects", prjSpace);
								
								List list = q.list();
								
								for (Object o : list)
								{
									TypeDeclaration d = (TypeDeclaration)o;
									
									if (pattern != null && pattern.matcher(decl.m_name).matches())
										leaves.add(d);
									else
										buf.add(d);
								}
								
								if (list.size() == 0 && pattern == null)
									leaves.add(decl);
								
								buf.remove(decl);
							}							
						}
					}
					else
					{
						Pattern pattern = Pattern.compile(names[i]);
						
						Set<TypeDeclaration> copySet = (Set)leaves.clone();
						
						for (TypeDeclaration decl : copySet)
						{
							Query q;
							
							if (decl == null)
							{
								q = session.createQuery("SELECT d FROM TypeDeclaration d INNER JOIN d.m_parent p WHERE p.m_parent IS NULL " +
									" AND d.m_kind = :kind AND d.m_project IN (:projects) ORDER BY d.m_name");
							}
							else
							{
								q = session.createQuery("FROM TypeDeclaration WHERE m_parent = :parent " +
									" AND m_kind = :kind AND m_project IN (:projects) ORDER BY m_name");
								
								q.setEntity("parent", decl);
							}
							
							q.setInteger("kind", TypeDeclKind.NAMESPACE.value());
							q.setParameterList("projects", prjSpace);
							
							List list = q.list();
							
							for (Object o : list)
							{
								TypeDeclaration d = (TypeDeclaration)o;
								
								if (pattern.matcher(d.m_name).matches())
									leaves.add(d);
							}
							
							leaves.remove(decl);
						}
					}
				}
				
				leafNodes.addAll(leaves);
				leaves.clear();				
			}
		}
		
		Map<String, SnapshotNode> snapshotMap = new HashMap<String, SnapshotNode>();
		
		for (TypeDeclaration decl : leafNodes)
		{
			if (decl != null)
			{
				snapshotMap.put(decl.m_id, new SnapshotNode(decl.getSourceFile().getID(), 
					decl.getID(), SnapshotNode.Type.NAMESPACE, decl.m_name));
				
			}
		}
		
		while (snapshotMap.size() > 0)
		{
			Set<String> idSet = new HashSet<String>();
			idSet.addAll(snapshotMap.keySet());
			
			for (String id : idSet)
			{
				
				Object obj = session.createQuery("SELECT p FROM TypeDeclaration d " + 
					" INNER JOIN d.m_parent p WHERE d.m_id = :id").setString("id", id).uniqueResult();
				
				if (obj instanceof TypeDeclaration)
				{
					TypeDeclaration parent = (TypeDeclaration)obj;
					SnapshotNode parentNode = snapshotMap.get(parent.getID());
					
					if (parentNode == null)
					{
						SnapshotNode node = new SnapshotNode(parent.getSourceFile().getID(), 
							parent.getID(), SnapshotNode.Type.NAMESPACE, parent.m_name);
						
						snapshotMap.put(parent.getID(), node);
						
						List<SnapshotNode> children = new ArrayList<SnapshotNode>();
						children.add(snapshotMap.get(id));
						node.setChildren(children);
					}
					else
					{
						parentNode.getChildren().add(snapshotMap.get(id));
					}
				}
				else
				{
					rootNode.getChildren().add(snapshotMap.get(id));					
				}
				
				snapshotMap.remove(id);
			}
		}

		session.getTransaction().commit();		
		
		return rootNode;
	}
}
