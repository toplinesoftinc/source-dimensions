package com.sourcedimensions.client.model;

public enum TypeCategory 
{
	CLASS(1<<0),
	INTERFACE(1<<1),
	ENUM(1<<2),
	ANONYMCLASS(1<<3, "ANONYM.CLASS"),
	ANNOTATION(1<<4),
	STRUCT(1<<5),
	DELEGATE(1<<6),
	ALL(1<<7);
	
	TypeCategory(int val)
	{
		value = val;
		name = name();
	}
	
	TypeCategory(int val, String n)
	{
		value = val;
		name = n;
	}
	
	private final int value;
	private final String name;
	
	public int value()
	{
		return value;
	}		
	
	public String toString()
	{
		return name;
	}
}
