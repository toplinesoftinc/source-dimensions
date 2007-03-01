package com.sourcedimensions.client.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.forms.ProjectListDialog;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.ws.consumer.WSConsumer;


public class OpenProjectAction implements IWorkbenchWindowActionDelegate
{
	private IWorkbenchWindow m_window;
	
	public void init(IWorkbenchWindow window)
	{
		m_window = window;
	}
	
	public void run(IAction action)
	{
		openProject(m_window);
	}
	
	public void selectionChanged(IAction action, ISelection selection)
	{		
	}
	
	public void dispose()
	{	
	}
	
	public static void openProject(IWorkbenchWindow window)
	{
		WSConsumer consumer = new WSConsumer();
		Set<Project> prjSet = null;
		Collection<Project> prjColl;
		boolean offline = false;
		Shell shell = window.getShell();
		Display display = PlatformUI.getWorkbench().getDisplay();
		
		try
		{
			prjSet = (Set<Project>)consumer.invokeWebService(display, shell, "getProjectList", new Object[] { });			
		}
		catch (Exception ex)
		{
			MessageDialog.openError(shell, "Web Service Error", ex.getMessage());
			offline = true;
		}
		
		if (consumer.wasCancelled())
		{
			offline = true;
		}
		
		if (!offline)
		{
			Map<String,Project> prjHash = new HashMap<String,Project>();
			
			for (Project prj : prjSet)
			{				
				prjHash.put(prj.getId(), prj);
			}
			
			List<Project> prjList = DbAdapter.getProjectList();
			
			for (Project prj : prjList)
			{
				if (prjHash.get(prj.getId()) == null)
				{
					prj.setDeleted(true);
					prjHash.put(prj.getId(), prj);
				}
			}
			
			for (Project prj : prjHash.values())
			{
				if (!prj.getDeleted())
					DbAdapter.saveProject(prj);
			}
			
			prjColl = prjHash.values();
		}
		else
			prjColl = DbAdapter.getProjectList();
		
		ProjectListDialog prjWindow = new ProjectListDialog(display, shell);
		prjWindow.loadProjects(prjColl);
		prjWindow.open();
		Project prj = prjWindow.getSelected();

		if (prj != null)
		{
			try
			{
				ProjectView view = (ProjectView)window.getActivePage().showView(ProjectView.ID);
				view.setProject(prj);
				CloseProjectAction.enableAction(true);
			}
			catch (PartInitException e)
			{
				MessageDialog.openError(shell, "UI error", e.getMessage());
			}
		}
	}
}
