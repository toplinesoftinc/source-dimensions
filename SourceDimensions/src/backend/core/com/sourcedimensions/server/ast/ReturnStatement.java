package com.sourcedimensions.server.ast;

public class ReturnStatement extends EmbeddedStatement 
{
	protected Expression m_returnExpr;
	
	public Expression getReturnExpr()
	{
		return m_returnExpr;
	}
	
	public void setReturnExpr(Expression expr)
	{
		m_returnExpr = expr;
		addChild(expr);
	}
}
