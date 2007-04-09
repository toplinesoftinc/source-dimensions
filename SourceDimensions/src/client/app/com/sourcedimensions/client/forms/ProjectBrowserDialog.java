package com.sourcedimensions.client.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;


public class ProjectBrowserDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,9"
	private Button m_selectButton;
	private Button m_cancelButton;
	private boolean m_isQuery;
	

	private static String m_sessionID;
	private Tree m_viewer = null;
	
	public ProjectBrowserDialog(Display display, Shell parent, boolean isQuery)
	{
		m_display = display;
		m_isQuery = isQuery;
		createShell(parent);
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
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("");
		m_shell.setLayout(null);
		m_shell.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_shell.setVisible(false);
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(373, 388));
		m_selectButton = new Button(m_shell, SWT.NONE);
		m_selectButton.setBounds(new Rectangle(68, 324, 88, 25));
		m_selectButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_selectButton.setSelection(true);
		m_selectButton.setToolTipText("Login");
		m_selectButton.setText("&Select");
		m_shell.setDefaultButton(m_selectButton);
		m_selectButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{

			}
		});

		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(211, 324));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_viewer = new Tree(getShell(), SWT.BORDER);
		m_viewer.setBounds(new Rectangle(16, 13, 333, 299));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});

		if (m_isQuery)
			m_shell.setText("Query Selection");
		else
			m_shell.setText("Snapshot Selection");
				
		super.createShell(parent);
	}
	
	public static String getSessionID()
	{
		return m_sessionID;
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
