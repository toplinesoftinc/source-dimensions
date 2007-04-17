package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.db.DuplicateNameException;
import com.sourcedimensions.client.forms.InputDialog;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.QueryGroup;
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
				"Folder", "&Folder name:", name, new InputDialog.MandatoryFieldValidator("Please enter folder name"));
			dialog.open();
			
			name = dialog.getValue();
			
			if (name == null)
				return;
			else
			{
				Integer id;
				Folder folder;
				Object element = m_selection.getFirstElement();
				boolean isQuery;
				
				if (element instanceof SnapshotGroup)
				{
					id = null;
					isQuery = false;
				}
				else if (element instanceof QueryGroup)
				{
					id = null;
					isQuery = true;
				}
				else
				{
					FolderObject f = (FolderObject)element;
					id = f.getID();
					isQuery = f.isQueryGroup();
				}
				
				try
				{
					folder = DbAdapter.addFolder(name, id, ProjectView.getProject().getId(), isQuery);
				}
				catch (DuplicateNameException e)
				{
					MessageDialog.openError(shell, "Error", "Duplicate folder name");
					continue;
				}
				
				TreeGroup selected = (TreeGroup)m_selection.getFirstElement();
				FolderObject folderObject = new FolderObject(folder.m_name, folder.m_id, isQuery);
				folderObject.initNew();
				selected.addChild(folderObject);
				
				ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
					
				if (view != null)
				{
					TreeViewer viewer = view.getViewer();
					
					viewer.refresh(selected);
					
					if (!viewer.getExpandedState(selected))
					{
						viewer.setExpandedState(selected, true);
					}
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
