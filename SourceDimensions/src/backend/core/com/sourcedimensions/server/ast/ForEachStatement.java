package com.sourcedimensions.server.ast;

import java.util.*;

public class ForEachStatement extends EmbeddedStatement
{
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 0);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 1);
	protected Type m_type;
	public String m_identifier;
	protected Expression m_initExpr;
	protected EmbeddedStatement m_forEachStmt;
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
	
	public Expression getInitExpr()
	{
		return m_initExpr;
	}
	
	public void setInitExpr(Expression expr)
	{
		m_initExpr = expr;
		addChild(expr);
	}
	
	public EmbeddedStatement getForEachStmt()
	{
		return m_forEachStmt;
	}
	
	public void setForEachStmt(EmbeddedStatement statement)
	{
		m_forEachStmt = statement;
		addChild(statement);
	}
	
	public String toString()
	{
		return toString(m_identifier);
	}
}
