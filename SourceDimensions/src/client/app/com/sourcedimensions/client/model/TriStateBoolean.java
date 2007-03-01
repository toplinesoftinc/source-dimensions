package com.sourcedimensions.client.model;

public enum TriStateBoolean 
{
	FALSE(0),
	TRUE(1),
	EITHER(2);
	
	TriStateBoolean(int val)
	{
		value = val;
	}
	
	private final int value;
	
	public int value()
	{
		return value;
	}				
}
