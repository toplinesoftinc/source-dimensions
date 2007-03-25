package com.sourcedimensions.client;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.sourcedimensions.client.db.DbAdapter;
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
			try
			{
				DbAdapter.tryConnection();
			}
			catch (Exception e)
			{
				Platform.endSplash();
				
				MessageDialog.openError(null, "Error", "\nAnother instance of the program has probably started on this computer. " +
					"The application cannot have more than one instance running on the same computer." +
					"\n\n(MESSAGE: " + e.getMessage() + ")");
				
				return IPlatformRunnable.EXIT_OK;
			}
		
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
