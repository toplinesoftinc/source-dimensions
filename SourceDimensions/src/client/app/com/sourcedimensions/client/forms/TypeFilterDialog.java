package com.sourcedimensions.client.forms;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import com.sourcedimensions.client.views.ProjectView;

public class TypeFilterDialog 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Display m_display;
	private Table m_typeCategoryList;
	private Label m_typeCategoryLabel;
	
	public TypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	private void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(487, 501));
		m_shell.setLayout(null);
		m_typeCategoryList = new Table(m_shell, SWT.BORDER | SWT.SINGLE | SWT.CHECK | SWT.FULL_SELECTION);
		m_typeCategoryList.setHeaderVisible(false);
		m_typeCategoryList.setLinesVisible(false);
		m_typeCategoryList.setBounds(new Rectangle(29, 33, 100, 68));
		m_typeCategoryList.addMouseListener(new MouseAdapter() 
		{
			public void mouseDoubleClick(MouseEvent e) 
			{
				int sel = m_typeCategoryList.getSelectionIndex();
				
				if (sel != -1)
				{
					TableItem item = m_typeCategoryList.getItem(sel); 
					item.setChecked(!item.getChecked());
				}
			}
		});
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoryLabel.setBounds(new Rectangle(29, 18, 89, 13));
		m_typeCategoryLabel.setText("Type Categories:");
		
		new TableItem(m_typeCategoryList, 0, 0).setText("CLASS");
		new TableItem(m_typeCategoryList, 0, 1).setText("INTERFACE");
		new TableItem(m_typeCategoryList, 0, 2).setText("ENUM");
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				new TableItem(m_typeCategoryList, 0, 3).setText("ANNOTATION");
				break;
				
			case CSHARP11:
			case CSHARP20:
				new TableItem(m_typeCategoryList, 0, 3).setText("STRUCT");
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
}
