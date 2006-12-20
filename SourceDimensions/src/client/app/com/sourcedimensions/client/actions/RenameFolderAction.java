package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.db.DupFolderNameException;
import com.sourcedimensions.client.forms.InputDialog;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;


public class RenameFolderAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
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
		FolderObject selected = (FolderObject)m_selection.getFirstElement();
		String name = selected.getName();
		
		while (true)
		{
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), 
				shell, "Folder", "&Folder name:", name, false);
			dialog.open();
			
			name = dialog.getValue();
			
			if (name == null)
				return;
			else
			{
				Integer id = selected.getID();
								
				try
				{
					DbAdapter.updateFolder(name, id);
				}
				catch (DupFolderNameException e)
				{
					MessageDialog.openError(shell, "Error", "Duplicate folder name");
					continue;
				}
				
				selected.setName(name);
				ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
					
				if (view != null)
				{
					view.getViewer().update(selected, null);
				}

				return;
			}
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
