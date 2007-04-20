package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.sourcedimensions.client.forms.SymbolQueryDialog;
import com.sourcedimensions.client.views.ProjectView.FolderObject;
import com.sourcedimensions.client.views.ProjectView.TreeObject;

public class CreateSymbolQueryAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate
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
 
 		SymbolQueryDialog dialog = new SymbolQueryDialog(shell);
 		
 		Object sel = m_selection.getFirstElement();
 		
 		if (sel instanceof FolderObject)
 		{
 			FolderObject folder = (FolderObject)sel;
 			String name = "";
 			
 			for (TreeObject cur = folder; cur instanceof FolderObject; cur = cur.getParent())
 			{
 				name = cur.getName() + "/" + name;
 			}
 			
 			dialog.setQueryName(name);
 		}
 		
 		dialog.open();
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
