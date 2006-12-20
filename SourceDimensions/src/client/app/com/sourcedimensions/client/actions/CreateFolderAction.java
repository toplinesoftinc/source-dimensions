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
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.SnapshotGroup;
import com.sourcedimensions.client.views.ProjectView.TreeGroup;


public class CreateFolderAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
{
	protected IStructuredSelection m_selection;
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}

	public void run(IAction action)
	{
		String name = null;
		Shell shell = m_window.getShell();
		
		while (true)
		{
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), shell, 
				"Folder", "&Folder name:", name, false);
			dialog.open();
			
			name = dialog.getValue();
			
			if (name == null)
				return;
			else
			{
				Integer id;
				Folder folder;
				
				if (m_selection.getFirstElement() instanceof SnapshotGroup)
					id = null;
				else
					id = ((FolderObject)m_selection.getFirstElement()).getID();
				
				try
				{
					folder = DbAdapter.addFolder(name, id, ProjectView.getProject().m_id);
				}
				catch (DupFolderNameException e)
				{
					MessageDialog.openError(shell, "Error", "Duplicate folder name");
					continue;
				}
				
				TreeGroup selected = (TreeGroup)m_selection.getFirstElement();
				selected.addChild(new FolderObject(folder.m_name, folder.m_id));
				
				ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
					
				if (view != null)
				{
					view.getViewer().refresh(selected);
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
