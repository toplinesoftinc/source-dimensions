package com.sourcedimensions.client.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.sourcedimensions.client.views.SnapshotView.SnapshotNodeTreeItem;


public class DeleteSnapshotNodeAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
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
			"Are you sure you want to delete selected snapshot node(s) and all its children?"))
		{
			Set<SnapshotView> updateView = new HashSet<SnapshotView>();
			
			for (Object o : m_selection.toList())
			{				
				SnapshotNodeTreeItem item = (SnapshotNodeTreeItem)o;			
				
				if (!deleteSnapshotNode(m_window, item, updateView))
					break;
			}
			
			if (updateView.size() > 0)
				updateView.iterator().next().refreshView();
		}
	}
	
	public static boolean deleteSnapshotNode(IWorkbenchWindow window, SnapshotNodeTreeItem selected, Set<SnapshotView> updateView)
	{
		Integer id = selected.getID();
		SnapshotNodeTreeItem parent = selected.getParent();
		
		try
		{
			DbAdapter.deleteSnapshotNode(id);
		}
		catch (Exception e)
		{
			return false;
		}
		
		TreeViewer viewer = selected.getViewer();
		
		if (parent == null)
		{
			updateView.add(selected.getSnapshotView());
		}
		else
		{
			parent.invalidate();
			viewer.refresh(parent);					
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
