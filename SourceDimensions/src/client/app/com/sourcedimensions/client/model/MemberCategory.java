package com.sourcedimensions.client.model;

public enum MemberCategory 
{
	FIELD(1<<0),
	CONSTANT(1<<1),
	CONSTRUCTOR(1<<2),
	DESTRUCTOR(1<<3),
	METHOD(1<<4),
	ANONYMMETHOD(1<<5, "ANONYM.METHOD"),
	PROPERTYGET(1<<6, "PROPERTY GET"),
	PROPERTYSET(1<<7, "PROPERTY SET"),
	EVENTADD(1<<8, "EVENT ADD"),
	EVENTREMOVE(1<<9, "EVENT REMOVE"),
	INDEXERGET(1<<10, "INDEXER GET"),
	INDEXERSET(1<<11, "INDEXER SET"),
	OPERATOR(1<<12),
	ENUMCONST(1<<13, "ENUM CONST."),
	ALL(1<<14);
	
	MemberCategory(int val)
	{
		value = val;
		name = name();
	}
	
	MemberCategory(int val, String n)
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
