package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;

public abstract class TypeMemberDialogBase extends DialogBase
{
	protected boolean m_anyParams;
	protected int m_modifiers;
	protected int m_memberCategories;
	protected Type m_type = new Type();
	protected String m_name;
	protected List<Parameter> m_paramList = new ArrayList<Parameter>();
	protected static final String[] m_triStateText = { "No", "Yes", "Optional" };
	
	public enum MemberCategory
	{
		FIELD(1<<0),
		CONSTANT(1<<1),
		CONSTRUCTOR(1<<2),
		DESTRUCTOR(1<<3),
		METHOD(1<<4),
		PROPERTY_GET(1<<5),
		PROPERTY_SET(1<<6),
		EVENT_ADD(1<<7),
		EVENT_REMOVE(1<<8),
		INDEXER_GET(1<<9),
		INDEXER_SET(1<<10),
		OPERATOR(1<<11),
		ENUM_CONST(1<<12),
		ALL(1<<13);
		
		MemberCategory(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}		
	}	
	
	public int getModifiers()
	{
		return m_modifiers;
	}
	
	public int getMemberCategories()
	{
		return m_memberCategories;
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public String getMemberName()
	{
		return m_name;
	}
	
	public List<Parameter> getParams()
	{
		return m_paramList;
	}
	
	public boolean getAnyParams()
	{
		return m_anyParams;
	}
}
