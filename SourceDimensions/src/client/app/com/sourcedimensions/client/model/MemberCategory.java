package com.sourcedimensions.client.model;

public enum MemberCategory 
{
	FIELD(1<<0),
	CONSTANT(1<<1),
	CONSTRUCTOR(1<<2),
	DESTRUCTOR(1<<3),
	METHOD(1<<4),
	ANONYM_METHOD(1<<5, "ANONYM.METHOD"),
	PROPERTY_GET(1<<6, "PROPERTY GET"),
	PROPERTY_SET(1<<7, "PROPERTY SET"),
	EVENT_ADD(1<<8, "EVENT ADD"),
	EVENT_REMOVE(1<<9, "EVENT REMOVE"),
	INDEXER_GET(1<<10, "INDEXER GET"),
	INDEXER_SET(1<<11, "INDEXER SET"),
	OPERATOR(1<<12),
	ENUM_CONST(1<<13, "ENUM CONST."),
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
