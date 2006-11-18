package com.sourcedimensions.server.ast;

import java.util.List;

public class Name extends AstNode
{	
	public String m_name;
	public List<TypeArgument> m_arguments = new AstArrayList<TypeArgument>(this, 0);
	
	public String toString()
	{
		return toString(m_name);
	}
}
