package com.sourcedimensions.server.query;

import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.server.ast.TypeDeclaration;


public class SymbolQueryEngine 
{
	public SnapshotNode execute(String projectId, SymbolQuery query)
	{
		return execFromRoot(projectId, null, query);
	}

	public SnapshotNode execFromRoot(String projectId, TypeDeclaration root, SymbolQuery query)
	{
		return null;
	}
}
