package com.sourcedimensions.server.ast;

public class TypeArgument extends Type 
{
	public TypeArgument() { }
	
	public TypeArgument(TypeArgKind kind)
	{
		m_kind = kind.value;
	}

	public int m_kind;
	protected Type m_refType;
	
	public TypeArgKind getKind()
	{
		return TypeArgKind.values()[m_kind];
	}
	
	public enum TypeArgKind
	{
		EXACT(0),
		SUPER(1),
		EXTENDS(2),
		WILDCARD(3);
		
		TypeArgKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}	
	
	public Type getRefType()
	{
		return m_refType;
	}
	
	public void setRefType(Type type)
	{
		m_refType = type;
		addChild(type);
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
