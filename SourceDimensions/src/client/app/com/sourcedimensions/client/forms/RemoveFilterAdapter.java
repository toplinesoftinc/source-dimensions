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
	protected org.eclipse.swt.widgets.List m_listCtrl;
	protected Table m_tableCtrl;
	protected Shell m_shell;
	protected List m_list;

	
	public RemoveFilterAdapter(Shell shell, org.eclipse.swt.widgets.List control)
	{
		this(shell, control, null);
	}
	
	public RemoveFilterAdapter(Shell shell, org.eclipse.swt.widgets.List ctrl, List list)
	{
		m_shell = shell;
		m_listCtrl = ctrl;
		m_list = list;
	}	
		
	public RemoveFilterAdapter(Shell shell, Table ctrl)
	{
		this(shell, ctrl, null);
	}
	
	public RemoveFilterAdapter(Shell shell, Table control, List list)
	{
		m_shell = shell;
		m_tableCtrl = control;
		m_list = list;
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		int[] sel;
		
		if (m_listCtrl != null)
			sel = m_listCtrl.getSelectionIndices();
		else
			sel = m_tableCtrl.getSelectionIndices();
		
		if (sel.length == 0)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter(s)");
		}
		else
		{
			if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
				"Are you sure you want to delete selected filter(s)?"))
			{
				if (m_listCtrl != null)
					m_listCtrl.remove(sel);
				else
					m_tableCtrl.remove(sel);
				
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
