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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import com.sourcedimensions.client.TriStateBoolean;


public class CSharpMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="0,-4"
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
		MemberCategory.PROPERTY_GET,
		MemberCategory.PROPERTY_SET,
		MemberCategory.EVENT_ADD,
		MemberCategory.EVENT_REMOVE,
		MemberCategory.INDEXER_GET,
		MemberCategory.INDEXER_SET,
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
	private Label m_arrayTypeLabel;
	private Combo m_arrayTypeCombo;
	private Label m_typeParamLabel;
	private Combo m_typeParamCombo;
	private Label m_nullableLabel;
	private Combo m_nullableCombo;
	private Label m_pointerLabel;
	private Combo m_pointerCombo;
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
	public CSharpMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		checkAllItems(m_modifierList);
		checkAllItems(m_operatorList);
	}
	
	public CSharpMemberDialog(Display display, Shell parent, String name, int categories, TriStateMask modifiers, 
			Type type, boolean anyParams, int operators)
	{
		m_display = display;
		createShell(parent);
		m_memberNameText.setText(name);
		m_arrayTypeCombo.select(type.m_isArray.value());
		m_typeParamCombo.select(type.m_isTypeParam.value());
		m_nullableCombo.select(type.m_isNullable.value());
		m_pointerCombo.select(type.m_isPointer.value());
		m_typeNameText.setText(type.m_name);
		m_anyParamsCheckBox.setSelection(anyParams);
		enableParamControls(!anyParams);
		
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
	}
	
	private void createShell(Shell parent) 
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
			new TableItem(m_memberCategoryList, 0, i).setText(m_categoryArray[i].toString().replace("_", " "));
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
		m_memberNameLabel.setLocation(new Point(272, 194));
		m_memberNameLabel.setSize(new Point(106, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(272, 209, 217, 19));
		m_operatorsLabel = new Label(getShell(), SWT.NONE);
		m_operatorsLabel.setBounds(new Rectangle(499, 86, 65, 13));
		m_operatorsLabel.setText("&Operators:");
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
		m_paramsTable = new Table(getShell(), SWT.BORDER);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(17, 312, 470, 206));
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setLocation(new Point(209, 284));
		m_addParamButton.setText("A&dd Filter...");
		m_addParamButton.setEnabled(false);
		m_addParamButton.setSize(new Point(88, 25));
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setLocation(new Point(304, 284));
		m_editParamButton.setText("&Edit Filter...");
		m_editParamButton.setEnabled(false);
		m_editParamButton.setSize(new Point(88, 25));
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setLocation(new Point(399, 284));
		m_removeParamButton.setText("&Remove Filter");
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.setLocation(new Point(528, 19));
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
				
				
				if (m_memberNameText.getEnabled())
				{
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
	
					m_name = m_memberNameText.getText();
				}
				else
				{
					m_name = "";
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
				m_type.m_name = m_typeNameText.getText();
				m_type.m_isArray = TriStateBoolean.values()[m_arrayTypeCombo.getSelectionIndex()];
				m_type.m_isTypeParam = TriStateBoolean.values()[m_typeParamCombo.getSelectionIndex()];
				m_type.m_isNullable = TriStateBoolean.values()[m_nullableCombo.getSelectionIndex()];
				m_type.m_isPointer = TriStateBoolean.values()[m_pointerCombo.getSelectionIndex()];
				
				m_cancel = false;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setLocation(new Point(528, 54));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_anyParamsCheckBox.setBounds(new Rectangle(272, 242, 104, 13));
		m_anyParamsCheckBox.setText("An&y Parameters");
		m_anyParamsCheckBox.setSelection(true);
	
		for (Operator op : Operator.values())
		{
			new TableItem(m_operatorList, 0).setText(getOperatorName(op));
		}
		
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			if (i < m_modifierArray.length - 1)
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString().toLowerCase());
			else
				new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString());
		}
				
		m_shell.setDefaultButton(m_okButton);
		postCreate(parent);
		
		m_memberNameText.setFocus();
	}

	protected Shell getShell()
	{
		return m_shell;
	}

	private void categorySelectionChanged()
	{
		boolean func = false;
		boolean oper = false;
		boolean oper_only = true;
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			if (m_memberCategoryList.getItem(i).getChecked())
			{
				switch (m_categoryArray[i])
				{
					case CONSTRUCTOR:
					case METHOD:
					case INDEXER_GET:
					case INDEXER_SET:
						func = true;
						oper_only = false;
						break;
	
					case OPERATOR:
						func = true;
						oper = true;
						break;
						
					default:
						oper_only = false;
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
		
		m_memberNameText.setEnabled(!oper_only);
		m_operatorList.setEnabled(oper);
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
		m_typeGroup.setBounds(new Rectangle(272, 16, 217, 162));
		m_arrayTypeLabel = new Label(m_typeGroup, SWT.NONE);
		m_arrayTypeLabel.setText("&Array Type:");
		m_arrayTypeLabel.setBounds(new Rectangle(9, 19, 69, 13));
		createArrayTypeCombo();
		m_typeParamLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeParamLabel.setBounds(new Rectangle(9, 66, 92, 13));
		m_typeParamLabel.setText("Type &Parameter:");
		createTypeParamCombo();
		m_nullableLabel = new Label(m_typeGroup, SWT.NONE);
		m_nullableLabel.setBounds(new Rectangle(113, 19, 50, 13));
		m_nullableLabel.setText("&Nullable:");
		createNullableCombo();
		m_pointerLabel = new Label(m_typeGroup, SWT.NONE);
		m_pointerLabel.setBounds(new Rectangle(113, 66, 61, 13));
		m_pointerLabel.setText("Poin&ter:");
		createPointerCombo();
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(9, 117, 91, 13));
		m_typeNameLabel.setText("Type Name &Filter:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(9, 132, 196, 19));
	}

	private void createArrayTypeCombo() 
	{
		m_arrayTypeCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_arrayTypeCombo.setLocation(new Point(8, 33));
		m_arrayTypeCombo.setSize(new Point(93, 21));
		
		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_arrayTypeCombo.add(m_triStateText[i], i);
		}

		m_arrayTypeCombo.select(0);		
	}

	private void createTypeParamCombo() 
	{
		m_typeParamCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_typeParamCombo.setLocation(new Point(9, 81));
		m_typeParamCombo.setSize(new Point(93, 21));
		
		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_typeParamCombo.add(m_triStateText[i], i);
		}

		m_typeParamCombo.select(0);
		
	}

	private void createNullableCombo() 
	{
		m_nullableCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_nullableCombo.setLocation(new Point(113, 33));
		m_nullableCombo.setSize(new Point(93, 21));
		
		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_nullableCombo.add(m_triStateText[i], i);
		}

		m_nullableCombo.select(0);
		
	}

	private void createPointerCombo() 
	{
		m_pointerCombo = new Combo(m_typeGroup, SWT.READ_ONLY);
		m_pointerCombo.setBounds(new Rectangle(113, 81, 93, 21));
		
		for (int i = 0; i < m_triStateText.length; i++)
		{
			m_pointerCombo.add(m_triStateText[i], i);
		}

		m_pointerCombo.select(0);		
	}
	
	public static String getOperatorName(Operator op)
	{
		return m_operatorNames[(int)(Math.log(op.value())/Math.log(2.0))];
	}
}
