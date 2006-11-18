package com.sourcedimensions.server.ast;

import java.util.*;

public class DelegateDeclaration extends Declaration 
{
	protected Type m_type;
	public String m_name;	
	public List<TypeParameter> m_typeParams = new AstArrayList<TypeParameter>(this, 0);
	public Set<Constraint> m_constraints = new AstHashSet<Constraint>(this, 1);
	public List<Parameter> m_parameters = new AstArrayList<Parameter>(this, 2);	
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 3);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 4);
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
	}
	
	public String toString()
	{
		return toString(m_name);
	}
}
