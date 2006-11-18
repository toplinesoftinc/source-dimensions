package com.sourcedimensions.server.ast;

public class DoWhileStatement extends EmbeddedStatement
{
	public DoWhileStatement() { }
	
	public DoWhileStatement(DoWhileStmtKind kind)
	{
		m_kind = kind.value;
	}
	
	protected int m_kind;
	protected Expression m_condition;
	protected EmbeddedStatement m_body;
	
	public DoWhileStmtKind getKind()
	{
		return DoWhileStmtKind.values()[m_kind];
	}
	
	public enum DoWhileStmtKind
	{
		DO(0),
		WHILE(1);
		
		DoWhileStmtKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getCondition()
	{
		return m_condition;
	}
	
	public void setCondition(Expression expr)
	{
		m_condition = expr;
		addChild(expr);
	}
	
	public EmbeddedStatement getBody()
	{
		return m_body;
	}
	
	public void setBody(EmbeddedStatement statement)
	{
		m_body = statement;
		addChild(statement);
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
