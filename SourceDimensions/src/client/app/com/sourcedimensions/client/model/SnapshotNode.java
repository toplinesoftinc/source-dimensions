package com.sourcedimensions.client.model;

import java.util.List;

public class SnapshotNode 
{
	protected Type m_type;
	protected String m_label;
	protected List<SnapshotNode> m_children;
	protected List<Reference> m_refs;	
	protected Integer m_id;
	
	public SnapshotNode()
	{
		m_type = Type.ROOT;
	}
	
	public SnapshotNode(Type type, String label)
	{
		m_type = type;
		m_label = label;
	}
		
	public Integer getID()
	{
		return m_id;
	}
	
	public void setID(Integer id)
	{
		m_id = id;
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
	
	public Level level()
	{
		switch (m_type)
		{
			case GLOBALNAMESPACE:
				return Level.GLOBAL;
				
			case NAMESPACE:
				return Level.NAMESPACE;
				
			case CLASS:
			case ANONYMCLASS:
			case INTERFACE:
			case DELEGATE:
			case ENUM:
			case STRUCT:
			case ANNOT:
				return Level.TYPEDECL;
				
			case ANONYMMETHOD:
			case CONST:
			case CONSTRUCTOR:
			case DESTRUCTOR:
			case ENUMCONST:
			case EVENT:
			case EVENTADD: 
			case EVENTREMOVE:
			case FIELD:
			case INDEXER:
			case INDEXERGET:
			case INDEXERSET:
			case METHOD:
			case OPERATOR:
			case PROPERTY:
			case PROPERTYGET:
			case PROPERTYSET:
			case FIXEDSIZEBUFFER:
			case INITBLOCK:
				return Level.MEMBER;
				
			case LOCAL:
			case PARAM:
				return Level.LOCALDECL;
				
			default:
				return Level.NONE; 
		}		
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
	
	public enum Level
	{
		NONE(0),
		GLOBAL(1),
		NAMESPACE(2),
		TYPEDECL(3),
		MEMBER(4),
		LOCALDECL(5);
		
		Level(int val)
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
	
	public enum Type
	{
		ROOT(0),
		GLOBALNAMESPACE(1),
		NAMESPACE(2),
		CLASS(3),
		ANONYMCLASS(4),
		INTERFACE(5),
		DELEGATE(6),
		ENUM(7),
		STRUCT(8),
		ANNOT(9),
		ANONYMMETHOD(10),
		CONST(11),
		CONSTRUCTOR(12),
		DESTRUCTOR(13),
		ENUMCONST(14),
		EVENT(15),
		EVENTADD(16), 
		EVENTREMOVE(17),
		FIELD(18),
		INDEXER(19),
		INDEXERGET(20),
		INDEXERSET(21),
		METHOD(22),
		OPERATOR(23),
		PROPERTY(24),
		PROPERTYGET(25),
		PROPERTYSET(26),
		FIXEDSIZEBUFFER(27),
		INITBLOCK(28),
		LOCAL(29),
		PARAM(30),
		BASECLASS(31),
		BASEINTERFACE(32),
		CLASSREF(33),
		INTERFACEREF(34),
		STRUCTREF(35),
		ENUMREF(36),
		DELEGATEREF(37);
	
		
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
