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
import com.sourcedimensions.client.forms.DialogBase;

public class WaitDialog extends DialogBase 
{

	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private ProgressBar m_progressBar;
	private Label m_waitLabel;
	private Button m_cancelButton;	
	private WSConsumer m_consumer;
	
	public WaitDialog(WSConsumer consumer, Shell parent)
	{
		createShell(parent);
		m_consumer = consumer;
	}

	public void close()
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				if (!m_shell.isDisposed())
					m_shell.close();
			}
		});
	}
	
	protected void createShell(Shell parent) 
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

		Util.centerWindow(m_shell, parent);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
