package com.sourcedimensions.server.ast;

public class TypeExpression extends Expression 
{
	public TypeExpression() { }
	
	public TypeExpression(TypeExprKind kind)
	{
		m_kind = kind.value;
	}

	public int m_kind;
	protected Type m_type;
	
	public TypeExprKind getKind()
	{
		return TypeExprKind.values()[m_kind];
	}
	
	public enum TypeExprKind
	{
		TYPEOF(0),
		SIZEOF(1),
		DEFAULT_VALUE(2);
		
		TypeExprKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
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
