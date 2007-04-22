package com.sourcedimensions.client.model;

public class Snapshot 
{
	public int m_id;
	
	protected String m_name;
	protected SnapshotNode m_root;
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}	
	
	public void setRoot(SnapshotNode root)
	{
		m_root = root;
	}
	
	public SnapshotNode getRoot()
	{
		return m_root;
	}
}
