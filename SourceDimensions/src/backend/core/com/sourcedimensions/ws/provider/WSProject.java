package com.sourcedimensions.ws.provider;

public class WSProject implements IProject
{
	public String m_id;
	public String m_name;
	public int m_lang;
	public boolean m_readOnly;
		
	public String getID()
	{
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public int getLanguage()
	{
		return m_lang;
	}
	
	public boolean getReadOnly()
	{
		return m_readOnly;
	}
}
