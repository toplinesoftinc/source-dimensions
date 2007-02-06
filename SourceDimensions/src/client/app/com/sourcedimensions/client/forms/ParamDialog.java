package com.sourcedimensions.client.forms;

import java.util.Iterator;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;


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
	
	public ParamDialog(Display display, Shell parent)
	{
		m_display = display;
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
		createM_typeGroup();
		m_shell.setSize(new Point(396, 421));
		m_shell.setLayout(null);
		m_paramModifiersLabel = new Label(getShell(), SWT.NONE);
		m_paramModifiersList = new Table(getShell(), SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		m_paramNameFilterLabel = new Label(getShell(), SWT.NONE);
		m_paramNameFilterLabel.setText("Parameter Name Filter:");
		m_paramNameFilterLabel.setLocation(new Point(129, 344));
		m_paramNameFilterLabel.setSize(new Point(120, 13));
		m_paramNameFilterText = new Text(getShell(), SWT.BORDER);
		m_paramNameFilterText.setSize(new Point(238, 19));
		m_paramNameFilterText.setLocation(new Point(129, 358));
		m_paramModifiersList.setHeaderVisible(false);
		m_paramModifiersList.setLinesVisible(false);
		m_paramModifiersList.setBounds(new Rectangle(11, 324, 104, 53));
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setLocation(new Point(288, 17));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setLocation(new Point(288, 52));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_paramModifiersLabel.setText("Parameter Modifiers:");
		m_paramModifiersLabel.setSize(new Point(108, 13));
		m_paramModifiersLabel.setLocation(new Point(11, 310));
		
		super.createShell(parent);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createParamPositionGroup() 
	{
		m_paramPositionGroup = new Group(getShell(), SWT.SHADOW_NONE);
		m_paramPositionGroup.setText("Position");
		m_paramPositionGroup.setLayout(null);
		m_paramPositionGroup.setBounds(new Rectangle(12, 12, 263, 167));
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
		m_notLessRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_anyRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_anyRadioButton.setBounds(new Rectangle(12, 136, 47, 16));
		m_anyRadioButton.setSelection(true);
		m_anyRadioButton.setText("Any");
		m_anyRadioButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				positionChanged();
			}
		});
		m_listPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_listPosSpinner.setMinimum(1);
		m_listPosSpinner.setDigits(0);
		m_listPosSpinner.setPageIncrement(1);
		m_listPosSpinner.setEnabled(false);
		m_listPosSpinner.setBounds(new Rectangle(77, 23, 49, 17));
		m_addToListPosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_posList = new List(m_paramPositionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		m_removePosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_notLessPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_noMorePosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMinSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMinSpinner.setMinimum(1);
		m_rangeMinSpinner.setPageIncrement(1);
		m_rangeMinSpinner.setEnabled(false);
		m_rangeMinSpinner.setBounds(new Rectangle(77, 107, 49, 17));
		m_rangeMaxSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMaxSpinner.setPageIncrement(1);
		m_rangeMaxSpinner.setMinimum(1);
		m_rangeMaxSpinner.setEnabled(false);
		m_rangeMaxSpinner.setBounds(new Rectangle(133, 107, 49, 17));
		m_noMorePosSpinner.setPageIncrement(1);
		m_noMorePosSpinner.setEnabled(false);
		m_noMorePosSpinner.setMinimum(1);
		m_noMorePosSpinner.setBounds(new Rectangle(77, 79, 49, 17));
		m_notLessPosSpinner.setPageIncrement(1);
		m_notLessPosSpinner.setEnabled(false);
		m_notLessPosSpinner.setMinimum(1);
		m_notLessPosSpinner.setBounds(new Rectangle(77, 51, 49, 17));
		m_addToListPosButton.setText(">>");
		m_addToListPosButton.setEnabled(false);
		m_addToListPosButton.setLocation(new Point(133, 23));
		m_addToListPosButton.setSize(new Point(54, 19));
		m_addToListPosButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				m_param.m_posList.add(m_listPosSpinner.getSelection());
				positionListChanged();
			}
		});
		m_posList.setBounds(new Rectangle(192, 24, 61, 127));
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
					m_param.m_posList.remove(Integer.parseInt(m_posList.getItem(i)));
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
		Iterator<Integer> iter = m_param.m_posList.iterator();
		
		while (iter.hasNext())
		{
			m_posList.add(iter.next().toString());
		}
	}

	/**
	 * This method initializes m_typeGroup	
	 *
	 */
	private void createM_typeGroup() {
		m_typeGroup = new Group(getShell(), SWT.NONE);
		m_typeGroup.setLayout(null);
		m_typeGroup.setText("&Type/Return Type");
		m_typeGroup.setBounds(new Rectangle(12, 185, 365, 120));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsList = new Table(m_typeGroup, SWT.HIDE_SELECTION | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setText("Type Name &Filter:");
		m_typeNameLabel.setLocation(new Point(134, 74));
		m_typeNameLabel.setSize(new Point(91, 13));
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(134, 88, 219, 19));
		m_typePropsLabel.setBounds(new Rectangle(14, 25, 88, 13));
		m_typePropsLabel.setText("Type Properties:");
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(14, 39, 111, 68));
	}
}
