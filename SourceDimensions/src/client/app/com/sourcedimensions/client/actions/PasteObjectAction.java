package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.sourcedimensions.client.Clipboard;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.db.DuplicateNameException;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.QueryObject;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;
import com.sourcedimensions.client.views.ProjectView.TreeGroup;
import com.sourcedimensions.client.views.ProjectView.TreeObject;


public class PasteObjectAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
{
	protected IStructuredSelection m_selection;
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}

	public void run(IAction action)
	{
		if (Clipboard.getSource() == null)
		{
			action.setEnabled(false);
			return;
		}
		
		TreeObject source = (TreeObject)Clipboard.getSource();
		TreeGroup dest = (TreeGroup)m_selection.getFirstElement();
		
		if (source.isQueryGroup() != dest.isQueryGroup())
		{
			MessageDialog.openError(m_window.getShell(), "Paste error", 
				"Copy/cut and paste cannot be performed between query and snapshot groups");
			return;
		}
		
		String name = source.getName();
		int i = 0;
		
		if (Clipboard.isCut())
		{
			while (true)
			{
				try
				{
					if (source instanceof FolderObject)
					{
						DbAdapter.moveFolder(source.getID(), dest.getID(), name);
					}
					else if (source instanceof QueryObject)
					{
						DbAdapter.moveQuery(source.getID(), dest.getID(), name);
					}
					else if (source instanceof SnapshotObject)
					{
						DbAdapter.moveSnapshot(source.getID(), dest.getID(), name);
					}
					
					source.setName(name);
					break;
				}
				catch (DuplicateNameException e)
				{
					name = "Copy " + ((i == 0) ? "" : "(" + Integer.toString(i)+ ") ") + "of " + source.getName();
					i++;
				}
			}
			
			source.getParent().deleteChild(source);			
			dest.addDbChild(source);
			
			source.setFading(false);
			
			Clipboard.resetSource();
		}
		else
		{
			int id = 0;
			String projectId = ProjectView.getProject().getId();
			Integer parentId;
			
			if (dest instanceof FolderObject)
				parentId = dest.getID();
			else
				parentId = null;
			
			while (true)
			{
				try
				{
					if (source instanceof FolderObject)
					{
						id = DbAdapter.addFolder(name, parentId, projectId, source.isQueryGroup()).m_id;
					}
					else if (source instanceof QueryObject)
					{
						SymbolQuery query = DbAdapter.getSymbolQuery(source.getID());
						id = DbAdapter.addSymbolQuery(projectId, parentId, query);
					}
					else if (source instanceof SnapshotObject)
					{
						id = DbAdapter.addSnapshot(projectId, parentId, DbAdapter.getSnapshot(source.getID()));
					}
					
					source.setID(id);
					source.setName(name);
				
					dest.addDbChild(source);
					
					break;
				}
				catch (DuplicateNameException e)
				{
					name = "Copy " + ((i == 0) ? "" : "(" + Integer.toString(i)+ ") ") + "of " + source.getName();
					i++;
				}
				
				if (source instanceof TreeGroup)
				{
					copyTreeGroup(projectId, (TreeGroup)source, dest);
				}
			}			
		}
		
		ProjectView view = (ProjectView)m_window.getActivePage().findView(ProjectView.ID);
		
		if (view != null)
		{
			TreeViewer viewer = view.getViewer();
			
			viewer.setExpandedState(dest, true);
			viewer.refresh();
		}
	}	

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{	
		action.setEnabled(Clipboard.getSource() != null);		
		m_window = targetPart.getSite().getWorkbenchWindow();
	}		
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (Clipboard.getSource() == null)
			action.setEnabled(false);

		if (selection instanceof IStructuredSelection)
			m_selection = (IStructuredSelection)selection;
	}

	public void dispose()
	{
	}
	
	protected void copyTreeGroup(String projectId, TreeGroup source, TreeGroup dest)
	{
		for (TreeObject o : source.getChildren())
		{
			TreeObject newObj = addNewObject(projectId, source.getID(), o.getName(), source, dest);
			
			if (o instanceof TreeGroup)
			{
				copyTreeGroup(projectId, (TreeGroup)o, (TreeGroup)newObj);
			}
		}
	}
	
	protected TreeObject addNewObject(String projectId, Integer parentId, String name, TreeObject source, TreeGroup dest)
	{
		int id = 0;
		TreeObject newObj = null;
		
		try
		{
			if (source instanceof FolderObject)
			{
				id = DbAdapter.addFolder(name, parentId, projectId, source.isQueryGroup()).m_id;
				newObj = new FolderObject(name, id, source.isQueryGroup());
			}
			else if (source instanceof QueryObject)
			{
				SymbolQuery query = DbAdapter.getSymbolQuery(source.getID());
				id = DbAdapter.addSymbolQuery(projectId, parentId, query);
				newObj = new QueryObject(name, id, parentId);
			}
			else if (source instanceof SnapshotObject)
			{
				SnapshotNode node = DbAdapter.getSnapshot(source.getID());
				id = DbAdapter.addSnapshot(projectId, parentId, node);
				newObj = new SnapshotObject(name, id, parentId);
			}
		}
		catch (DuplicateNameException e)
		{			
		}
		
		dest.addDbChild(newObj);
		
		return newObj;
	}
}