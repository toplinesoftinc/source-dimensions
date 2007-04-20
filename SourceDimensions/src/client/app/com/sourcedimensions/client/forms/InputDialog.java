package com.sourcedimensions.client.forms;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.*;

public class InputDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Button m_cancelButton;
	private Button m_okButton;
	private Text m_textValue;
	private Label m_textLabel;
	private Validator m_validator;  //  @jve:decl-index=0:
	private static String m_value;  //  @jve:decl-index=0:


	public InputDialog(Shell parent, String title, 
		String label, String text, Validator validator)
	{
		m_value = text;
		m_validator = validator;
		createShell(parent, title, label);
	}	
	
	private void createShell(Shell parent, String title, String label)
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		if (parent != null)
			m_shell.setParent(parent);		
		m_shell.setText(title);
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(298, 133));
		m_shell.setLayout(null);
		m_textLabel = new Label(m_shell, SWT.NONE);
		m_textLabel.setBounds(new Rectangle(13, 18, 266, 14));
		m_textLabel.setText(label);
		m_textLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));		
		m_textValue = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_textValue.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_textValue.setSize(new Point(266, 18));
		m_textValue.setLocation(new Point(13, 33));
		if (m_value != null)
			m_textValue.setText(m_value);
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setToolTipText("Cancel");
		m_okButton.setText("O&k");
		m_okButton.setLocation(new Point(47, 68));
		m_okButton.setSize(new Point(88, 25));
		m_okButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_okButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (m_validator != null && !m_validator.validate(m_shell, m_textValue.getText()))
				{
					return;
				}
		
				m_value = m_textValue.getText();
				m_shell.close();				
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(158, 68));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				cancelClose();
			}
		});

		m_shell.setDefaultButton(m_okButton);
		super.createShell(parent);
	}

	public String getValue()
	{
		return m_value;
	}
	
	protected void cancelClose()
	{
		m_value = null;
		m_shell.close();
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
	
	public static abstract class Validator
	{
		public abstract boolean validate(Shell shell, String value);
	}
	
	
	public static class MandatoryFieldValidator extends Validator
	{
		protected String m_message;
		
		public MandatoryFieldValidator()
		{
			m_message = "Please enter text";
		}
		
		public MandatoryFieldValidator(String message)
		{
			m_message = message;
		}
		
		public boolean validate(Shell shell, String value)
		{
			if (value.trim().length() == 0)
			{
				MessageDialog.openError(shell, "Incorrect input", m_message);
				return false;
			}
			else
				return true;
		}
	}
}
