package com.sourcedimensions.client;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import com.sourcedimensions.client.actions.ActionManager;
import com.sourcedimensions.client.forms.Login;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.client.views.ProjectView;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor 
{

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) 
    {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) 
    {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public IWorkbenchWindow getWindow()
    {
    	return getWindowConfigurer().getWindow();    	
    }
    
    public void preWindowOpen() 
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowMenuBar(true);
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(true);
    }
    
    public void postWindowOpen()
    {
    	if (Login.getSessionID() != null)
    	{
			Project prj = ActionManager.getProject(Display.getDefault(), 
				getWindowConfigurer().getWindow().getShell());
			
			if (prj != null)
			{
				try
				{
					ProjectView view = (ProjectView)getWindow().getActivePage().showView(ProjectView.ID);
					view.setProject(prj);
				}
				catch (PartInitException e)
				{
					MessageDialog.openError(getWindow().getShell(), "UI error", e.getMessage());
				}
			}
    	}
    }
}
