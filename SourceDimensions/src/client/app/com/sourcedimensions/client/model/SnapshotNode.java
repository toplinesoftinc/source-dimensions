package com.sourcedimensions.client.model;

import java.util.List;

public class SnapshotNode 
{
	protected Type m_type;
	protected String m_label;
	protected List<SnapshotNode> m_children;
	protected String m_fileId;
	protected long m_startPos = -1L, m_endPos = -1L;
	
	public Integer m_id;

	public SnapshotNode()
	{
		m_type = Type.ROOT;
	}
	
	public SnapshotNode(Type type, String label)
	{
		m_type = type;
		m_label = label;
	}
	
	public Type getType()
	{
		return m_type;
	}
	
	public void setType(Type type)
	{
		m_type = type;
	}
	
	public String getLabel()
	{
		return m_label;
	}
	
	public void setLabel(String label)
	{
		m_label = label;
	}
	
	public String getFileId()
	{
		return m_fileId;
	}
	
	public void setFileId(String fileId)
	{
		m_fileId = fileId;
	}
	
	public long getStartPos()
	{
		return m_startPos;
	}

	public void setStartPos(long startPos)
	{
		m_startPos = startPos;
	}
	
	public long getEndPos()
	{
		return m_endPos;
	}
	
	public void setEndPos(long endPos)
	{
		m_endPos = endPos;
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
		ROOT(0),
		NAMESPACE_DECL(1),
		CLASS_DECL(2),
		ANONYMOUS_CLASS_DECL(3),
		INTERFACE_DECL(4),
		DELEGATE_DECL(5),
		ENUM_DECL(6),
		STRUCT_DECL(7),
		ANNOT_DECL(8),
		ANONYM_METHOD_DECL(9),
		CONST_DECL(10),
		CONSTRUCTOR_DECL(11),
		DESTRUCTOR_DECL(12),
		ENUM_CONST_DECL(13),
		EVENT_DECL(14),
		EVENT_ADD_DECL(15), 
		EVENT_REMOVE_DECL(16),
		FIELD_DECL(17),
		INDEXER_DECL(18),
		INDEXER_GET_DECL(19),
		INDEXER_SET_DECL(20),
		METHOD_DECL(21),
		OPERATOR_DECL(22),
		PROPERTY_DECL(23),
		PROPERTY_GET_DECL(24),
		PROPERTY_SET_DECL(25),
		LOCAL_DECL(26),
		PARAM_DECL(27),
		BASE_CLASS(28),
		BASE_INTERFACE(29),
		CLASS_REF(30),
		INTERFACE_REF(31),
		STRUCT_REF(32),
		ENUM_REF(33),
		DELEGATE_REF(34);
		
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
