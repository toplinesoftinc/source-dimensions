package com.sourcedimensions.client.model;

import java.util.List;

public class SnapshotNode 
{
	protected String m_name;
	protected Type m_type;
	protected List<SnapshotNode> m_children;
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
	}
	
	public List<SnapshotNode> getChildren()
	{
		return m_children;
	}
	
	public void setChildren(List<SnapshotNode> children)
	{
		m_children = children;
	}
	
	public enum Type
	{
		NAMESPACE(0),
		CLASS(1),
		ANONYMOUS_CLASS(2),
		INTERFACE(3),
		DELEGATE(4),
		ENUM(5),
		STRUCT(6),
		ANNOTATION(7),
		ANONYMOUS_METHOD(8),
		CONSTANT(9),
		CONSTRUCTOR(10),
		DESTRUCTOR(11),
		ENUM_CONST(12),
		EVENT(13),
		EVENT_ADD(14),
		EVENT_REMOVE(15),
		FIELD(16),
		INDEXER(17),
		INDEXER_GET(18),
		INDEXER_SET(19),
		METHOD(20),
		OPERATOR(21),
		PROPERTY(22),
		PROPERTY_GET(23),
		PROPERTY_SET(24);
		
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
