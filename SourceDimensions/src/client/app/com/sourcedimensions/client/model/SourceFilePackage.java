package com.sourcedimensions.client.model;

import java.util.Map;


public class SourceFilePackage 
{
	protected Map<String,String> m_fileMap;
	protected Map<String,String> m_encodingMap;
	protected byte[] m_data;


	public void setFileMap(Map<String,String> fileMap)
	{
		m_fileMap = fileMap;
	}
	
	public Map<String,String> getFileMap()
	{
		return m_fileMap;
	}
	
	public void setEncodingMap(Map<String,String> encodingMap)
	{
		m_encodingMap = encodingMap;
	}
	
	public Map<String,String> getEncodingMap()
	{
		return m_encodingMap;
	}
	
	public void setData(byte[] data)
	{
		m_data = data;
	}
	
	public byte[] getData()
	{
		return m_data;
	}
}
