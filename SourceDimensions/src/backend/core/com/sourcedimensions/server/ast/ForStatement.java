package com.sourcedimensions.server.ast;

import java.util.*;

public class ForStatement extends EmbeddedStatement 
{
	protected LocalVariableDeclaration m_initVarDecl;
	public List<Expression> m_initExprList = new AstArrayList<Expression>(this, 0);
	protected Expression m_condition;
	public List<Expression> m_iteration = new AstArrayList<Expression>(this, 1);
	protected EmbeddedStatement m_forStmt;
	
	public LocalVariableDeclaration getInitVarDecl()
	{
		return m_initVarDecl;
	}
	
	public void setInitVarDecl(LocalVariableDeclaration declaration)
	{
		m_initVarDecl = declaration;
		addChild(declaration);
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
	
	public EmbeddedStatement getForStmt()
	{
		return m_forStmt;
	}
	
	public void setForStmt(EmbeddedStatement statement)
	{
		m_forStmt = statement;
		addChild(statement);
	}
}
