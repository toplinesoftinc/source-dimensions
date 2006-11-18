package com.sourcedimensions.server.sys;

import java.util.*;
import com.sourcedimensions.server.exceptions.*;

public class Folder extends FileSystemElement
{
	public Map<String,FileSystemElement> m_children = new HashMap<String,FileSystemElement>();
	
	protected Folder() { }
	
	protected Folder(Project prj)
	{
		m_project = prj;
	}
	
	public Folder addFolder(String name) throws DuplicateFileNameException
	{
		Folder folder = new Folder();
		
		if (m_children.get(name) == null)
			addFileSystemElement(name, folder);
		else
			throw new DuplicateFileNameException(name);
		
		return folder;
	}
	
	public SourceFile addFile(String name) throws DuplicateFileNameException
	{
		SourceFile file = new SourceFile(m_project);	

		if (m_children.get(name) == null)
			addFileSystemElement(name, file);
		else
			throw new DuplicateFileNameException(name);

		return file;
	}
	
	public FileSystemElement removeFileSystemElement(String name)
	{
		FileSystemElement fse = m_children.get(name);
		
		if (fse != null)
		{
			m_children.remove(name);
			fse.m_parentFolder = null;
		}
		
		return fse;
	}
	
	protected void addFileSystemElement(String name, FileSystemElement fse)
	{
		fse.m_name = name;
		fse.m_project = m_project;
		fse.m_parentFolder = this;
		m_children.put(name, fse);
	}
}
