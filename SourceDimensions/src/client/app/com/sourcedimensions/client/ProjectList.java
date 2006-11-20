package com.sourcedimensions.client;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.graphics.Rectangle;

import com.sourcedimensions.client.model.Project;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Font;

public class ProjectList 
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="7,11"
	private List m_prjList = null;
	private ArrayList<String> m_idSet = new ArrayList<String>();
	private Button m_openButton = null;
	private Button m_cancelButton = null;
	private Display m_display = null;
	private String m_selected = null;  //  @jve:decl-index=0:

	ProjectList(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	/**
	 * This method initializes sShell
	 */
	public void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.BORDER);
		m_shell.setParent(parent);
		m_shell.setText("Projects");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setVisible(true);
		m_shell.setSize(new Point(467, 364));
		m_shell.setLayout(null);
		
		m_prjList = new List(m_shell, SWT.BORDER | SWT.V_SCROLL);
		m_prjList.setBounds(new Rectangle(16, 15, 427, 265));
		m_openButton = new Button(m_shell, SWT.NONE);
		m_openButton.setBounds(new Rectangle(117, 296, 88, 25));
		m_openButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_openButton.setText("Open");
		m_openButton.setToolTipText("Open");
		m_openButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				int sel = m_prjList.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select project");
				}
				else
				{
					m_selected = m_prjList.getItem(sel);
					m_shell.close();
				}
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setBounds(new Rectangle(251, 296, 88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.setText("Cancel");
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				m_selected = null;
				m_shell.close();
			}
		});
		
		m_shell.setDefaultButton(m_openButton);
		
		Util.centerWindow(m_shell, parent);
	}

	public void loadList(Map<String, Project> list)
	{
		m_prjList.removeAll();
		m_idSet.clear();
		
		for (String id : list.keySet())
		{
			m_idSet.add(id);
			m_prjList.add(list.get(id).m_name);
		}		
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
	
	public String getSelected()
	{
		return m_selected;
	}
}
