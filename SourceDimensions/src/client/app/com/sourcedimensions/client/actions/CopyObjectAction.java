package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sourcedimensions.client.Clipboard;


public class CopyObjectAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
{
	protected IStructuredSelection m_selection;
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}

	public void run(IAction action)
	{
		Clipboard.setSource(m_selection.toList(), false);		
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{	
		m_window = targetPart.getSite().getWorkbenchWindow();
	}		
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (selection instanceof IStructuredSelection)
			m_selection = (IStructuredSelection)selection;
	}

	public void dispose()
	{
	}
}