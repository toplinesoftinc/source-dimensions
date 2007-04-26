package com.sourcedimensions.client;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.sourcedimensions.client.actions.CloseProjectAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor 
{
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction closePrjAction;

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
    	closePrjAction = new CloseProjectAction(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) 
    {
    	MenuManager prjMenu = new MenuManager("&Project", "project");
    	prjMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_project"));
    	prjMenu.add(closePrjAction);
    	prjMenu.add(new Separator());    	
    	prjMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_folder"));
    	prjMenu.add(new Separator());
    	prjMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_object"));
    	prjMenu.add(new Separator());
    	prjMenu.add(exitAction);

    	MenuManager queryMenu = new MenuManager("&Query", "query");
    	queryMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_query"));
    	queryMenu.add(new Separator());    	
    	queryMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_query2"));    	
    	
    	MenuManager snapshotMenu = new MenuManager("&Snapshot", "snapshot");
    	snapshotMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_snapshot"));
    	
    	MenuManager winMenu = new MenuManager("&Window", "window");
    	winMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_window"));
    	
    	MenuManager helpMenu = new MenuManager("&Help", "help");
    	helpMenu.add(aboutAction);
    	
    	menuBar.add(prjMenu);
    	menuBar.add(queryMenu);
    	menuBar.add(snapshotMenu);
    	menuBar.add(winMenu);
    	menuBar.add(helpMenu);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar)
    {
    	IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
    	coolBar.add(toolbar);
    	coolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_project"));    	
    	coolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_folder"));    	
    	coolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_query"));
    	coolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_snapshot"));
    	coolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "_object"));    	
    }
}
