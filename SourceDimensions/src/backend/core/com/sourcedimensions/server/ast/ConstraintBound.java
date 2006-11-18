package com.sourcedimensions.server.ast;

public class ConstraintBound extends AstNode 
{
	public ConstraintBound() { }
	
	public ConstraintBound(ConstraintBoundKind kind)
	{
		m_kind = kind.value;
	}
	
	protected int m_kind;
	protected Type m_type;
	
	public ConstraintBoundKind getKind()
	{
		return ConstraintBoundKind.values()[m_kind];
	}
	
	public enum ConstraintBoundKind
	{
		TYPE(0),
		CLASS(1),
		STRUCT(2),
		CONSTRUCTOR(3);
		
		ConstraintBoundKind(int val)
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
