package com.sourcedimensions.client.forms;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.views.ProjectView;

public class BaseTypeFilterDialog 
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="3,2"
	private Display m_display;	
	private String m_value;
	private Button m_classCheckBox = null;
	private Button m_interfaceCheckBox = null;
	private Button m_intTypeCheckBox = null;
	private Label m_typeCategoryLabel = null;
	private Label m_baseTypeNameFilterLabel = null;
	private Text m_baseTypeFilterText = null;
	private Combo m_integralTypeCombo = null;
	private Button m_okButton = null;
	private Button m_cancelButton = null;
	
	public BaseTypeFilterDialog(Display display, Shell parent, String value)
	{
		m_display = display;
		m_value = value;		
		createShell(parent, value);
	}	
	
	private void createShell(Shell parent, String value)
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Base Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(333, 163));
		m_shell.setToolTipText("");
		m_shell.setSize(new Point(334, 170));
		m_shell.setLayout(null);
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_classCheckBox = new Button(m_shell, SWT.CHECK);
		m_classCheckBox.setBounds(new Rectangle(16, 28, 52, 16));
		m_classCheckBox.setText("CLASS");
		m_interfaceCheckBox = new Button(m_shell, SWT.CHECK);
		m_interfaceCheckBox.setBounds(new Rectangle(16, 48, 77, 16));
		m_interfaceCheckBox.setText("INTERFACE");
		m_intTypeCheckBox = new Button(m_shell, SWT.CHECK);
		m_intTypeCheckBox.setBounds(new Rectangle(16, 69, 97, 16));
		m_intTypeCheckBox.setText("INTEGRAL TYPE");
		m_intTypeCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean checked = m_intTypeCheckBox.getSelection();
				
				m_classCheckBox.setEnabled(!checked);
				m_interfaceCheckBox.setEnabled(!checked);
				m_baseTypeFilterText.setVisible(checked);
				m_integralTypeCombo.setVisible(checked);
			}
		});
		m_typeCategoryLabel.setBounds(new Rectangle(15, 8, 90, 13));
		m_typeCategoryLabel.setText("&Type Categories:");
		m_baseTypeNameFilterLabel = new Label(m_shell, SWT.NONE);
		m_baseTypeNameFilterLabel.setBounds(new Rectangle(124, 26, 113, 13));
		m_baseTypeNameFilterLabel.setText("&Base Type Name Filter:");
		m_baseTypeFilterText = new Text(m_shell, SWT.BORDER);
		m_baseTypeFilterText.setBounds(new Rectangle(124, 42, 188, 19));
		createIntegralTypeCombo();
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setLocation(new Point(52, 99));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String val;
				
				if (m_intTypeCheckBox.getSelection())
					val = m_integralTypeCombo.getText();
				else
					val = m_baseTypeFilterText.getText();
				
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
				
				m_value = val;
				m_shell.close();
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(181, 99));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				m_intTypeCheckBox.setVisible(false);
				break;
				
			case CSHARP11:
			case CSHARP20:
				m_intTypeCheckBox.setVisible(true);
		}

		Control[] widgets = m_shell.getChildren();

		for (int i = 0; i < widgets.length; i++) 
		{ 
			widgets[i].addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.keyCode == SWT.ESC)
						cancelClose();
				}
				
				public void keyReleased(KeyEvent e)
				{				
				}
			});
		}
		
		Util.centerWindow(m_shell, parent);		
	}

	public void open()
	{
		m_shell.open();

		while (!m_shell.isDisposed()) 
		{
			if (!m_display.readAndDispatch()) 
				m_display.sleep();
		}		
	}	

	public String getValue()
	{
		return m_value;
	}
	
	private void cancelClose()
	{
		m_value = null;
		m_shell.close();
	}
	
	private void createIntegralTypeCombo() 
	{
		m_integralTypeCombo = new Combo(m_shell, SWT.READ_ONLY);
		m_integralTypeCombo.setVisible(false);
		m_integralTypeCombo.setText("");
		m_integralTypeCombo.setVisibleItemCount(9);
		m_integralTypeCombo.setBounds(new Rectangle(124, 39, 186, 21));
		m_integralTypeCombo.add("sbyte");
		m_integralTypeCombo.add("byte");
		m_integralTypeCombo.add("short");
		m_integralTypeCombo.add("ushort");
		m_integralTypeCombo.add("int");
		m_integralTypeCombo.add("uint");
		m_integralTypeCombo.add("long");
		m_integralTypeCombo.add("ulong");
		m_integralTypeCombo.add("char");		
	}
}
