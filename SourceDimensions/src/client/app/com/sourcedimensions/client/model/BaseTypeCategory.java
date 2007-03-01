package com.sourcedimensions.client.model;

public enum BaseTypeCategory 
{
	CLASS(0),
	INTERFACE(1),
	CLASS_INTERFACE(2),
	INTEGRAL_TYPE(3);
	
	BaseTypeCategory(int val)
	{
		value = val;
	}
	
	private final int value;
	
	public int value()
	{
		return value;
	}
}
