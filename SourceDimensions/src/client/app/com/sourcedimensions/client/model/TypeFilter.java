package com.sourcedimensions.client.model;

import java.util.List;


public class TypeFilter 
{	
	protected int m_categories;
	protected TriStateMask m_modifiers;
	protected boolean m_allBaseTypes;
	protected boolean m_innerTypes;
	protected List<BaseType> m_baseTypes;
	protected Delegate m_delegate;
	protected String m_name;
	
	public void setCategories(int categories) 
	{
		m_categories = categories;
	}
	
	public int getCategories() 
	{
		return m_categories;
	}

	public void setModifiers(TriStateMask modifiers) 
	{
		m_modifiers = modifiers;
	}

	public TriStateMask getModifiers()
	{
		return m_modifiers;
	}

	public void setAllBaseTypes(boolean allBaseTypes) 
	{
		m_allBaseTypes = allBaseTypes;
	}

	public boolean getAllBaseTypes() 
	{
		return m_allBaseTypes;
	}

	public void setInnerTypes(boolean innerTypes) 
	{
		m_innerTypes = innerTypes;
	}

	public boolean getInnerTypes() 
	{
		return m_innerTypes;
	}

	public void setBaseTypes(List<BaseType> baseTypes) 
	{
		m_baseTypes = baseTypes;
	}

	public List<BaseType> getBaseTypes() {
		return m_baseTypes;
	}

	public void setDelegate(Delegate delegate) 
	{
		m_delegate = delegate;
	}

	public Delegate getDelegate() 
	{
		return m_delegate;
	}

	public void setName(String name) 
	{
		m_name = name;
	}

	public String getName() 
	{
		return m_name;
	}
}
