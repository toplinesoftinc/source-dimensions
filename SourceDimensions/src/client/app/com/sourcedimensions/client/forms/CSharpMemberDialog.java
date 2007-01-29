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
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-95,-73"
	private Label m_memberCategoryLabel = null;
	private Table m_memberCategoryList = null;
	private Button m_allCategoriesButton = null;
	private Button m_allModifiersButton = null;
	private Label m_modifierListLabel = null;
	private Table m_modifierList = null;
	private MemberCategory[] m_categoryArray = 
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
		MemberCategory.OPERATOR
	};
	private Modifier[] m_modifierArray =
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
		Modifier.UNSAFE
	};
	private Group m_typeGroup = null;
	private Label m_arrayTypeLabel = null;
	private Combo m_arrayTypeCombo = null;
	private Label m_typeParamLabel = null;
	private Combo m_typeParamCombo = null;
	private Label m_nullableLabel = null;
	private Combo m_nullableCombo = null;
	private Label m_pointerLabel = null;
	private Combo m_pointerCombo = null;
	private Label m_typeNameLabel = null;
	private Text m_typeNameText = null;
	private Label m_memberNameLabel = null;
	private Text m_memberNameText = null;
	private Button m_anyParamsCheckBox = null;
	private Label m_operatorsLabel = null;
	private Table m_operatorList = null;
	private Button m_okButton = null;
	private Button m_cancelButton = null;
	private Label m_paramsLabel = null;
	private Table m_paramsTable = null;
	private Button m_addParamButton = null;
	private Button m_editParamButton = null;
	private Button m_removeParamButton = null;
	private Button m_allOperatorsButton = null;

		
	public CSharpMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		checkAllItems(m_memberCategoryList);
		checkAllItems(m_modifierList);
		checkAllItems(m_operatorList);
	}
	
	public CSharpMemberDialog(Display display, Shell parent, String name, int categories, int modifiers, 
			Type type, boolean anyParams)
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
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			m_memberCategoryList.getItem(i).setChecked((categories & m_categoryArray[i].value()) != 0);
		}
	
		categorySelectionChanged();
		
		for (int i = 0; i < m_modifierList.getItemCount(); i++)
		{
			m_modifierList.getItem(i).setChecked((modifiers & m_modifierArray[i].value()) != 0);
		}
	}
	
	private void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Member Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(638, 575));
		m_shell.setLayout(null);
		m_memberCategoryLabel = new Label(getShell(), SWT.NONE);
		m_memberCategoryLabel.setText("Member Cate&gories:");
		m_memberCategoryLabel.setLocation(new Point(17, 8));
		m_memberCategoryLabel.setSize(new Point(100, 13));
		m_memberCategoryList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_memberCategoryList.setHeaderVisible(false);
		m_memberCategoryList.setLinesVisible(false);
		m_memberCategoryList.setBounds(new Rectangle(17, 22, 122, 220));
		m_memberCategoryList.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				categorySelectionChanged();
			}
		});
		m_allCategoriesButton = new Button(getShell(), SWT.NONE);
		m_modifierListLabel = new Label(getShell(), SWT.NONE);
		m_modifierList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		for (int i = 0; i < m_categoryArray.length; i++)
		{
			new TableItem(m_memberCategoryList, 0, i).setText(m_categoryArray[i].toString().replace("_", " "));
		}				
		m_allCategoriesButton.setLocation(new Point(17, 245));
		m_allCategoriesButton.setSize(new Point(80, 23));
		m_allCategoriesButton.setText("All &Categories");
		m_allCategoriesButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				checkAllItems(m_memberCategoryList);
			}
		});
		m_allModifiersButton = new Button(getShell(), SWT.NONE);
		m_allModifiersButton.setLocation(new Point(151, 245));
		m_allModifiersButton.setText("All &Modifiers");
		m_allModifiersButton.setSize(new Point(80, 23));
		m_allModifiersButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				checkAllItems(m_modifierList);
			}
		});
		m_modifierListLabel.setBounds(new Rectangle(152, 8, 53, 13));
		m_modifierListLabel.setText("Mo&difiers:");
		m_modifierList.setHeaderVisible(false);
		m_modifierList.setLinesVisible(false);
		m_modifierList.setBounds(new Rectangle(151, 22, 107, 220));
		createTypeGroup();
		m_memberNameLabel = new Label(getShell(), SWT.NONE);
		m_memberNameLabel.setText("&Member Name Filter:");
		m_memberNameLabel.setLocation(new Point(272, 208));
		m_memberNameLabel.setSize(new Point(106, 13));
		m_memberNameText = new Text(getShell(), SWT.BORDER);
		m_memberNameText.setBounds(new Rectangle(272, 223, 204, 19));
		m_operatorsLabel = new Label(getShell(), SWT.NONE);
		m_operatorsLabel.setBounds(new Rectangle(500, 87, 65, 13));
		m_operatorsLabel.setText("&Operators:");
		m_operatorList = new Table(getShell(), SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_operatorList.setHeaderVisible(false);
		m_operatorList.setLinesVisible(false);
		m_operatorList.setBounds(new Rectangle(500, 101, 116, 403));
		m_allOperatorsButton = new Button(getShell(), SWT.NONE);
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				enableParamControls(!m_anyParamsCheckBox.getSelection());
			}
		});
		m_paramsLabel = new Label(getShell(), SWT.NONE);
		m_paramsLabel.setBounds(new Rectangle(18, 311, 102, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable = new Table(getShell(), SWT.BORDER);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setBounds(new Rectangle(18, 326, 470, 206));
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setLocation(new Point(210, 298));
		m_addParamButton.setText("A&dd Filter...");
		m_addParamButton.setEnabled(false);
		m_addParamButton.setSize(new Point(88, 25));
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setLocation(new Point(305, 298));
		m_editParamButton.setText("&Edit Filter...");
		m_editParamButton.setEnabled(false);
		m_editParamButton.setSize(new Point(88, 25));
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setLocation(new Point(400, 298));
		m_removeParamButton.setText("&Remove Filter");
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.setLocation(new Point(528, 13));
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
		m_cancelButton.setLocation(new Point(528, 48));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_anyParamsCheckBox.setBounds(new Rectangle(272, 255, 104, 13));
		m_anyParamsCheckBox.setText("An&y Parameters");
		m_anyParamsCheckBox.setSelection(true);
		m_allOperatorsButton.setText("All Operators");
		m_allOperatorsButton.setSize(new Point(80, 23));
		m_allOperatorsButton.setLocation(new Point(536, 509));
		m_allOperatorsButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				checkAllItems(m_operatorList);
			}
		});
	
		for (Operator op : Operator.values())
		{
			new TableItem(m_operatorList, 0).setText(op.getName());
		}
		
		for (int i = 0; i < m_modifierArray.length; i++)
		{
			new TableItem(m_modifierList, 0, i).setText(m_modifierArray[i].toString().toLowerCase());
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
		
		for (int i = 0; i < m_memberCategoryList.getItemCount(); i++)
		{
			switch (m_categoryArray[i])
			{
				case CONSTRUCTOR:
				case METHOD:
				case INDEXER_GET:
				case INDEXER_SET:
					if (m_memberCategoryList.getItem(i).getChecked())
					{
						func = true;
					}
					break;

				case OPERATOR:
					if (m_memberCategoryList.getItem(i).getChecked())
					{
						func = true;
						oper = true;
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
		
		m_operatorList.setEnabled(oper);
		m_allOperatorsButton.setEnabled(oper);
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

	
	public enum Operator
	{
		UNARY_PLUS(0),
		UNARY_MINUS(1),
		NOT(2),
		COMPLEMENT(3),
		INCREMENT(4),
		DECREMENT(5),
		TRUE(6),
		FALSE(7),
		PLUS(8),
		MINUS(9),
		MULT(10),
		DIVISION(11),
		REMINDER(12),
		BITWISE_AND(13),
		BITWISE_OR(14),
		BITWISE_XOR(15),
		LSHIFT(16),
		RSHIFT(17),
		EQ(18),
		NOT_EQ(19),
		GT(20),
		LESS(21),
		GT_EQ(22),
		LESS_EQ(23),
		IMPL_CONV(24),
		EXPL_CONV(25);
		
		Operator(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
		
		public String getName()
		{
			return names[value];
		}
		
		protected String[] names =
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
			"Expl.Conversion"
		};
	}
}
