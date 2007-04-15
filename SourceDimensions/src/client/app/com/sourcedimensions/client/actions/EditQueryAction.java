package com.sourcedimensions.client.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.forms.SymbolQueryDialog;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.views.ProjectView.QueryObject;


public class EditQueryAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
	protected IStructuredSelection m_selection;	
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window) 
	{
		m_window = window;
	}

	public void run(IAction action) 
	{
		runQueryEdit(m_window.getShell(), (QueryObject)m_selection.getFirstElement());
	}
	
	public static void runQueryEdit(Shell shell, QueryObject object)
	{
		runQueryEdit(shell, DbAdapter.getSymbolQuery(object.getID()));
	}
	
	public static void runQueryEdit(Shell shell, SymbolQuery query)
	{
		SymbolQueryDialog dialog = new SymbolQueryDialog(PlatformUI.getWorkbench().getDisplay(), shell, query);		
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
