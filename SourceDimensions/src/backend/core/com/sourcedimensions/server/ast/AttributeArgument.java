package com.sourcedimensions.server.ast;

public class AttributeArgument extends AstNode
{
	protected ElementValue m_value;
	
	public ElementValue getValue()
	{
		return m_value;
	}
	
	public void setValue(ElementValue value)
	{
		m_value = value;
		addChild(value);
	}	
}
