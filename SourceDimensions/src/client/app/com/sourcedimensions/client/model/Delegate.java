package com.sourcedimensions.client.model;

import java.util.List;

public class Delegate 
{
	protected String m_name;
	protected Type m_type;
	protected boolean m_anyParams;
	protected List<Parameter> m_paramList;
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
	}
	
	public boolean getAnyParams()
	{
		return m_anyParams;
	}
	
	public void setAnyParams(boolean anyParams)
	{
		m_anyParams = anyParams; 
	}
	
	public List<Parameter> getParamList()
	{
		return m_paramList;
	}
	
	public void setParamList(List<Parameter> paramList)
	{
		m_paramList = paramList;
	}
}
