package com.sourcedimensions.server.sys.profile;

public class Limit 
{
	public String m_id;
	
	protected int m_type = Type.NAMESPACE.value;
	protected long m_value;

	
	public Type getType()
	{
		return Type.values()[m_type];
	}
	
	public void setType(Type type)
	{
		m_type = type.value;
	}
	
	public long getValue()
	{
		return m_value;
	}
	
	public void setValue(long value)
	{
		m_value = value;
	}
	
	public enum Type
	{
		NAMESPACE(0),
		TYPEDECL(1),
		MEMBER(2);
		
		Type(int val) 
		{
			value = val;
		}

		private final int value;

		public int value()
		{
			return value;
		}
	}
}
