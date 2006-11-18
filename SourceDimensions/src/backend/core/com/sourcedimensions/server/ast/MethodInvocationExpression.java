package com.sourcedimensions.server.ast;

import java.util.*;

public class MethodInvocationExpression extends Expression 
{
	protected Expression m_methodRef;
	public List<Expression> m_arguments = new AstArrayList<Expression>(this, 0);
	public List<Expression> m_argList = new AstArrayList<Expression>(this, 1);
	public List<TypeArgument> m_typeArguments = new AstArrayList<TypeArgument>(this, 2);
	
	public Expression getMethodRef()
	{
		return m_methodRef;
	}
	
	public void setMethodRef(Expression expr)
	{
		m_methodRef = expr;
		addChild(expr);
	}
}
