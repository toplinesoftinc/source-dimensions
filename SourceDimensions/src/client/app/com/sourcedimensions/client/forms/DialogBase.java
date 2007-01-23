package com.sourcedimensions.client.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.sourcedimensions.client.Util;

public abstract class DialogBase 
{
	protected Display m_display;
	protected boolean m_cancel;
	protected static final String[] m_triStateText = { "No", "Yes", "Optional" };
	
	protected abstract Shell getShell();
	
	public void open()
	{
		Shell shell = getShell();
		shell.open();

		while (!shell.isDisposed()) 
		{
			if (!m_display.readAndDispatch()) 
				m_display.sleep();
		}		
	}
		
	protected void checkAllItems(Table table)
	{
		for (int i = 0; i < table.getItemCount(); i++)
		{
			table.getItem(i).setChecked(true);
		}
	}
	
	protected void postCreate(Shell parent)
	{	
		Shell shell = getShell();
		
		addKeyListener(shell, new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ESC)
					cancelClose();
			}
		});
		
		Util.centerWindow(shell, parent);
	}
	
	private void addKeyListener(Composite composite, KeyListener listener)
	{
		Control[] widgets = composite.getChildren();

		for (int i = 0; i < widgets.length; i++) 
		{ 
			widgets[i].addKeyListener(listener);
			
			if (widgets[i] instanceof Composite)
			{
				addKeyListener((Composite)widgets[i], listener);
			}
		}		
	}
	
	public boolean isCancelled()
	{
		return m_cancel;
	}

	protected void cancelClose()
	{
		m_cancel = true;
		getShell().close();
	}	
}
