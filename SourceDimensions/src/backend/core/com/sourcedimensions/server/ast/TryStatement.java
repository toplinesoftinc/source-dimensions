package com.sourcedimensions.server.ast;

import java.util.*;

public class TryStatement extends EmbeddedStatement 
{
	protected BlockStatement m_tryBlock;
	public List<CatchBlock> m_catchSet = new AstArrayList<CatchBlock>(this, 0);
	protected BlockStatement m_generalCatch;
	protected BlockStatement m_finallyBlock;
	
	public BlockStatement getTryBlock()
	{
		return m_tryBlock;
	}
	
	public void setTryBlock(BlockStatement block)
	{
		m_tryBlock = block;
		addChild(block);
	}
	
	public BlockStatement getGeneralCatch()
	{
		return m_generalCatch; 
	}
	
	public void setGeneralCatch(BlockStatement block)
	{
		m_generalCatch = block;
		addChild(block);
	}
	
	public BlockStatement getFinallyBlock()
	{
		return m_finallyBlock;
	}
	
	public void setFinallyBlock(BlockStatement block)
	{
		m_finallyBlock = block;
		addChild(block);
	}
}
