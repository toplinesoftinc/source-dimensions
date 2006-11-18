package com.sourcedimensions.server.ast;

public class TypeDeclarationMember extends AbstractMember 
{
	protected Declaration m_declaration;
	
	public Declaration getDeclaration()
	{
		return m_declaration;
	}
	
	public void setDeclaration(Declaration declaration)
	{
		m_declaration = declaration;
		addChild(declaration);
	}
}
