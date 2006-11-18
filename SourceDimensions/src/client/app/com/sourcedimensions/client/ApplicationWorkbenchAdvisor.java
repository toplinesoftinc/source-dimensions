package com.sourcedimensions.client;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor 
{
	private static final String PERSPECTIVE_ID = "SourceDimensionsClient.perspective";

	public void initialize(IWorkbenchConfigurer configurer)
	{
		configurer.setSaveAndRestore(true);
	}
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() 
	{
		return PERSPECTIVE_ID;
	}
}
