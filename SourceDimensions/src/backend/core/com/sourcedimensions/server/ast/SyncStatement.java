package com.sourcedimensions.server.ast;

public class SyncStatement extends EmbeddedStatement
{
	protected Expression m_lockExpr;
	protected EmbeddedStatement m_statement;
	
	public Expression getLockExpr()
	{
		return m_lockExpr;
	}
	
	public void setLockExpr(Expression expr)
	{
		m_lockExpr = expr;
		addChild(expr);
	}
	
	public EmbeddedStatement getStatement()
	{
		return m_statement;
	}
	
	public void setStatement(EmbeddedStatement statement)
	{
		m_statement = statement;
		addChild(statement);
	}
}
