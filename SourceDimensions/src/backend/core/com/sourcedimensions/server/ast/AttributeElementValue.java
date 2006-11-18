package com.sourcedimensions.server.ast;

public class AttributeElementValue extends ElementValue
{
	protected AttributeBlock m_block;
	
	public AttributeBlock getBlock()
	{
		return m_block;
	}
	
	public void setBlock(AttributeBlock block)
	{
		m_block = block;
		addChild(block);
	}
}
