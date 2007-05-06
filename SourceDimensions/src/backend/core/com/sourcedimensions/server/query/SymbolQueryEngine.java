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

		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(m_db, projectId);
		
		Session session = m_db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		if (root == null || root.getKind() == TypeDeclKind.NAMESPACE)
		{
			HashSet<TypeDeclaration> leaves = new HashSet<TypeDeclaration>();
			
			for (String filter : query.getNamespaceFilter())
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
									q = session.createQuery("FROM TypeDeclaration WHERE m_parent IS NULL " +
										" AND m_kind = :kind AND m_project IN (:projects)");
								}
								else
								{
									q = session.createQuery("FROM TypeDeclaration WHERE m_parent = :parent " +
										" AND m_kind = :kind AND m_project IN (:projects)");
									
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
								q = session.createQuery("FROM TypeDeclaration WHERE m_parent IS NULL " +
									" AND m_kind = :kind AND m_project IN (:projects)");
							}
							else
							{
								q = session.createQuery("FROM TypeDeclaration WHERE m_parent = :parent " +
									" AND m_kind = :kind AND m_project IN (:projects)");
								
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
		
		Map<TypeDeclaration, SnapshotNode> snapshotMap = new HashMap<TypeDeclaration, SnapshotNode>();
		
		for (TypeDeclaration decl : leafNodes)
		{
			if (decl != null)
			{
				snapshotMap.put(decl, new SnapshotNode(SnapshotNode.Type.NAMESPACE, decl.m_name));
				
			}
		}
		
		while (snapshotMap.size() > 0)
		{
			TypeDeclaration[] copyArray = (TypeDeclaration[])snapshotMap.keySet().toArray();
			
			for (TypeDeclaration decl : copyArray)
			{
				TypeDeclaration parent = (TypeDeclaration)decl.getParent();
				
				if (parent == root)
				{
					rootNode.getChildren().add(snapshotMap.get(decl));
				}
				else
				{
					SnapshotNode parentNode = snapshotMap.get(parent);
					
					if (parentNode == null)
					{
						SnapshotNode node = new SnapshotNode(SnapshotNode.Type.NAMESPACE, parent.m_name);
						snapshotMap.put(decl, node);
						
						List<SnapshotNode> children = new ArrayList<SnapshotNode>();
						children.add(snapshotMap.get(decl));
						node.setChildren(children);
					}
					else
					{
						parentNode.getChildren().add(snapshotMap.get(decl));
					}
				}
				
				snapshotMap.remove(decl);
			}
		}

		session.getTransaction().commit();		
		
		return rootNode;
	}
}
