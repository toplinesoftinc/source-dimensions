package com.sourcedimensions.client.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.forms.Login;
import com.sourcedimensions.client.forms.ProjectList;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.ws.consumer.WSConsumer;
import com.sourcedimensions.ws.provider.IProject;
import java.util.*;


public class ActionManager
{
	public static Project getProject(Display display, Shell shell)
	{
		WSConsumer consumer = new WSConsumer();
		Set<IProject> prjSet = null;
		Collection<Project> prjColl;
		boolean offline = false;
		
		try
		{
			prjSet = (Set<IProject>)consumer.invokeWebService(display, shell, 
				"getProjectList", new Object[] { Login.getSessionID() });			
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
			
			for (IProject prj : prjSet)
			{
				Project p = new Project();
				
				p.m_id = prj.getID();
				p.m_name = prj.getName();
				p.m_language = prj.getLanguage();
				p.m_readOnly = prj.getReadOnly();
				
				for (IProject parent : prj.getParents())
				{
					Project proj = new Project();
					
					proj.m_id = parent.getID();
					proj.m_name = parent.getName();
					proj.m_language = parent.getLanguage();
					proj.m_readOnly = parent.getReadOnly();
					
					p.m_parents.add(proj);
				}
				
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
			
			prjColl = prjHash.values();
		}
		else
			prjColl = DbAdapter.getProjectList();
		
		ProjectList prjWindow = new ProjectList(display, shell);
		prjWindow.loadProjects(prjColl);
		prjWindow.open();

		return prjWindow.getSelected();
	}
}
