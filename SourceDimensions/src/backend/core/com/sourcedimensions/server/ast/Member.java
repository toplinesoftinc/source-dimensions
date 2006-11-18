package com.sourcedimensions.server.ast;

public class Member extends AbstractMember 
{
	public Member() { }

	protected Type m_type;
	
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
