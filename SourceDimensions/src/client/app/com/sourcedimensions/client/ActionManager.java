package com.sourcedimensions.client;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.sourcedimensions.client.Login;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.ws.consumer.WSConsumer;
import com.sourcedimensions.ws.provider.IProject;
import java.util.*;


public class ActionManager
{
	public static void loadProject(Display display, Shell shell)
	{
		WSConsumer consumer = new WSConsumer();
		Set<IProject> prjSet; 
		
		try
		{
			prjSet = (Set<IProject>)consumer.invokeWebService(display, shell, 
				"getProjectList", new Object[] { Login.getSessionID() });			
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
		
		Map<String,Project> prjHash = new HashMap<String,Project>();
		
		for (IProject prj : prjSet)
		{
			Project p = new Project();
			
			p.m_id = prj.getID();
			p.m_name = prj.getName();
			p.m_language = prj.getLanguage();
			p.m_readOnly = prj.getReadOnly();
			
			prjHash.put(prj.getID(), p);
		}
		
		List<Project> prjList = DbAdapter.getProjectList();
		
		for (Project prj : prjList)
		{
			if (prjHash.get(prj.m_id) == null)
			{
				prj.m_deleted = true;
				prjHash.put(prj.m_id, prj);
			}
		}
		
		for (Project prj : prjHash.values())
		{
			if (!prj.m_deleted)
				DbAdapter.saveProject(prj);
		}
		
		ProjectList prjWindow = new ProjectList(display, shell);
		prjWindow.loadProjects(prjHash.values());
		prjWindow.open();
		
		// TODO: loading project
	}
}
