package com.sourcedimensions.client.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;


public class SnapshotView extends ViewPart 
{
	public final static String ID = "com.sourcedimensions.client.views.SnapshotView";	
	private TreeViewer m_viewer;

	public void createPartControl(Composite parent) 
	{
		m_viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		m_viewer.setInput(getViewSite());
	}

	public void setFocus() 
	{
		m_viewer.getControl().setFocus();
	}
}