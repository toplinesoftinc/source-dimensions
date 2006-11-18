package com.sourcedimensions.server.ast;

public class BinaryExpression extends Expression 
{
	public BinaryExpression() { }
	
	public BinaryExpression(BinaryExprKind kind)
	{
		m_kind = kind.value;
	}
	
	protected int m_kind;
	
	protected Expression m_leftOperand;
	protected Expression m_rightOperand;
	
	public BinaryExprKind getKind()
	{
		return BinaryExprKind.values()[m_kind];
	}
		
	public enum BinaryExprKind
	{
		NULL_COALESCE(0),
		MEMBER_ACCESS(1),
		PTR_MEM_ACCESS(2),
		REM(3),
		DIV(4),
		MULT(5),
		MINUS(6),
		PLUS(7),
		USHIFT(8),
		LSHIFT(9),
		RSHIFT(10),
		GT_EQUAL(11),
		LESS_EQUAL(12),
		GREATER(13),
		LESS(14),
		NOT_EQUAL(15),
		EQUAL(16),
		BITWISE_AND(17),
		XOR(18),
		BITWISE_OR(19),
		AND(20),
		OR(21),
		ASSIGNMENT(22),
		PLUS_ASSIGNMENT(23),
		MINUS_ASSIGNMENT(24),
		MULT_ASSIGNMENT(25),
		DIV_ASSIGNMENT(26),
		AND_ASSIGNMENT(27),
		OR_ASSIGNMENT(28),
		XOR_ASSIGNMENT(29),
		REM_ASSIGNMENT(30),
		LSHIFT_ASSIGNMENT(31),
		RSHIFT_ASSIGNMENT(32),
		USHIFT_ASSIGNMENT(33),
		ARRAY_ACCESS(34);
		
		BinaryExprKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getLeftOperand()
	{
		return m_leftOperand;
	}
	
	public void setLeftOperand(Expression expr)
	{
		m_leftOperand = expr;
		addChild(expr);
	}
	
	public Expression getRightOperand()
	{
		return m_rightOperand;
	}
	
	public void setRightOperand(Expression expr)
	{
		m_rightOperand = expr;
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
