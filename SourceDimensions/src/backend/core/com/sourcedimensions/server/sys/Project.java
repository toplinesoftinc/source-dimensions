package com.sourcedimensions.server.sys;

import java.util.*;

public class Project 
{
	public String m_id;
	
	public Set<Project> m_parents = new HashSet<Project>();
	public String m_name;
	public String m_rootPath;
	public boolean m_readOnly = false;
	protected int m_language;
	protected Folder m_root;
	
	public Project()
	{
		m_root = new Folder(this);
	}
	
	public String getID()
	{ 
		return m_id;
	}	
	
	public Folder getRoot()
	{
		return m_root;
	}
	
	public void setRoot(Folder root)
	{
		m_root = root;
	}
	
	public Language getLanguage()
	{
		return Language.values()[m_language];
	}
	
	public void setLanguage(Language lang)
	{
		m_language = lang.value();
	}
	
	public int getLangValue()
	{
		return m_language;
	}
	
	public void setLangValue(int lang)
	{
		m_language = lang;
	}
	
	public enum Language
	{
		JAVA_14(0),
		JAVA_15(1),
		CSHARP_11(2),
		CSHARP_20(3);
		
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
}
