package com.sourcedimensions.server.ast;

import java.util.*;

public class FixedStatement extends EmbeddedStatement 
{
	protected Type m_type;
	public Set<Declarator> m_declarators = new AstHashSet<Declarator>(this, 0);
	protected EmbeddedStatement m_fixedStatement;
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
	
	public EmbeddedStatement getFixedStatement()
	{
		return m_fixedStatement;
	}
	
	public void setFixedStatement(EmbeddedStatement statement)
	{
		m_fixedStatement = statement;
		addChild(statement);
	}
}
