package com.sourcedimensions.server.ast;

import java.util.*;

public class Accessor extends AstNode 
{
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 0);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 1);
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
