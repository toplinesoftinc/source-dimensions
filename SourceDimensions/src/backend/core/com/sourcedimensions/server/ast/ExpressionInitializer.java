package com.sourcedimensions.server.ast;

public class ExpressionInitializer extends Initializer 
{
	protected Expression m_expression;
	
	public Expression getExpression()
	{
		return m_expression;
	}
	
	public void setExpression(Expression expr)
	{
		m_expression = expr;
		addChild(expr);
	}
}
