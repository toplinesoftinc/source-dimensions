package com.sourcedimensions.client;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import com.sourcedimensions.client.actions.ActionManager;


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
    
    public void preWindowOpen() 
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
    }
    
    public void postWindowOpen()
    {
		ActionManager.loadProject(Display.getDefault(), 
			getWindowConfigurer().getWindow().getShell());
    }
}
