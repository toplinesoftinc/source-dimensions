package com.sourcedimensions.client;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import com.sourcedimensions.client.views.*;

public class Perspective implements IPerspectiveFactory 
{
	public void createInitialLayout(IPageLayout layout) 
	{
		layout.addPlaceholder(ProjectView.ID, IPageLayout.LEFT, 0.5f, layout.getEditorArea());
	}
}
