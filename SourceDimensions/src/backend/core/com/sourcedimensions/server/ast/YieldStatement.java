package com.sourcedimensions.server.ast;

public class YieldStatement extends EmbeddedStatement 
{
	public YieldStatement() { }
	
	public YieldStatement(YieldStmtKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	protected Expression m_yieldExpr;
	
	public YieldStmtKind getKind()
	{
		return YieldStmtKind.values()[m_kind];
	}
	
	public enum YieldStmtKind
	{
		BREAK(0),
		RETURN(1);
		
		YieldStmtKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getYieldExpr()
	{
		return m_yieldExpr;
	}
	
	public void setYieldExpr(Expression expr)
	{
		m_yieldExpr = expr;
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
