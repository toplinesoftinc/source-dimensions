package com.sourcedimensions.client.forms;

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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import com.sourcedimensions.client.model.*;
import com.sourcedimensions.client.model.TriStateBoolean;
import com.sourcedimensions.client.model.Modifier;
import com.sourcedimensions.client.model.Parameter;
import com.sourcedimensions.client.model.TriStateMask;
import com.sourcedimensions.client.model.Type;


public class CSharpMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-11,-16"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;
	private Label m_modifierListLabel;
	private Table m_modifierList;
	
	private final static MemberCategory[] m_categoryArray = 
	{
		MemberCategory.FIELD,
		MemberCategory.CONSTANT,
		MemberCategory.CONSTRUCTOR,
		MemberCategory.DESTRUCTOR,
		MemberCategory.METHOD,
		MemberCategory.ANONYMMETHOD,
		MemberCategory.PROPERTYGET,
		MemberCategory.PROPERTYSET,
		MemberCategory.EVENTADD,
		MemberCategory.EVENTREMOVE,
		MemberCategory.INDEXERGET,
		MemberCategory.INDEXERSET,
		MemberCategory.OPERATOR,
		MemberCategory.ALL
	};
	private final static Modifier[] m_modifierArray =
	{
		Modifier.NEW,
		Modifier.PUBLIC,
		Modifier.PROTECTED,
		Modifier.INTERNAL,
		Modifier.PRIVATE,		
		Modifier.STATIC,
		Modifier.VIRTUAL,
		Modifier.SEALED,
		Modifier.OVERRIDE,		
		Modifier.ABSTRACT,
		Modifier.EXTERN,
		Modifier.READONLY,
		Modifier.VOLATILE,
		Modifier.UNSAFE,
		Modifier.ALL
	};
	protected final static String[] m_operatorNames =
	{
		"+x",
		"-x",
		"!",
		"~",
		"++",
		"--",
		"true",
		"false",
		"x+y",
		"x-y",
		"*",
		"/",
		"%",
		"&",
		"|",
		"^",
		"<<",
		">>",
		"==",
		"!=",
		">",
		"<",
		">=",
		"<=",
		"Impl.Conversion",
		"Expl.Conversion",
		"ALL"
	};

	private Group m_typeGroup;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_memberNameLabel;
	private Text m_memberNameText;
	private Button m_anyParamsCheckBox;
	private Label m_operatorsLabel;
	private Table m_operatorList;
	private Button m_okButton;
	private Button m_cancelButton;
	private Label m_paramsLabel;
	private Table m_paramsTable;
	private Button m_addParamButton;
	private Button m_editParamButton;
	private Button m_removeParamButton;
	private Label m_typePropsLabel;
	private Table m_typePropsList;
	
	public CSharpMemberDialog(Shell parent)
	{
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		setAllItems(m_modifierList, TriStateBoolean.TRUE);
		checkAllItems(m_operatorList);
	}
	
	public CSharpMemberDialog(Shell parent, String name, int categories, TriStateMask modifiers, 
			Type type, boolean anyParams, int operators, List<Parameter> paramList)
	{
		createShell(parent);
		m_memberNameText.setText(name);
		m_typeNameText.setText(type.getName());
		m_anyParamsCheckBox.setSelection(anyParams);
		enableParamControls(!anyParams);
		
		if (!anyParams)
		{
			populateParamList(m_paramsTable, paramList);
		}
			
		for (int i = 0; i < m_operatorList.getItemCount(); i++)
		{
			m_operatorList.getItem(i).setChecked((operators & Operator.values()[i].value()) != 0);
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
		
		for (int i = 0; i < m_typePropsList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_typePropsList.getItem(i), 
					type.getTypeProps().getMask(Type.Property.values()[i].value()));
		}		
	}
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Member Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(638, 563));
		m_shell.setLayout(null);
		m_memberCategoryLabel = new Label(getShell(), SWT.NONE);
		m_memberCategoryLabel.setText("Member Cate&gories:");
		m_memberCategoryLabel.setLocation(new Point(17, 8));
		m_memberCategoryLabel.setSize(new Point(100, 13));
		m_memberCategoryList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_memberCategoryList.setHeaderVisible(false);
		m_memberCategoryList.setLinesVisible(false);
		m_memberCategoryList.setBounds(new Rectangle(17, 22, 122, 233));
		m_memberCategoryList.addSelectionListener(new AllItemsAdapter());
		m_memberCategoryList.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				categorySelectionChanged();
			}
		});
		m_modifierListLabel = new Label(getShell(), SWT.NONE);
		m_modifierList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		for (int i = 0; i < m_categoryArray.length; i++)
		{
			new TableItem(m_memberCategoryList, 0, i).setText(m_categoryArray[i].toString());
		}				
		m_modifierListLabel.setBounds(new Rectangle(152, 8, 53, 13));
		m_modifierListLabel.setText("Mo&difiers:");
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setBounds(new Rectangle(151, 22, 107, 233));
		m_modifierList.addSelectionListener(new TriStateCheckBoxAdapter());
		m_modifierList.addSelectionListener(new AllItemsAdapter());
		createTypeGroup();
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("&Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(269, 194));
		m_memberNameLabel.setSize(new Point(106, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(269, 209, 218, 19));
		m_operatorsLabel = new Label(getShell(), SWT.NONE);
		m_operatorsLabel.setText("&Operators:");
		m_operatorsLabel.setLocation(new Point(499, 87));
		m_operatorsLabel.setSize(new Point(65, 13));
		m_operatorList = new Table(getShell(), SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_operatorList.setHeaderVisible(false);
		m_operatorList.setLinesVisible(false);
		m_operatorList.setBounds(new Rectangle(499, 101, 116, 417));
		m_operatorList.addSelectionListener(new AllItemsAdapter());
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				enableParamControls(!m_anyParamsCheckBox.getSelection());
			}
		});
		m_paramsLabel = new Label(getShell(), SWT.NONE);
		m_paramsLabel.setBounds(new Rectangle(17, 297, 102, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(17, 312, 470, 206));
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		column.setWidth((int)(0.13 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Pos/Num");
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
		m_addParamButton.setLocation(new Point(209, 284));
		m_addParamButton.setText("A&dd Filter...");
		m_addParamButton.setEnabled(false);
		m_addParamButton.setSize(new Point(88, 25));
		m_addParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				addParam(m_paramsTable);
			}
		});
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setLocation(new Point(304, 284));
		m_editParamButton.setText("&Edit Filter...");
		m_editParamButton.setEnabled(false);
		m_editParamButton.setSize(new Point(88, 25));
		m_editParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editParam(m_paramsTable);
			}
		});
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setLocation(new Point(399, 284));
		m_removeParamButton.setText("&Remove Filter");
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_removeParamButton.addSelectionListener(
			new RemoveFilterAdapter(m_shell, m_paramsTable, m_paramList));
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.setLocation(new Point(528, 22));
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
				
				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");					
					return;
				}

				try
				{
					Type.validateTypeName(val);
				}
				catch (Type.EmptyNameSectionException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input", "Type Name section cannot be empty (like \"com//abc\")");
					return;					
				}
				catch (PatternSyntaxException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input",
							"Type Name pattern \"" + ex.getPattern() + "\" has the following error: " + ex.getMessage());			
					return;
				}
				
				if (m_memberNameText.getEnabled())
				{
					val = m_memberNameText.getText().trim();
					
					if (val.length() == 0 && ((m_memberCategories & ~(MemberCategory.CONSTRUCTOR.value() | 
								MemberCategory.DESTRUCTOR.value() | MemberCategory.ANONYMMETHOD.value())) != 0))
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
	
					m_name = m_memberNameText.getText();
				}
				else
				{
					m_name = "";
				}

				m_modifiers.reset();
				
				for (int i = 0; i < m_modifierList.getItemCount(); i++)
				{
					m_modifiers.setMask(m_modifierArray[i].value(), getTriStateBoolValue(m_modifierList.getItem(i)));
				}
				
				m_operators = 0;
				
				if (m_operatorList.getEnabled())
				{
					for (int i = 0; i < m_operatorList.getItemCount(); i++)
					{
						if (m_operatorList.getItem(i).getChecked())
						{
							m_operators += Operator.values()[i].value();
						}
					}
				}
				
				m_anyParams = m_anyParamsCheckBox.getSelection();
				m_type.setName(m_typeNameText.getText());
				m_type.getTypeProps().reset();
				
				for (int i = 0; i < m_typePropsList.getItemCount(); i++)
				{
					m_type.getTypeProps().setMask(Type.Property.values()[i].value(), getTriStateBoolValue(m_typePropsList.getItem(i)));
				}
				
				if (!m_paramsTable.getEnabled())
				{
					m_paramList.clear();
				}
				
				m_cancel = false;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setLocation(new Point(528, 57));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_anyParamsCheckBox.setBounds(new Rectangle(269, 242, 104, 13));
		m_anyParamsCheckBox.setText("An&y Parameters");
		m_anyParamsCheckBox.setSelection(true);
	
		for (Operator op : Operator.values())
		{
			new TableItem(m_operatorList, 0).setText(getOperatorName(op));
		}
		
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString());
		}
				
		m_shell.setDefaultButton(m_okButton);
		super.createShell(parent);
		
		m_memberNameText.setFocus();
	}
	
	protected Shell getShell()
	{
		return m_shell;
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
		
		boolean oper_only = (cat != 0) && ((cat & ~MemberCategory.OPERATOR.value()) == 0);
		boolean oper = (cat & MemberCategory.OPERATOR.value()) != 0;
		
		boolean param = (cat & (MemberCategory.CONSTRUCTOR.value() 
				| MemberCategory.METHOD.value() | MemberCategory.ANONYMMETHOD.value() 
				| MemberCategory.INDEXERGET.value() | MemberCategory.INDEXERSET.value() 
				| MemberCategory.OPERATOR.value())) != 0; 

		boolean noname = (cat != 0) && ((cat & ~(MemberCategory.CONSTRUCTOR.value() 
				| MemberCategory.DESTRUCTOR.value() | MemberCategory.ANONYMMETHOD.value())) == 0);
		
		m_memberNameText.setEnabled(!oper_only && !noname);
		m_operatorList.setEnabled(oper);
		m_anyParamsCheckBox.setEnabled(param);
		
		if (param)
		{
			enableParamControls(!m_anyParamsCheckBox.getSelection());
		}
		else
		{
			enableParamControls(param);
		}
	}
	
	
	private void enableParamControls(boolean enable)
	{
		m_paramsTable.setEnabled(enable);
		m_addParamButton.setEnabled(enable);
		m_editParamButton.setEnabled(enable);
		m_removeParamButton.setEnabled(enable);		
	}	
	
	private void createTypeGroup() 
	{
		m_typeGroup = new Group(getShell(), SWT.NONE);
		m_typeGroup.setText("&Type/Return Type");
		m_typeGroup.setLayout(null);
		m_typeGroup.setLocation(new Point(270, 17));
		m_typeGroup.setSize(new Point(218, 159));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsList = new Table(m_typeGroup, SWT.HIDE_SELECTION | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(11, 113, 91, 13));
		m_typeNameLabel.setText("Type Name &Filter:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(11, 128, 196, 19));
		m_typePropsLabel.setBounds(new Rectangle(11, 23, 88, 13));
		m_typePropsLabel.setText("Type Properties:");
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(11, 37, 111, 68));
		m_typePropsList.addSelectionListener(new TriStateCheckBoxAdapter());
		Enum[] values = Type.Property.values();
		for (int i = 0; i < values.length; i++)
		{
			new TableItem(m_typePropsList, 0, i).setText(values[i].toString());
		}					
	}

	public static String getOperatorName(Operator op)
	{
		return m_operatorNames[(int)(Math.log(op.value())/Math.log(2.0))];
	}
}
