package com.sourcedimensions.server.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sourcedimensions.client.model.SnapshotNode;


public class NamedSnapshotNode extends SnapshotNode 
{
	protected Map<String, NamedSnapshotNode> m_namedContainer = new HashMap<String, NamedSnapshotNode>();
	protected NamedSnapshotNode m_parent;
	protected String m_name;
	
	public NamedSnapshotNode()
	{
		super();
	}
	
	public NamedSnapshotNode(Type type, String label)
	{
		super(type, label);
	}

	public void addChild(NamedSnapshotNode child)
	{
		addChild(child.getName(), child);
	}
	
	public void addChild(String name, NamedSnapshotNode child)
	{
		NamedSnapshotNode node = m_namedContainer.get(name);
		
		if (node == null)
		{
			m_namedContainer.put(name, child);
			
			if (m_children == null)
			{
				m_children = new ArrayList<SnapshotNode>();
			}
			
			m_children.add(child);
			child.m_parent = this;
		}
		else
		{
			List<Reference> refs = child.getRefs();
			
			if (refs != null)
			{
				if (m_refs == null)
					m_refs = new ArrayList<Reference>();
				
				m_refs.addAll(refs);
			}
		}
		
		child.setName(name);
	}

	public NamedSnapshotNode clone()
	{
		NamedSnapshotNode clone = new NamedSnapshotNode(m_type, m_label);
		
		clone.setName(m_name);
		
		if (m_refs != null)
		{
			clone.m_refs = new ArrayList<Reference>();
			clone.m_refs.addAll(m_refs);
		}
		
		return clone;
	}
	
	public NamedSnapshotNode getChild(String name)
	{
		return m_namedContainer.get(name);
	}
	
	public NamedSnapshotNode getParent()
	{
		return m_parent;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
}
