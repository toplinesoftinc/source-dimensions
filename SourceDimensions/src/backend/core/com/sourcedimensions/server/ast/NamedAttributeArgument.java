package com.sourcedimensions.server.ast;

public class NamedAttributeArgument extends AttributeArgument 
{
	public String m_name;
	
	public String toString()
	{
		return toString(m_name);
	}
}
