package com.sourcedimensions.client.forms;

import java.util.HashSet;
import java.util.Set;

public class Parameter 
{
	public enum Position
	{
		ANY(0),
		EXACT(1),
		LESS_EQ(2),
		GREATER_EQ(3),
		BETWEEN(4),
		LIST(5);
		
		Position(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}						
	}
	
	public enum Modifier 
	{
		REF(1<<0),
		OUT(1<<1),
		PARAMS(1<<2),
		FINAL(1<<3),
		VAR_ARITY(1<<4);
		
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

	public TriStateMask m_modifiers = new TriStateMask();
	public Type m_type = new Type();
	public String m_name;
	public Position m_posType;
	public int m_posValue, m_posMin, m_posMax;
	public Set<Integer> m_posList = new HashSet<Integer>();
}
