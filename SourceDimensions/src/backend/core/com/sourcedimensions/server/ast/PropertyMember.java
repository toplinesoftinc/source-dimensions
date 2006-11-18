package com.sourcedimensions.server.ast;

import java.util.*;

public class PropertyMember extends Member 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 2);
	protected Accessor m_getAccessor, m_setAccessor;
	
	public Accessor getGetAccessor()
	{
		return m_getAccessor;
	}
	
	public void setGetAccessor(Accessor accessor)
	{
		m_getAccessor = accessor;
		addChild(accessor);
	}
	
	public Accessor getSetAccessor()
	{
		return m_setAccessor;
	}
	
	public void setSetAccessor(Accessor accessor)
	{
		m_setAccessor = accessor;
		addChild(accessor);
	}
}
