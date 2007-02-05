package com.sourcedimensions.client.forms;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.List;


public class ParamDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-18,-38"
	private Group m_paramPositionGroup;
	private Button m_listPosRadioButton;
	private Button m_rangeRadioButton = null;
	private Button m_noMoreRadioButton = null;
	private Button m_noLessRadioButton = null;
	private Button m_anyRadioButton = null;
	private Spinner m_listPosSpinner = null;
	private Spinner m_rangeMinSpinner = null;
	private Spinner m_rangeMaxSpinner = null;
	private Spinner m_noMorePosSpinner = null;
	private Spinner m_noLessPosSpinner = null;
	private Button m_addToListPosButton = null;
	private List m_posList = null;
	private Button m_removePosButton = null;
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
		m_shell.setSize(new Point(402, 290));
		m_shell.setLayout(null);
		
		super.createShell(parent);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}

	private void createParamPositionGroup() 
	{
		m_paramPositionGroup = new Group(getShell(), SWT.SHADOW_NONE | SWT.BORDER);
		m_paramPositionGroup.setText("Position");
		m_paramPositionGroup.setLayout(null);
		m_paramPositionGroup.setBounds(new Rectangle(14, 14, 264, 156));
		m_listPosRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_listPosRadioButton.setText("List");
		m_listPosRadioButton.setLocation(new Point(12, 18));
		m_listPosRadioButton.setSize(new Point(49, 16));
		m_rangeRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_rangeRadioButton.setText("Range");
		m_rangeRadioButton.setLocation(new Point(12, 102));
		m_rangeRadioButton.setSize(new Point(47, 16));
		m_noMoreRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_noMoreRadioButton.setBounds(new Rectangle(12, 74, 62, 16));
		m_noMoreRadioButton.setText("No More");
		m_noLessRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_noLessRadioButton.setText("No Less");
		m_noLessRadioButton.setLocation(new Point(12, 46));
		m_noLessRadioButton.setSize(new Point(53, 16));
		m_anyRadioButton = new Button(m_paramPositionGroup, SWT.RADIO);
		m_anyRadioButton.setBounds(new Rectangle(12, 130, 47, 16));
		m_anyRadioButton.setText("Any");
		m_listPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_listPosSpinner.setMinimum(1);
		m_listPosSpinner.setDigits(0);
		m_listPosSpinner.setPageIncrement(1);
		m_listPosSpinner.setBounds(new Rectangle(77, 17, 49, 17));
		m_rangeMinSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMinSpinner.setMinimum(1);
		m_rangeMinSpinner.setPageIncrement(1);
		m_rangeMinSpinner.setBounds(new Rectangle(77, 101, 49, 17));
		m_rangeMaxSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_rangeMaxSpinner.setPageIncrement(1);
		m_rangeMaxSpinner.setMinimum(1);
		m_rangeMaxSpinner.setBounds(new Rectangle(133, 101, 49, 17));
		m_noMorePosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_noMorePosSpinner.setPageIncrement(1);
		m_noMorePosSpinner.setMinimum(1);
		m_noMorePosSpinner.setBounds(new Rectangle(77, 73, 49, 17));
		m_noLessPosSpinner = new Spinner(m_paramPositionGroup, SWT.BORDER);
		m_noLessPosSpinner.setPageIncrement(1);
		m_noLessPosSpinner.setMinimum(1);
		m_noLessPosSpinner.setBounds(new Rectangle(77, 45, 49, 17));
		m_addToListPosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_addToListPosButton.setBounds(new Rectangle(133, 17, 54, 17));
		m_addToListPosButton.setText(">>");
		m_posList = new List(m_paramPositionGroup, SWT.BORDER);
		m_posList.setBounds(new Rectangle(191, 17, 57, 101));
		m_removePosButton = new Button(m_paramPositionGroup, SWT.NONE);
		m_removePosButton.setBounds(new Rectangle(158, 58, 29, 23));
		m_removePosButton.setText("X");
	}

}
