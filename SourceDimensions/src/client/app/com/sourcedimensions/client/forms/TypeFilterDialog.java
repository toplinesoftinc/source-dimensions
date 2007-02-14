package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
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
import com.sourcedimensions.client.TriStateBoolean;
import com.sourcedimensions.client.views.ProjectView;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;


public class TypeFilterDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-5,-27"
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
	private List<BaseType> m_baseTypes = new ArrayList<BaseType>();  //  @jve:decl-index=0:
	private Button m_allBaseTypesCheckBox;
	private int m_typeCategories;
	private TriStateMask m_modifiers = new TriStateMask();  //  @jve:decl-index=0:
	private boolean m_allBaseTypes;
	private String m_typeName = "";  //  @jve:decl-index=0:
	private TriStateBoolean m_internalType;
	private Table m_internalTypeCheckBox = null;
	
	private final static Modifier[] javaModifiers = 
	{
		Modifier.PUBLIC,
		Modifier.PROTECTED,
		Modifier.PRIVATE,
		Modifier.ABSTRACT,
		Modifier.STATIC,
		Modifier.FINAL,
		Modifier.STRICTFP,
		Modifier.ALL
    };

	public final static Modifier[] csharpModifiers =
    {
		Modifier.PUBLIC,
		Modifier.PROTECTED,
		Modifier.PRIVATE,
		Modifier.ABSTRACT,
		Modifier.STATIC,
		Modifier.NEW,
		Modifier.INTERNAL,
		Modifier.SEALED,
		Modifier.ALL
    };	
	
	public final static TypeCategory[] javaTypeCategories = 
    {
		TypeCategory.CLASS,
		TypeCategory.INTERFACE,
		TypeCategory.ENUM,
		TypeCategory.ANONYM_CLASS,
		TypeCategory.ANNOTATION,
		TypeCategory.ALL
	};
	
	public final static TypeCategory[] csharpTypeCategories = 
    {
		TypeCategory.CLASS,
		TypeCategory.INTERFACE,
		TypeCategory.ENUM,
		TypeCategory.STRUCT,
		TypeCategory.DELEGATE,
		TypeCategory.ALL
	};	
	
	public enum TypeCategory
	{
		CLASS(1<<0),
		INTERFACE(1<<1),
		ENUM(1<<2),
		ANONYM_CLASS(1<<3, "ANONYM.CLASS"),
		ANNOTATION(1<<4),
		STRUCT(1<<5),
		DELEGATE(1<<6),
		ALL(1<<7);
		
		TypeCategory(int val)
		{
			value = val;
			name = name();
		}
		
		TypeCategory(int val, String n)
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

	public TypeFilterDialog(Display display, Shell parent, String typeName,	int categories, 
		TriStateMask modifiers, TriStateBoolean internalType, boolean allBaseTypes, List<BaseType> baseTypes)
	{
		m_display = display;
		createShell(parent);
		m_typeNameText.setText(typeName);
		m_allBaseTypesCheckBox.setSelection(allBaseTypes);
		setBaseTypeControls();
	
		setTriStateBoolValue(m_internalTypeCheckBox.getItem(0), internalType);
		
		if (baseTypes != null & !allBaseTypes)
		{
			m_baseTypes = baseTypes;
			
			for (BaseType t : m_baseTypes)
			{
				TableItem item = new TableItem(m_baseTypesTable, 0);
				item.setText(0, BaseTypeFilterDialog.getTypeCategoryName(t.m_category));				
				item.setText(1, t.m_name);
			}
		}
		
		Modifier[] mf = getModifierArray();
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_modifierList.getItem(i), modifiers.getMask(mf[i].value()));
		}
		
		TypeCategory[] cf = getTypeCategoryArray();
		
		for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
		{
			m_typeCategoryList.getItem(i).setChecked((categories & cf[i].value()) != 0);
		}
		
		categorySelectionChanged();
	}
		
	public TypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_typeCategoryList);
		setAllItems(m_modifierList, TriStateBoolean.EITHER);
	}
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(456, 424));
		m_shell.setLayout(null);
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoryList = new Table(m_shell, SWT.BORDER | SWT.SINGLE | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_typeCategoryList.setHeaderVisible(false);
		m_typeCategoryList.setLinesVisible(false);
		m_typeCategoryList.setBounds(new Rectangle(17, 21, 112, 112));
		m_typeCategoryList.addSelectionListener(new AllItemsAdapter());
		m_typeCategoryList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.CHECK)
				{
					categorySelectionChanged();
				}
			}
		});
		m_typeCategoryLabel.setBounds(new Rectangle(18, 7, 89, 13));
		m_typeCategoryLabel.setText("&Type Categories:");
		m_internalTypeCheckBox = new Table(getShell(), SWT.CHECK | SWT.BORDER | SWT.HIDE_SELECTION | SWT.FULL_SELECTION);
		m_typeNameLabel = new Label(m_shell, SWT.NONE);
		m_typeNameText = new Text(m_shell, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(17, 158, 223, 19));
		TypeCategory[] cats = getTypeCategoryArray();
		
		for (int i = 0; i < cats.length; i++)
		{
			if (cats[i] == null)
				break;
			
			new TableItem(m_typeCategoryList, 0, i).setText(cats[i].toString());
		}
			
		m_modifierListLabel = new Label(m_shell, SWT.NONE);
		m_modifierListLabel.setBounds(new Rectangle(247, 7, 56, 13));
		m_modifierListLabel.setText("&Modifiers:");

		m_modifierList = new Table(m_shell, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setBounds(new Rectangle(247, 21, 85, 156));
		m_modifierList.addSelectionListener(new TriStateCheckBoxAdapter());
		m_modifierList.addSelectionListener(new AllItemsAdapter());
		
		Modifier[] mods = getModifierArray();
		
		for (int i = 0; i < mods.length; i++)
		{
			if (mods[i] == null)
				break;
			
			new TableItem(m_modifierList, 0, i).setText(mods[i].toString());
		}

		m_typeNameLabel.setBounds(new Rectangle(17, 144, 101, 13));
		m_typeNameLabel.setText("Type &Name Filter:");
		m_allBaseTypesCheckBox = new Button(m_shell, SWT.CHECK);
		m_baseTypesLabel = new Label(m_shell, SWT.NONE);
		m_baseTypesLabel.setText("&Base Types Filter List:");
		m_baseTypesLabel.setLocation(new Point(17, 215));
		m_baseTypesLabel.setSize(new Point(110, 13));
		m_baseTypesTable = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION);
		m_baseTypesTable.setHeaderVisible(true);
		m_baseTypesTable.setLinesVisible(true);
		m_baseTypesTable.setEnabled(false);
		m_baseTypesTable.setLocation(new Point(17, 230));
		m_baseTypesTable.setSize(new Point(315, 149));
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
		column.setWidth((int)(0.4 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Category");
		column = new TableColumn(m_baseTypesTable, SWT.LEFT, 1);
		column.setWidth((int)(0.6 * width));
		column.setText("Name");		
		column.setResizable(true);
		column.setMoveable(true);
		m_addBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_addBaseTypeButton.setLocation(new Point(344, 229));
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
					item.setText(0, input.getTypeCategoryName());
					item.setText(1, val);					
					m_baseTypes.add(new BaseType(val, input.getTypeCategory().value()));
				}				
			}		
		});
		m_editBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_editBaseTypeButton.setLocation(new Point(344, 271));
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
		m_removeBaseTypeButton.setLocation(new Point(344, 313));
		m_removeBaseTypeButton.setText("&Remove filter");
		m_removeBaseTypeButton.setEnabled(false);
		m_removeBaseTypeButton.setSize(new Point(88, 25));
		m_removeBaseTypeButton.addSelectionListener(
				new RemoveFilterAdapter(m_shell, m_baseTypesTable, m_baseTypes)); 
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setLocation(new Point(345, 20));
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
				
				if (m_typeNameText.getEnabled())
				{
					String val = m_typeNameText.getText().trim();
					
					if (val.length() == 0)
					{
						MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");					
						return;
					}
	
					String[] names = val.split("/");
					
					for (String name : names)
					{
						if (name.trim().length() == 0)
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
					
					m_typeName = val;
				}
				else
				{
					m_typeName = "";
				}
				
				TypeCategory[] tc = getTypeCategoryArray();
				
				m_typeCategories = 0;
				
				for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
				{
					if (m_typeCategoryList.getItem(i).getChecked())
					{
						m_typeCategories += tc[i].value();
					}
				}
				
				Modifier[] mf = getModifierArray();
				m_modifiers.reset();
			
				if (m_modifierList.getEnabled())
				{
					for (int i = 0; i < m_modifierList.getItemCount(); i++)
					{
						m_modifiers.setMask(mf[i].value(), getTriStateBoolValue(m_modifierList.getItem(i)));
					}
				}
				
				m_allBaseTypes = m_allBaseTypesCheckBox.getSelection();
				m_internalType = getTriStateBoolValue(m_internalTypeCheckBox.getItem(0));
				
				m_cancel = false;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(345, 60));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_internalTypeCheckBox.setHeaderVisible(false);
		m_internalTypeCheckBox.setLocation(new Point(135, 21));
		m_internalTypeCheckBox.setLinesVisible(false);
		m_internalTypeCheckBox.setSize(new Point(106, 20));
		new TableItem(m_internalTypeCheckBox, 0).setText("Internal Types");
		m_internalTypeCheckBox.addSelectionListener(new TriStateCheckBoxAdapter());
		m_allBaseTypesCheckBox.setBounds(new Rectangle(17, 186, 89, 13));
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

		super.createShell(parent);
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
	
	public TriStateMask getModifiers()
	{
		return m_modifiers;
	}

	protected TypeCategory[] getTypeCategoryArray()
	{
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				return javaTypeCategories;
  				
  			case CSHARP11:
  			case CSHARP20:
  				return csharpTypeCategories;
  				
  			default:
  				return null;
  		}
 	}
	
	protected Modifier[] getModifierArray()
	{
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				return javaModifiers;
  				
  			case CSHARP11:
  			case CSHARP20:
  				return csharpModifiers;

  			default:
  				return null;
  		}
	}
	
	public List<BaseType> getBaseTypes()
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
	
	public TriStateBoolean getInternalType()
	{
		return m_internalType;
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
				m_baseTypesTable.getItem(sel).getText(1), m_baseTypes.get(sel).m_category);
			
			input.open();
			String val = input.getValue();
			
			if (val != null)
			{
				TableItem item = m_baseTypesTable.getItem(sel);
				item.setText(0, input.getTypeCategoryName());				
				item.setText(1, val);
				m_baseTypes.set(sel, new BaseType(val, input.getTypeCategory().value()));
			}							
		}		
	}

	protected void categorySelectionChanged()
	{
		int cat = 0;
		TypeCategory[] tc = getTypeCategoryArray();
		
		for (int i = 0; i < m_typeCategoryList.getItemCount(); i++)
		{
			if (m_typeCategoryList.getItem(i).getChecked())
			{
				cat += tc[i].value();
			}
		}
		
		boolean enabled = (cat == 0) || (cat & ~TypeCategory.ANONYM_CLASS.value()) != 0;
		
		m_typeNameText.setEnabled(enabled);
		m_internalTypeCheckBox.setEnabled(enabled);
		m_modifierList.setEnabled(enabled);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
