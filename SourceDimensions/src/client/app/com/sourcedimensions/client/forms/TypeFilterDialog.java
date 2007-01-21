package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.sourcedimensions.client.views.ProjectView;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;

public class TypeFilterDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="6,-4"
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
	private ArrayList<BaseType> m_baseTypes = new ArrayList<BaseType>();  //  @jve:decl-index=0:
	private Button m_allBaseTypesCheckBox;
	private int m_typeCategories;
	private int m_modifiers;
	private boolean m_allBaseTypes;
	private String m_typeName = "";
	private Button m_allModifiersButton;
	private Button m_allCategoriesButton = null;
	
	public enum TypeCategory
	{
		CLASS(1<<0),
		INTERFACE(1<<1),
		ENUM(1<<2),
		ANNOTATION(1<<3),
		STRUCT(1<<4),
		ALL(1<<5);
		
		TypeCategory(int val)
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
		
		Modifier[] mf = getModifierArray();
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			m_modifierList.getItem(i).setChecked((modifiers & mf[i].value()) != 0);
		}
		
		TypeCategory[] cf = getTypeCategoryArray();
		
		for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
		{
			m_typeCategoryList.getItem(i).setChecked((categories & cf[i].value()) != 0);
		}
	}
		
	public TypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_typeCategoryList);
		checkAllItems(m_modifierList);
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

		TypeCategory[] cats = getTypeCategoryArray();
		
		for (int i = 0; i < cats.length - 1; i++)
		{
			if (cats[i] == null)
				break;
			
			new TableItem(m_typeCategoryList, 0, i).setText(cats[i].toString());
		}
		
		m_modifierListLabel = new Label(m_shell, SWT.NONE);
		m_modifierListLabel.setBounds(new Rectangle(231, 8, 56, 13));
		m_modifierListLabel.setText("&Modifiers:");

		m_modifierList = new Table(m_shell, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setBounds(new Rectangle(231, 22, 100, 136));
		
		Modifier[] mods = getModifierArray();
		for (int i = 0; i < mods.length - 1; i++)
		{
			if (mods[i] == null)
				break;
			
			new TableItem(m_modifierList, 0, i).setText(mods[i].toString().toLowerCase());
		}

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
		column.setWidth((int)(0.6 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name Filter");
		column = new TableColumn(m_baseTypesTable, SWT.LEFT, 1);
		column.setWidth((int)(0.4 * width));
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
				
				TypeCategory[] tf = getTypeCategoryArray();
				
				boolean all = true;
				m_typeCategories = 0;
				
				for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
				{
					if (m_typeCategoryList.getItem(i).getChecked())
					{
						m_typeCategories += tf[i].value();
					}
					else
						all = false;
				}
				
				if (all)
					m_typeCategories += tf[tf.length - 1].value();
			
				Modifier[] mf = getModifierArray();

				all = true;
				m_modifiers = 0;
				
				for (int i = 0; i < m_modifierList.getItemCount(); i++)
				{
					if (m_modifierList.getItem(i).getChecked())
					{
						m_modifiers += mf[i].value();
					}
					else
						all = false;
				}
				
				if (all)
					m_modifiers += mf[mf.length - 1].value(); 
							
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
		m_allCategoriesButton = new Button(m_shell, SWT.NONE);
		m_allCategoriesButton.setLocation(new Point(136, 31));
		m_allCategoriesButton.setText("All Cate&gories");
		m_allCategoriesButton.setSize(new Point(80, 23));
		m_allCategoriesButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				checkAllItems(m_typeCategoryList);
			}
		});
		m_allModifiersButton.setText("All Modi&fiers");
		m_allModifiersButton.setSize(new Point(80, 23));
		m_allModifiersButton.setLocation(new Point(136, 63));
		m_allModifiersButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				checkAllItems(m_modifierList);
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

		postCreate(parent);
		m_shell.setDefaultButton(m_okButton);
		m_typeNameText.setFocus();		
	}
	
	protected void setBaseTypeControls()
	{
		boolean checked = m_allBaseTypesCheckBox.getSelection();

		m_baseTypesTable.setEnabled(!checked);
		m_addBaseTypeButton.setEnabled(!checked);
		m_editBaseTypeButton.setEnabled(!checked);
		m_removeBaseTypeButton.setEnabled(!checked);		
	}

	public int getTypeCategories()
	{
		return m_typeCategories;
	}
	
	public int getModifiers()
	{
		return m_modifiers;
	}

	protected TypeCategory[] getTypeCategoryArray()
	{
		TypeCategory[] flags = new TypeCategory[] 
      		    {
      				TypeCategory.CLASS,
      				TypeCategory.INTERFACE,
      				TypeCategory.ENUM,
      				null,
      				TypeCategory.ALL
      			};
		          		
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				flags[3] = TypeCategory.ANNOTATION;
  				break;
  				
  			case CSHARP11:
  			case CSHARP20:
  				flags[3] = TypeCategory.STRUCT; 
  		}
  		
  		return flags;
	}
	
	protected Modifier[] getModifierArray()
	{
		Modifier[] flags = new Modifier[]
			    {
					Modifier.PUBLIC,
					Modifier.PROTECTED,
					Modifier.PRIVATE,
					Modifier.ABSTRACT,
					Modifier.STATIC,
					null,
					null,
					null,
					Modifier.ALL
			    };
		          		
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				flags[5] = Modifier.FINAL;
  				flags[6] = Modifier.STRICTFP;				
  				break;
  				
  			case CSHARP11:
  			case CSHARP20:
  				flags[5] = Modifier.NEW;
  				flags[6] = Modifier.INTERNAL;
  				flags[7] = Modifier.SEALED;
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
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
