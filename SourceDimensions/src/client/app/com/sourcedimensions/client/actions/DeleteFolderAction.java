package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.TreeGroup;

public class DeleteFolderAction implements IWorkbenchWindowActionDelegate
{
	protected IStructuredSelection m_selection;	
	protected IWorkbenchWindow m_window;
	
	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}

	public void run(IAction action)
	{
		Shell shell = m_window.getShell();
		
		if (MessageDialog.openQuestion(shell, "Deletion confirmation", 
			"Are you sure you want to delete selected folder with all sub-folders and snapshots?"))
		{
			FolderObject selected = (FolderObject)m_selection.getFirstElement();
			int id = selected.getID();			
			TreeGroup parent = selected.getParent();

			DbAdapter.deleteFolder(id);
			parent.deleteChild(selected);
			
			ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
			
			if (view != null)
			{
				view.getViewer().refresh(parent);
			}
		}
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
