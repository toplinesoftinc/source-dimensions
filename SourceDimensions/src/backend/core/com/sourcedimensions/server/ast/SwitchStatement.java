package com.sourcedimensions.server.ast;

import java.util.*;

public class SwitchStatement extends EmbeddedStatement 
{
	protected Expression m_switchExpr;
	public List<SwitchSection> m_sections = new AstArrayList<SwitchSection>(this, 0);
	
	public Expression getSwitchExpr()
	{
		return m_switchExpr;
	}
	
	public void setSwitchExpr(Expression expr)
	{
		m_switchExpr = expr;
		addChild(expr);
	}
}
