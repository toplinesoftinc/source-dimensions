package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import com.sourcedimensions.client.views.SnapshotView;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;


public class OpenSnapshotAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
 	protected IStructuredSelection m_selection;	
 	protected IWorkbenchWindow m_window;

 	
 	public void init(IWorkbenchWindow window) 
 	{
 		m_window = window;
 	}

 	public void run(IAction action) 
 	{
 		openSnapshot(m_window, (SnapshotObject)m_selection.getFirstElement());
 	}
 	
 	public static void openSnapshot(IWorkbenchWindow window, SnapshotObject node)
 	{
 		try
 		{
 			window.getActivePage().openEditor(new SnapshotView.Input(node), SnapshotView.ID);
 		}
 		catch (PartInitException e)
 		{
 			MessageDialog.openError(window.getShell(), "UI error", e.getMessage());
 		}
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
