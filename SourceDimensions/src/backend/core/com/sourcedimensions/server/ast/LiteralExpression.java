package com.sourcedimensions.server.ast;


public class LiteralExpression extends Expression 
{
	public LiteralExpression() { }
	
	public LiteralExpression(LiteralExprKind kind)
	{
		m_kind = kind.value;
	}

	public String m_value;	
	protected int m_kind;
	
	public LiteralExprKind getKind()
	{
		return LiteralExprKind.values()[m_kind];
	}
	
	public enum LiteralExprKind
	{
		CHAR(0),
		STRING(1),
		INT(2),
		FLOAT(3),
		TRUE(4),
		FALSE(5),
		NULL(6);
		
		LiteralExprKind(int val)
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
		if (m_value != null && m_value.length() > 0)
			return toString(getKind().toString() + "/" + m_value);
		else
			return toString(getKind().toString());
	}
}
