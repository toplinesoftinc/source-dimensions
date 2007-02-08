package com.sourcedimensions.client.forms;


public class Type 
{
	public enum Property
	{
		ARRAY(1<<0),
		TYPE_PARAM(1<<1),		
		POINTER(1<<2),
		NULLABLE(1<<3);

		Property(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}				
	}
	
	public TriStateMask m_typeProps = new TriStateMask();
	public String m_name;
	
	public static String typePropsToString(TriStateMask mask)
	{
		String str = "";
		
		for (Property p : Property.values())
		{
			switch (mask.getMask(p.value()))
			{
				case TRUE:
					if (str.length() > 0)
						str += ",";
					
					str += p.name().replace("_", " ");
					break;
					 
				case EITHER:
					if (str.length() > 0)
						str += ",";
					
					str += "(" + p.name().replace("_", " ") + ")";			
			}
		}
		
		return str;
	}
}
