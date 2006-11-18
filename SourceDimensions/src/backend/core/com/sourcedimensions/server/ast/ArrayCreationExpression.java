package com.sourcedimensions.server.ast;

import java.util.*;

public class ArrayCreationExpression extends Expression 
{
	protected Type m_type;
	public List<Expression> m_dimExpr = new AstArrayList<Expression>(this, 0);
	public int m_rank;
	protected ArrayInitializer m_initializer;
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
	
	public ArrayInitializer getInitializer()
	{
		return m_initializer;
	}
	
	public void setInitializer(ArrayInitializer initializer)
	{
		m_initializer = initializer;
		addChild(initializer);
	}
	
	public String toString()
	{
		return toString(Integer.toString(m_rank));
	}
}
