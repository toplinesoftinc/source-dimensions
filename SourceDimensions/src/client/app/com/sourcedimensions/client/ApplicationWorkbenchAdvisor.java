package com.sourcedimensions.client;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor 
{
	private static final String PERSPECTIVE_ID = "SourceDimensionsClient.perspective";
	public ApplicationWorkbenchWindowAdvisor windowAdvisor;

	public void initialize(IWorkbenchConfigurer configurer)
	{
		//configurer.setSaveAndRestore(true);
	}
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        windowAdvisor = new ApplicationWorkbenchWindowAdvisor(configurer);
        return windowAdvisor;
    }

	public String getInitialWindowPerspectiveId() 
	{
		return PERSPECTIVE_ID;
	}
	
	public boolean preShutdown()
	{
		return MessageDialog.openQuestion(windowAdvisor.getWindow().getShell(), 
			"Exit confirmation", "Do you want to exit application?");
	}
}
