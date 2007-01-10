package com.sourcedimensions.client.forms;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;

public class BaseTypeFilterDialog 
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label m_typeCategoriesLabel = null;
	/**
	 * This method initializes m_shell
	 */
	private void createShell()
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		m_shell.setText("Base Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setToolTipText("");
		m_shell.setSize(new Point(305, 246));
		m_shell.setLayout(null);
		m_typeCategoriesLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoriesLabel.setText("Type Categories:");
		m_typeCategoriesLabel.setBounds(new Rectangle(9, 8, 83, 13));
	}
}
