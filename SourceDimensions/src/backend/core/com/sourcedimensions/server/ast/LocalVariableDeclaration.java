package com.sourcedimensions.server.ast;

import java.util.*;

public class LocalVariableDeclaration extends AstNode 
{
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 0);
	protected Type m_type;
	public Set<Declarator> m_declarators = new AstHashSet<Declarator>(this, 1);
	public boolean m_const = false;
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
}
