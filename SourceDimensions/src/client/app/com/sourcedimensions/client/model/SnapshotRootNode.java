package com.sourcedimensions.client.model;

import java.util.Set;

public class SnapshotRootNode extends SnapshotNode 
{
	protected Set<SnapshotNode> m_internalRoots;
		
	public SnapshotRootNode()
	{
		super();
	}
	
	public void setInternalRoots(Set<SnapshotNode> roots)
	{
		m_internalRoots = roots;
	}
	
	public Set<SnapshotNode> getInternalRoots()
	{
		return m_internalRoots;
	}
}
