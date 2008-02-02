package com.sourcedimensions.server.sys;

import com.sourcedimensions.server.ast.*;


public class SourceFile extends FileSystemElement 
{
	protected CompilationUnit m_unit;
	protected String m_fullName;
	protected String m_encoding;	
	protected boolean m_binary;	
	
	
	SourceFile() { }
	
	protected SourceFile(Project prj)
	{
		m_project = prj;
	}
	
	public CompilationUnit getCompilationUnit()
	{
		return m_unit;
	}
	
	public void setCompilationUnit(CompilationUnit unit)
	{
		if (unit != null)
			unit.setSourceFile(this);
		
		m_unit = unit;
	}
	
	public String getFullName()
	{
		return m_fullName;
	}
	
	public void setFullName(String fullName)
	{
		m_fullName = fullName;
	}
	
	public String getEncoding()
	{
		return m_encoding;
	}
	
	public void setEncoding(String encoding)
	{
		m_encoding = encoding; 
	}
	
	public boolean getBinary()
	{
		return m_binary;
	}
	
	public void setBinary(boolean binary)
	{
		m_binary = binary;
	}
	
	public void delete()
	{
		super.delete();
		
		if (m_unit != null)
			m_unit.setSourceFile(null);
	}
}
