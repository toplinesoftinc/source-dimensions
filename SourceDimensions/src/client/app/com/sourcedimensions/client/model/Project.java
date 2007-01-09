package com.sourcedimensions.client.model;

import java.util.HashSet;
import java.util.Set;

public class Project 
{
	public String m_id;
	public String m_name;
	public int m_language;
	public boolean m_readOnly;
	public boolean m_deleted = false;
	public Set<Project> m_parents = new HashSet<Project>();
	
	public enum Language
	{
		JAVA14(0),
		JAVA15(1),
		CSHARP11(2),
		CSHARP20(3);
		
		Language(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	protected String[] langNames = 
	{
		"Java 1.4",
		"Java 1.5",
		"C# 1.1",
		"C# 2.0"		
	};

	public Language getLanguage()
	{
		return Language.values()[m_language];
	}	
	
	public String getLangName()
	{
		if (m_language >= 0 && m_language < langNames.length)
			return langNames[m_language];
		else
			return "?";
	}
}
