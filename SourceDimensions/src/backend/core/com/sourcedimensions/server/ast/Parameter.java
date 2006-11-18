package com.sourcedimensions.server.ast;

import java.util.*;

public class Parameter extends AstNode 
{
	public Parameter() { }
	
	public Parameter(ParamKind kind)
	{
		m_kind = kind.value;
	}

    protected int m_kind;
        
	protected Type m_type;
	public String m_name;
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 0);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 1);
	public boolean m_varParam;
	public boolean m_finalParam;
	
	public ParamKind getKind()
	{
		return ParamKind.values()[m_kind];
	}
	
	public enum ParamKind
	{
		VALUE(0),
		REF(1),
		OUT(2),
		ARGLIST(3);
		
		ParamKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
		addChild(type);
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
