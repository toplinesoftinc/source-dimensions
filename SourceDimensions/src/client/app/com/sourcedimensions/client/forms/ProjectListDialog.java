package com.sourcedimensions.client.forms;

import java.util.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
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

public class ProjectListDialog implements MouseListener
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="-28,-6"
	private ArrayList<Project> m_prjSet = new ArrayList<Project>();
	private Button m_openButton = null;
	private Button m_cancelButton = null;
	private Display m_display = null;
	private Project m_selected = null;  //  @jve:decl-index=0:
	private Table m_prjList = null;

	
	public void mouseDoubleClick(MouseEvent e)
	{
		int sel = m_prjList.getSelectionIndex();
		
		if (sel != -1)
		{
			m_selected = m_prjSet.get(sel);
			m_shell.close();
		}
	}
	
	public void mouseDown(MouseEvent e)
	{
	}
	
	public void mouseUp(MouseEvent e)
	{	
	}
	
	public ProjectListDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	public void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.BORDER);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Projects");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(470, 359));
		m_shell.setLayout(null);
		
		Util.centerWindow(m_shell, parent);
		m_shell.setVisible(true);
		
		m_openButton = new Button(m_shell, SWT.NONE);
		m_openButton.setBounds(new Rectangle(117, 295, 88, 25));
		m_openButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_openButton.setText("O&pen");
		m_openButton.setToolTipText("Open");
		m_openButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_prjList.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select project");
				}
				else
				{
					m_selected = m_prjSet.get(sel);
					m_shell.close();
				}
			}
		});

		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setBounds(new Rectangle(258, 295, 88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setToolTipText("Cancel");
		m_prjList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		m_prjList.addMouseListener(this);
		m_prjList.setHeaderVisible(true);
		m_prjList.setLinesVisible(false);
		m_prjList.setBounds(new Rectangle(19, 17, 424, 262));
		new TableColumn(m_prjList, SWT.LEFT, 0).setText("Name");
		new TableColumn(m_prjList, SWT.LEFT, 1).setText("Language");
		
		int width = m_prjList.getBounds().width;
		
		m_prjList.getColumn(0).setWidth((int)(0.75 * width));
		m_prjList.getColumn(1).setWidth((int)(0.25 * width));
		
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				cancelClose();
			}
		});
		
		m_shell.setDefaultButton(m_openButton);
		
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
		
	}

	protected void cancelClose()
	{
		m_selected = null;
		m_shell.close();
	}

	public void loadProjects(Collection<Project> projs)
	{
		Image imgChkmark = Util.getSharedImage(IImageKeys.IMG_CHECKMARK);
		Image imgCross = Util.getSharedImage(IImageKeys.IMG_CROSS);

		m_prjList.removeAll();
		m_prjSet.clear();
		
		for (Project prj : projs)
		{
			m_prjSet.add(prj);
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
	
	public Project getSelected()
	{
		return m_selected;
	}
}
