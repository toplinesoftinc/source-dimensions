package com.sourcedimensions.server.sys.profile;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.*;


public class Database
{
	protected static final String m_databaseConfigFile = "database.cfg.xml";
	protected static final String m_profileConfigFile = "profile.cfg.xml";
	
	public String m_id;
		
	protected String m_serverUrl;
	protected String m_databaseName;
	protected String m_userName;
	protected String m_password;
	protected static SessionFactory m_profileSessionFactory;
	protected static Configuration m_config = new Configuration().configure(m_databaseConfigFile);
	protected SessionFactory m_dbSessionFactory;
	
	protected Set<Account> m_accounts = new HashSet<Account>();	
	
	static 
    {
    	try 
    	{
    		m_profileSessionFactory = new Configuration().configure(m_profileConfigFile).buildSessionFactory();
    	} 
    	catch (Throwable ex) 
    	{
    		throw new ExceptionInInitializerError(ex);
    	}
    }
	
	public static SessionFactory getProfileSessionFactory()
	{
		return m_profileSessionFactory;
	}

	
	public String getID()
	{
		return m_id;
	}

	public Database()
	{
	}
	
	public Database(String serverUrl, String databaseName, String userName, String password)
	{
		m_serverUrl = serverUrl;
		m_databaseName = databaseName;
		m_userName = userName;
		m_password = password;
		createDbSessionFactory();
	}
	
	public String getServerUrl() 
	{
		return m_serverUrl;
	}
	
	public void setServerUrl(String serverUrl) 
	{
		m_serverUrl = serverUrl;
		createDbSessionFactory();
	}
	
	public String getDatabaseName() 
	{
		return m_databaseName;
	}

	public void setDatabaseName(String name) 
	{
		m_databaseName = name;
		createDbSessionFactory();
	}

	public String getPassword() 
	{
		return m_password;
	}

	public void setPassword(String password) 
	{
		m_password = password;
		createDbSessionFactory();
	}

	public String getUserName() 
	{
		return m_userName;
	}

	public void setUserName(String name) 
	{
		m_userName = name;
		createDbSessionFactory();
	}
	
	protected void createDbSessionFactory()
	{
		m_config.setProperty(Environment.URL, "jdbc:postgresql://" + m_serverUrl + "/" + m_databaseName + 
				"?user=" + m_userName + "&password=" + m_password);
		m_dbSessionFactory = m_config.buildSessionFactory();
	}
	
	public SessionFactory getDbSessionFactory()
	{
		if (m_dbSessionFactory == null)
			createDbSessionFactory();
		
		return m_dbSessionFactory;
	}
	
	public Account addAccount()
	{
		Account a = new Account(this);
		m_accounts.add(a);
		return a;
	}
	
	public boolean removeAccount(Account a)
	{
		return m_accounts.remove(a);
	}
	
	public int getAccountCount()
	{
		return m_accounts.size();
	}
	
	public Iterator<Account> getAccountIterator()
	{
		return m_accounts.iterator();
	}	
}
