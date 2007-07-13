package com.sourcedimensions.client.forms;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.model.*;

public class ParamDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-37,-94"
	private Group m_paramPositionGroup;
	private Button m_listPosRadioButton;
	private Button m_rangeRadioButton;
	private Button m_noMoreRadioButton;
	private Button m_notLessRadioButton;
	private Button m_anyRadioButton;
	private Spinner m_listPosSpinner;
	private Spinner m_rangeMinSpinner;
	private Spinner m_rangeMaxSpinner;
	private Spinner m_noMorePosSpinner;
	private Spinner m_notLessPosSpinner;
	private Button m_addToListPosButton;
	private List m_posList;
	private Button m_removePosButton;
	private Parameter m_param = new Parameter();  //  @jve:decl-index=0:
	private Set<Integer> m_posListSet = new TreeSet<Integer>();
	private Group m_typeGroup;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_typePropsLabel;
	private Table m_typePropsList;
	private Label m_paramNameFilterLabel;
	private Text m_paramNameFilterText;
	private Label m_paramModifiersLabel;
	private Table m_paramModifiersList;
	private Button m_okButton;
	private Button m_cancelButton;
	private Button m_exactRadioButton;
	private Spinner m_exactPosSpinner;
	private Button m_quantitativeCheckBox;
	
	private static Parameter.Modifier[] m_javaModifiers = new Parameter.Modifier[]
    {
		Parameter.Modifier.FINAL,
		Parameter.Modifier.VAR_ARITY
    };
	
	private static Parameter.Modifier[] m_csharpModifiers = new Parameter.Modifier[]
    {
		Parameter.Modifier.OUT,
		Parameter.Modifier.REF,
		Parameter.Modifier.PARAMS
    };

	private static Type.Property[] m_javaTypeProps = new Type.Property[]
	{
		Type.Property.ARRAY,
		Type.Property.TYPEPARAM,
    };

	public ParamDialog(Shell parent, Parameter param)
	{
		createShell(parent);

		m_anyRadioButton.setSelection(false);
		
		switch (param.getPosType())
		{
			case LIST:
				m_listPosRadioButton.setSelection(true);
				
				m_posListSet.clear();
				for (Integer i : param.getPosList())
				{
					m_posListSet.add(i);
				}
				
				positionListChanged();
				break;

			case GREATEREQ:
				m_notLessRadioButton.setSelection(true);
				m_notLessPosSpinner.setSelection(param.getPosMin());				
				break;
								
			case LESSEQ:
				m_noMoreRadioButton.setSelection(true);
				m_noMorePosSpinner.setSelection(param.getPosMax());
				break;
				
			case BETWEEN:
				m_rangeRadioButton.setSelection(true);
				m_rangeMinSpinner.setSelection(param.getPosMin());
				m_rangeMaxSpinner.setSelection(param.getPosMax());
				break;				
				
			case EXACT:
				m_exactRadioButton.setSelection(true);
				m_exactPosSpinner.setSelection(param.getPosValue());
				break;
				
			case ANY:
				m_anyRadioButton.setSelection(true);
		}
		
		positionChanged();

		m_quantitativeCheckBox.setSelection(param.getQuantitative());
		
		quantitativeSelectionChanged();
		
		for (int i = 0; i < m_typePropsList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_typePropsList.getItem(i), 
				param.getType().getTypeProps().getMask(getTypePropArray()[i].value()));
		}
				
		m_typeNameText.setText(param.getType().getName());

		for (int i = 0; i < m_paramModifiersList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_paramModifiersList.getItem(i),
				param.getModifiers().getMask(getModifierArray()[i].value()));
		}
				
		m_paramNameFilterText.setText(param.getName());
	}
	
	public ParamDialog(Shell parent)
	{
		createShell(parent);
	}	
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);		
		
		m_shell.setText("Parameter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		createParamPositionGroup();
		createTypeGroup();
		m_shell.setSize(new Point(396, 453));
		m_shell.setLayout(null);
		m_paramModifiersLabel = new Label(getShell(), SWT.NONE);
		m_paramModifiersList = new Table(getShell(), SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_paramModifiersList.addSelectionListener(new TriStateCheckBoxAdapter());
		m_paramNameFilterLabel = new Label(getShell(), SWT.NONE);
		m_paramNameFilterLabel.setText("Parameter Name Filter:");
		m_paramNameFilterLabel.setLocation(new Point(130, 340));
		m_paramNameFilterLabel.setSize(new Point(120, 13));
		m_paramNameFilterText = new Text(getShell(), SWT.BORDER);
		m_paramNameFilterText.setSize(new Point(230, 19));
		m_paramNameFilterText.setLocation(new Point(130, 354));
		m_paramModifiersList.setHeaderVisible(false);
		m_paramModifiersList.setLinesVisible(false);
		m_paramModifiersList.setBounds(new Rectangle(11, 354, 104, 53));
		m_quantitativeCheckBox = new Button(getShell(), SWT.CHECK);
		for (Parameter.Modifier m : getModifierArray())
		{
			new TableItem(m_paramModifiersList, 0).setText(m.toString().replace("_", ".").toLowerCase());
		}
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setLocation(new Point(289, 17));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{
				if (!m_quantitativeCheckBox.getSelection())
				{
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
	
					val = m_paramNameFilterText.getText().trim();
					
					if (val.length() == 0)
					{
						MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Parameter Name Filter");					
						return;
					}
	
					try
					{
						Pattern.compile(val);
					}
					catch(PatternSyntaxException ex)
					{
						MessageDialog.openError(m_shell, "Incorrect input",
							"Pattern for parameter name \"" + val + "\" has the following error: " + ex.getMessage());
						return;
					}
				}
				
				if (m_listPosRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.LIST);
					m_param.getPosList().clear();
					
					for (Integer i : m_posListSet)
					{
						m_param.getPosList().add(i);
					}	
				}
				else if (m_notLessRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.GREATEREQ);
					m_param.setPosMin(m_notLessPosSpinner.getSelection());
				}
				else if (m_noMoreRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.LESSEQ);
					m_param.setPosMax(m_noMorePosSpinner.getSelection());
				}
				else if (m_exactRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.EXACT);
					m_param.setPosValue(m_exactPosSpinner.getSelection());
				}
				else if (m_rangeRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.BETWEEN);
					m_param.setPosMin(m_rangeMinSpinner.getSelection());
					m_param.setPosMax(m_rangeMaxSpinner.getSelection());
				}
				else if (m_anyRadioButton.getSelection())
				{
					m_param.setPosType(Parameter.Position.ANY);
				}
								
				if (m_quantitativeCheckBox.getSelection())
				{
					m_param.getType().setName("");
					m_param.getType().getTypeProps().reset();
					
					m_param.setName("");
					m_param.getModifiers().reset();
				}
				else
				{
					m_param.getType().setName(m_typeNameText.getText());
					
					m_param.getType().getTypeProps().reset();
	
					for (int i = 0; i < m_typePropsList.getItemCount(); i++)
					{
						m_param.getType().getTypeProps().setMask(getTypePropArray()[i].value(), 
							getTriStateBoolValue(m_typePropsList.getItem(i)));
					}
					
					m_param.setName(m_paramNameFilterText.getText());
					
					m_param.getModifiers().reset();
					
					for (int i = 0; i < m_paramModifiersList.getItemCount(); i++)
					{
						m_param.getModifiers().setMask(getModifierArray()[i].value(), 
							getTriStateBoolValue(m_paramModifiersList.getItem(i)));
					}
				}

				m_param.setQuantitative(m_quantitativeCheckBox.getSelection());
				
				m_cancel = false;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setLocation(new Point(289, 52));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_quantitativeCheckBox.setBounds(new Rectangle(130, 384, 106, 16));
		m_quantitativeCheckBox.setText("&Quantitative filter");
		m_quantitativeCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				quantitativeSelectionChanged();
			}
		});
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		m_paramModifiersLabel.setText("Parameter Modifiers:");
		m_paramModifiersLabel.setSize(new Point(108, 13));
		m_paramModifiersLabel.setLocation(new Point(11, 340));
		
		super.createShell(parent);
		
		m_typeNameText.setFocus();
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createParamPositionGroup() 
	{
		m_paramPositionGroup = new Group(getShell(), SWT.SHADOW_NONE);
		m_paramPositionGroup.setText("Position/Number of parameters");
		m_paramPositionGroup.setLayout(null);
		m_paramPositionGroup.setBounds(new Rectangle(12, 12, 268, 190));
		m_listPosRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_listPosRadioButton.setText("List");
		m_listPosRadioButton.setLocation(new Point(12, 24));
		m_listPosRadioButton.setSize(new Point(49, 16));
		m_notLessRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_noMoreRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_listPosRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_rangeRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_rangeRadioButton.setText("Range");
		m_rangeRadioButton.setLocation(new Point(12, 108));
		m_rangeRadioButton.setSize(new Point(58, 16));
		m_rangeRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_noMoreRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				positionChanged();
			}
		});
		m_notLessRadioButton.setText("Not Less");
		m_notLessRadioButton.setSize(new Point(60, 16));
		m_notLessRadioButton.setLocation(new Point(12, 52));
		m_exactRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_notLessRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_anyRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_anyRadioButton.setSelection(true);
		m_anyRadioButton.setSize(new Point(47, 16));
		m_anyRadioButton.setLocation(new Point(12, 164));
		m_anyRadioButton.setText("Any");
		m_anyRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_listPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_listPosSpinner.setMinimum(0);
		m_listPosSpinner.setDigits(0);
		m_listPosSpinner.setPageIncrement(1);
		m_listPosSpinner.setEnabled(false);
		m_listPosSpinner.setSelection(0);
		m_listPosSpinner.setBounds(new Rectangle(77, 23, 49, 17));
		m_addToListPosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_posList = new List(m_paramPositionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		m_removePosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_notLessPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_noMorePosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMinSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMinSpinner.setMinimum(0);
		m_rangeMinSpinner.setPageIncrement(1);
		m_rangeMinSpinner.setEnabled(false);
		m_rangeMinSpinner.setSelection(0);
		m_rangeMinSpinner.setBounds(new Rectangle(77, 107, 49, 17));
		m_rangeMaxSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMaxSpinner.setPageIncrement(1);
		m_rangeMaxSpinner.setMinimum(0);
		m_rangeMaxSpinner.setEnabled(false);
		m_rangeMaxSpinner.setSelection(0);
		m_rangeMaxSpinner.setBounds(new Rectangle(133, 107, 49, 17));
		m_exactRadioButton.setSize(new Point(51, 16));
		m_exactRadioButton.setLocation(new Point(12, 136));
		m_exactRadioButton.setText("Exact");
		m_exactRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_exactPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_exactPosSpinner.setLocation(new Point(77, 136));
		m_exactPosSpinner.setPageIncrement(1);
		m_exactPosSpinner.setMinimum(0);
		m_exactPosSpinner.setEnabled(false);
		m_exactPosSpinner.setSelection(0);
		m_exactPosSpinner.setSize(new Point(49, 17));
		m_noMorePosSpinner.setPageIncrement(1);
		m_noMorePosSpinner.setSelection(0);
		m_noMorePosSpinner.setEnabled(false);
		m_noMorePosSpinner.setMinimum(0);
		m_noMorePosSpinner.setBounds(new Rectangle(77, 79, 49, 17));
		m_notLessPosSpinner.setPageIncrement(1);
		m_notLessPosSpinner.setSelection(0);
		m_notLessPosSpinner.setEnabled(false);
		m_notLessPosSpinner.setMinimum(0);
		m_notLessPosSpinner.setBounds(new Rectangle(77, 51, 49, 17));
		m_addToListPosButton.setText(">>");
		m_addToListPosButton.setEnabled(false);
		m_addToListPosButton.setLocation(new Point(133, 23));
		m_addToListPosButton.setSize(new Point(54, 19));
		m_addToListPosButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				m_posListSet.add(m_listPosSpinner.getSelection());
				positionListChanged();
			}
		});
		m_posList.setBounds(new Rectangle(192, 24, 61, 130));
		m_posList.setEnabled(false);
		m_removePosButton.setBounds(new Rectangle(156, 61, 31, 23));
		m_removePosButton.setEnabled(false);
		m_removePosButton.setText("X");
		m_removePosButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_removePosButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				for (int i : m_posList.getSelectionIndices())
				{
					m_posListSet.remove(Integer.parseInt(m_posList.getItem(i)));
				}
				
				m_posList.remove(m_posList.getSelectionIndices());
			}
		});
		m_noMoreRadioButton.setBounds(new Rectangle(12, 80, 62, 16));
		m_noMoreRadioButton.setText("No More");
	}

	protected void positionChanged()
	{
		for (Control ctrl : m_paramPositionGroup.getChildren())
		{
			if (ctrl instanceof Button)
			{
				if ((ctrl.getStyle() & SWT.RADIO) == 0)
				{
					ctrl.setEnabled(false);
				}
			}
			else
			{
				ctrl.setEnabled(false);
			}
		}
		
		if (m_listPosRadioButton.getSelection())
		{
			m_listPosSpinner.setEnabled(true);
			m_addToListPosButton.setEnabled(true);
			m_removePosButton.setEnabled(true);
			m_posList.setEnabled(true);			
		}
		else if (m_notLessRadioButton.getSelection())
		{
			m_notLessPosSpinner.setEnabled(true);
		}
		else if (m_noMoreRadioButton.getSelection())
		{
			m_noMorePosSpinner.setEnabled(true);
		}
		else if (m_exactRadioButton.getSelection())
		{
			m_exactPosSpinner.setEnabled(true);
		}
		else if (m_rangeRadioButton.getSelection())
		{
			m_rangeMinSpinner.setEnabled(true);
			m_rangeMaxSpinner.setEnabled(true);
		}		
	}
	
	public Parameter getParam()
	{
		return m_param;
	}
	
	public void positionListChanged()
	{
		m_posList.removeAll();
		Iterator<Integer> iter = m_posListSet.iterator();
		
		while (iter.hasNext())
		{
			m_posList.add(iter.next().toString());
		}
	}

	private void createTypeGroup() 
	{
		m_typeGroup = new Group(getShell(), SWT.NONE);
		m_typeGroup.setLayout(null);
		m_typeGroup.setText("&Type/Return Type");
		m_typeGroup.setBounds(new Rectangle(12, 211, 365, 116));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsList = new Table(m_typeGroup, SWT.HIDE_SELECTION | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		m_typePropsList.addSelectionListener(new TriStateCheckBoxAdapter());
		for (Type.Property prop : getTypePropArray())
		{
			new TableItem(m_typePropsList, 0).setText(prop.toString().replace("_", " "));
		}							
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setText("Type Name &Filter:");
		m_typeNameLabel.setLocation(new Point(131, 21));
		m_typeNameLabel.setSize(new Point(91, 13));
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(131, 35, 217, 19));
		m_typePropsLabel.setBounds(new Rectangle(11, 21, 88, 13));
		m_typePropsLabel.setText("Type Properties:");
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(11, 35, 111, 70));
	}
	
	protected Type.Property[] getTypePropArray()
	{
  		switch (ProjectView.getProject().language())
  		{
  			case JAVA14:
  			case JAVA15:
  				return m_javaTypeProps;
  				 				
  			case CSHARP11:
  			case CSHARP20:
  				return Type.Property.values();

  			default:
  				return null;
  		}		
	}
	
	protected Parameter.Modifier[] getModifierArray()
	{
  		switch (ProjectView.getProject().language())
  		{
  			case JAVA14:
  			case JAVA15:
  				return m_javaModifiers;
  				 				
  			case CSHARP11:
  			case CSHARP20:
  				return m_csharpModifiers;

  			default:
  				return null;
  		}
		
	}
	
	protected void quantitativeSelectionChanged()
	{
		boolean sel = !m_quantitativeCheckBox.getSelection();
		
		m_typePropsList.setEnabled(sel);
		m_typeNameText.setEnabled(sel);
		m_paramModifiersList.setEnabled(sel);
		m_paramNameFilterText.setEnabled(sel);
	}
}
