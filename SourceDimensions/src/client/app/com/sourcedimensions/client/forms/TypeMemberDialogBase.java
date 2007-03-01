package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.model.*;


public abstract class TypeMemberDialogBase extends DialogBase
{
	protected boolean m_anyParams;
	protected TriStateMask m_modifiers = new TriStateMask();
	protected int m_memberCategories;
	protected int m_operators;
	protected Type m_type = new Type();
	protected String m_name;
	protected List<Parameter> m_paramList = new ArrayList<Parameter>();
	
	
	public TriStateMask getModifiers()
	{
		return m_modifiers;
	}
	
	public int getMemberCategories()
	{
		return m_memberCategories;
	}
	
	public int getOperators()
	{
		return m_operators;
	}	
	
	public Type getType()
	{
		return m_type;
	}
	
	public String getMemberName()
	{
		return m_name;
	}
	
	public List<Parameter> getParams()
	{
		return m_paramList;
	}
	
	public boolean getAnyParams()
	{
		return m_anyParams;
	}	
	
	protected void addParam(Table table)
	{
		ParamDialog dialog = new ParamDialog(PlatformUI.getWorkbench().getDisplay(), getShell());
		
		dialog.open();
		
		if (!dialog.isCancelled())
		{
			Parameter param = dialog.getParam();
			TableItem item = new TableItem(table, SWT.NONE);
			
			m_paramList.add(param);
			item.setText(0, param.positionToString());
			item.setText(1, param.modifiersToString());
			item.setText(2, param.m_type.typePropsToString());
			item.setText(3, param.m_type.m_name);
			item.setText(4, param.m_name);
		}		
	}

	protected void editParam(Table table)
	{
		int sel = table.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(getShell(), "Selection", "Please select parameter");			
		}
		else
		{		
			ParamDialog dialog = new ParamDialog(PlatformUI.getWorkbench().getDisplay(), getShell(), m_paramList.get(sel));
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				Parameter param = dialog.getParam();
				
				m_paramList.set(sel, param);
				TableItem item = table.getItem(sel);
				
				item.setText(0, param.positionToString());
				item.setText(1, param.modifiersToString());
				item.setText(2, param.m_type.typePropsToString());
				item.setText(3, param.m_type.m_name);
				item.setText(4, param.m_name);
			}
		}
	}

	protected void populateParamList(Table table, List<Parameter> paramList)
	{
		table.removeAll();
		m_paramList = paramList;
		
		for (Parameter param : paramList)
		{		
			TableItem item = new TableItem(table, SWT.NONE);
			
			item.setText(0, param.positionToString());
			item.setText(1, param.modifiersToString());
			item.setText(2, param.m_type.typePropsToString());
			item.setText(3, param.m_type.m_name);
			item.setText(4, param.m_name);			
		}
	}
}
