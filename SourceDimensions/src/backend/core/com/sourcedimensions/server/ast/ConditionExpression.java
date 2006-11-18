package com.sourcedimensions.server.ast;

public class ConditionExpression extends Expression 
{
	protected Expression m_condition;
	protected Expression m_trueExpr;
	protected Expression m_falseExpr;
	
	public Expression getCondition()
	{
		return m_condition;
	}
	
	public void setCondition(Expression condition)
	{
		m_condition = condition;
		addChild(condition);
	}
	
	public Expression getTrueExpr()
	{
		return m_trueExpr;
	}
	
	public void setTrueExpr(Expression expr)
	{
		m_trueExpr = expr;
		addChild(expr);
	}
	
	public Expression getFalseExpr()
	{
		return m_falseExpr;
	}
	
	public void setFalseExpr(Expression expr)
	{
		m_falseExpr = expr;
		addChild(expr);
	}
}
