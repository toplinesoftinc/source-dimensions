package com.sourcedimensions.client.forms;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import com.sourcedimensions.client.Util;


public class JavaMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-8,-10"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;
	private MemberCategory[] m_categoryArray = 
	{
		MemberCategory.FIELD,
		MemberCategory.CONSTRUCTOR,
		MemberCategory.METHOD,
		MemberCategory.ENUM_CONST
	};
	private Modifier[] m_modifierArray =
	{
		Modifier.PUBLIC,
		Modifier.PROTECTED,
		Modifier.PRIVATE,
		Modifier.ABSTRACT,
		Modifier.STATIC,
		Modifier.FINAL,
		Modifier.SYNCHRONIZED,
		Modifier.NATIVE,
		Modifier.STRICTFP,
		Modifier.TRANSIENT,
		Modifier.VOLATILE
	};
	private Button m_allCategoriesButton = null;
	private Button m_allModifiersButton = null;
	private Table m_modifierList = null;
	private Label m_modifierListLabel = null;
	private Label m_memberNameLabel = null;
	private Text m_memberNameText = null;
	private Button m_anyParamsCheckBox = null;
	private Button m_okButton = null;
	private Button m_cancelButton = null;
	private Label m_paramsLabel = null;
	private Table m_paramsTable = null;
	private Button m_addParamButton = null;
	private Button m_editParamButton = null;
	private Button m_removeParamButton = null;
	private Group m_typeGroup = null;  //  @jve:decl-index=0:visual-constraint="367,52"
	private Label m_typeNameLabel = null;
	private Text m_typeNameText = null;
	private Combo m_arrayTypeCombo = null;
	private Combo m_typeParamCombo = null;
	private Label m_arrayTypeLabel = null;
	private Label m_typeParamLabel = null;	
	
	public JavaMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		checkAllItems(m_modifierList);
	}
	
	public JavaMemberDialog(Display display, Shell parent, String name, int categories, int modifiers, 
		Type type, boolean anyParams)
	{
		m_display = display;
		createShell(parent);
		m_memberNameText.setText(name);
		m_arrayTypeCombo.select(type.m_isArray.value());
		m_typeParamCombo.select(type.m_isTypeParam.value());
		m_typeNameText.setText(type.m_name);
		m_anyParamsCheckBox.setSelection(anyParams);
		enableParamControls(!anyParams);
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			m_memberCategoryList.getItem(i).setChecked((categories & m_categoryArray[i].value()) != 0);
		}
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			m_modifierList.getItem(i).setChecked((modifiers & m_modifierArray[i].value()) != 0);
		}
	}
		
	private void createShell(Shell parent)  
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		m_shell.setText("Member Filter");
		m_shell.setSize(new Point(671, 450));
		
		if (parent != null)
			m_shell.setParent(parent);
				
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setLayout(null);
		m_memberCategoryLabel = new Label(m_shell, SWT.NONE);
		m_memberCategoryLabel.setText("Member Cate&gories:");
		m_memberCategoryLabel.setLocation(new Point(17, 8));
		m_memberCategoryLabel.setSize(new Point(122, 13));
		m_memberCategoryList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.HIDE_SELECTION);
		m_memberCategoryList.setHeaderVisible(false);
		m_memberCategoryList.setLinesVisible(false);
		m_memberCategoryList.setLocation(new Point(17, 22));
		m_memberCategoryList.setSize(new Point(122, 78));
		m_memberCategoryList.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (e.detail == SWT.CHECK)
				{
					categorySelectionChanged();
				}
			}
		});
		for (int i = 0; i < m_categoryArray.length; i++)
		{
			new TableItem(m_memberCategoryList, 0, i).setText(m_categoryArray[i].toString().replace("_", " "));
		}		
		m_allCategoriesButton = new Button(getShell(), SWT.NONE);
		m_allCategoriesButton.setText("All &Categories");
		m_allCategoriesButton.setLocation(new Point(147, 30));
		m_allCategoriesButton.setSize(new Point(80, 23));
		m_allCategoriesButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				checkAllItems(m_memberCategoryList);
				categorySelectionChanged();
			}
		});
		m_allModifiersButton = new Button(getShell(), SWT.NONE);
		m_allModifiersButton.setText("All &Modifiers");
		m_allModifiersButton.setLocation(new Point(147, 62));
		m_allModifiersButton.setSize(new Point(80, 23));
		m_allModifiersButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{
				checkAllItems(m_modifierList);
			}
		});
		m_modifierListLabel = new Label(getShell(), SWT.NONE);
		m_modifierList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setLocation(new Point(235, 22));
		m_modifierList.setSize(new Point(115, 174));
		createTypeGroup();
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("&Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(17, 123));
		m_memberNameLabel.setSize(new Point(108, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(17, 137, 208, 19));
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.setBounds(new Rectangle(17, 174, 101, 16));
		m_anyParamsCheckBox.setText("Any &Parameters");
		m_anyParamsCheckBox.setSelection(true);
		m_anyParamsCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				enableParamControls(!m_anyParamsCheckBox.getSelection());
			}
		});
		m_paramsLabel = new Label(getShell(), SWT.NONE);
		m_paramsLabel.setBounds(new Rectangle(18, 200, 106, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(18, 215, 539, 189));
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		column.setWidth((int)(0.13 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Position");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 1);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 2);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 3);
		column.setWidth((int)(0.09 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Array");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 4);
		column.setWidth((int)(0.12 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Var.arity");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 5);
		column.setWidth((int)(0.16 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type param.");		
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setBounds(new Rectangle(567, 215, 88, 25));
		m_addParamButton.setEnabled(false);
		m_addParamButton.setText("A&dd Filter...");
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setBounds(new Rectangle(567, 257, 88, 25));
		m_editParamButton.setEnabled(false);
		m_editParamButton.setText("&Edit Filter...");
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setBounds(new Rectangle(567, 302, 88, 25));
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setText("&Remove Filter");
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setLocation(new Point(565, 22));
		m_okButton.setVisible(true);
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean checked = false;
				
				for(int i = 0; i < m_memberCategoryList.getItemCount(); i++)
				{
					checked |= m_memberCategoryList.getItem(i).getChecked();
				}
				
				if (!checked)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please select at least one Type Member Category");
					return;
				}

				if (m_typeNameText.getText().trim().length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");					
					return;
				}
				
				
				String val = m_memberNameText.getText().trim();
				
				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Member Name Filter");					
					return;
				}

				try
				{
					Pattern.compile(val);
				}
				catch(PatternSyntaxException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input",
						"Pattern \"" + val + "\" has the following error: " + ex.getMessage());
					return;
				}
							
				boolean all = true;
				m_memberCategories = 0;
				
				for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
				{
					if (m_memberCategoryList.getItem(i).getChecked())
					{
						m_memberCategories += m_categoryArray[i].value();
					}
					else
						all = false;
				}
				
				if (all)
					m_memberCategories += MemberCategory.ALL.value();

				all = true;
				m_modifiers = 0;
				
				for (int i = 0; i < m_modifierList.getItemCount(); i++)
				{
					if (m_modifierList.getItem(i).getChecked())
					{
						m_modifiers += m_modifierArray[i].value();
					}
					else
						all = false;
				}
				
				if (all)
					m_modifiers += Modifier.ALL.value();
				
				m_name = m_memberNameText.getText();
				m_anyParams = m_anyParamsCheckBox.getSelection();
				m_type.m_name = m_typeNameText.getText();
				m_type.m_isArray = Util.TriStateBoolean.values()[m_arrayTypeCombo.getSelectionIndex()];
				m_type.m_isTypeParam = Util.TriStateBoolean.values()[m_typeParamCombo.getSelectionIndex()];
				
				m_cancel = false;
				m_shell.close();
				
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(566, 62));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_modifierListLabel.setBounds(new Rectangle(238, 8, 55, 13));
		m_modifierListLabel.setText("Mo&difiers:");
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString().toLowerCase());
		}
		
		m_memberNameText.setFocus();
		m_shell.setDefaultButton(m_okButton);
		postCreate(parent);
	}

	private void enableParamControls(boolean enable)
	{
		m_paramsTable.setEnabled(enable);
		m_addParamButton.setEnabled(enable);
		m_editParamButton.setEnabled(enable);
		m_removeParamButton.setEnabled(enable);		
	}

	private void categorySelectionChanged()
	{
		boolean func = false;
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			MemberCategory cat = m_categoryArray[i];
			
			if (cat == MemberCategory.CONSTRUCTOR || cat == MemberCategory.METHOD)
			{
				if (m_memberCategoryList.getItem(i).getChecked())
				{
					func = true;
					break;
				}
			}
		}
		
		m_anyParamsCheckBox.setEnabled(func);
		
		if (func)
		{
			enableParamControls(!m_anyParamsCheckBox.getSelection());
		}
		else
		{
			enableParamControls(false);
		}
		
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createTypeGroup() 
	{
		m_typeGroup = new Group(getShell(), SWT.SHADOW_NONE);
		m_typeGroup.setText("&Type/Return type");
		m_typeGroup.setBounds(new Rectangle(359, 17, 198, 179));
		m_arrayTypeLabel = new Label(m_typeGroup, SWT.NONE);
		createArrayTypeCombo();
		m_typeParamLabel = new Label(m_typeGroup, SWT.NONE);
		createTypeParamCombo();
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(9, 135, 87, 13));
		m_typeNameLabel.setText("Type Name Filter:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(9, 149, 181, 19));
		m_arrayTypeLabel.setBounds(new Rectangle(9, 18, 62, 13));
		m_arrayTypeLabel.setText("&Array Type:");
		m_typeParamLabel.setBounds(new Rectangle(9, 72, 90, 13));
		m_typeParamLabel.setText("Type &Parameter:");
	}

	private void createArrayTypeCombo() 
	{
		m_arrayTypeCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_arrayTypeCombo.setVisibleItemCount(3);
		m_arrayTypeCombo.setBounds(new Rectangle(9, 33, 97, 21));

		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_arrayTypeCombo.add(m_triStateText[i], i);
		}

		m_arrayTypeCombo.select(0);
	}

	private void createTypeParamCombo() 
	{
		m_typeParamCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_typeParamCombo.setVisibleItemCount(3);
		m_typeParamCombo.setBounds(new Rectangle(9, 86, 97, 21));

		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_typeParamCombo.add(m_triStateText[i], i);
		}
		
		m_typeParamCombo.select(0);
	}
}
