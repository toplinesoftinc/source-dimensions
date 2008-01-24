package com.sourcedimensions.ws.provider;

import java.util.*;
import org.codehaus.xfire.fault.XFireFault;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.server.query.SymbolQueryEngine;
import com.sourcedimensions.server.sys.Project.Language;
import com.sourcedimensions.server.sys.profile.*;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.utils.DatabaseHelper;


public class WebService implements IWebService
{
	public String login(String userName, String password) throws XFireFault
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		User user = (User)session.createQuery("FROM User WHERE m_name = :name AND m_password = :password").
			setString("name", userName).setString("password", password).uniqueResult();

		session.getTransaction().commit();		
		
		if (user == null)
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.LOGIN_FAILED.name());
			throw fault;
		}
		else
		{
			UserSession userSession = UserSession.createSession(user);
			return userSession.getID();
		}
	}
	
	private void verifySession(String sessionID) throws XFireFault
	{
		if (!UserSession.validateSession(sessionID))
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.SESSION_EXPIRED.name());
			throw fault;
		}
	}
	
	private void verifyLanguage(String sessionID, String projectId) throws XFireFault
	{
		boolean valid = true;
		
		Database db = DatabaseHelper.getDbBySessionID(sessionID);
		
		Session session = db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		Project prj = (Project)session.get(Project.class, projectId);	
		session.getTransaction().commit();
		
		if (prj == null)
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.PROJECT_NOT_FOUND.name());
			throw fault;
		}
		
		session = Database.getProfileSessionFactory().getCurrentSession();
		session.beginTransaction();
	
		Account account = (Account)session.createQuery("SELECT a FROM UserSession s " +
			"INNER JOIN s.m_user.m_account a WHERE s.m_id = :id").setString("id", sessionID).uniqueResult();	
		
		if (account != null)
		{
			switch (account.getLangAccess())
			{
				case ALL:
					break;
					
				case JAVA:
					if (prj.getLanguage() == Language.CSHARP_11 || 
						prj.getLanguage() == Language.CSHARP_20)
					{
						valid = false;
					}
					break;
					
				case CSHARP:
					if (prj.getLanguage() == Language.JAVA_14 || 
						prj.getLanguage() == Language.JAVA_15)
					{
						valid = false;
					}
			}
		}
		
		session.getTransaction().commit();
		
		if (!valid)
		{
			XFireFault fault = new XFireFault(new Exception());			
			fault.setRole(FaultValues.LANG_ACCESS_DENIED.name());
			throw fault;			
		}
	}
	
	public Set<com.sourcedimensions.client.model.Project> getProjectList(String sessionID) throws XFireFault
	{
		Set<com.sourcedimensions.client.model.Project> prjSet = new HashSet<com.sourcedimensions.client.model.Project>();
		verifySession(sessionID);
			
		Session session = Database.getProfileSessionFactory().openSession();
				
		session.beginTransaction();

		Account account = (Account)session.createQuery("SELECT s.m_user.m_account FROM UserSession s WHERE s.m_id = :id").
			setString("id", sessionID).uniqueResult();
		
		Set<String> prjIDs = new HashSet<String>();
		prjIDs.addAll(account.m_projectIds);
		
		Database db = account.getDatabase();
		
		SessionFactory factory = db.getDbSessionFactory(); 
		
		session.getTransaction().commit();
		session.close();
		
		session = factory.openSession();
		
		session.beginTransaction();
		
		for (String id : prjIDs)
		{
			Project p = (Project)session.get(Project.class, id);
			
			com.sourcedimensions.client.model.Project prj = 
					new com.sourcedimensions.client.model.Project();

			prj.setId(p.getID());
			prj.setName(p.m_name);
			prj.setLanguage(p.getLangValue());
			
			for (Project pp : p.m_parents)
			{
				com.sourcedimensions.client.model.Project wpp = 
						new com.sourcedimensions.client.model.Project();

				wpp.setId(pp.getID());
				wpp.setName(pp.m_name);
				wpp.setLanguage(pp.getLangValue());

				prj.getParents().add(wpp);
			}

			prjSet.add(prj);
		}
		
		session.getTransaction().commit();
		session.close();
		
		return prjSet;
	}
	
	public Snapshot runSymbolQuery(String sessionID, String projectId, SymbolQuery query) throws XFireFault
	{
		verifySession(sessionID);
		verifyLanguage(sessionID, projectId);
		SymbolQueryEngine engine = new SymbolQueryEngine(sessionID);
		Snapshot snapshot = new Snapshot();
		snapshot.setRoot(engine.execute(projectId, query));
		
		return snapshot;
	}
	
	public Set<SnapshotNode> runSymbolSubquery(String sessionID, String projectId, Set<SnapshotNode> rootSet, SymbolQuery query) throws XFireFault
	{
		verifySession(sessionID);
		verifyLanguage(sessionID, projectId);
		SymbolQueryEngine engine = new SymbolQueryEngine(sessionID);
		return engine.execute(projectId, rootSet, query);
	}	
}