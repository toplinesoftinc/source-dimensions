package com.sourcedimensions.server.ast;

public class CheckedStatement extends EmbeddedStatement 
{
	public CheckedStatement() { }
	
	public CheckedStatement(CheckedStmtKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	protected BlockStatement m_checkedBlock;
	
	public CheckedStmtKind getKind()
	{
		return CheckedStmtKind.values()[m_kind];
	}
	
	public enum CheckedStmtKind
	{
		CHECKED(0),
		UNCHECKED(1);
		
		CheckedStmtKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public BlockStatement getCheckedBlock()
	{
		return m_checkedBlock;
	}
	
	public void setCheckedBlock(BlockStatement block)
	{
		m_checkedBlock = block;
		addChild(block);
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
