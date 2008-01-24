package com.sourcedimensions.client.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.forms.ProjectBrowserDialog;
import com.sourcedimensions.client.forms.SymbolQueryDialog;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.SnapshotView.SnapshotNodeTreeItem;
import com.sourcedimensions.ws.consumer.WSConsumer;


public class SubqueryAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
 	protected IStructuredSelection m_selection;	
 	protected IWorkbenchWindow m_window;

 	public void init(IWorkbenchWindow window) 
 	{
 		m_window = window;
 	}

 	public void run(IAction action) 
 	{
 		List<SnapshotNodeTreeItem> selection = m_selection.toList();
 		Set<Integer> idSet = new HashSet<Integer>();
 		
 		for (Object o : selection)
 		{
 			SnapshotNodeTreeItem node = (SnapshotNodeTreeItem)o; 			
 			idSet.add(node.getID());
 		} 
 		
 		for (Object o : selection)
 		{
 			SnapshotNodeTreeItem node = (SnapshotNodeTreeItem)o;
 			
 			for (SnapshotNodeTreeItem cur = node.getParent(); cur != null; cur = cur.getParent())
 			{
 				if (idSet.contains(cur.getID()))
 				{
 					MessageDialog.openError(m_window.getShell(), "Incorrect selection", 
 							"Subquery selection must not contain any two nodes with direct or indirect parent-child relation in the tree. " + 
 							"Please change selection.");
 						return;
 				}
 			}
 		}
 		
		ProjectBrowserDialog browser = new ProjectBrowserDialog(m_window.getShell(), true);
			
		browser.open();
		
		if (!browser.isCancelled())
		{
			SymbolQuery query;
			
			try
			{
				query = DbAdapter.getSymbolQuery(browser.getResultID());
			}
			catch (Exception e)
			{
				return;
			}
			
			SymbolQueryDialog dialog = new SymbolQueryDialog(m_window.getShell(), query, selection);
			
			dialog.open();
		}
 	}
 	
 	public static boolean executeQuery(Shell shell, List<SnapshotNodeTreeItem> selection, SymbolQuery query)
 	{
 		Set<Integer> idSet = new HashSet<Integer>();
 		
 		for (Object o : selection)
 		{
 			SnapshotNodeTreeItem node = (SnapshotNodeTreeItem)o; 			
 			idSet.add(node.getID());
 		} 

 		for (Integer id : idSet)
 		{
 			boolean hasChildren = false;
 			
 			try
 			{
 				hasChildren = DbAdapter.hasSnapshotNodeChildren(id);
 			}
 			catch (Exception ex)
 			{
 				return false;
 			}
 			
 			if (hasChildren)
 			{
 				if (!MessageDialog.openQuestion(shell, "Confirmation", "Some of selected nodes have children " +
 						"which will all be removed before placing new nodes under respective parents. Do you want to continue?"))
 					return false;

 				break;
 			} 			
 		}
 		
 		Map<Integer, SnapshotNodeTreeItem> nodeMap = new HashMap<Integer, SnapshotNodeTreeItem>();
 		Set<SnapshotNode> rootSet = new HashSet<SnapshotNode>();
 		
 		for (Object o : selection)
 		{
 			SnapshotNodeTreeItem item = (SnapshotNodeTreeItem)o;
 			nodeMap.put(item.getID(), item);
  		}

		try
		{
			rootSet.addAll(DbAdapter.getSnapshotNodeList(idSet));			
		}
		catch (Exception e)
		{
			return false;
		}
 		
 		WSConsumer consumer = new WSConsumer();
 		String projectId = ProjectView.getProject().getId();
		Set<SnapshotNode> resultSet;

 		try
		{
			resultSet = (Set)consumer.invokeWebService(shell, 
				"runSymbolSubquery", new Object[] { projectId, rootSet, query });			
		}
		catch (Exception ex)
		{
			MessageDialog.openError(shell, "Web Service Error", ex.getMessage());
			return false;
		}
		
		if (consumer.wasCancelled())
			return true;

		for (SnapshotNode s : resultSet)
		{
			SnapshotNodeTreeItem n = nodeMap.get(s.getID());
			
			try
			{
				DbAdapter.deleteSnapshotNodeChildren(s.getID());
				DbAdapter.saveSnapshotNodeChildren(s);
			}
			catch (Exception e)
			{
				return false;
			}

			n.invalidate();
			
			TreeViewer viewer = n.getViewer();
			
			viewer.refresh(n);
			viewer.setExpandedState(n, false);
		} 	
				 	
 		return true;
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
