package com.sourcedimensions.server.ast;

import java.util.*;

public class AttributeItem extends AstNode 
{
	protected UserDefinedType m_type;
	public List<AttributeArgument> m_arguments = new AstArrayList<AttributeArgument>(this, 0);
	
	public UserDefinedType getType()
	{
		return m_type;
	}
	
	public void setType(UserDefinedType type)
	{
		m_type = type;
		addChild(type);
	}
}
