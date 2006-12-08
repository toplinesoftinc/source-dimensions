package com.sourcedimensions.client.forms;

import org.codehaus.xfire.fault.XFireFault;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.*;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.ws.consumer.WSConsumer;
import com.sourcedimensions.ws.provider.IWebService;
import org.eclipse.swt.graphics.Image;

public class LoginDialog
{

	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,9"
	private Label m_userNameLabel;
	private Text m_userNameText;
	private Label m_passwordLabel;
	private Text m_passwordText;
	private Button m_loginButton;
	private Button m_cancelButton;

	private Display m_display;  //  @jve:decl-index=0:
	private static String m_sessionID;
	
	public LoginDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(display, parent);
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
	
	/**
	 * This method initializes m_shell
	 */
	private void createShell(Display display, Shell parent) 
	{
		m_shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		if (parent != null)
			m_shell.setParent(parent);
		m_shell.setText("Login");
		m_shell.setLayout(null);
		m_shell.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_shell.setVisible(false);
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(293, 163));
		m_userNameLabel = new Label(m_shell, SWT.NONE);
		m_userNameLabel.setText("User name:");
		m_userNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_userNameLabel.setBounds(new Rectangle(16, 24, 78, 16));
		m_userNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_passwordLabel = new Label(m_shell, SWT.NONE);
		m_passwordLabel.setBounds(new Rectangle(17, 59, 68, 16));
		m_passwordLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_passwordLabel.setText("Password:");
		m_userNameText.setBounds(new Rectangle(96, 21, 170, 19));
		m_userNameText.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_passwordText = new Text(m_shell, SWT.BORDER | SWT.PASSWORD | SWT.LEFT);
		m_passwordText.setBounds(new Rectangle(96, 56, 170, 19));
		m_passwordText.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_loginButton = new Button(m_shell, SWT.NONE);
		m_loginButton.setBounds(new Rectangle(41, 96, 88, 25));
		m_loginButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_loginButton.setSelection(true);
		m_loginButton.setToolTipText("Login");
		m_loginButton.setText("Login");
		m_shell.setDefaultButton(m_loginButton);
		m_loginButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				WSConsumer consumer = new WSConsumer();
				
				if (m_userNameText.getText().trim().length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input", "Please enter user name");
					return;
				}

				try
				{
					Object[] params = new Object[] {m_userNameText.getText(), m_passwordText.getText()};
					m_sessionID = (String)consumer.loginToServer(m_display, m_shell, "login", params);
				}
				catch (XFireFault fault)
				{
					String title = "Error", message = fault.getMessage();
					
					if (fault.getRole() != null)
					{
						if (fault.getRole().equals(IWebService.FaultValues.LOGIN_FAILED.name()))
						{
							title = "Login failure";
							message = "User name or password is incorrect. Please try again.";
						}
					}
					
					MessageDialog.openError(m_shell, title, message);
					
					return;
				}
				catch (Exception ex)
				{
					MessageDialog.openError(m_shell, "Error", ex.getMessage());
					return;
				}
				
				if (!consumer.wasCancelled())
					m_shell.close();

			}
		});

		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setText("Cancel");
		m_cancelButton.setLocation(new Point(153, 96));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		
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
		
		m_display = display;
		Util.centerWindow(m_shell, parent);
	}

	protected void cancelClose()
	{
		m_sessionID = null;
		m_shell.close();		
	}
	
	public static String getSessionID()
	{
		return m_sessionID;
	}	
}
