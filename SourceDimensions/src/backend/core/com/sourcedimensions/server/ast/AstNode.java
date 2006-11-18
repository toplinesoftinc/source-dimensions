package com.sourcedimensions.server.ast;

import com.sourcedimensions.server.sys.*;
import java.util.*;

import org.hibernate.Hibernate;


public abstract class AstNode 
{
	public String m_id;
	protected AstNode m_parent;
	protected Project m_project;
	protected SourceFile m_file;
	protected Integer m_role;
	
	public int m_left;
	public int m_right;
		
	protected Set<AstNode> m_children = new HashSet<AstNode>();
	
	public String getID()
	{
		return m_id;
	}
	
	void setRole(int role)
	{
		m_role = role;
	}
	
	public AstNode getParent()
	{
		return m_parent;
	}
	
	public void setProject(Project prj)
	{
		m_project = prj;
	}
	
	public Project getProject()
	{
		return m_project;
	}
	
	public void setSourceFile(SourceFile file)
	{
		m_file = file;
	}
	
	public SourceFile getSourceFile()
	{
		return m_file;
	}	
	
	void addChild(AstNode node)
	{
		if (node != null)
		{
			m_children.add(node);
			node.m_parent = this;
		}
	}
	
	boolean removeChild(AstNode node)
	{
		return m_children.remove(node);
	}
	
	void removeAllChildren()
	{
		m_children.clear();
	}
	
	public Iterator<AstNode> getChildIterator()
	{
		return m_children.iterator();
	}
	
	public int getChildCount()
	{
		return m_children.size();
	}
	
	public void setKind(int kind) 
	{
	}
	
	public String toString()
	{
		return Hibernate.getClass(this).getSimpleName();
	}
	
	protected String toString(String value)
	{
		String str = Hibernate.getClass(this).getSimpleName();
		
		if (value != null && value.length() > 0)
			str += " [" + value + "]";
		
		return str;
	}
}
