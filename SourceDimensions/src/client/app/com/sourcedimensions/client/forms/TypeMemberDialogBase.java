package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;


public abstract class TypeMemberDialogBase extends DialogBase
{
	protected boolean m_anyParams;
	protected TriStateMask m_modifiers = new TriStateMask();
	protected int m_memberCategories;
	protected int m_operators;
	protected Type m_type = new Type();
	protected String m_name;
	protected List<Parameter> m_paramList = new ArrayList<Parameter>();
	
	public enum MemberCategory
	{
		FIELD(1<<0),
		CONSTANT(1<<1),
		LOCAL_VAR(1<<2, "LOCAL VAR"),
		CONSTRUCTOR(1<<3),
		DESTRUCTOR(1<<4),
		METHOD(1<<5),
		ANONYM_METHOD(1<<6, "ANONYM.METHOD"),
		PROPERTY_GET(1<<7, "PROPERTY GET"),
		PROPERTY_SET(1<<8, "PROPERTY SET"),
		EVENT_ADD(1<<9, "EVENT ADD"),
		EVENT_REMOVE(1<<10, "EVENT REMOVE"),
		INDEXER_GET(1<<11, "INDEXER GET"),
		INDEXER_SET(1<<12, "INDEXER SET"),
		OPERATOR(1<<13),
		ENUM_CONST(1<<14, "ENUM CONST."),
		ALL(1<<15);
		
		MemberCategory(int val)
		{
			value = val;
			name = name();
		}
		
		MemberCategory(int val, String n)
		{
			value = val;
			name = n;
		}
		
		private final int value;
		private final String name;
		
		public int value()
		{
			return value;
		}
		
		public String toString()
		{
			return name;
		}
	}	
	
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
			item.setText(2, Type.typePropsToString(param.m_type.m_typeProps));
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
				item.setText(2, Type.typePropsToString(param.m_type.m_typeProps));
				item.setText(3, param.m_type.m_name);
				item.setText(4, param.m_name);
			}
		}
	}
	
	protected void deleteParam(Table table)
	{
		int sel = table.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(getShell(), "Selection", "Please select parameter");
		}
		else
		{
			if (MessageDialog.openQuestion(getShell(), "Deletion confirmation", 
				"Are you sure you want to delete selected parameter?"))
			{
				table.remove(sel);
				m_paramList.remove(sel);
			}
		}					
	}
	
	protected void populateParamList(Table table, List<Parameter> paramList)
	{
		table.removeAll();
		m_paramList.clear();
		
		for (Parameter param : paramList)
		{
			m_paramList.add(param);
			
			TableItem item = new TableItem(table, SWT.NONE);
			
			item.setText(0, param.positionToString());
			item.setText(1, param.modifiersToString());
			item.setText(2, Type.typePropsToString(param.m_type.m_typeProps));
			item.setText(3, param.m_type.m_name);
			item.setText(4, param.m_name);			
		}
	}
}
