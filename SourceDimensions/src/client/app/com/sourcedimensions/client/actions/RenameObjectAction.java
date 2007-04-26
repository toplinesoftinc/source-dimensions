package com.sourcedimensions.client.actions;

import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.db.DuplicateNameException;
import com.sourcedimensions.client.forms.InputDialog;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.SnapshotView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.QueryObject;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;
import com.sourcedimensions.client.views.ProjectView.TreeObject;


public class RenameObjectAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
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
		TreeObject selected = (TreeObject)m_selection.getFirstElement();
		String name = selected.getName();
		
		String object = "";
		
		if (selected instanceof QueryObject)
		{
			object = "Query";
		}
		else if (selected instanceof SnapshotObject)
		{
			object = "Snapshot";
		}
		else if (selected instanceof FolderObject)
		{
			object = "Folder";
		}

		while (true)
		{
			InputDialog dialog = new InputDialog(shell,	object, "&" + object + " name:", 
				name, new NewNameValidator("Please enter " + object + " name"));
			
			dialog.open();
			
			name = dialog.getValue();
			
			if (name == null)
				return;
			else
			{
				Integer id = selected.getID();
								
				try
				{
					if (selected instanceof QueryObject)
					{
						DbAdapter.updateQuery(id, name);
					}
					else if (selected instanceof SnapshotObject)
					{
						DbAdapter.updateSnapshot(id, name);
					}
					else if (selected instanceof FolderObject)
					{
						DbAdapter.updateFolder(id, name);
					}					
				}
				catch (DuplicateNameException e)
				{
					MessageDialog.openError(shell, "Error", "Duplicate name");
					continue;
				}
				catch (Exception e)
				{
					return;
				}
				
				selected.setName(name);
				ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
					
				if (view != null)
					view.getViewer().refresh();
				
				SnapshotView.renameSnapshot(id, name);

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
	
	
	protected class NewNameValidator extends InputDialog.MandatoryFieldValidator
	{
		public NewNameValidator()
		{
			super();
		}
		
		public NewNameValidator(String message)
		{
			super(message);
		}
		
		public boolean validate(Shell shell, String value)
		{
			if (super.validate(shell, value))
			{
				if (Pattern.matches(".*" + Folder.DIVIDER_REGEX + ".*", value))
				{					
					MessageDialog.openError(shell, "Incorrect input", "Name cannot have " +
						"path dividing characters.");
					
					return false;
				}
				else
					return true;
			}
			else
				return false;
		}
	}
}
