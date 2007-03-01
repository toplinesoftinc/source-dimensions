package com.sourcedimensions.client.model;

import java.util.HashSet;
import java.util.Set;
import com.sourcedimensions.client.model.TriStateMask;

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
	
	public String positionToString()
	{
		switch (m_posType)
		{
			case LIST:
				{
					String str = "";
					Object[] items = m_posList.toArray();
					
					for (int i = 0; i < items.length; i++)
					{
						if (i > 0)
							str += ",";
						
						str += (Integer)items[i];
					}
					
					return str;
				}
				
			case BETWEEN:
				return m_posMin + " - " + m_posMax;
				
			case LESS_EQ:
				return "<= " + m_posMax;
				
			case GREATER_EQ:
				return ">= " + m_posMin;
				
			case EXACT:
				return Integer.toString(m_posValue);
				
			case ANY:
				return "<ANY>";
				
			default:
				return "";
		}
	}
	
	public String modifiersToString()
	{
		String str = "";
		
		for (Parameter.Modifier f : Parameter.Modifier.values())
		{
			switch (m_modifiers.getMask(f.value()))
			{
				case TRUE:
					if (str.length() > 0)
						str += ",";
					
					str += f.name().toLowerCase().replace("_", ".");
					break;
					 
				case EITHER:
					if (str.length() > 0)
						str += ",";
					
					str += "(" + f.name().toLowerCase().replace("_", ".") + ")";
			}
		}
		
		return str;
	}
	

	public TriStateMask m_modifiers = new TriStateMask();
	public Type m_type = new Type();
	public String m_name;
	public Position m_posType;
	public int m_posValue, m_posMin, m_posMax;
	public Set<Integer> m_posList = new HashSet<Integer>();
}
