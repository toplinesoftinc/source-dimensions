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
import org.eclipse.ui.PlatformUI;


public class JavaMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-19,-38"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;

	private final static int m_arrayItem = 0;
	private final static int m_typeParamItem = 1;
	
	private final static MemberCategory[] m_categoryArray = 
	{
		MemberCategory.FIELD,
		MemberCategory.LOCAL_VAR,
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
	private Group m_throwGroup;
	private Button m_anyThrowCheckBox;
	private org.eclipse.swt.widgets.List m_throwFilterList;
	private Button m_addThrowFilterButton;
	private Button m_editThrowFilterButton;
	private Button m_removeThrowFilterButton;
	private boolean m_anyThrow;
	private List<String> m_throwList = new ArrayList<String>();
	
	public JavaMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		setAllItems(m_modifierList, TriStateBoolean.EITHER);
	}
	
	public JavaMemberDialog(Display display, Shell parent, String name, int categories, TriStateMask modifiers, 
		Type type, boolean anyParams, List<Parameter> paramList, boolean anyThrow, List<String> throwList)
	{
		m_display = display;
		createShell(parent);
		m_memberNameText.setText(name);
		setTriStateBoolValue(m_typePropsList.getItem(m_arrayItem), type.m_typeProps.getMask(Type.Property.ARRAY.value()));
		setTriStateBoolValue(m_typePropsList.getItem(m_typeParamItem), type.m_typeProps.getMask(Type.Property.TYPE_PARAM.value()));
		m_typeNameText.setText(type.m_name);
		m_anyParamsCheckBox.setSelection(anyParams);
		enableParamControls(!anyParams);
		
		if (!anyParams)
		{
			populateParamList(m_paramsTable, paramList);
		}
		
		m_anyThrowCheckBox.setSelection(anyThrow);
		enableThrowControls(!anyThrow);
		
		if (!anyThrow)
		{
			for (String filter : throwList)
			{
				m_throwList.add(filter);
				m_throwFilterList.add(filter);
			}
		}
		
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
		
	protected void createShell(Shell parent)
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		m_shell.setText("Member Filter");
		
		if (parent != null)
			m_shell.setParent(parent);
				
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setLayout(null);
		m_shell.setSize(new Point(643, 542));
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
		m_modifierList.setLocation(new Point(147, 22));
		m_modifierList.setSize(new Point(115, 192));
		m_modifierList.addSelectionListener(new TriStateCheckBoxAdapter());
		m_modifierList.addSelectionListener(new AllItemsAdapter());
		createTypeGroup();
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("&Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(19, 209));
		m_memberNameLabel.setSize(new Point(108, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(19, 223, 243, 19));
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.setBounds(new Rectangle(19, 256, 101, 16));
		m_anyParamsCheckBox.setText("Any &Parameters");
		m_anyParamsCheckBox.setSelection(true);
		m_anyThrowCheckBox = new Button(getShell(), SWT.CHECK);
		createThrowGroup();
		m_anyParamsCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				enableParamControls(!m_anyParamsCheckBox.getSelection());
			}
		});
		m_paramsLabel = new Label(getShell(), SWT.NONE);
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsLabel.setLocation(new Point(17, 295));
		m_paramsLabel.setSize(new Point(106, 13));
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(17, 309, 515, 189));
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		column.setWidth((int)(0.13 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Positions");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 1);
		column.setWidth((int)(0.17 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 2);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type Properties");		
		column = new TableColumn(m_paramsTable, SWT.LEFT, 3);
		column.setWidth((int)(0.24 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type");
		column = new TableColumn(m_paramsTable, SWT.LEFT, 4);
		column.setWidth((int)(0.26 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		m_paramsTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_paramsTable.getSelectionIndex() != -1)
				{
					editParam(m_paramsTable);
				}
			}			
		});				
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setBounds(new Rectangle(541, 308, 88, 25));
		m_addParamButton.setEnabled(false);
		m_addParamButton.setText("A&dd Filter...");
		m_addParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				addParam(m_paramsTable);
			}
		});
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setBounds(new Rectangle(541, 350, 88, 25));
		m_editParamButton.setEnabled(false);
		m_editParamButton.setText("&Edit Filter...");
		m_editParamButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editParam(m_paramsTable);
			}
		});
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_removeParamButton.setLocation(new Point(541, 392));
		m_removeParamButton.setText("&Remove Filter");
		m_removeParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				deleteParam(m_paramsTable);
			}
		});
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setLocation(new Point(541, 23));
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

				m_memberCategories = 0;
				
				for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
				{
					if (m_memberCategoryList.getItem(i).getChecked())
					{
						m_memberCategories += m_categoryArray[i].value();
					}
				}				

				String val = m_typeNameText.getText().trim();
				
				if (val.length() == 0 && ((m_memberCategories & ~MemberCategory.CONSTRUCTOR.value()) != 0))
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");					
					return;
				}

				try
				{
					Pattern.compile(val);
				}
				catch(PatternSyntaxException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input",
						"Pattern for type name \"" + val + "\" has the following error: " + ex.getMessage());
					return;
				}				

				if (m_memberNameText.getEnabled())
				{
					val = m_memberNameText.getText().trim();
					
					if (val.length() == 0 && ((m_memberCategories & ~MemberCategory.CONSTRUCTOR.value()) != 0))
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
							"Pattern for member name \"" + val + "\" has the following error: " + ex.getMessage());
						return;
					}
				}
				else
					m_memberNameText.setText("");
							
				m_modifiers.reset();
				
				for (int i = 0; i < m_modifierList.getItemCount(); i++)
				{
					m_modifiers.setMask(m_modifierArray[i].value(), getTriStateBoolValue(m_modifierList.getItem(i)));
				}
				
				m_name = m_memberNameText.getText();
				m_anyParams = m_anyParamsCheckBox.getSelection();
				m_anyThrow = m_anyThrowCheckBox.getSelection();
				m_type.m_name = m_typeNameText.getText();				
				m_type.m_typeProps.reset();
				m_type.m_typeProps.setMask(Type.Property.ARRAY.value(), getTriStateBoolValue(m_typePropsList.getItem(m_arrayItem)));
				m_type.m_typeProps.setMask(Type.Property.TYPE_PARAM.value(), getTriStateBoolValue(m_typePropsList.getItem(m_typeParamItem)));
				
				if (!m_paramsTable.getEnabled())
				{
					m_paramList.clear();
				}
				
				if (!m_throwFilterList.getEnabled())
				{
					m_throwFilterList.removeAll();
				}
				
				m_cancel = false;
				m_shell.close();				
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(541, 60));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_modifierListLabel.setText("Mo&difiers:");
		m_modifierListLabel.setLocation(new Point(147, 8));
		m_modifierListLabel.setSize(new Point(55, 13));
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			if (i < m_modifierArray.length - 1)
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString().toLowerCase());
			else
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString());
		}
		
		m_memberNameText.setFocus();
		m_shell.setDefaultButton(m_okButton);
		m_anyThrowCheckBox.setBounds(new Rectangle(173, 256, 88, 16));
		m_anyThrowCheckBox.setSelection(true);
		m_anyThrowCheckBox.setText("Any Thro&w");
		m_anyThrowCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				enableThrowControls(!m_anyThrowCheckBox.getSelection());
			}
		});
		
		super.createShell(parent);
	}

	private void enableParamControls(boolean enable)
	{
		m_paramsTable.setEnabled(enable);
		m_addParamButton.setEnabled(enable);
		m_editParamButton.setEnabled(enable);
		m_removeParamButton.setEnabled(enable);		
	}

	private void enableThrowControls(boolean enable)
	{
		m_throwFilterList.setEnabled(enable);
		m_addThrowFilterButton.setEnabled(enable);
		m_editThrowFilterButton.setEnabled(enable);
		m_removeThrowFilterButton.setEnabled(enable);
	}
	
	private void categorySelectionChanged()
	{
		int cat = 0;
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			if (m_memberCategoryList.getItem(i).getChecked())
			{
				cat += m_categoryArray[i].value();
			}
		}				
		
		boolean func = (cat & (MemberCategory.CONSTRUCTOR.value() | MemberCategory.METHOD.value())) != 0;
		boolean noname = (cat != 0) && ((cat & ~MemberCategory.CONSTRUCTOR.value()) == 0);
		
		m_anyParamsCheckBox.setEnabled(func);
		
		if (func)
		{
			enableParamControls(!m_anyParamsCheckBox.getSelection());
			enableThrowControls(!m_anyThrowCheckBox.getSelection());
		}
		else
		{
			enableParamControls(func);
			enableThrowControls(func);
		}
		
		m_memberNameText.setEnabled(!noname);
	}
	
	public boolean getAnyThrow()
	{
		return m_anyThrow;
	}
	
	public List<String> getThrowList()
	{
		return m_throwList;
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createTypeGroup() 
	{
		m_typeGroup = new Group(getShell(), SWT.SHADOW_NONE);
		m_typeGroup.setText("&Type/Return type");
		m_typeGroup.setLocation(new Point(273, 18));
		m_typeGroup.setSize(new Point(259, 135));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsList = new Table(m_typeGroup, SWT.BORDER | SWT.CHECK);
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(12, 88, 87, 13));
		m_typeNameLabel.setText("Type Name Filter:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(12, 102, 235, 19));
		m_typePropsLabel.setBounds(new Rectangle(11, 18, 87, 13));
		m_typePropsLabel.setText("Type Properties:");
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(11, 33, 119, 43));
		new TableItem(m_typePropsList, 0, m_arrayItem).setText("ARRAY");
		new TableItem(m_typePropsList, 0, m_typeParamItem).setText("TYPE PARAMETER");
		m_typePropsList.addSelectionListener(new TriStateCheckBoxAdapter());
	}

	private void editThrowFilter()
	{
		int sel = m_throwFilterList.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
		}
		else
		{
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
				"Filter", "Throw Filter:", m_throwFilterList.getItem(sel), new ThrowFilterValidator());
			
			dialog.open();
			String val = dialog.getValue();
			
			if (val != null)
			{
				m_throwFilterList.setItem(sel, val);
				m_throwList.set(sel, val);
			}							
		}				
	}
	
	private void createThrowGroup() 
	{
		m_throwGroup = new Group(getShell(), SWT.NONE);
		m_throwGroup.setLayout(null);
		m_throwGroup.setText("Throw Filter");
		m_throwGroup.setEnabled(true);
		m_throwGroup.setBounds(new Rectangle(274, 161, 258, 135));
		m_throwFilterList = new org.eclipse.swt.widgets.List(m_throwGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		m_throwFilterList.setBounds(new Rectangle(12, 19, 129, 103));
		m_throwFilterList.setEnabled(false);
		m_throwFilterList.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_throwFilterList.getSelectionIndex() != -1)
				{
					editThrowFilter();
				}
			}			
		});				
		
		m_addThrowFilterButton = new Button(m_throwGroup, SWT.NONE);
		m_addThrowFilterButton.setLocation(new Point(158, 19));
		m_addThrowFilterButton.setText("Add Filter...");
		m_addThrowFilterButton.setEnabled(false);
		m_addThrowFilterButton.setSize(new Point(88, 25));
		m_addThrowFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
					"Filter", "Throw Filter:", "", new ThrowFilterValidator());

				dialog.open();
				String val = dialog.getValue();
				
				if (val != null)
				{
					m_throwFilterList.add(val);
					m_throwList.add(val);
				}
			}
		});
		m_editThrowFilterButton = new Button(m_throwGroup, SWT.NONE);
		m_editThrowFilterButton.setLocation(new Point(158, 57));
		m_editThrowFilterButton.setText("Edit Filter...");
		m_editThrowFilterButton.setEnabled(false);
		m_editThrowFilterButton.setSize(new Point(88, 25));
		m_editThrowFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_throwFilterList.getSelectionIndex() != -1)
				{
					editThrowFilter();
				}
			}
		});
		m_removeThrowFilterButton = new Button(m_throwGroup, SWT.NONE);
		m_removeThrowFilterButton.setLocation(new Point(158, 95));
		m_removeThrowFilterButton.setText("Remove Filter");
		m_removeThrowFilterButton.setEnabled(false);
		m_removeThrowFilterButton.setSize(new Point(88, 25));
		m_removeThrowFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{
				int sel = m_throwFilterList.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(getShell(), "Selection", "Please select filter");
				}
				else
				{
					if (MessageDialog.openQuestion(getShell(), "Deletion confirmation", 
						"Are you sure you want to delete selected filter?"))
					{
						m_throwFilterList.remove(sel);
						m_throwList.remove(sel);
					}
				}
			}
		});
	}
	
	
	protected class ThrowFilterValidator extends InputDialog.MandatoryFieldValidator
	{
		public ThrowFilterValidator()
		{
			super("Please enter filter");
		}
		
		public boolean validate(Shell shell, String value)
		{
			if (!super.validate(shell, value))
			{
				return false;
			}
			else
			{
				try
				{
					Pattern.compile(value);
				}
				catch(PatternSyntaxException e)
				{
					MessageDialog.openError(shell, "Incorrect input",
						"Pattern \"" + value + "\" has the following error: " + e.getMessage());
					return false;
				}
				
				return true;
			}
		}
	}	
}
