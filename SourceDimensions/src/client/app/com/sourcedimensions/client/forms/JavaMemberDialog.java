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
import com.sourcedimensions.client.TriStateBoolean;


public class JavaMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-8,-26"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;

	protected final static int m_arrayItem = 0;
	protected final static int m_typeParamItem = 1;
	
	private final static MemberCategory[] m_categoryArray = 
	{
		MemberCategory.FIELD,
		MemberCategory.CONSTRUCTOR,
		MemberCategory.METHOD,
		MemberCategory.ENUM_CONST,
		MemberCategory.ALL
	};
	private final static Modifier[] m_modifierArray =
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
		Modifier.VOLATILE,
		Modifier.ALL
	};
	private Table m_modifierList;
	private Label m_modifierListLabel;
	private Label m_memberNameLabel;
	private Text m_memberNameText;
	private Button m_anyParamsCheckBox;
	private Button m_okButton;
	private Button m_cancelButton;
	private Label m_paramsLabel;
	private Table m_paramsTable;
	private Button m_addParamButton;
	private Button m_editParamButton;
	private Button m_removeParamButton;
	private Group m_typeGroup;  //  @jve:decl-index=0:visual-constraint="367,52"
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_typePropsLabel;
	private Table m_typePropsList;	
	
	public JavaMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		setAllItems(m_modifierList, TriStateBoolean.EITHER);
	}
	
	public JavaMemberDialog(Display display, Shell parent, String name, int categories, TriStateMask modifiers, 
		Type type, boolean anyParams)
	{
		m_display = display;
		createShell(parent);
		m_memberNameText.setText(name);
		setTriStateBoolValue(m_typePropsList.getItem(m_arrayItem), type.m_typeProps.getMask(Type.Property.ARRAY.value()));
		setTriStateBoolValue(m_typePropsList.getItem(m_typeParamItem), type.m_typeProps.getMask(Type.Property.TYPE_PARAM.value()));
		m_typeNameText.setText(type.m_name);
		m_anyParamsCheckBox.setSelection(anyParams);
		enableParamControls(!anyParams);
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			m_memberCategoryList.getItem(i).setChecked((categories & m_categoryArray[i].value()) != 0);
		}
	
		categorySelectionChanged();
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_modifierList.getItem(i), modifiers.getMask(m_modifierArray[i].value()));
		}
	}
		
	private void createShell(Shell parent)  
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		m_shell.setText("Member Filter");
		
		if (parent != null)
			m_shell.setParent(parent);
				
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setLayout(null);
		m_shell.setSize(new Point(651, 460));
		m_memberCategoryLabel = new Label(m_shell, SWT.NONE);
		m_memberCategoryLabel.setText("Member Cate&gories:");
		m_memberCategoryLabel.setLocation(new Point(17, 8));
		m_memberCategoryLabel.setSize(new Point(106, 13));
		m_memberCategoryList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.HIDE_SELECTION);
		m_memberCategoryList.setHeaderVisible(false);
		m_memberCategoryList.setLinesVisible(false);
		m_memberCategoryList.setLocation(new Point(17, 22));
		m_memberCategoryList.setSize(new Point(122, 98));
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
		m_memberCategoryList.addSelectionListener(new AllItemsAdapter());
		m_modifierListLabel = new Label(getShell(), SWT.NONE);
		m_modifierList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setLocation(new Point(206, 23));
		m_modifierList.setSize(new Point(115, 192));
		m_modifierList.addSelectionListener(new TriStateCheckBoxAdapter());
		m_modifierList.addSelectionListener(new AllItemsAdapter());
		createTypeGroup();
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("&Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(17, 132));
		m_memberNameLabel.setSize(new Point(108, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(17, 146, 179, 19));
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.setBounds(new Rectangle(17, 183, 101, 16));
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
		m_paramsLabel.setBounds(new Rectangle(18, 211, 106, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(18, 226, 513, 189));
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		column.setWidth((int)(0.2 * width));
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
		column.setText("Type Properties");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 3);
		column.setWidth((int)(0.30 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setBounds(new Rectangle(542, 225, 88, 25));
		m_addParamButton.setEnabled(false);
		m_addParamButton.setText("A&dd Filter...");
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setBounds(new Rectangle(542, 267, 88, 25));
		m_editParamButton.setEnabled(false);
		m_editParamButton.setText("&Edit Filter...");
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_removeParamButton.setLocation(new Point(542, 309));
		m_removeParamButton.setText("&Remove Filter");
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setLocation(new Point(541, 21));
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
							
				m_memberCategories = 0;
				
				for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
				{
					if (m_memberCategoryList.getItem(i).getChecked())
					{
						m_memberCategories += m_categoryArray[i].value();
					}
				}
				
				m_modifiers.reset();
				
				for (int i = 0; i < m_modifierList.getItemCount(); i++)
				{
					m_modifiers.setMask(m_modifierArray[i].value(), getTriStateBoolValue(m_modifierList.getItem(i)));
				}
				
				m_name = m_memberNameText.getText();
				m_anyParams = m_anyParamsCheckBox.getSelection();
				m_type.m_name = m_typeNameText.getText();				
				m_type.m_typeProps.reset();
				m_type.m_typeProps.setMask(Type.Property.ARRAY.value(), getTriStateBoolValue(m_typePropsList.getItem(m_arrayItem)));
				m_type.m_typeProps.setMask(Type.Property.TYPE_PARAM.value(), getTriStateBoolValue(m_typePropsList.getItem(m_typeParamItem)));
				
				m_cancel = false;
				m_shell.close();				
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(541, 61));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_modifierListLabel.setBounds(new Rectangle(206, 8, 55, 13));
		m_modifierListLabel.setText("Mo&difiers:");
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			if (i < m_modifierArray.length - 1)
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString().toLowerCase());
			else
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString());
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
		m_typeGroup.setBounds(new Rectangle(333, 17, 196, 143));
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(12, 97, 87, 13));
		m_typeNameLabel.setText("Type Name Filter:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(12, 111, 172, 19));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsLabel.setBounds(new Rectangle(12, 24, 87, 13));
		m_typePropsLabel.setText("Type Properties:");
		m_typePropsList = new Table(m_typeGroup, SWT.BORDER | SWT.CHECK);
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(12, 39, 119, 43));
		new TableItem(m_typePropsList, 0, m_arrayItem).setText("ARRAY");
		new TableItem(m_typePropsList, 0, m_typeParamItem).setText("TYPE PARAMETER");
		m_typePropsList.addSelectionListener(new TriStateCheckBoxAdapter());
	}
}
