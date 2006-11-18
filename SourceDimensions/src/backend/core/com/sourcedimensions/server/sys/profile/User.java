package com.sourcedimensions.server.sys.profile;

public class User 
{
	protected String m_id;
	
	public String m_name;
	public String m_password;
	public String m_fullName;	
	public boolean m_superUser;
	public Account m_account;
	
	public String getID()
	{
		return m_id;
	}
}
