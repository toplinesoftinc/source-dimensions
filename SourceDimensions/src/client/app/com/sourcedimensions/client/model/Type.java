package com.sourcedimensions.client.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Type 
{
	public enum Property
	{
		ARRAY(1<<0),
		TYPE_PARAM(1<<1, "TYPE PARAM"),		
		POINTER(1<<2),
		NULLABLE(1<<3);

		Property(int val)
		{
			value = val;
			name = name();
		}
		
		Property(int val, String n)
		{
			value = val;
			name = n;
		}
		
		private final int value;
		private final String name;
		
		public int value()
		{
			return value;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	public TriStateMask m_typeProps = new TriStateMask();
	public String m_name;
	
	public String typePropsToString()
	{
		String str = "";
		
		for (Property p : Property.values())
		{
			switch (m_typeProps.getMask(p.value()))
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
	
	public static void validateTypeName(String typeName) 
		throws PatternSyntaxException, EmptyNameSectionException
	{
		String[] names = typeName.split("/");
		
		for (String name : names)
		{
			if (name.trim().length() == 0)
			{
				throw new EmptyNameSectionException();
			}
			
			if (name.equals("**"))
			{
				continue;
			}

			Pattern.compile(name);
		}		
	}
	
	public static class EmptyNameSectionException extends Exception 
	{
		public static final long serialVersionUID = -1; 
	}
}
