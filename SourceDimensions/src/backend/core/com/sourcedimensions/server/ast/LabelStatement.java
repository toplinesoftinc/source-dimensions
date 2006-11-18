package com.sourcedimensions.server.ast;

public class LabelStatement extends EmbeddedStatement 
{
	public String m_label;
	protected AbstractStatement m_statement;
	
	public AbstractStatement getStatement()
	{
		return m_statement;
	}
	
	public void setStatement(AbstractStatement statement)
	{
		m_statement = statement;
		addChild(statement);
	}
	
	public String toString()
	{
		return toString(m_label);
	}
}
