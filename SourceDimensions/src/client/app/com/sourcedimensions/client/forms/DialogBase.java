package com.sourcedimensions.client.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.sourcedimensions.client.model.TriStateBoolean;
import com.sourcedimensions.client.Util;

public abstract class DialogBase 
{
	protected boolean m_cancel = true;
	protected static final String[] m_triStateText = { "Exclude", "Only", "Include"};
	
	protected abstract Shell getShell();
	
	public void open()
	{
		Shell shell = getShell();
		shell.open();

		while (!shell.isDisposed()) 
		{
			Display display = Display.getCurrent();
			
			if (!display.readAndDispatch()) 
				display.sleep();
		}		
	}
		
	protected void checkAllItems(Table table)
	{
		for (int i = 0; i < table.getItemCount(); i++)
		{
			table.getItem(i).setChecked(true);
		}
	}
	
	protected void setAllItems(Table table, TriStateBoolean value)
	{
		for (int i = 0; i < table.getItemCount(); i++)
		{	
			setTriStateBoolValue(table.getItem(i), value);
		}
	}
	
	
	protected void createShell(Shell parent)
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
	
	protected TriStateBoolean getTriStateBoolValue(TableItem item)
	{
		if (item.getGrayed())
		{
			return TriStateBoolean.EITHER;
		}
		else
		{
			if (item.getChecked())
			{
				return TriStateBoolean.TRUE;
			}
			else
			{
				return TriStateBoolean.FALSE;
			}
		}
	}
	
	protected void setTriStateBoolValue(TableItem item, TriStateBoolean value)
	{
		item.setGrayed(value == TriStateBoolean.EITHER);
		item.setChecked(value == TriStateBoolean.TRUE || value == TriStateBoolean.EITHER);
	}
	
	
	protected class TriStateCheckBoxAdapter extends SelectionAdapter 
	{
		public void widgetSelected(SelectionEvent e)
		{
			if (e.detail == SWT.CHECK)
			{
				TableItem item = (TableItem)e.item;

				if (item.getChecked())
				{
					item.setGrayed(true);
				}
				else
				{
					if (item.getGrayed())
					{
						item.setChecked(true);
						item.setGrayed(false);
					}
				}
			}
		}
	}

	
	protected class AllItemsAdapter extends SelectionAdapter 
	{
		public void widgetSelected(SelectionEvent e)
		{
			if (e.detail == SWT.CHECK)
			{
				TableItem item = (TableItem)e.item;
				Table parent = item.getParent();
				int count = parent.getItemCount() - 1;
				TableItem last = parent.getItem(count);
				
				if (item == last)
				{
					for (int i = 0; i < count; i++)
					{
						TableItem ti = parent.getItem(i);
						
						ti.setGrayed(item.getGrayed());
						ti.setChecked(item.getChecked());
					}
				}
				else
				{
					last.setGrayed(false);
					last.setChecked(false);
				}
			}
		}
	}
}
