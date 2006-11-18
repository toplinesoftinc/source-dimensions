package com.sourcedimensions.server.ast;

public class CatchBlock extends AstNode 
{
	protected Parameter m_parameter;
	protected BlockStatement m_catchBlock;
	
	public Parameter getParameter()
	{
		return m_parameter;
	}
	
	public void setParameter(Parameter parameter)
	{
		m_parameter = parameter;
		addChild(parameter);
	}
	
	public BlockStatement getCatchBlock()
	{
		return m_catchBlock;
	}
	
	public void setCatchBlock(BlockStatement block)
	{
		m_catchBlock = block;
		addChild(block);
	}
}
