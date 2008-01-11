package com.sourcedimensions.server.ast;

import java.util.*;

public class IndexerMember extends Member 
{
	public List<Name> m_indexerName = new AstArrayList<Name>(this, 2);
	public List<Parameter> m_indexerParams = new AstArrayList<Parameter>(this, 3);
	protected Accessor m_getAccessor, m_setAccessor;
	protected Type m_interfaceType;
	
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
	
	public void setInterfaceType(Type type)
	{
		m_interfaceType = type;
	}
	
	public Type getInterfaceType()
	{
		return m_interfaceType;
	}
}
