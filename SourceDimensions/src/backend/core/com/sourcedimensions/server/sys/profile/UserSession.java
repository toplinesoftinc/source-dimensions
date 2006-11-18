package com.sourcedimensions.server.sys.profile;

import java.util.*;

import org.hibernate.Session;

public class UserSession 
{
	protected String m_id;
	
	public User m_user;
	public Date m_created;
	public Date m_lastHit;
	
	protected static final long m_sessionLifetime = 1000 * 1800; // in millisec.  
	
	public String getID()
	{
		return m_id;
	}

	public static UserSession createSession(User user)
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		session.createQuery("DELETE FROM UserSession WHERE m_user = :user").setEntity("user", user).executeUpdate();
		
		UserSession userSession = new UserSession();
		userSession.m_user = user;
		userSession.m_created = new Date();
		userSession.m_lastHit = new Date();
		
		session.save(userSession);
		
		session.getTransaction().commit();
		
		return userSession;
	}
	
	public static boolean validateSession(String sessionID)
	{
		boolean valid = true;
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		UserSession userSession = (UserSession)session.get(UserSession.class, sessionID);
		
		if (userSession == null)
			valid = false;
		else
		{
			Date now = new Date();
			if (now.getTime() - userSession.m_lastHit.getTime() > m_sessionLifetime)
			{
				session.delete(userSession);
				valid = false;
			}
		}
		
		session.getTransaction().commit();
		
		return valid;
	}
}
