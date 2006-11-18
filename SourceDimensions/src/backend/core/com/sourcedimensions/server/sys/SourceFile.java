package com.sourcedimensions.server.sys;

import com.sourcedimensions.server.ast.*;


public class SourceFile extends FileSystemElement 
{
	protected CompilationUnit m_unit;

	public boolean m_binary;	
	
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
	
	public void delete()
	{
		super.delete();
		
		if (m_unit != null)
			m_unit.setSourceFile(null);
	}
}
