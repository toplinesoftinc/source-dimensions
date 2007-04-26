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
import com.sourcedimensions.client.views.ProjectView;

public class ShowProjectViewAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
 	protected IStructuredSelection m_selection;	
 	protected IWorkbenchWindow m_window;

 	
 	public void init(IWorkbenchWindow window) 
 	{
 		m_window = window;
 	}

 	public void run(IAction action) 
 	{
 		try
 		{
 			m_window.getActivePage().showView(ProjectView.ID);
 		}
 		catch (PartInitException e)
 		{
			MessageDialog.openError(m_window.getShell(), "UI error", e.getMessage()); 			
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
