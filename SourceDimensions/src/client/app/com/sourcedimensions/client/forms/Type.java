package com.sourcedimensions.client.forms;


public class Type 
{
	public enum TypeProperty
	{
		ARRAY(1),
		POINTER(2),
		NULLABLE(2<<1),
		TYPE_PARAM(2<<2);
		
		TypeProperty(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}				
	}
	
	
	public TypeProperty m_props;
	public String m_name;
}
