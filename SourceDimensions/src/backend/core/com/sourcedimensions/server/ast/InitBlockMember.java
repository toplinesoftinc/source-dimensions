package com.sourcedimensions.server.ast;

public class InitBlockMember extends AbstractMember 
{
	public boolean m_staticBlock;
	protected BlockStatement m_block;
	
	public BlockStatement getBlock()
	{
		return m_block;
	}
	
	public void setBlock(BlockStatement block)
	{
		m_block = block;
		addChild(block);
	}
}
