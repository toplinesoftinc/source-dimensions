package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;

public abstract class TypeMemberDialogBase extends DialogBase
{
	protected Modifier m_modifiers;
	protected TypeMemberCategory m_categories;
	protected Type m_type;
	protected String m_name;
	protected List<Parameter> m_paramList = new ArrayList<Parameter>();
	
	public enum TypeMemberCategory
	{
		FIELD(1),
		CONSTANT(2),
		CONSTRUCTOR(2<<1),
		DESTRUCTOR(2<<2),
		METHOD(2<<3),
		PROPERTY_GET(2<<4),
		PROPERTY_SET(2<<5),
		EVENT_ADD(2<<6),
		EVENT_REMOVE(2<<7),
		INDEXER_GET(2<<8),
		INDEXER_SET(2<<9),
		OPERATOR(2<<10),
		ENUM_CONST(2<<11);
		
		TypeMemberCategory(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}		
	}	
	
	public Modifier getModifier()
	{
		return m_modifiers;
	}
	
	public TypeMemberCategory getCategory()
	{
		return m_categories;
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public List<Parameter> getParams()
	{
		return m_paramList;
	}
}
