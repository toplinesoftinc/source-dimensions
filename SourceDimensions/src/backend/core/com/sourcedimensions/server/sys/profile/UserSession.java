package com.sourcedimensions.server.sys.profile;

import java.util.*;

import org.hibernate.Session;

import com.sourcedimensions.server.utils.SystemProps;

public class UserSession 
{
	protected String m_id;
	
	public User m_user;
	public Date m_created;
	public Date m_lastHit;

	
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
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		boolean valid = true;
		
		session.beginTransaction();
		
		UserSession userSession = (UserSession)session.createQuery("FROM UserSession WHERE m_id = :id").setString("id", sessionID).uniqueResult();	
		
		if (userSession == null)
			valid = false;
		else
		{
			Date now = new Date();
			if ((now.getTime() - userSession.m_lastHit.getTime()) > (1000 * SystemProps.getSessionLifetime()))
			{
				session.delete(userSession);
				valid = false;
			}
			else
			{
				userSession.m_lastHit = now;
				session.update(userSession);
			}
		}
		
		session.getTransaction().commit();
		
		return valid;
	}
}
