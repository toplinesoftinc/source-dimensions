package com.sourcedimensions.server.ast;

import java.util.*;

public class InstanceCreationExpression extends Expression 
{
	protected Type m_type;
	public List<Expression> m_arguments = new AstArrayList<Expression>(this, 0);
	public Set<AbstractMember> m_members = new AstHashSet<AbstractMember>(this, 1);
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
}
