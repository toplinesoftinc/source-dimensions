package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;
import com.sourcedimensions.client.model.*;


public class DelegateDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="4,-11"
	private Group m_typeGroup;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_typePropsLabel;
	private Table m_typePropsList;
	private Table m_paramsTable;
	private Label m_paramsLabel;
	private Button m_okButton;
	private Button m_cancelButton;
	private Button m_addParamButton;
	private Button m_editParamButton;
	private Button m_removeParamButton;
	private Button m_anyParamsCheckBox;
	protected Type m_type = new Type();  //  @jve:decl-index=0:
	protected List<Parameter> m_paramList = new ArrayList<Parameter>();  //  @jve:decl-index=0:
	protected boolean m_anyParams;
	
	public DelegateDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	public DelegateDialog(Display display, Shell parent, Type type, boolean anyParams, List<Parameter> paramList)
	{
		m_display = display;
		createShell(parent);

		m_typeNameText.setText(type.getName());
		m_anyParamsCheckBox.setSelection(anyParams);

		setParamControls();
		
		if (!anyParams)
		{
			m_paramsTable.removeAll();
			m_paramList.clear();
			
			for (Parameter param : paramList)
			{
				m_paramList.add(param);
				
				TableItem item = new TableItem(m_paramsTable, SWT.NONE);
				
				item.setText(0, param.positionToString());
				item.setText(1, param.modifiersToString());
				item.setText(2, param.getType().typePropsToString());
				item.setText(3, param.getType().getName());
				item.setText(4, param.getName());			
			}
			
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
		
		m_shell.setText("Delegate");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		createTypeGroup();
		m_shell.setSize(new Point(502, 437));
		m_shell.setLayout(null);
		m_paramsLabel = new Label(getShell(), SWT.NONE);
		m_paramsTable = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		m_paramsTable.setEnabled(false);
		m_paramsTable.setHeaderVisible(true);
		m_paramsTable.setLocation(new Point(13, 189));
		m_paramsTable.setLinesVisible(true);
		m_paramsTable.setSize(new Point(470, 206));
		m_paramsLabel.setBounds(new Rectangle(13, 174, 100, 13));
		m_paramsLabel.setText("Parameter &Filter List:");
		m_paramsTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_paramsTable.getSelectionIndex() != -1)
				{
					editParams();
				}
			}			
		});
		
		m_addParamButton = new Button(getShell(), SWT.NONE);
		m_addParamButton.setLocation(new Point(205, 160));
		m_addParamButton.setText("A&dd Filter...");
		m_addParamButton.setEnabled(false);
		m_addParamButton.setSize(new Point(88, 25));
		m_addParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				ParamDialog dialog = new ParamDialog(PlatformUI.getWorkbench().getDisplay(), getShell());
				
				dialog.open();
				
				if (!dialog.isCancelled())
				{
					Parameter param = dialog.getParam();
					TableItem item = new TableItem(m_paramsTable, SWT.NONE);
					
					m_paramList.add(param);
					item.setText(0, param.positionToString());
					item.setText(1, param.modifiersToString());
					item.setText(2, param.getType().typePropsToString());
					item.setText(3, param.getType().getName());
					item.setText(4, param.getName());
				}		
			}
		});
		m_editParamButton = new Button(getShell(), SWT.NONE);
		m_editParamButton.setLocation(new Point(300, 160));
		m_editParamButton.setText("&Edit Filter...");
		m_editParamButton.setEnabled(false);
		m_editParamButton.setSize(new Point(88, 25));
		m_editParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editParams();
			}
		});
		m_removeParamButton = new Button(getShell(), SWT.NONE);
		m_removeParamButton.setLocation(new Point(395, 160));
		m_removeParamButton.setText("&Remove Filter");
		m_removeParamButton.setEnabled(false);
		m_removeParamButton.setSize(new Point(88, 25));
		m_removeParamButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_paramsTable.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(getShell(), "Selection", "Please select parameter");
				}
				else
				{
					if (MessageDialog.openQuestion(getShell(), "Deletion confirmation", 
						"Are you sure you want to delete selected parameter?"))
					{
						m_paramsTable.remove(sel);
						m_paramList.remove(sel);
					}
				}					
			}
		});
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setLocation(new Point(395, 19));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
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
				
				m_type.setName(val);
				m_anyParams = m_anyParamsCheckBox.getSelection();
				
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
		m_cancelButton.setLocation(new Point(395, 54));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_anyParamsCheckBox = new Button(getShell(), SWT.CHECK);
		m_anyParamsCheckBox.setBounds(new Rectangle(15, 141, 98, 16));
		m_anyParamsCheckBox.setText("An&y Parameters");
		m_anyParamsCheckBox.setSelection(true);
		m_anyParamsCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				setParamControls();
			}
		});
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		TableColumn column = new TableColumn(m_paramsTable, SWT.LEFT, 0);
		double width = m_paramsTable.getBounds().width - 2 * m_paramsTable.getBorderWidth();
		column.setText("Positions");
		column.setWidth((int) (0.13 * width));
		column.setResizable(true);
		column = new TableColumn(m_paramsTable, SWT.LEFT, 1);
		column.setText("Modifiers");
		column.setWidth((int) (0.17 * width));
		column.setResizable(true);
		column = new TableColumn(m_paramsTable, SWT.LEFT, 2);
		column.setText("Type Props");
		column.setWidth((int) (0.2 * width));
		column.setResizable(true);
		column = new TableColumn(m_paramsTable, SWT.LEFT, 3);
		column.setText("Type");
		column.setWidth((int) (0.24 * width));
		column.setResizable(true);
		column = new TableColumn(m_paramsTable, SWT.LEFT, 4);
		column.setText("Name");
		column.setResizable(true);
		column.setWidth((int) (0.26 * width));
		
		super.createShell(parent);
		
		m_typeNameText.setFocus();
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createTypeGroup() 
	{
		m_typeGroup = new Group(getShell(), SWT.NONE);
		m_typeGroup.setLayout(null);
		m_typeGroup.setText("&Return Type");
		m_typeGroup.setBounds(new Rectangle(13, 13, 351, 118));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsList = new Table(m_typeGroup, SWT.HIDE_SELECTION | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setText("Type Name &Filter:");
		m_typeNameLabel.setLocation(new Point(132, 22));
		m_typeNameLabel.setSize(new Point(91, 13));
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(132, 37, 205, 19));
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
	
	public Type getType()
	{
		return m_type;
	}
	
	public List<Parameter> getParamList()
	{
		return m_paramList;
	}
	
	public List<Parameter> getParams()
	{
		return m_paramList;
	}	
	
	public boolean getAnyParams()
	{
		return m_anyParams;
	}
	
	protected void setParamControls()
	{
		boolean checked = m_anyParamsCheckBox.getSelection();

		m_addParamButton.setEnabled(!checked);
		m_editParamButton.setEnabled(!checked);
		m_removeParamButton.setEnabled(!checked);
		m_paramsTable.setEnabled(!checked);
	}
	
	protected void editParams()
	{
		int sel = m_paramsTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(getShell(), "Selection", "Please select parameter");			
		}
		else
		{		
			ParamDialog dialog = new ParamDialog(PlatformUI.getWorkbench().getDisplay(), getShell(), m_paramList.get(sel));
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				Parameter param = dialog.getParam();
				
				m_paramList.set(sel, param);
				TableItem item = m_paramsTable.getItem(sel);
				
				item.setText(0, param.positionToString());
				item.setText(1, param.modifiersToString());
				item.setText(2, param.getType().typePropsToString());
				item.setText(3, param.getType().getName());
				item.setText(4, param.getName());
			}
		}
	}
}
