package com.sourcedimensions.client.forms;

import java.util.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Table;

import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.model.Project;

public class ProjectList 
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="-28,-6"
	private ArrayList<String> m_idSet = new ArrayList<String>();
	private Button m_openButton = null;
	private Button m_cancelButton = null;
	private Display m_display = null;
	private String m_selected = null;  //  @jve:decl-index=0:
	private Table m_prjList = null;

	public ProjectList(Display display, Shell parent)
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
		if (parent != null)
			m_shell.setParent(parent);
		m_shell.setText("Projects");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(470, 364));
		m_shell.setLayout(null);
		
		Util.centerWindow(m_shell, parent);
		m_shell.setVisible(true);
		
		m_openButton = new Button(m_shell, SWT.NONE);
		m_openButton.setBounds(new Rectangle(117, 295, 88, 25));
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
					m_selected = m_idSet.get(sel);
					m_shell.close();
				}
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setBounds(new Rectangle(258, 295, 88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.setText("Cancel");
		m_cancelButton.setToolTipText("Cancel");
		m_prjList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		m_prjList.setHeaderVisible(true);
		m_prjList.setLinesVisible(false);
		m_prjList.setBounds(new Rectangle(19, 17, 424, 262));
		new TableColumn(m_prjList, SWT.LEFT, 0).setText("Name");
		new TableColumn(m_prjList, SWT.LEFT, 1).setText("Language");
		
		int width = m_prjList.getBounds().width;
		
		m_prjList.getColumn(0).setWidth((int)(0.75 * width));
		m_prjList.getColumn(1).setWidth((int)(0.25 * width));
		
		m_cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				m_selected = null;
				m_shell.close();
			}
		});
		
		m_shell.setDefaultButton(m_openButton);
	}

	public void loadProjects(Collection<Project> projs)
	{
		Image imgChkmark = Util.createImageDescriptor(IImageKeys.IMG_CHECKMARK).createImage();
		Image imgCross = Util.createImageDescriptor(IImageKeys.IMG_CROSS).createImage();
		m_prjList.removeAll();
		m_idSet.clear();
		
		for (Project prj : projs)
		{
			m_idSet.add(prj.m_id);
			TableItem item = new TableItem(m_prjList, SWT.NONE);
			
			if (prj.m_deleted)
				item.setImage(imgCross);
			else
				item.setImage(imgChkmark);

			item.setText(0, prj.m_name);
			item.setText(1, prj.getLangName());
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
