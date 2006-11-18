package com.sourcedimensions.server.ast;

import java.util.*;

public class EventMember extends Member 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 2);
	public Set<Declarator> m_declarators = new AstHashSet<Declarator>(this, 3);
	protected Accessor m_addAccessor, m_removeAccessor;
	
	public Accessor getAddAccessor()
	{
		return m_addAccessor;
	}
	
	public void setAddAccessor(Accessor accessor)
	{
		m_addAccessor = accessor;
		addChild(accessor);
	}
	
	public Accessor getRemoveAccessor()
	{
		return m_removeAccessor;
	}
	
	public void setRemoveAccessor(Accessor accessor)
	{
		m_removeAccessor = accessor;
		addChild(accessor);
	}
}
