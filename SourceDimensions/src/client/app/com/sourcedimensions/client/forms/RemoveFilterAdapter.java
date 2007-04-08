package com.sourcedimensions.client.forms;

import java.util.ArrayList;
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
		int[] sel = m_table.getSelectionIndices();
		
		if (sel.length == 0)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter(s)");
		}
		else
		{
			if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
				"Are you sure you want to delete selected filter(s)?"))
			{
				m_table.remove(sel);
				
				if (m_list != null)
				{
					boolean[] flags = new boolean[m_list.size()];
					
					for (int i = 0; i < flags.length; i++)
					{
						flags[i] = true;
					}
					
					for (int i : sel)
					{
						flags[i] = false;
					}
						
					List lst = new ArrayList();
					
					for (int i = 0; i < m_list.size(); i++)
					{
						if (flags[i])
						{
							lst.add(m_list.get(i));
						}
					}
						
					m_list.clear();
					m_list.addAll(lst);
				}
			}
		}				
	}		
}
