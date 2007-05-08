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
	
	public NamedSnapshotNode()
	{
		super();
	}
	
	public NamedSnapshotNode(Type type, String label)
	{
		super(type, label);
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
			
			getChildren().add(child);
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
	}
	
	public NamedSnapshotNode getChild(String name)
	{
		return m_namedContainer.get(name);
	}
	
	public NamedSnapshotNode getParent()
	{
		return m_parent;
	}
}
