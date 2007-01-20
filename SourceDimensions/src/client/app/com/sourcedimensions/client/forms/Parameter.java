package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;

public class Parameter 
{
	public enum ParamPosition
	{
		ANY(0),
		EXACT(1),
		LESS_EQ(2),
		MORE_EQ(3),
		BETWEEN(4),
		LIST(5);
		
		ParamPosition(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}						
	}
	
	public Type m_type = new Type();
	public String m_name;
	public boolean m_varParam;
	public ParamPosition m_posType;		
	public List<Integer> m_posList = new ArrayList<Integer>();
}
