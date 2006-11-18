package com.sourcedimensions.server.ast;

public class IfStatement extends EmbeddedStatement  
{
	protected Expression m_condition;
	protected EmbeddedStatement m_ifStmt;
	protected EmbeddedStatement m_elseStmt;
	
	public Expression getCondition()
	{
		return m_condition;
	}
	
	public void setCondition(Expression expression)
	{
		m_condition = expression;
		addChild(expression);
	}
	
	public EmbeddedStatement getIfStmt()
	{
		return m_ifStmt;
	}
	
	public void setIfStmt(EmbeddedStatement statement)
	{
		m_ifStmt = statement;
		addChild(statement);
	}
	
	public EmbeddedStatement getElseStmt()
	{
		return m_elseStmt;
	}
	
	public void setElseStmt(EmbeddedStatement statement)
	{
		m_elseStmt = statement;
		addChild(statement);
	}
}
