package com.sourcedimensions.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class SystemProps
{
	protected static final String m_fileName = "system.properties";
	protected static final String m_propImportThreadNum = "import.thread.num";
	
	protected static int m_importThreadNum = 5;
	
	static
	{
		Properties props = new Properties();
		
		try
		{
			props.load(new FileInputStream("." + File.separator + m_fileName));
			
			String val = props.getProperty(m_propImportThreadNum);
			
			if (val != null)
				m_importThreadNum = Integer.parseInt(val);
		}
		catch (Exception e)
		{
			System.out.println("Error during system properties reading: " + e.getMessage());
		}
	}
	
	public static int getImportThreadNum()
	{
		return m_importThreadNum;
	}
}
