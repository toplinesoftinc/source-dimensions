package com.sourcedimensions.client;

public class Clipboard 
{
	protected static Object m_source = null;
	protected static boolean m_cut;
	
	public static void setSource(Object source, boolean cut)
	{
		m_source = source;
		m_cut = cut;
	}
	
	public static Object getSource()
	{
		return m_source;
	}
	
	public static boolean isCut()
	{
		return m_cut;
	}
	
	public static void resetSource()
	{
		m_source = null;
	}
}
