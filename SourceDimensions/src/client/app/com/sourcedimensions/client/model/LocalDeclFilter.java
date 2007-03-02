package com.sourcedimensions.client.model;

public class LocalDeclFilter
{
	protected TriStateBoolean m_final;
	protected Type m_type;
	protected String m_name;
	
	public void setFinal(TriStateBoolean isFinal) 
	{
		m_final = isFinal;
	}
	
	public TriStateBoolean getFinal() 
	{
		return m_final;
	}

	public void setType(Type type) 
	{
		m_type = type;
	}

	public Type getType() 
	{
		return m_type;
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
