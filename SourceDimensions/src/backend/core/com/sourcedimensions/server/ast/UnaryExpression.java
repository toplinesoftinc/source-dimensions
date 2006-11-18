package com.sourcedimensions.server.ast;

public class UnaryExpression extends Expression 
{
	public UnaryExpression() { }
	
	public UnaryExpression(UnaryExprKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	protected Expression m_operand;
	
	public UnaryExprKind getKind()
	{
		return UnaryExprKind.values()[m_kind];
	}
	
	public enum UnaryExprKind
	{
		ADDRESSOF(0),
		PTR_INDIRECTION(1),
		PLUS(2),
		MINUS(3),
		NOT(4),
		INVERSION(5),
		PRE_INCR(6),
		PRE_DECR(7),
		POST_INCR(8),
		POST_DECR(9),
		CHECKED(10),
		UNCHECKED(11),
		PARENTHESIZED(12),
		MAKE_REF(13),
		REF_TYPE(14),
		REF_ARG(15),
		OUT_ARG(16);
		
		UnaryExprKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getOperand()
	{
		return m_operand;
	}
	
	public void setOperand(Expression expr)
	{
		m_operand = expr;
		addChild(expr);
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
