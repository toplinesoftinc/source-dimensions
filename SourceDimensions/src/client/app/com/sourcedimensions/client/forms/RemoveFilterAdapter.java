package com.sourcedimensions.client.forms;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class RemoveFilterAdapter extends SelectionAdapter 
{
	protected Table m_table;
	protected Shell m_shell;
	protected List m_list;
	
	public RemoveFilterAdapter(Shell shell, Table table)
	{
		m_shell = shell;
		m_table = table;
	}
	
	public RemoveFilterAdapter(Shell shell, Table table, List list)
	{
		m_shell = shell;
		m_table = table;
		m_list = list;
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		int sel = m_table.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
		}
		else
		{
			if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
				"Are you sure you want to delete selected filter?"))
			{
				m_table.remove(sel);
				
				if (m_list != null)
				{
					m_list.remove(sel);
				}
			}
		}				
	}		
}
