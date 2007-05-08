package com.sourcedimensions.client.model;

import java.util.List;

public class SnapshotNode 
{
	protected Type m_type;
	protected String m_label;
	protected List<SnapshotNode> m_children;
	protected List<Reference> m_refs;
	
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
		
	public List<SnapshotNode> getChildren()
	{
		return m_children;
	}
	
	public void setChildren(List<SnapshotNode> children)
	{
		m_children = children;
	}
	
	public List<Reference> getRefs()
	{
		return m_refs;
	}
	
	public void setRefs(List<Reference> refs)
	{
		m_refs = refs;
	}
	
	public static class Reference
	{
		protected String m_id;
		protected String m_fileId;
		protected int m_startPos;
		protected int m_endPos;

		public Reference()
		{
			
		}
		
		public Reference(String id, String fileId, int startPos, int endPos)
		{
			m_id = id;
			m_fileId = fileId;
			m_startPos = startPos;
			m_endPos = endPos;
		}
		
		public void setId(String id)
		{
			m_id = id;
		}
		
		public String getId()
		{
			return m_id;
		}
		
		public void setFileId(String fileId)
		{
			m_fileId = fileId;
		}
		
		public String getFileId()
		{
			return m_fileId;
		}
		
		public void setStartPos(int startPos)
		{
			m_startPos = startPos;
		}
		
		public int getStartPos()
		{
			return m_startPos;
		}
		
		public void setEndPos(int endPos)
		{
			m_endPos = endPos;
		}
		
		public int getEndPos()
		{
			return m_endPos;
		}
	}
	
	public enum Type
	{
		ROOT(0),
		NAMESPACE(1),
		CLASS(2),
		ANONYMCLASS(3),
		INTERFACE(4),
		DELEGATE(5),
		ENUM(6),
		STRUCT(7),
		ANNOT(8),
		ANONYMMETHOD(9),
		CONST(10),
		CONSTRUCTOR(11),
		DESTRUCTOR(12),
		ENUMCONST(13),
		EVENT(14),
		EVENTADD(15), 
		EVENTREMOVE(16),
		FIELD(17),
		INDEXER(18),
		INDEXERGET(19),
		INDEXERSET(20),
		METHOD(21),
		OPERATOR(22),
		PROPERTY(23),
		PROPERTYGET(24),
		PROPERTYSET(25),
		LOCAL(26),
		PARAM(27),
		BASECLASS(28),
		BASEINTERFACE(29),
		CLASSREF(30),
		INTERFACEREF(31),
		STRUCTREF(32),
		ENUMREF(33),
		DELEGATEREF(34);
		
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
