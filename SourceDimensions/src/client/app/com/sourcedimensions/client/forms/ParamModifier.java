package com.sourcedimensions.client.forms;

public enum ParamModifier 
{
	REF(1<<0),
	OUT(1<<1),
	PARAMS(1<<2),
	FINAL(1<<3),
	VAR_ARITY(1<<4);
	
	ParamModifier(int val)
	{
		value = val;
	}
	
	private final int value;
	
	public int value()
	{
		return value;
	}		
}
