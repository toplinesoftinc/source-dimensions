package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IViewReference;
import com.sourcedimensions.client.views.ProjectView;


public class CloseProjectAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow m_window;
	
	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}
	
	public void run(IAction action)
	{
		if (ProjectView.getProject() != null)
		{
			if (MessageDialog.openQuestion(m_window.getShell(), "Close confirmation", 
				"Do you want to close current project?"))
			{
				try
				{
					ProjectView view = (ProjectView)m_window.getActivePage().showView(ProjectView.ID);
					view.setProject(null);
				}
				catch (PartInitException e)
				{
					MessageDialog.openError(m_window.getShell(), "UI error", e.getMessage());
				}
	
				for (IViewReference viewRef : m_window.getActivePage().getViewReferences())
				{
					m_window.getActivePage().hideView(viewRef);
				}
			}
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection)
	{		
	}
	
	public void dispose()
	{	
	}
}
