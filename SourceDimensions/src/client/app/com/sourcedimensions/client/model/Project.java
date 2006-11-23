package com.sourcedimensions.client.model;

public class Project 
{
	public String m_id;
	public String m_name;
	public int m_language;
	public boolean m_readOnly;
	public boolean m_deleted = false;
	
	protected String[] langNames = 
	{
		"Java 1.4",
		"Java 1.5",
		"C# 1.1",
		"C# 2.0"		
	};
	
	public String getLangName()
	{
		if (m_language >= 0 && m_language < langNames.length)
			return langNames[m_language];
		else
			return "?";
	}
}
