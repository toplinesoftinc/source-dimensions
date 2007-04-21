package com.sourcedimensions.client.model;

public class Snapshot 
{
	public int m_id;
	
	protected String m_name;
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public enum Type
	{
		ROOT(0),
		NAMESPACE(1),
		CLASS(2),
		ANONYMOUS_CLASS(3),
		INTERFACE(4),
		DELEGATE(5),
		ENUM(6),
		STRUCT(7),
		ANNOTATION(8),
		ANONYMOUS_METHOD(9),
		CONSTANT(10),
		CONSTRUCTOR(11),
		DESTRUCTOR(12),
		ENUM_CONST(13),
		EVENT(14),
		EVENT_ADD(15), 
		EVENT_REMOVE(16),
		FIELD(17),
		INDEXER(18),
		INDEXER_GET(19),
		INDEXER_SET(20),
		METHOD(21),
		OPERATOR(22),
		PROPERTY(23),
		PROPERTY_GET(24),
		PROPERTY_SET(25);
		
		Type(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
		
		public String toString()
		{
			return name().replace('_', ' ');
		}
	}
}
