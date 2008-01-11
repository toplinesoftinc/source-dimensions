package com.sourcedimensions.server.utils;


import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SystemProps
{
	protected static final String m_fileName = "system.properties";
	
	protected static final String m_propImportThreadNum = "import.thread.num";
	protected static final String m_propQueryMemberPageSize = "query.member.batchsize";
	protected static final String m_propSessionLifetime = "session.lifetime";
	
	protected static final int DEFAULT_IMPORT_THREADNUM = 5;
	protected static final int DEFAULT_QUERY_MEMBER_BATCHSIZE = 100;
	protected static final long DEFAULT_SESSION_LIFETIME = 1800;

	protected static int m_importThreadNum = DEFAULT_IMPORT_THREADNUM;
	protected static int m_queryMemberBatchSize = DEFAULT_QUERY_MEMBER_BATCHSIZE;
	protected static long m_sessionLifetime = DEFAULT_SESSION_LIFETIME;
	
	protected static Logger log4j = Logger.getLogger(SystemProps.class.getPackage().getName());
	
	
	static
	{
		Properties props = new Properties();
		
		try
		{
			InputStream stream = SystemProps.class.getClassLoader().getResourceAsStream(m_fileName);
			
			if (stream == null)
			{
				log4j.error("Cannot find file " + m_fileName);
			}
			else
			{			
				props.load(stream);
				
				String val = props.getProperty(m_propImportThreadNum);
				
				if (val != null)
					m_importThreadNum = Integer.parseInt(val);
				
				val = props.getProperty(m_propQueryMemberPageSize);
	
				if (val != null)
					m_queryMemberBatchSize = Integer.parseInt(val);

				val = props.getProperty(m_propSessionLifetime);
				
				if (val != null)
					m_sessionLifetime = Integer.parseInt(val);				
				
				stream.close();
			}
		}
		catch (Exception e)
		{
			log4j.error("Error during system properties reading: " + e.getMessage());
		}
	}
	
	public static int getImportThreadNum()
	{
		return m_importThreadNum;
	}

	public static int getQueryMemberBatchSize()
	{
		return m_queryMemberBatchSize;
	}
	
	public static long getSessionLifetime()
	{
		return m_sessionLifetime;
	}
}
