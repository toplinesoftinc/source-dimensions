package com.sourcedimensions.client.forms;

public enum Modifier 
{
	
	NEW(1<<0),
	PUBLIC(1<<1),
	PROTECTED(1<<2),
	PRIVATE(1<<3),
	ABSTRACT(1<<4),
	STATIC(1<<5),
	FINAL(1<<6),
	SYNCHRONIZED(1<<7),
	NATIVE(1<<8),
	STRICTFP(1<<9),
	TRANSIENT(1<<10),
	VOLATILE(1<<11),
	INTERNAL(1<<12),
	VIRTUAL(1<<13),
	SEALED(1<<14),
	OVERRIDE(1<<15),
	EXTERN(1<<16),
	READONLY(1<<17),
	UNSAFE(1<<18),
	ALL(1<<19);
	
	Modifier(int val)
	{
		value = val;
	}
	
	private final int value;
	
	public int value()
	{
		return value;
	}		
}
