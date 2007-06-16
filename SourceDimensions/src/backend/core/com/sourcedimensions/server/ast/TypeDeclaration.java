package com.sourcedimensions.server.ast;

import java.util.*;

public class TypeDeclaration extends Declaration 
{
	public TypeDeclaration() { }
	
	public TypeDeclaration(TypeDeclKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;	
	
	public String m_name;
	public Set<Type> m_baseTypes = new AstHashSet<Type>(this, 0);
	public Set<UserDefinedType> m_baseInterfaces = new AstHashSet<UserDefinedType>(this, 1);	
	public List<TypeParameter> m_typeParams = new AstArrayList<TypeParameter>(this, 2);
	public Set<Constraint> m_constraints = new AstHashSet<Constraint>(this, 3);
	public Set<AbstractMember> m_members = new AstHashSet<AbstractMember>(this, 4);
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 5);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 6);
	public Set<Directive> m_directives = new AstHashSet<Directive>(this, 7);
	
	public TypeDeclKind getKind()
	{
		return TypeDeclKind.values()[m_kind];
	}
	
	public enum TypeDeclKind
	{
		CLASS(0),
		INTERFACE(1),
		STRUCT(2),
		ENUM(3),
		NAMESPACE(4),
		ANNOT_TYPE(5);
		
		TypeDeclKind(int val) 
		{
			value = val;
		}

		private final int value;

		public int value()
		{
			return value;
		}
	}
	
	public void setKind(int kind) 
	{
		m_kind = kind;
	}
	
	public String toString()
	{
		if (m_name != null && m_name.length() > 0)
			return toString(getKind().toString() + "/" + m_name);
		else
			return toString(getKind().toString());
	}
}
