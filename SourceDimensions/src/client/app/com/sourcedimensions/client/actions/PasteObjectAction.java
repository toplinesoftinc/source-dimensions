package com.sourcedimensions.client.actions;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.sourcedimensions.client.Clipboard;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.QueryNode;
import com.sourcedimensions.client.model.SnapshotNode;
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
		TreeObject source = (TreeObject)Clipboard.getSource();
		TreeGroup dest = (TreeGroup)m_selection;
		
		if (source.isQueryGroup() != dest.isQueryGroup())
		{
			MessageDialog.openError(m_window.getShell(), "Paste error", 
				"Copy/cut and paste cannot be performed between query and snapshot groups");
			return;
		}
		
		Set<String> names = new HashSet<String>();
		String prjId = ProjectView.getProject().getId();
		Integer parentId;
		
		if (dest instanceof FolderObject)
			parentId = dest.getID(); 
		else
			parentId = null;
		
		if (source instanceof FolderObject)
		{
			List<Folder> folderList = DbAdapter.getFolderList(parentId, prjId, dest.isQueryGroup());
			
			for (Folder f : folderList)
				names.add(f.m_name);
		}
		else if (source instanceof QueryObject)
		{
			List<QueryNode> queryList = DbAdapter.getQueryList(prjId, parentId);
			
			for (QueryNode q : queryList)
				names.add(q.m_name);
		}
		else if (source instanceof SnapshotObject)
		{
			List<SnapshotNode> snapshotList = DbAdapter.getSnapshotList(prjId, parentId);
			
			for (SnapshotNode s : snapshotList)
				names.add(s.getName());
		}
		
		String copyName = source.getName();
		
		for (int i = 0; names.contains(copyName); i++)
		{
			copyName = "Copy " + ((i == 0) ? "" : "(" + Integer.toString(i)+ ") ") + "of " + source.getName();
		}
		
		//TODO
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{	
		m_window = targetPart.getSite().getWorkbenchWindow();
	}		
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (Clipboard.getSource() == null)
		{
			action.setEnabled(false);
		}

		if (selection instanceof IStructuredSelection)
			m_selection = (IStructuredSelection)selection;
	}

	public void dispose()
	{
	}
}