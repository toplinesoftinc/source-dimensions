package com.sourcedimensions.client.forms;

import java.util.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.*;
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

public class ProjectListDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-28,-6"
	private ArrayList<Project> m_prjSet = new ArrayList<Project>();  //  @jve:decl-index=0:
	private Button m_openButton;
	private Button m_cancelButton;
	private Project m_selected;  //  @jve:decl-index=0:
	private Table m_prjList;
	private Button m_showAllCheckBox = null;

	
	public ProjectListDialog(Shell parent)
	{
		createShell(parent);
	}
	
	public void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Projects");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(470, 362));
		m_shell.setLayout(null);		
		
		m_openButton = new Button(m_shell, SWT.NONE);
		m_openButton.setBounds(new Rectangle(239, 292, 88, 25));
		m_openButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
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
		m_cancelButton.setBounds(new Rectangle(355, 292, 88, 25));
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setToolTipText("Cancel");
		m_prjList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		m_prjList.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				int sel = m_prjList.getSelectionIndex();
				
				if (sel != -1)
				{
					m_selected = m_prjSet.get(sel);
					m_shell.close();
				}
			}			
		});
		m_prjList.setHeaderVisible(true);
		m_prjList.setLinesVisible(false);
		m_prjList.setBounds(new Rectangle(19, 17, 424, 262));
		m_showAllCheckBox = new Button(getShell(), SWT.CHECK | SWT.RIGHT);
		m_showAllCheckBox.setBounds(new Rectangle(19, 292, 140, 16));
		m_showAllCheckBox.setText("Show All Loaded Projects");
		m_showAllCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				reloadProjects();
			}
		});
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
		super.createShell(parent);
	}

	public void loadProjects(Collection<Project> projects)
	{	
		m_prjSet.clear();
		m_prjSet.addAll(projects);

		reloadProjects();
	}
	
	protected void reloadProjects()
	{
		Image imgChkmark = Util.getSharedImage(IImageKeys.IMG_CHECKMARK);
		Image imgCross = Util.getSharedImage(IImageKeys.IMG_CROSS);

		m_prjList.removeAll();
		
		for (Project prj : m_prjSet)
		{
			if (!prj.getDeleted() || m_showAllCheckBox.getSelection())
			{				
				TableItem item = new TableItem(m_prjList, SWT.NONE);
				
				if (prj.getDeleted())
					item.setImage(imgCross);
				else
					item.setImage(imgChkmark);
	
				item.setText(0, prj.getName());
				item.setText(1, prj.langName());
			}
		}		
	}
	
	public Project getSelected()
	{
		return m_selected;
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
