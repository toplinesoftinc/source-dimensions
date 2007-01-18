package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.views.ProjectView;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;

public class TypeFilterDialog 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="9,11"
	private Display m_display;
	private Table m_typeCategoryList;
	private Label m_typeCategoryLabel;
	private Label m_modifierListLabel;
	private Table m_modifierList;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_baseTypesLabel;
	private Table m_baseTypesTable;
	private Button m_addBaseTypeButton;
	private Button m_editBaseTypeButton;
	private Button m_removeBaseTypeButton;
	private Button m_okButton;
	private Button m_cancelButton;
	private boolean m_cancel;
	private ArrayList<BaseType> m_baseTypes = new ArrayList<BaseType>();  //  @jve:decl-index=0:
	private Button m_allBaseTypesCheckBox;
	private int m_typeCategories;
	private int m_modifiers;
	private boolean m_allBaseTypes;
	private String m_typeName = "";
	private Button m_allModifiersButton;
	
	public enum TypeCategoryFlag
	{
		CLASS(1),
		INTERFACE(2),
		ENUM(2<<1),
		ANNOTATION(2<<2),
		STRUCT(2<<3),
		ALL(2<<4);
		
		TypeCategoryFlag(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}		
	}
	
	public class BaseType
	{
		public String m_name;
		public int m_category; 
		
		public BaseType(String name, int category)
		{
			m_name = name;
			m_category = category;
		}		
	}

	public TypeFilterDialog(Display display, Shell parent, String typeName, 
			int categories, int modifiers, boolean allBaseTypes, ArrayList<BaseType> baseTypes)
	{
		m_display = display;
		createShell(parent);
		m_typeNameText.setText(typeName);
		m_allBaseTypesCheckBox.setSelection(allBaseTypes);
		setBaseTypeControls();
		
		if (baseTypes != null & !allBaseTypes)
		{
			m_baseTypes = baseTypes;
			
			for (BaseType t : m_baseTypes)
			{
				TableItem item = new TableItem(m_baseTypesTable, 0);
				item.setText(0, t.m_name);
				item.setText(1, BaseTypeFilterDialog.getTypeCategoryName(t.m_category));
			}
		}
		
		int[] flags = getModifierArray();
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			m_modifierList.getItem(i).setChecked((modifiers & flags[i]) != 0);
		}
		
		flags = getTypeCategoryArray();
		
		for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
		{
			m_typeCategoryList.getItem(i).setChecked((categories & flags[i]) != 0);
		}
	}
		
	public TypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		selectAllItems(m_typeCategoryList);
		selectAllItems(m_modifierList);
	}
	
	private void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(454, 392));
		m_shell.setLayout(null);
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoryList = new Table(m_shell, SWT.BORDER | SWT.SINGLE | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_typeCategoryList.setHeaderVisible(false);
		m_typeCategoryList.setLinesVisible(false);
		m_typeCategoryList.setBounds(new Rectangle(17, 22, 106, 72));
		m_typeCategoryLabel.setBounds(new Rectangle(17, 8, 89, 13));
		m_typeCategoryLabel.setText("&Type Categories:");
		m_typeNameLabel = new Label(m_shell, SWT.NONE);
		m_typeNameText = new Text(m_shell, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(17, 115, 206, 19));
		m_allModifiersButton = new Button(m_shell, SWT.NONE);
		
		new TableItem(m_typeCategoryList, 0, 0).setText("CLASS");
		new TableItem(m_typeCategoryList, 0, 1).setText("INTERFACE");
		new TableItem(m_typeCategoryList, 0, 2).setText("ENUM");
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				new TableItem(m_typeCategoryList, 0, 3).setText("ANNOTATION");
				break;
				
			case CSHARP11:
			case CSHARP20:
				new TableItem(m_typeCategoryList, 0, 3).setText("STRUCT");
		}
		
		m_modifierListLabel = new Label(m_shell, SWT.NONE);
		m_modifierListLabel.setBounds(new Rectangle(231, 8, 56, 13));
		m_modifierListLabel.setText("&Modifiers:");

		m_modifierList = new Table(m_shell, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setBounds(new Rectangle(231, 22, 100, 136));

		m_typeNameLabel.setBounds(new Rectangle(17, 101, 101, 13));
		m_typeNameLabel.setText("Type &Name Filter:");
		m_allBaseTypesCheckBox = new Button(m_shell, SWT.CHECK);
		m_baseTypesLabel = new Label(m_shell, SWT.NONE);
		m_baseTypesLabel.setBounds(new Rectangle(17, 172, 110, 13));
		m_baseTypesLabel.setText("&Base Types Filter List:");
		m_baseTypesTable = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION);
		m_baseTypesTable.setHeaderVisible(true);
		m_baseTypesTable.setLinesVisible(true);
		m_baseTypesTable.setEnabled(false);
		m_baseTypesTable.setBounds(new Rectangle(17, 186, 315, 162));
		m_baseTypesTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_baseTypesTable.getSelectionIndex() != -1)
				{
					editBaseType();
				}
			}			
		});
		double width = m_baseTypesTable.getBounds().width - 2 * m_baseTypesTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_baseTypesTable, SWT.LEFT, 0);
		column.setWidth((int)(0.65 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name Filter");
		column = new TableColumn(m_baseTypesTable, SWT.LEFT, 1);
		column.setWidth((int)(0.35 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Category");
		m_addBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_addBaseTypeButton.setLocation(new Point(346, 185));
		m_addBaseTypeButton.setText("A&dd filter...");
		m_addBaseTypeButton.setEnabled(false);
		m_addBaseTypeButton.setSize(new Point(88, 25));
		m_addBaseTypeButton.addSelectionListener(new SelectionAdapter()
		{   
			public void widgetSelected(SelectionEvent e) 
			{    
				BaseTypeFilterDialog input = new BaseTypeFilterDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
				input.open();
				String val = input.getValue();
				
				if (val != null)
				{
					TableItem item = new TableItem(m_baseTypesTable, SWT.NONE);
					item.setText(0, val);
					item.setText(1, input.getTypeCategoryName());
					m_baseTypes.add(new BaseType(val, input.getTypeCategory().value()));
				}				
			}		
		});
		m_editBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_editBaseTypeButton.setLocation(new Point(346, 227));
		m_editBaseTypeButton.setText("&Edit filter...");
		m_editBaseTypeButton.setEnabled(false);
		m_editBaseTypeButton.setSize(new Point(88, 25));
		m_editBaseTypeButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editBaseType();
			}
		});
		m_removeBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_removeBaseTypeButton.setLocation(new Point(346, 272));
		m_removeBaseTypeButton.setText("&Remove filter");
		m_removeBaseTypeButton.setEnabled(false);
		m_removeBaseTypeButton.setSize(new Point(88, 25));
		m_removeBaseTypeButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				int sel = m_baseTypesTable.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
				}
				else
				{
					if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
						"Are you sure you want to delete selected filter?"))
					{
						m_baseTypesTable.remove(sel);
					}
				}				
			}
		});
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setLocation(new Point(346, 22));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean checked = false;
				
				for(int i = 0; i < m_typeCategoryList.getItemCount(); i++)
				{
					checked |= m_typeCategoryList.getItem(i).getChecked();
				}
				
				if (!checked)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please select at least one Type Category");
					return;
				}
				
				String val = m_typeNameText.getText();
				
				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");					
					return;
				}

				String[] names = val.split("/");
				
				for (String name : names)
				{
					if (name.length() == 0)
					{
						MessageDialog.openError(m_shell, "Incorrect input", "Namespace section cannot be empty (like \"com//abc\")");
						return;
					}
					
					if (name.equals("**"))
					{
						continue;
					}
					
					try
					{
						Pattern.compile(name);
					}
					catch(PatternSyntaxException ex)
					{
						MessageDialog.openError(m_shell, "Incorrect input",
							"Pattern \"" + name + "\" has the following error: " + ex.getMessage());
						return;
					}
				}				
				
				m_typeCategories = calcTypeCategoryFlags();
				m_modifiers = calcModifierFlags();
				m_typeName = m_typeNameText.getText();
				m_allBaseTypes = m_allBaseTypesCheckBox.getSelection();
				
				m_cancel = false;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(346, 62));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_allModifiersButton.setBounds(new Rectangle(149, 71, 74, 23));
		m_allModifiersButton.setText("All Modi&fiers");
		m_allModifiersButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				selectAllItems(m_modifierList);
			}
		});
		m_allBaseTypesCheckBox.setBounds(new Rectangle(17, 145, 89, 15));
		m_allBaseTypesCheckBox.setText("&All Base Types");
		m_allBaseTypesCheckBox.setSelection(true);
		m_allBaseTypesCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				setBaseTypeControls();
			}
		});
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		new TableItem(m_modifierList, 0, 0).setText("public");
		new TableItem(m_modifierList, 0, 1).setText("protected");
		new TableItem(m_modifierList, 0, 2).setText("private");
		new TableItem(m_modifierList, 0, 3).setText("abstract");
		new TableItem(m_modifierList, 0, 4).setText("static");		
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				new TableItem(m_modifierList, 0, 5).setText("final");
				new TableItem(m_modifierList, 0, 6).setText("strictfp");				
				break;
				
			case CSHARP11:
			case CSHARP20:
				new TableItem(m_modifierList, 0, 5).setText("new");
				new TableItem(m_modifierList, 0, 6).setText("internal");				
				new TableItem(m_modifierList, 0, 7).setText("sealed");
		}
		
		Control[] widgets = m_shell.getChildren();

		for (int i = 0; i < widgets.length; i++) 
		{ 
			widgets[i].addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.keyCode == SWT.ESC)
						cancelClose();
				}
			});
		}
		
		m_typeNameText.setFocus();		
		Util.centerWindow(m_shell, parent);				
	}
	
	protected void selectAllItems(Table table)
	{
		for (int i = 0; i < table.getItemCount(); i++)
		{
			table.getItem(i).setChecked(true);
		}		
	}

	protected void setBaseTypeControls()
	{
		boolean checked = m_allBaseTypesCheckBox.getSelection();

		m_baseTypesTable.setEnabled(!checked);
		m_addBaseTypeButton.setEnabled(!checked);
		m_editBaseTypeButton.setEnabled(!checked);
		m_removeBaseTypeButton.setEnabled(!checked);		
	}
	
	public void open()
	{
		m_shell.open();

		while (!m_shell.isDisposed()) 
		{
			if (!m_display.readAndDispatch()) 
				m_display.sleep();
		}		
	}

	public int getTypeCategories()
	{
		return m_typeCategories;
	}
	
	public int getModifiers()
	{
		return m_modifiers;
	}
	
	protected int calcTypeCategoryFlags()
	{
		int ret = 0;
		int[] flags = getTypeCategoryArray();
		
		boolean all = true;
		
		for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
		{
			if (m_typeCategoryList.getItem(i).getChecked())
			{
				ret += flags[i];
			}
			else
				all = false;
		}
		
		if (all)
			ret += flags[flags.length - 1];

		return ret;
	}
	
	protected int calcModifierFlags()
	{
		int ret = 0;
		int[] flags = getModifierArray();

		boolean all = true;
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			if (m_modifierList.getItem(i).getChecked())
			{
				ret += flags[i];
			}
			else
				all = false;
		}
		
		if (all)
			ret += flags[flags.length - 1]; 
		
		return ret;
	}
	
	protected int[] getTypeCategoryArray()
	{
		int[] flags = new int[] 
      		    {
      				TypeCategoryFlag.CLASS.value(),
      				TypeCategoryFlag.INTERFACE.value(),
      				TypeCategoryFlag.ENUM.value(),
      				0,
      				TypeCategoryFlag.ALL.value()
      			};
		          		
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				flags[3] = TypeCategoryFlag.ANNOTATION.value();
  				break;
  				
  			case CSHARP11:
  			case CSHARP20:
  				flags[3] = TypeCategoryFlag.STRUCT.value(); 
  		}
  		
  		return flags;
	}
	
	protected int[] getModifierArray()
	{
		int[] flags = new int[]
			    {
					Modifier.PUBLIC.value(),
					Modifier.PROTECTED.value(),
					Modifier.PRIVATE.value(),
					Modifier.ABSTRACT.value(),
					Modifier.STATIC.value(),
					0,
					0,
					0,
					Modifier.ALL.value()
			    };
		          		
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				flags[5] = Modifier.FINAL.value();
  				flags[6] = Modifier.STRICTFP.value();				
  				break;
  				
  			case CSHARP11:
  			case CSHARP20:
  				flags[5] = Modifier.NEW.value();
  				flags[6] = Modifier.INTERNAL.value();
  				flags[7] = Modifier.SEALED.value();
  		}

  		return flags;
	}
	
	public ArrayList<BaseType> getBaseTypes()
	{
		return m_baseTypes;
	}
	
	public boolean getAllBaseTypes()
	{
		return m_allBaseTypes;
	}

	public String getTypeName()
	{
		return m_typeName;
	}
	
	public boolean isCancelled()
	{
		return m_cancel;
	}

	protected void cancelClose()
	{
		m_cancel = true;
		m_shell.close();
	}
	
	private void editBaseType()
	{
		int sel = m_baseTypesTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
		}
		else
		{
			BaseTypeFilterDialog input = new BaseTypeFilterDialog(PlatformUI.getWorkbench().getDisplay(), m_shell,
				m_baseTypesTable.getItem(sel).getText(0), m_baseTypes.get(sel).m_category);
			
			input.open();
			String val = input.getValue();
			
			if (val != null)
			{
				TableItem item = m_baseTypesTable.getItem(sel);
				item.setText(0, val);
				item.setText(1, input.getTypeCategoryName());
				m_baseTypes.set(sel, new BaseType(val, input.getTypeCategory().value()));
			}							
		}		
	}
}
