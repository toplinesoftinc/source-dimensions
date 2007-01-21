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


public class JavaMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="9,11"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;
	private TypeMemberCategory[] m_categoryArray = 
	{
		TypeMemberCategory.FIELD,
		TypeMemberCategory.CONSTRUCTOR,
		TypeMemberCategory.METHOD,
		TypeMemberCategory.ENUM_CONST
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
	
	public JavaMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		checkAllItems(m_modifierList);
	}
	
	private void createShell(Shell parent)  
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		m_shell.setText("Type Member Filter");
		m_shell.setSize(new Point(471, 460));
		
		if (parent != null)
			m_shell.setParent(parent);
				
		m_shell.setText("Type Member Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setLayout(null);
		m_memberCategoryLabel = new Label(m_shell, SWT.NONE);
		m_memberCategoryLabel.setText("Type &Member Categories:");
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
		m_allCategoriesButton.setLocation(new Point(148, 30));
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
		m_allModifiersButton.setLocation(new Point(148, 62));
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
		m_modifierList.setLocation(new Point(238, 22));
		m_modifierList.setSize(new Point(115, 179));
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("Type &Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(17, 123));
		m_memberNameLabel.setSize(new Point(138, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(17, 137, 206, 19));
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
		m_paramsLabel.setBounds(new Rectangle(15, 213, 106, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(15, 228, 339, 189));
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Position");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 1);
		column.setWidth((int)(0.4 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type Filter");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 2);
		column.setWidth((int)(0.4 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name Filter");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 3);
		column.setWidth((int)(0.15 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Array");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 4);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Var.arity");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 5);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type param.");		
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setBounds(new Rectangle(365, 228, 88, 25));
		m_addParamButton.setEnabled(false);
		m_addParamButton.setText("A&dd Filter...");
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setBounds(new Rectangle(365, 270, 88, 25));
		m_editParamButton.setEnabled(false);
		m_editParamButton.setText("&Edit Filter...");
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setBounds(new Rectangle(365, 315, 88, 25));
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setText("&Remove Filter");
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setLocation(new Point(365, 22));
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
				
				String val = m_memberNameText.getText();
				
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
					m_memberCategories += m_categoryArray[m_categoryArray.length - 1].value();

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
					m_modifiers += m_modifierArray[m_modifierArray.length - 1].value();
				
				m_name = m_memberNameText.getText();
				m_anyParams = m_anyParamsCheckBox.getSelection();
				
				m_cancel = false;
				m_shell.close();
				
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(365, 62));
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
			TypeMemberCategory cat = m_categoryArray[i];
			
			if (cat == TypeMemberCategory.CONSTRUCTOR || cat == TypeMemberCategory.METHOD)
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
}
