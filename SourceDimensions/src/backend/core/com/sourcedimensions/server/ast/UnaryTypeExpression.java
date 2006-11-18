package com.sourcedimensions.server.ast;

public class UnaryTypeExpression extends Expression 
{
	public UnaryTypeExpression() { }
	
	public UnaryTypeExpression(UnaryTypeExprKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	protected Expression m_operand;
	protected Type m_type;
	
	public UnaryTypeExprKind getKind()
	{
		return UnaryTypeExprKind.values()[m_kind];
	}
	
	public enum UnaryTypeExprKind
	{
		IS(0),
		AS(1),
		CAST(2),
		STACKALLOC(3),
		TYPE_MEMBER_ACCESS(4),
		REF_VALUE(5);
		
		UnaryTypeExprKind(int val)
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
