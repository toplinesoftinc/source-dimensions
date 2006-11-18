package com.sourcedimensions.server.ast;

import java.util.Set;

public class TypeParameter extends AstNode 
{	
	public String m_name;
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 0);
	
	public String toString()
	{
		return toString(m_name);
	}
}
