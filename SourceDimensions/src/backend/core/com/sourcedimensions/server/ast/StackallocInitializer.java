package com.sourcedimensions.server.ast;

public class StackallocInitializer extends Initializer 
{
	protected Type m_type;
	protected Expression m_expression;
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
	
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
