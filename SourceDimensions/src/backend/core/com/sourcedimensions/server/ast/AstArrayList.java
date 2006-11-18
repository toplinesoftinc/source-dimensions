package com.sourcedimensions.server.ast;

import java.util.*;

public class AstArrayList<E extends AstNode> extends ArrayList<E> 
{
	private static final long serialVersionUID = 7526472295622776147L;
	protected AstNode m_parent;
	protected Integer m_role;
	
	public AstArrayList(AstNode parent, int role)
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
	
	public void add(int index, E element)
	{
		m_parent.addChild(element);
		element.setRole(m_role);
		super.add(index, element);
	}
	
	public boolean addAll(Collection<? extends E> c)
	{
		List<AstNode> del = new ArrayList<AstNode>();
		
		for (AstNode o : c)
		{
			if (o != null)
			{
				m_parent.addChild(o);
				o.setRole(m_role);
			}
			else
				del.add(o);
		}
		
		for (AstNode o : del)
		{
			c.remove(o);
		}
		
		return super.addAll(c);
	}
	
	public boolean addAll(int index, Collection<? extends E> c)
	{		
		List<AstNode> del = new ArrayList<AstNode>();
		
		for (AstNode o : c)
		{
			if (o != null)
			{
				m_parent.addChild(o);
				o.setRole(m_role);
			}
			else
				del.add(o);
		}		

		for (AstNode o : del)
		{
			c.remove(o);
		}
				
		return super.addAll(index, c);
	}

	public E remove(int index)
	{
		int k = 0;
		for (Iterator<AstNode> i = m_parent.getChildIterator(); i.hasNext(); k++)
		{
			AstNode a = i.next();
			if (k == index)
			{
				m_parent.removeChild(a);
				break;
			}
		}
		
		return super.remove(index);
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
				AstNode a = (AstNode)o;
				m_parent.removeChild(a);
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
			{
				m_parent.removeChild(a);
			}
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
