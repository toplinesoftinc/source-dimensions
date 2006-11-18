package com.sourcedimensions.server.ast;

public class SelfReferenceExpression extends Expression
{
	public SelfReferenceExpression() { }
	
	public SelfReferenceExpression(SelfRefExprKind kind)
	{
		m_kind = kind.value;
	}
	
	protected int m_kind;	
	
	public SelfRefExprKind getKind()
	{
		return SelfRefExprKind.values()[m_kind];
	}
	
	public enum SelfRefExprKind
	{
		THIS(0),
		BASE(1),
		CLASS(2);
		
		SelfRefExprKind(int val)
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
