package com.sourcedimensions.client.forms;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Table;

public class JavaMemberDialog extends TypeMemberDialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label m_memberCategoryLabel;
	private Table m_memberCategoryList;
	private TypeMemberCategory[] m_categoryArray = 
	{
		TypeMemberCategory.FIELD,
		TypeMemberCategory.CONSTRUCTOR,
		TypeMemberCategory.METHOD,
		TypeMemberCategory.ENUM_CONST
	};
	
	public JavaMemberDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
		m_memberCategoryList.selectAll();
	}
	
	private void createShell(Shell parent)  
	{
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		if (parent != null)
			m_shell.setParent(parent);
				
		m_shell.setText("Type Member Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(445, 400));
		m_shell.setLayout(null);
		m_memberCategoryLabel = new Label(m_shell, SWT.NONE);
		m_memberCategoryLabel.setText("Type &Member Category:");
		m_memberCategoryLabel.setLocation(new Point(17, 8));
		m_memberCategoryLabel.setSize(new Point(122, 13));
		m_memberCategoryList = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.HIDE_SELECTION);
		m_memberCategoryList.setHeaderVisible(false);
		m_memberCategoryList.setLinesVisible(false);
		m_memberCategoryList.setLocation(new Point(17, 22));
		m_memberCategoryList.setSize(new Point(131, 95));
		for (int i = 0; i < m_categoryArray.length; i++)
		{
			new TableItem(m_memberCategoryList, 0, i).setText(m_categoryArray[i].toString().replace("_", " "));
		}
		
		postCreate(parent);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
