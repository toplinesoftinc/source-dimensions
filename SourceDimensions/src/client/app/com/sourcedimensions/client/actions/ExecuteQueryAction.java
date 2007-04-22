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
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.ProjectView.TreeObject;
import com.sourcedimensions.ws.consumer.WSConsumer;


public class ExecuteQueryAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
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
		int id = selected.getID();
		
		SymbolQuery query;
		
		try
		{
			query = DbAdapter.getSymbolQuery(id);
		}
		catch (Exception e)
		{
			return;
		}
		executeQuery(shell, query);
 	}

 	public static void executeQuery(Shell shell, SymbolQuery query)
 	{
		WSConsumer consumer = new WSConsumer();
		Snapshot snapshot;
		String dest = query.getDestination();
		
		if (dest.trim().length() == 0 || Pattern.matches(".*" + Folder.DIVIDER_REGEX, dest))
		{
			MessageDialog.openError(shell, "Destination not specified", "The query does not have destination "+
				"snapshot name specified or path specified does not include snapshot name. Please enter " + 
				"destination name and execute this query again.");
			
			EditQueryAction.runQueryEdit(shell, query);
			
			return;
		}
		
		Object found;
		
		try
		{
			found = DbAdapter.findObject(ProjectView.getProject().getId(), dest, false);
		}
		catch (Exception e)
		{
			return;
		}
		
		if (found != null)
		{
			if (!MessageDialog.openQuestion(shell, "Overwrite confirmation", "There is a snapshot"  + 
				" with the same name which will be deleted and re-created with new contents. Do you want to continue?")) 
			{
				return;
			}
		}
		
		try
		{
			snapshot = (Snapshot)consumer.invokeWebService(shell, "runSymbolQuery", new Object[] { query });			
		}
		catch (Exception ex)
		{
			MessageDialog.openError(shell, "Web Service Error", ex.getMessage());
			return;
		}
		
		if (consumer.wasCancelled())
		{
			return;
		}
		
		if (snapshot == null)
		{
			MessageDialog.openInformation(shell, "Query Results", "No item found for the specified query");
			return;
		}
		else
		{
			String[] sections = dest.split(Folder.DIVIDER_REGEX);
			String name = sections[sections.length - 1];
			
			snapshot.setName(name);

			if (found != null)
			{
				try
				{
					DbAdapter.deleteSnapshot(((Snapshot)found).m_id);
				}
				catch (Exception e)
				{
					return;
				}
				
				ProjectView.getSnapshotGroup().deleteObject(sections);
			}
			
			ProjectView.getSnapshotGroup().addSnapshotNode(snapshot, dest);
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
