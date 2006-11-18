package com.sourcedimensions.server.ast;

public class JumpStatement extends EmbeddedStatement
{
	public JumpStatement() { }
	
	public JumpStatement(JumpStmtKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;
	public String m_label;
	protected Expression m_jumpExpr;
	
	public JumpStmtKind getKind()
	{
		return JumpStmtKind.values()[m_kind];
	}
	
	public enum JumpStmtKind
	{
		BREAK(0),
		CONTINUE(1),
		RETURN(2),
		THROW(3),
		GOTO_ID(4),
		GOTO_CASE(5),
		GOTO_DEFAULT(6);
		
		JumpStmtKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getJumpExpr()
	{
		return m_jumpExpr;
	}
	
	public void setJumpExpr(Expression expr)
	{
		m_jumpExpr = expr;
		addChild(expr);
	}
	
	public void setKind(int kind) 
	{
		m_kind = kind;
	}
	
	public String toString()
	{
		if (m_label != null && m_label.length() > 0)
			return toString(getKind().toString() + "/" + m_label);
		else
			return toString(getKind().toString());
	}
}
