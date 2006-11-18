package com.sourcedimensions.server.sys;

public abstract class FileSystemElement
{
	protected String m_id;
	Project m_project;
	Folder m_parentFolder;
	public String m_name;
	
	public String getID()
	{
		return m_id;
	}
	
	public Project getProject()
	{
		return m_project;
	}
	
	public Folder getParent()
	{
		return m_parentFolder;
	}
	
	public void delete()
	{
		if (m_parentFolder != null)
			m_parentFolder.removeFileSystemElement(m_name);
	}	
}
