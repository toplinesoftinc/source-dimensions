package com.sourcedimensions.ws.provider;

import org.codehaus.xfire.fault.XFireFault;
import org.hibernate.Session;

import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.sys.profile.User;
import com.sourcedimensions.server.sys.profile.UserSession;


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
	
	public String getProjects(String sessionID) throws XFireFault
	{
		verifySession(sessionID);
		
		return "";
	}
}