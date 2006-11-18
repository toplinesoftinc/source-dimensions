package com.sourcedimensions.server.ast;

public class UsingStatement extends EmbeddedStatement 
{
	protected ResourceAcquisition m_resAcquisition;
	protected EmbeddedStatement m_statement;
	
	public ResourceAcquisition getResAcquisition()
	{
		return m_resAcquisition;
	}
	
	public void setResAcquisition(ResourceAcquisition resAcquisition)
	{
		m_resAcquisition = resAcquisition;
		addChild(resAcquisition);
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
