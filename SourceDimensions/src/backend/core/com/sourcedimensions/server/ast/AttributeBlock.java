package com.sourcedimensions.server.ast;

import java.util.*;

public class AttributeBlock extends AstNode 
{
	public String m_target;
	public Set<AttributeItem> m_items = new AstHashSet<AttributeItem>(this, 0);
	
	public String toString()
	{
		return toString(m_target);
	}
}
