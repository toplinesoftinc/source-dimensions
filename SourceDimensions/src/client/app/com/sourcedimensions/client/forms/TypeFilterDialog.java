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
	private Table m_modifiersList;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_baseTypesLabel;
	private Table m_baseTypesTable;
	private Button m_addBaseTypeButton;
	private Button m_editBaseTypeButton;
	private Button m_removeBaseTypeButton;
	private Button m_okButton;
	private Button m_cancelButton;
	private ArrayList<BaseTypeFilterDialog.TypeCategory> m_baseTypeCategories = 
		new ArrayList<BaseTypeFilterDialog.TypeCategory>();  //  @jve:decl-index=0:

	public enum TypeCategoryFlag
	{
		CLASS(1),
		INTERFACE(2),
		ENUM(4),
		ANNOTATION(8),
		STRUCT(16);
		
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
	
	public enum ModifierFlag
	{
		PUBLIC(1),
		PROTECTED(2),
		PRIVATE(4),
		ABSTRACT(8),
		STATIC(16),
		FINAL(32),
		STRICTFP(64),
		NEW(128),
		INTERNAL(256),
		SEALED(512);
		
		ModifierFlag(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}		
	}
	
	public TypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
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
		m_typeCategoryList.addMouseListener(new CheckListDblClickMouseAdapter()); 
		m_typeCategoryLabel.setBounds(new Rectangle(17, 8, 89, 13));
		m_typeCategoryLabel.setText("&Type Categories:");
		
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

		m_modifiersList = new Table(m_shell, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_modifiersList.setHeaderVisible(false);
		m_modifiersList.setLinesVisible(false);
		m_modifiersList.setBounds(new Rectangle(231, 22, 100, 136));
		m_modifiersList.addMouseListener(new CheckListDblClickMouseAdapter());

		m_typeNameLabel = new Label(m_shell, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(17, 125, 101, 13));
		m_typeNameLabel.setText("Type &Name Filter:");
		m_typeNameText = new Text(m_shell, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(17, 139, 201, 19));
		m_baseTypesLabel = new Label(m_shell, SWT.NONE);
		m_baseTypesLabel.setBounds(new Rectangle(18, 172, 110, 13));
		m_baseTypesLabel.setText("&Base Types Filter List:");
		m_baseTypesTable = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION);
		m_baseTypesTable.setHeaderVisible(true);
		m_baseTypesTable.setLinesVisible(true);
		m_baseTypesTable.setBounds(new Rectangle(17, 186, 315, 162));
		m_baseTypesTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				editBaseType();
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
		m_addBaseTypeButton.setSize(new Point(88, 25));
		m_addBaseTypeButton.addSelectionListener(new SelectionAdapter()
		{   
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{    
				BaseTypeFilterDialog input = new BaseTypeFilterDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
				input.open();
				String val = input.getValue();
				
				if (val != null)
				{
					TableItem item = new TableItem(m_baseTypesTable, SWT.NONE);
					item.setText(0, val);
					item.setText(1, input.getTypeCategoryName());
					m_baseTypeCategories.add(input.getTypeCategory());
				}				
			}		
		});
		m_editBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_editBaseTypeButton.setLocation(new Point(346, 227));
		m_editBaseTypeButton.setText("&Edit filter...");
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
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(346, 62));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				m_shell.close();
			}
		});
		new TableItem(m_modifiersList, 0, 0).setText("public");
		new TableItem(m_modifiersList, 0, 1).setText("protected");
		new TableItem(m_modifiersList, 0, 2).setText("private");
		new TableItem(m_modifiersList, 0, 3).setText("abstract");
		new TableItem(m_modifiersList, 0, 4).setText("static");		
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				new TableItem(m_modifiersList, 0, 5).setText("final");
				new TableItem(m_modifiersList, 0, 6).setText("strictfp");				
				break;
				
			case CSHARP11:
			case CSHARP20:
				new TableItem(m_modifiersList, 0, 5).setText("new");
				new TableItem(m_modifiersList, 0, 6).setText("internal");				
				new TableItem(m_modifiersList, 0, 7).setText("sealed");
		}

		Control[] widgets = m_shell.getChildren();

		for (int i = 0; i < widgets.length; i++) 
		{ 
			widgets[i].addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.keyCode == SWT.ESC)
						m_shell.close();
				}
			});
		}
		
		Util.centerWindow(m_shell, parent);				
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
				m_baseTypesTable.getItem(sel).getText(0), m_baseTypeCategories.get(sel));
			
			input.open();
			String val = input.getValue();
			
			if (val != null)
			{
				TableItem item = m_baseTypesTable.getItem(sel);
				item.setText(0, val);
				item.setText(1, input.getTypeCategoryName());
				m_baseTypeCategories.set(sel, input.getTypeCategory());
			}							
		}		
	}
	
	protected class CheckListDblClickMouseAdapter extends MouseAdapter 
	{
		public void mouseDoubleClick(MouseEvent e) 
		{
			if (e.getSource() instanceof Table)
			{
				Table src = (Table)e.getSource();
				
				int sel = src.getSelectionIndex();
	
				if (sel != -1)
				{
					TableItem item = src.getItem(sel); 
					item.setChecked(!item.getChecked());
				}
			}
		}
	}	
	
	
}
