package com.sourcedimensions.server.ast;

import java.util.*;

public class DataMember extends Member
{
	public DataMember() { }
	
	public DataMember(DataMemberKind kind)
	{
		m_kind = kind.value;
	}
	
	protected int m_kind;
	public Set<Declarator> m_declarators = new AstHashSet<Declarator>(this, 2);
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
	
	public DataMemberKind getKind()
	{
		return DataMemberKind.values()[m_kind];
	}
	
	public enum DataMemberKind
	{
		FIELD(0),
		CONST(1);
		
		DataMemberKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public void setKind(int kind) 
	{
		m_kind = kind;
	}
	
	public String toString()
	{
		return toString(getKind().toString());
	}
}
