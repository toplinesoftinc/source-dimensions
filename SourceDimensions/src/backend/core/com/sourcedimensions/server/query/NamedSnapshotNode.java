package com.sourcedimensions.server.query;

import com.sourcedimensions.client.model.SnapshotNode;


public class NamedSnapshotNode extends SnapshotNode 
{
	public NamedSnapshotNode()
	{
		super();
	}
	
	public NamedSnapshotNode(String fileId, String originId, Type type, String label)
	{
		super(fileId, originId, type, label);
	}

}
