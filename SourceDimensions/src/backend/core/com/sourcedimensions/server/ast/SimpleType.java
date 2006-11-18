package com.sourcedimensions.server.ast;

public class SimpleType extends Type 
{
	public SimpleType() { }
	
	public SimpleType(SimpleTypeKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;

	public SimpleTypeKind getKind()
	{
		return SimpleTypeKind.values()[m_kind];
	}
	
	public enum SimpleTypeKind
	{
		VOID(0),
		BOOL(1),
		BYTE(2),
		SHORT(3),
		INT(4),
		LONG(5),
		UBYTE(6),
		USHORT(7),
		UINT(8),
		ULONG(9),
		FLOAT(10),
		DOUBLE(11),
		DECIMAL(12),
		CHAR(13),
		STRING(14),
		OBJECT(15);
		
		SimpleTypeKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public void setKind(int kind) 
	{
		m_kind = kind;
	}
	
	public String toString()
	{
		return toString(getKind().toString());
	}
}
