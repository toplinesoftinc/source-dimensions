package com.sourcedimensions.server.ast;

import java.util.*;

public class AnonymousMethodExpression extends Expression 
{
	public List<Parameter> m_parameters = new AstArrayList<Parameter>(this, 0);
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
