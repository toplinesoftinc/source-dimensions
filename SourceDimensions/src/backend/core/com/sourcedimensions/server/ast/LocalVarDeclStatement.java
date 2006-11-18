package com.sourcedimensions.server.ast;

public class LocalVarDeclStatement extends AbstractStatement 
{
	protected LocalVariableDeclaration m_declaration;
	
	public LocalVariableDeclaration getDeclaration()
	{
		return m_declaration;
	}
	
	public void setDeclaration(LocalVariableDeclaration declaration)
	{
		m_declaration = declaration;
		addChild(declaration);
	}
}
