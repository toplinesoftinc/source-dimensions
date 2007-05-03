package com.sourcedimensions.client.model;

public enum Operator
{
	UNARYPLUS(1<<0),
	UNARYMINUS(1<<1),
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
	BITWISEAND(1<<13),
	BITWISEOR(1<<14),
	BITWISEXOR(1<<15),
	LSHIFT(1<<16),
	RSHIFT(1<<17),
	EQ(1<<18),
	NOTEQ(1<<19),
	GT(1<<20),
	LESS(1<<21),
	GTEQ(1<<22),
	LESSEQ(1<<23),
	IMPLCONV(1<<24),
	EXPLCONV(1<<25),
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
