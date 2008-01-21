package com.sourcedimensions.client.model;

public class Snapshot 
{
	public int m_id;
	
	protected String m_name;
	protected String m_fullName;
	protected SnapshotRootNode m_root;
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}	
	
	public String getFullName()
	{
		return m_fullName;
	}
	
	public void setFullName(String fullName	)
	{
		m_fullName = fullName;
	}
	
	public void setRoot(SnapshotRootNode root)
	{
		m_root = root;
	}
	
	public SnapshotRootNode getRoot()
	{
		return m_root;
	}
}
