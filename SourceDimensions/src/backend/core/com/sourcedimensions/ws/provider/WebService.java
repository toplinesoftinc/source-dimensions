package com.sourcedimensions.ws.provider;

import java.util.*;
import org.codehaus.xfire.fault.XFireFault;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sourcedimensions.server.sys.profile.*;
import com.sourcedimensions.server.sys.Project;


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
	
	private boolean verifySession(String sessionID) throws XFireFault
	{
		if (!UserSession.validateSession(sessionID))
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.SESSION_EXPIRED.name());
			throw fault;
		}
		else
			return true;
	}
	
	public Set<IProject> getProjectList(String sessionID) throws XFireFault
	{
		Set<IProject> prjSet = new HashSet<IProject>();
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
			WSProject prj = new WSProject();

			prj.m_id = p.getID();
			prj.m_name = p.m_name;
			prj.m_lang = p.getLangValue();
			
			for (Project pp : p.m_parents)
			{
				WSProject wpp = new WSProject();

				wpp.m_id = pp.getID();
				wpp.m_name = pp.m_name;
				wpp.m_lang = pp.getLangValue();

				prj.m_parents.add(wpp);
			}

			prjSet.add(prj);
		}
		
		session.getTransaction().commit();
		session.close();
		
		return prjSet;
	}
}