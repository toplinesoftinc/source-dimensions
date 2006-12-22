package com.sourcedimensions.ws.consumer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import com.sourcedimensions.client.Util;

public class WaitDialog 
{

	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private ProgressBar m_progressBar = null;
	private Label m_waitLabel = null;
	private Button m_cancelButton = null;
	
	private Display m_display = null;  //  @jve:decl-index=0:
	private WSConsumer m_consumer = null;
	
	public WaitDialog(Display display, WSConsumer consumer, Shell parent)
	{
		createShell(display, parent);
		m_consumer = consumer;
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
	
	public void close()
	{
		m_display.syncExec(new Runnable()
		{
			public void run()
			{
				if (!m_shell.isDisposed())
					m_shell.close();
			}
		});
	}
	
	/**
	 * This method initializes m_shell
	 */
	private void createShell(Display display, Shell parent) 
	{
		m_shell = new Shell(SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		if (parent != null)
			m_shell.setParent(parent);
		m_shell.setText("");
		m_shell.setSize(new Point(372, 132));
		m_shell.setLayout(null);
		m_progressBar = new ProgressBar(m_shell, SWT.INDETERMINATE | SWT.SMOOTH | SWT.BORDER);
		m_progressBar.setBounds(new Rectangle(19, 43, 329, 15));
		m_waitLabel = new Label(m_shell, SWT.CENTER);
		m_waitLabel.setBounds(new Rectangle(21, 20, 328, 18));
		m_waitLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_waitLabel.setText("Connecting to server ...");
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(139, 67));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() 
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{
				m_shell.close();
				m_consumer.cancelRequest();
			}
		});
		m_display = display;
		Util.centerWindow(m_shell, parent);
	}

}
