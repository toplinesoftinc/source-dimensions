package com.sourcedimensions.server.ast;

public class UnsafeStatement extends EmbeddedStatement 
{
	protected BlockStatement m_unsafeBlock;
	
	public BlockStatement getUnsafeBlock()
	{
		return m_unsafeBlock;
	}
	
	public void setUnsafeBlock(BlockStatement block)
	{
		m_unsafeBlock = block;
		addChild(block);
	}
}
