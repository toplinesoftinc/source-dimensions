package com.sourcedimensions.server.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.profile.Database;

public class DatabaseHelper 
{
	public static Database getDbBySessionID(String sessionID)
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		Database db = (Database)session.createQuery("SELECT db FROM UserSession s " +
			" INNER JOIN s.m_user.m_account.m_database db WHERE s.m_id = :id").setString("id", sessionID).uniqueResult();
				
		session.getTransaction().commit();
		
		return db;
	}
	
	public static Set<Project> getProjectSpace(Session session, String projectID)
	{
		Set<Project> set = new HashSet<Project>();
		int size = 0;
		List<Project> list = new ArrayList<Project>();
		List<Project> cur = new ArrayList<Project>();
		
		cur.add((Project)session.get(Project.class, projectID));
		set.addAll(cur);
		
		while (size != set.size())
		{
			size = set.size();
			
			list.addAll(session.createQuery("SELECT pars FROM Project p " + 
				" INNER JOIN p.m_parents pars WHERE p IN (:parents)").setParameterList("parents", cur).list());

			cur.clear();
			cur.addAll(list);
			set.addAll(list);
			list.clear();
		}
		
		return set;
	}
}
