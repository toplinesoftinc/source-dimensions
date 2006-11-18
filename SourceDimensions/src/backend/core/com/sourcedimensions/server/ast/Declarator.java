package com.sourcedimensions.server.ast;

public class Declarator extends AstNode 
{
	protected Initializer m_initializer;
	public String m_name;
 
	public Initializer getInitializer()
	{
		return m_initializer;
	}
	
	public void setInitializer(Initializer initializer)
	{
		m_initializer = initializer;
		addChild(initializer);
	}
	
	public String toString()
	{
		return toString(m_name);
	}
}