package com.sourcedimensions.client.model;

import java.util.HashSet;
import java.util.Set;

public class Project 
{
	protected String m_id;
	protected String m_name;
	protected int m_language;
	protected boolean m_readOnly;
	protected boolean m_deleted = false;
	protected Set<Project> m_parents = new HashSet<Project>();

	public String getId()
	{
		return m_id;
	}
	
	public void setId(String id)
	{
		m_id = id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public int getLanguage()
	{
		return m_language;
	}
	
	public void setLanguage(int lang)
	{
		m_language = lang;
	}
	
	public boolean getReadOnly()
	{
		return m_readOnly;
	}
	
	public void setReadOnly(boolean readOnly)
	{
		m_readOnly = readOnly;
	}
	
	public boolean getDeleted()
	{
		return m_deleted;
	}
	
	public void setDeleted(boolean deleted)
	{
		m_deleted = deleted;
	}
	
	public Set<Project> getParents()
	{
		return m_parents;
	}
	
	public void setParents(Set<Project> parents)
	{
		m_parents = parents;
	}

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

	public Language language()
	{
		return Language.values()[m_language];
	}	
	
	public String langName()
	{
		if (m_language >= 0 && m_language < langNames.length)
			return langNames[m_language];
		else
			return "?";
	}
}
