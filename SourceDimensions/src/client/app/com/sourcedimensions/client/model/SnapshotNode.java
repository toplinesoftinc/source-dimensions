package com.sourcedimensions.client.model;

import java.util.List;

public class SnapshotNode 
{
	protected String m_name;
	protected Type m_type;
	protected String m_originId;
	
	protected List<SnapshotNode> m_children;
	
	public SnapshotNode()
	{		
	}
	
	public SnapshotNode(String name)
	{
		setType(Type.ROOT);
		setName(name);
	}
	
	public SnapshotNode(Type type)
	{
		m_type = type;
	}
	
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
	
	public String getOriginID()
	{
		return m_originId;
	}
	
	public void setOriginID(String originId)
	{
		m_originId = originId;
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
