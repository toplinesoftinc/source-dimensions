package com.sourcedimensions.client;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.sourcedimensions.client.forms.LoginDialog;


/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable 
{
	public static final String PLUGIN_ID = "com.sourcedimensions.client";	
	
	public Object run(Object args) throws Exception 
	{
		Display display = PlatformUI.createDisplay();
		
		try 
		{
			Platform.endSplash();
							
			new LoginDialog(display, null).open();
			
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			
			if (returnCode == PlatformUI.RETURN_RESTART) 
			{
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} 
		finally 
		{
			display.dispose();
		}
	}
}
