package com.sourcedimensions.server.ast;

public class SwitchLabel extends AstNode 
{
	public SwitchLabel() { }
	
	public SwitchLabel(SwitchLabelKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	protected Expression m_labelExpr;
	
	public SwitchLabelKind getKind()
	{
		return SwitchLabelKind.values()[m_kind];
	}
	
	public enum SwitchLabelKind
	{
		CASE(0),
		DEFAULT(1);
		
		SwitchLabelKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getLabelExpr()
	{
		return m_labelExpr;
	}
	
	public void setLabelExpr(Expression expr)
	{
		m_labelExpr = expr;
		addChild(expr);
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
