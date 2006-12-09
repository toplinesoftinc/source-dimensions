package com.sourcedimensions.client;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor 
{
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) 
    {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) 
    {
    	exitAction = ActionFactory.QUIT.create(window);
    	register(exitAction);
    	aboutAction = ActionFactory.ABOUT.create(window);
    	register(aboutAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) 
    {
    	MenuManager prjMenu = new MenuManager("&Project", "project");
    	prjMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_project"));
    	prjMenu.add(new Separator());    	
    	prjMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_folder"));
    	prjMenu.add(new Separator());
    	prjMenu.add(exitAction);
    	
    	MenuManager helpMenu = new MenuManager("&Help", "help");
    	helpMenu.add(aboutAction);
    	
    	menuBar.add(prjMenu);
    	menuBar.add(helpMenu);
    }
}
