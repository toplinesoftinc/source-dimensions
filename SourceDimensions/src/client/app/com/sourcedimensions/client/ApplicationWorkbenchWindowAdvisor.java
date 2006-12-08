package com.sourcedimensions.client;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import com.sourcedimensions.client.actions.OpenProjectAction;
import com.sourcedimensions.client.forms.LoginDialog;


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
    	if (LoginDialog.getSessionID() != null)
    	{
			OpenProjectAction.openProject(getWindowConfigurer().getWindow());
    	}
    }
}
