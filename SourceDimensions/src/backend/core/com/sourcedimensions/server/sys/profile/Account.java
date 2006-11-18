package com.sourcedimensions.server.sys.profile;


import java.util.*;

public class Account 
{
	public String m_id;	

	public String m_ownerName;
	public Date m_creationDate;
	public Date m_expDate;
	public boolean m_writeAccess;
	protected int m_langAccess;
	
	protected Set<User> m_users = new HashSet<User>();
	public Set<String> m_projectIds = new HashSet<String>();
	protected Database m_database;
	protected int m_status;
	
	Account()
	{
	}
	
	public Account(Database database)
	{
		m_database = database;
	}
	
	public enum AccountStatus
	{
		OPEN(0),
		SUSPENDED(1),
		CLOSED(2);
		
		
		AccountStatus(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}		
	}
	
	public String getID()
	{
		return m_id;
	}
	
	public Database getDatabase()
	{
		return m_database;
	}
	
	public void setStatus(AccountStatus status)
	{
		m_status = status.value;
	}
	
	public void setStatusValue(int status)
	{
		m_status = status;
	}
	
	public AccountStatus getStatus()
	{
		return AccountStatus.values()[m_status];
	}
	
	public int getStatusValue()
	{
		return m_status;
	}
	
	public User addUser()
	{
		User u = new User();
		u.m_account = this;
		m_users.add(u);
		return u;
	}
	
	public boolean removeUser(User u)
	{
		return m_users.remove(u);
	}
	
	public int getUserCount()
	{
		return m_users.size();
	}
	
	public Iterator<User> getUserIterator()
	{
		return m_users.iterator();
	}
	
	public LangAccess getLangAccess()
	{
		return LangAccess.values()[m_langAccess];
	}

	public int getLangAccessValue()
	{
		return m_langAccess;
	}
	
	public void setLangAccess(LangAccess access)
	{
		m_langAccess = access.value();
	}
	
	public void setLangAccessValue(int access)
	{
		m_langAccess = access;
	}	

	public enum LangAccess
	{
		JAVA(0),
		CSHARP(1),
		ALL(2);
		
		LangAccess(int val) 
		{
			value = val;
		}

		private final int value;

		public int value()
		{
			return value;
		}
	}		
}
