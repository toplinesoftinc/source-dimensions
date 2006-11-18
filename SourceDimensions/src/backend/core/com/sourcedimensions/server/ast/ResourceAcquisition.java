package com.sourcedimensions.server.ast;


public class ResourceAcquisition extends AstNode
{
	public ResourceAcquisition() { }
	
	public ResourceAcquisition(ResAcquisitionKind kind)
	{
		m_kind = kind.value;
	}

    protected int m_kind;	
	protected Expression m_expression;
	protected LocalVariableDeclaration m_declaration;

	public ResAcquisitionKind getKind()
	{
		return ResAcquisitionKind.values()[m_kind];
	}
	
	public enum ResAcquisitionKind
	{
		VAR_DECL(0),
		EXPR(1);
		
		ResAcquisitionKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Expression getExpression()
	{
		return m_expression;
	}
	
	public void setExpression(Expression expr)
	{
		m_expression = expr;
		addChild(expr);
	}
	
	public LocalVariableDeclaration getDeclaration()
	{
		return m_declaration;
	}
	
	public void setDeclaration(LocalVariableDeclaration declaration)
	{
		m_declaration = declaration;
		addChild(declaration);
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
