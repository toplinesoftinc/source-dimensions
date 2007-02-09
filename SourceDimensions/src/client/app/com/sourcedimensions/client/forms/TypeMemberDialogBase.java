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
		LOCAL_VAR(1<<2),
		CONSTRUCTOR(1<<3),
		DESTRUCTOR(1<<4),
		METHOD(1<<5),
		PROPERTY_GET(1<<6),
		PROPERTY_SET(1<<7),
		EVENT_ADD(1<<8),
		EVENT_REMOVE(1<<9),
		INDEXER_GET(1<<10),
		INDEXER_SET(1<<11),
		OPERATOR(1<<12),
		ENUM_CONST(1<<13),
		ALL(1<<14);
		
		MemberCategory(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
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
	
	protected String positionToString(Parameter param)
	{
		switch (param.m_posType)
		{
			case LIST:
				{
					String str = "";
					Object[] items = param.m_posList.toArray();
					
					for (int i = 0; i < items.length; i++)
					{
						if (i > 0)
							str += ",";
						
						str += (Integer)items[i];
					}
					
					return str;
				}
				
			case BETWEEN:
				return param.m_posMin + " - " + param.m_posMax;
				
			case LESS_EQ:
				return "<= " + param.m_posMax;
				
			case GREATER_EQ:
				return ">= " + param.m_posMin;
				
			case EXACT:
				return Integer.toString(param.m_posValue);
				
			case ANY:
				return "<ANY>";
				
			default:
				return "";
		}
	}
	
	protected String modifiersToString(TriStateMask mask)
	{
		String str = "";
		
		for (Parameter.Modifier f : Parameter.Modifier.values())
		{
			switch (mask.getMask(f.value()))
			{
				case TRUE:
					if (str.length() > 0)
						str += ",";
					
					str += f.name().toLowerCase().replace("_", ".");
					break;
					 
				case EITHER:
					if (str.length() > 0)
						str += ",";
					
					str += "(" + f.name().toLowerCase().replace("_", ".") + ")";
			}
		}
		
		return str;
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
			item.setText(0, positionToString(param));
			item.setText(1, modifiersToString(param.m_modifiers));
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
				
				item.setText(0, positionToString(param));
				item.setText(1, modifiersToString(param.m_modifiers));
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
			
			item.setText(0, positionToString(param));
			item.setText(1, modifiersToString(param.m_modifiers));
			item.setText(2, Type.typePropsToString(param.m_type.m_typeProps));
			item.setText(3, param.m_type.m_name);
			item.setText(4, param.m_name);			
		}
	}
}
