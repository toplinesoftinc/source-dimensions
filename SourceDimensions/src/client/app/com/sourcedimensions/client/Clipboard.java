package com.sourcedimensions.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Clipboard 
{
	protected static List m_source = new ArrayList();
	protected static boolean m_cut;
	
	public static void setSource(Collection source, boolean cut)
	{
		resetSource();
		
		for (Object o : source)
		{
			m_source.add(o);
		}

		m_cut = cut;
	}
	
	public static List getSource()
	{
		return m_source;
	}
	
	public static boolean isCut()
	{
		return m_cut;
	}
	
	public static void resetSource()
	{
		m_source.clear();
	}
}
