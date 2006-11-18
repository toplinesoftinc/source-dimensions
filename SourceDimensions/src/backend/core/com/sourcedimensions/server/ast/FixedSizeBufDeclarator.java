package com.sourcedimensions.server.ast;

public class FixedSizeBufDeclarator extends AstNode 
{	
	protected Expression m_expression;
	public String m_name;
	
	public Expression getExpression()
	{
		return m_expression;
	}
	
	public void setExpression(Expression expr)
	{
		m_expression = expr;
		addChild(expr);
	}
	
	public String toString()
	{
		return toString(m_name);
	}
}
