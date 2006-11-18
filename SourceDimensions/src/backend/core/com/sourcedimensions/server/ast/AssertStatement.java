package com.sourcedimensions.server.ast;

public class AssertStatement extends EmbeddedStatement 
{
	protected Expression m_evalExpr;
	protected Expression m_msgDetailExpr;
	
	public Expression getEvalExpr()
	{
		return m_evalExpr;
	}
	
	public void setEvalExpr(Expression expr)
	{
		m_evalExpr = expr;
		addChild(expr);
	}
	
	public Expression getMsgDetailExpr()
	{
		return m_msgDetailExpr;
	}
	
	public void setMsgDetailExpr(Expression expr)
	{
		m_msgDetailExpr = expr;
		addChild(expr);
	}
}
