package com.sourcedimensions.client.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Type 
{
	public enum Property
	{
		ARRAY(1<<0),
		TYPEPARAM(1<<1, "TYPE PARAM"),		
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
	
	protected TriStateMask m_typeProps = new TriStateMask();
	protected String m_name;
	
	public TriStateMask getTypeProps()
	{
		return m_typeProps;
	}
	
	public void setTypeProps(TriStateMask typeProps)
	{
		m_typeProps = typeProps;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
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
		if (typeName.trim().length() == 0)
		{
			throw new EmptyNameSectionException();
		}
		
		Pattern.compile(typeName);
	}
	
	public static class EmptyNameSectionException extends Exception 
	{
		public static final long serialVersionUID = -1; 
	}
}
