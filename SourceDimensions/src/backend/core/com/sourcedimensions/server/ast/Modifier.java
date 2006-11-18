package com.sourcedimensions.server.ast;


public class Modifier extends AstNode 
{
	public Modifier() { }
	
	public Modifier(ModifierKind kind)
	{
		m_kind = kind.value;
	}
		
	protected int m_kind;	
	
	public ModifierKind getKind()
	{
		return ModifierKind.values()[m_kind];
	}
	
	public enum ModifierKind
	{
		PUBLIC(0),
		PROTECTED(1),
		PRIVATE(2),
		STATIC(3),
		ABSTRACT(4),
		FINAL(5),
		NATIVE(6),
		SYNCHRONIZED(7),
		TRANSIENT(8),
		VOLATILE(9),
		STRICTFP(10),
		UNSAFE(11),
		EXTERN(12),
		INTERNAL(13),
		READONLY(14),
		VIRTUAL(15),
		OVERRIDE(16),
		NEW(17),
		PARTIAL(18);
		
		ModifierKind(int val)
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
