package com.sourcedimensions.client.model;

import java.util.List;


public class MemberFilter 
{
	protected int m_categories;
	protected TriStateMask m_modifiers;
	protected int m_operators;
	protected boolean m_anyParams;
	protected Type m_type;
	protected List<Parameter> m_paramList;
	protected boolean m_anyThrows;
	protected List<String> m_throwList;
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

	public void setOperators(int operators) 
	{
		m_operators = operators;
	}

	public int getOperators() 
	{
		return m_operators;
	}

	public void setAnyParams(boolean anyParams) 
	{
		m_anyParams = anyParams;
	}

	public boolean getAnyParams() 
	{
		return m_anyParams;
	}

	public void setType(Type type) 
	{
		m_type = type;
	}

	public Type getType() 
	{
		return m_type;
	}

	public void setParamList(List<Parameter> paramList) 
	{
		m_paramList = paramList;
	}

	public List<Parameter> getParamList() 
	{
		return m_paramList;
	}

	public void setAnyThrows(boolean anyThrows) 
	{
		m_anyThrows = anyThrows;
	}

	public boolean getAnyThrows() 
	{
		return m_anyThrows;
	}

	public void setThrowList(List<String> throwList) 
	{
		m_throwList = throwList;
	}

	public List<String> getThrowList() 
	{
		return m_throwList;
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
