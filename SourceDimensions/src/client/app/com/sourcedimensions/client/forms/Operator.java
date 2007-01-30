package com.sourcedimensions.client.forms;

public enum Operator
{
	UNARY_PLUS(1<<0),
	UNARY_MINUS(1<<1),
	NOT(1<<2),
	COMPLEMENT(1<<3),
	INCREMENT(1<<4),
	DECREMENT(1<<5),
	TRUE(1<<6),
	FALSE(1<<7),
	PLUS(1<<8),
	MINUS(1<<9),
	MULT(1<<10),
	DIVISION(1<<11),
	REMINDER(1<<12),
	BITWISE_AND(1<<13),
	BITWISE_OR(1<<14),
	BITWISE_XOR(1<<15),
	LSHIFT(1<<16),
	RSHIFT(1<<17),
	EQ(1<<18),
	NOT_EQ(1<<19),
	GT(1<<20),
	LESS(1<<21),
	GT_EQ(1<<22),
	LESS_EQ(1<<23),
	IMPL_CONV(1<<24),
	EXPL_CONV(1<<25),
	ALL(1<<26);
	
	Operator(int val)
	{
		value = val;
	}
	
	private final int value;
	
	public int value()
	{
		return value;
	}
}
