package com.sourcedimensions.client.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.SnapshotView;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.QueryGroup;
import com.sourcedimensions.client.views.ProjectView.QueryObject;
import com.sourcedimensions.client.views.ProjectView.SnapshotGroup;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;
import com.sourcedimensions.client.views.ProjectView.TreeGroup;
import com.sourcedimensions.client.views.ProjectView.TreeObject;


public class DeleteObjectAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
	protected IStructuredSelection m_selection;	
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window) 
	{
		m_window = window;
	}

	public void run(IAction action) 
	{
		if (MessageDialog.openQuestion(m_window.getShell(), "Deletion confirmation", 
			"Are you sure you want to delete selected object(s)?"))
		{
			for (Object o : m_selection.toList())
			{
				if (!deleteObject(m_window, (TreeObject)o))
					break;
			}
		}
	}
	
	public static boolean deleteObject(IWorkbenchWindow window, TreeObject selected)
	{
		Integer id = selected.getID();			
		TreeGroup parent = selected.getParent();
		
		try
		{
			if (selected instanceof QueryObject)
			{
				DbAdapter.deleteQuery(id);
			}
			else if (selected instanceof SnapshotObject)
			{
				DbAdapter.deleteSnapshot(id);
				SnapshotView.closeSnapshot(id);
			}
			else if (selected instanceof FolderObject)
			{
				if (!((FolderObject)selected).isQueryGroup())					
					closeSnapshots(id);
				
				DbAdapter.deleteFolder(id);				
			}
			else if (selected instanceof QueryGroup)
			{
				DbAdapter.deleteAll(ProjectView.getProject().getId(), true);
			}
			else if (selected instanceof SnapshotGroup)
			{
				closeSnapshots(null);
				DbAdapter.deleteAll(ProjectView.getProject().getId(), false);
			}
		}
		catch (Exception e)
		{
			return false;
		}
		
		if (id != null)
			parent.deleteChild(selected);
		else
			((TreeGroup)selected).deleteAllChildren();
		
		ProjectView view = (ProjectView)window.getActivePage().findView(ProjectView.ID);
		
		if (view != null)
		{
			TreeViewer viewer = view.getViewer();
			viewer.refresh(parent);
			
			if (id == null)
				viewer.refresh(selected);
		}
		
		return true;
	}
	
	protected static void closeSnapshots(Integer folderId)
	{
		String projectId = ProjectView.getProject().getId();
		
		try
		{
			List<Snapshot> snapshotList = DbAdapter.getSnapshotList(projectId, folderId);
			
			for (Snapshot s : snapshotList)
				SnapshotView.closeSnapshot(s.m_id);
			
			List<Folder> folderList = DbAdapter.getFolderList(folderId, projectId, false);
			
			for (Folder f : folderList)
				closeSnapshots(f.m_id);
		}
		catch(Exception e)
		{			
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
