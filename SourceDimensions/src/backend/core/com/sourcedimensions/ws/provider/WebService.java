package com.sourcedimensions.ws.provider;

import java.util.Set;
import org.codehaus.xfire.fault.XFireFault;
import org.hibernate.Session;
import com.sourcedimensions.server.sys.profile.*;


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
	
	private UserSession verifySession(String sessionID) throws XFireFault
	{
		UserSession us = UserSession.validateSession(sessionID);
		
		if (us == null)
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.SESSION_EXPIRED.name());
			throw fault;
		}
		else
			return us;
	}
	
	public Set<IProject> getProjectList(String sessionID) throws XFireFault
	{
		verifySession(sessionID);
		
		
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();

		// TODO
		
		session.getTransaction().commit();
		
		return null;
	}
}