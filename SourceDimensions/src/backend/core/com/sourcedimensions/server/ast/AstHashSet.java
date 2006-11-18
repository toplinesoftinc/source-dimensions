package com.sourcedimensions.server.ast;

import java.util.*;

public class AstHashSet<E extends AstNode> extends HashSet<E> 
{
	private static final long serialVersionUID = 7526472295622776147L;
	protected AstNode m_parent;
	protected Integer m_role;
	
	public AstHashSet(AstNode parent, int role)
	{
		m_parent = parent;
		m_role = role;
	}
	
	public boolean add(E o)
	{
		m_parent.addChild(o);
		o.setRole(m_role);
		return super.add(o);
	}
	
	public boolean addAll(Collection<? extends E> c)
	{
		for (AstNode o : c)
		{
			m_parent.addChild(o);
			o.setRole(m_role);
		}
		
		return super.addAll(c);
	}	
	
	public boolean remove(Object o)
	{
		if (o instanceof AstNode)
		{
			m_parent.removeChild((AstNode)o);
		}
		
		return super.remove(o);
	}
	
	public boolean removeAll(Collection<?> c)
	{
		for (Object o : c)
		{
			if (o instanceof AstNode)
			{
				m_parent.removeChild((AstNode)o);
			}
		}
		
		return super.removeAll(c);
	}
	
	public boolean retainAll(Collection<?> c)
	{
		for (Iterator<AstNode> i = m_parent.getChildIterator(); i.hasNext(); )
		{
			AstNode a = i.next();
			if (!c.contains(a))
				i.remove();
		}
		
		return super.retainAll(c);
	}
	
	public AstNode getParent()
	{
		return m_parent;
	}
	
	public int getRole()
	{
		return m_role;
	}
}