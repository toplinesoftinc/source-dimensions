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
import com.sourcedimensions.client.model.Snapshot;
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
		if (Clipboard.getSource().size() == 0)
		{
			action.setEnabled(false);
			return;
		}
		
		boolean success = true;
		
		for (Object o : Clipboard.getSource())
		{
			success = pasteObject(m_window, (TreeObject)o, (TreeGroup)m_selection.getFirstElement());
			
			if (!success)
				break;
		}
		
		
		if (Clipboard.isCut() && success)
			Clipboard.resetSource();
	}	

	public static boolean pasteObject(IWorkbenchWindow window, TreeObject source, TreeGroup dest)
	{
		TreeObject parent = source.getParent();
		
		if (source.isQueryGroup() != dest.isQueryGroup())
		{
			MessageDialog.openError(window.getShell(), "Paste error", 
				"Copy/cut and paste cannot be performed between query and snapshot groups");
			
			return false;
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
				}
				catch (DuplicateNameException e)
				{
					name = "Copy " + ((i == 0) ? "" : "(" + Integer.toString(i)+ ") ") + "of " + source.getName();
					i++;
					
					continue;
				}
				catch (Exception e)
				{
					return false;
				}
				
				break;
			}
			
			source.setName(name);
			source.getParent().deleteChild(source);			
			dest.addDbChild(source);
			source.setFading(false);
		}
		else
		{
			String projectId = ProjectView.getProject().getId();
			
			while (true)
			{
				TreeObject newObj;
				
				try
				{
					newObj = addNewObject(projectId, name, source, dest);
				}
				catch (DuplicateNameException e)
				{
					name = "Copy " + ((i == 0) ? "" : "(" + Integer.toString(i)+ ") ") + "of " + source.getName();
					i++;
					
					continue;
				}
				catch (Exception e)
				{
					return false;
				}
	
				if (source instanceof TreeGroup)
				{
					copyTreeGroup(projectId, (TreeGroup)source, (TreeGroup)newObj);
				}
				
				break;
			}			
		}
		
		ProjectView view = (ProjectView)window.getActivePage().findView(ProjectView.ID);
		
		if (view != null)
		{
			TreeViewer viewer = view.getViewer();
					
			viewer.setExpandedState(dest, true);
			
			if (Clipboard.isCut())
				viewer.refresh(parent);
			
			viewer.refresh(dest);
		}
		
		return true;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{	
		action.setEnabled(Clipboard.getSource().size() != 0);		
		m_window = targetPart.getSite().getWorkbenchWindow();
	}		
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		if (Clipboard.getSource().size() == 0)
			action.setEnabled(false);

		if (selection instanceof IStructuredSelection)
			m_selection = (IStructuredSelection)selection;
	}

	public void dispose()
	{
	}
	
	protected static void copyTreeGroup(String projectId, TreeGroup source, TreeGroup dest)
	{
		for (TreeObject o : source.getChildren())
		{
			TreeObject newObj;
			
			try
			{
				newObj = addNewObject(projectId, o.getName(), o, dest);
			}
			catch (Exception e)
			{
				return;
			}
			
			if (o instanceof TreeGroup)
			{
				copyTreeGroup(projectId, (TreeGroup)o, (TreeGroup)newObj);
			}
		}
	}
	
	protected static TreeObject addNewObject(String projectId, String name, TreeObject source, TreeGroup dest) throws DuplicateNameException, Exception
	{
		int id = 0;
		TreeObject newObj = null;
		Integer parentId = dest.getID();
		
		if (source instanceof FolderObject)
		{
			id = DbAdapter.addFolder(name, parentId, projectId, source.isQueryGroup()).m_id;
			newObj = new FolderObject(name, id, source.isQueryGroup());
		}
		else if (source instanceof QueryObject)
		{
			SymbolQuery query = DbAdapter.getSymbolQuery(source.getID());
			
			query.setName(name);
			
			if (dest instanceof FolderObject)
				query.setFullName(DbAdapter.getFolderPath(dest.getID()) + name);
			else
				query.setFullName(name);
			
			id = DbAdapter.addSymbolQuery(projectId, parentId, query);
			newObj = new QueryObject(name, id);
		}
		else if (source instanceof SnapshotObject)
		{
			id = DbAdapter.copySnapshot(source.getID(), parentId, id, name);
			newObj = new SnapshotObject(name, id);
		}
		
		dest.addDbChild(newObj);
		
		return newObj;
	}
}