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
	
	protected SnapshotNode executeFromRoot(Session session, String projectId, TypeDeclaration root, SymbolQuery query)
	{
		NamedSnapshotNode rootNode = new NamedSnapshotNode();
		
		Set<Project> prjSpace = DatabaseHelper.getProjectSpace(session, projectId);

		if (root == null || root.getKind() == TypeDeclKind.NAMESPACE)
		{
			List<String> namespaceFilter = new ArrayList<String>();
			
			if (query.getAllNamespaces())
				namespaceFilter.add("**");
			else
				namespaceFilter.addAll(query.getNamespaceFilter());			
			
			for (String filter : namespaceFilter)
			{
				String[] names = filter.split(Folder.DIVIDER);
				
				applyNamespaceFilter(session, prjSpace, rootNode, null, names, 0);
			}			
		}
		
		return rootNode;
	}
	
	protected void applyNamespaceFilter(Session session, Set<Project> prjSpace, 
			NamedSnapshotNode node, TypeDeclaration decl, String[] filter, int pos)
	{
		if (filter[pos].equals("**"))
		{
			String lookahead = null;
			
			for (int j = pos + 1; j < filter.length; j++)
			{
				if (!filter[j].equals("**"))
				{
					lookahead = filter[j];
					break;
				}
			}
			
			//TODO: apply filter with wildcards
		}
		else
		{
			Pattern pattern = Pattern.compile(filter[pos]);

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
			
			List<TypeDeclaration> list = q.list();
			
			if (list.size() == 0)
			{
				if (node.getRefs() == null)
					node.setRefs(new ArrayList<Reference>());
				
				node.getRefs().add(new Reference(decl.getID(), decl.getSourceFile().getID(),
						decl.m_left, decl.m_right));
			}
			
			for (TypeDeclaration d : list)
			{	
				if (pattern.matcher(d.m_name).matches())
				{
					NamedSnapshotNode n = new NamedSnapshotNode(Type.NAMESPACE, d.m_name);
					node.addChild(d.m_name, n);
					
					if (filter.length > (pos + 1))
					{
						applyNamespaceFilter(session, prjSpace, n, d, filter, pos + 1);
					}
				}
			}			
		}
	}
}
