package com.sourcedimensions.client.model;

public class BaseType 
{
	protected String m_name;
	protected int m_category; 

	public BaseType()
	{
	}
	
	public BaseType(String name, int category)
	{
		setName(name);
		setCategory(category);
	}

	public void setName(String name) 
	{
		m_name = name;
	}

	public String getName() 
	{
		return m_name;
	}

	public void setCategory(int category) 
	{
		m_category = category;
	}

	public int getCategory() 
	{
		return m_category;
	}		
}
