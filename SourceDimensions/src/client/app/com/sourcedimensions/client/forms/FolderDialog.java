package com.sourcedimensions.client.forms;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.*;
import com.sourcedimensions.client.Util;

public class FolderDialog
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Button m_cancelButton = null;
	private Button m_OkButton = null;
	private Text m_folderNameText = null;
	private Label m_folderNameLabel = null;

	private Display m_display;  //  @jve:decl-index=0:
	private static String m_folderName;  //  @jve:decl-index=0:
	
	public void open()
	{
		m_shell.open();

		while (!m_shell.isDisposed()) 
		{
			if (!m_display.readAndDispatch()) 
				m_display.sleep();
		}		
	}
	
	
	private void createShell(Shell parent)
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		if (parent != null)
			m_shell.setParent(parent);		
		m_shell.setText("Folder");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(298, 138));
		m_shell.setLayout(null);
		m_folderNameLabel = new Label(m_shell, SWT.NONE);
		m_folderNameLabel.setBounds(new Rectangle(13, 16, 85, 16));
		m_folderNameLabel.setText("Folder name:");
		m_folderNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));		
		m_folderNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_folderNameText.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_folderNameText.setSize(new Point(266, 19));
		m_folderNameText.setLocation(new Point(13, 33));
		if (m_folderName != null)
			m_folderNameText.setText(m_folderName);
		m_OkButton = new Button(m_shell, SWT.NONE);
		m_OkButton.setToolTipText("Cancel");
		m_OkButton.setText("Ok");
		m_OkButton.setLocation(new Point(47, 73));
		m_OkButton.setSize(new Point(88, 25));
		m_OkButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_OkButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (m_folderNameText.getText().trim().length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input", "Please enter folder name");
					return;
				}
		
				m_folderName = m_folderNameText.getText();
				m_shell.close();				
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setText("Cancel");
		m_cancelButton.setLocation(new Point(158, 73));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				cancelClose();
			}
		});

		m_shell.setDefaultButton(m_OkButton);
		
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

	
	public FolderDialog(Display display, Shell parent)
	{
		this(display, parent, null);
	}

	public FolderDialog(Display display, Shell parent, String text)
	{
		m_display = display;
		m_folderName = text;
		createShell(parent);
	}

	public String getFolderName()
	{
		return m_folderName;
	}
	
	protected void cancelClose()
	{
		m_folderName = null;
		m_shell.close();
	}
}
