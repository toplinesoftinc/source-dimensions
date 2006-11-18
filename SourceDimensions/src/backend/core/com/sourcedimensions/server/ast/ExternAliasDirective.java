package com.sourcedimensions.server.ast;

public class ExternAliasDirective extends Directive 
{
	public String m_identifier;
	
	public String toString()
	{
		return toString(m_identifier);
	}
}
