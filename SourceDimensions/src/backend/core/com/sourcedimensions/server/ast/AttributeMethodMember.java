package com.sourcedimensions.server.ast;

public class AttributeMethodMember extends Member 
{
	protected ElementValue m_defaultValue;
	public String m_name;
		
	public ElementValue getDefaultValue()
	{
		return m_defaultValue;
	}
	
	public void setDefaultValue(ElementValue defaultValue)
	{
		m_defaultValue = defaultValue;
		addChild(defaultValue);
	}
	
	public String toString()
	{
		return toString(m_name);
	}
}
