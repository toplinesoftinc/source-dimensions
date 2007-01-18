package com.sourcedimensions.client.forms;

public enum Modifier 
{
	
	NEW(1),
	PUBLIC(2),
	PROTECTED(2<<1),
	PRIVATE(2<<2),
	ABSTRACT(2<<3),
	STATIC(2<<4),
	FINAL(2<<5),
	SYNCHRONIZED(2<<6),
	NATIVE(2<<7),
	STRICTFP(2<<8),
	TRANSIENT(2<<9),
	VOLATILE(2<<10),
	INTERNAL(2<<11),
	VIRTUAL(2<<12),
	SEALED(2<<13),
	OVERRIDE(2<<14),
	EXTERN(2<<15),
	READONLY(2<<16),
	ALL(2<<17);
	
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
