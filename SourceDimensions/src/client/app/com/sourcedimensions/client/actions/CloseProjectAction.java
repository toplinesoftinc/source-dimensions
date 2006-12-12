package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import com.sourcedimensions.client.views.ProjectView;


public class CloseProjectAction extends Action implements IWorkbenchAction, IObjectActionDelegate
{
	private IWorkbenchWindow m_window;
	private static IWorkbenchAction m_instance;
	
	public final static String ID = "com.sourcedimensions.client.closeProject";

	public CloseProjectAction()
	{		
	}
	
	public CloseProjectAction(IWorkbenchWindow window)
	{
		m_window = window;
		m_instance = this;
		setId(ID);
		setText("&Close Project");
		enableAction(false);
	}

	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}
		
	public static void enableAction(boolean enabled)
	{
		if (m_instance != null)
			m_instance.setEnabled(enabled);
	}

	public void run(IAction action)
	{
		run();
	}	
	
	public void run()
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
					enableAction(false);
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
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{	
		m_window = targetPart.getSite().getWorkbenchWindow();
	}		
	
	public void selectionChanged(IAction action, ISelection selection)
	{		
	}	
	
	public void dispose()
	{	
	}
}
