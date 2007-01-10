package com.sourcedimensions.client.forms;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.forms.InputDialog.MandatoryFieldValidator;
import com.sourcedimensions.client.forms.SymbolQueryDialog.NamespaceFilterValidator;
import com.sourcedimensions.client.views.ProjectView;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;

public class TypeFilterDialog 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="9,11"
	private Display m_display;
	private Table m_typeCategoryList;
	private Label m_typeCategoryLabel;
	private Label m_modifierListLabel = null;
	private Table m_modifiersList = null;
	private Label m_typeNameLabel = null;
	private Text m_typeNameText = null;
	private Label m_baseTypesLabel = null;
	private Table m_baseTypesTable = null;
	private Button m_addBaseTypeButton = null;
	private Button m_editBaseTypeButton = null;
	private Button m_removeBaseTypeButton = null;
	private Button m_okButton = null;
	private Button m_cancelButton = null;
	
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
		m_shell.setSize(new Point(429, 392));
		m_shell.setLayout(null);
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoryList = new Table(m_shell, SWT.BORDER | SWT.SINGLE | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_typeCategoryList.setHeaderVisible(false);
		m_typeCategoryList.setLinesVisible(false);
		m_typeCategoryList.setBounds(new Rectangle(17, 23, 106, 72));
		m_typeCategoryList.addMouseListener(new CheckListDblClickMouseAdapter()); 
		m_typeCategoryLabel.setBounds(new Rectangle(17, 8, 89, 13));
		m_typeCategoryLabel.setText("&Type Categories:");
		
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

		m_modifierListLabel = new Label(m_shell, SWT.NONE);
		m_modifierListLabel.setBounds(new Rectangle(220, 8, 56, 13));
		m_modifierListLabel.setText("&Modifiers:");

		m_modifiersList = new Table(m_shell, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		m_modifiersList.setHeaderVisible(false);
		m_modifiersList.setLinesVisible(false);
		m_modifiersList.setBounds(new Rectangle(220, 23, 100, 136));
		m_modifiersList.addMouseListener(new CheckListDblClickMouseAdapter());

		m_typeNameLabel = new Label(m_shell, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(17, 126, 101, 13));
		m_typeNameLabel.setText("Type &Name Filter:");
		m_typeNameText = new Text(m_shell, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(17, 140, 194, 19));
		m_baseTypesLabel = new Label(m_shell, SWT.NONE);
		m_baseTypesLabel.setBounds(new Rectangle(18, 172, 110, 13));
		m_baseTypesLabel.setText("&Base Types Filter List:");
		m_baseTypesTable = new Table(m_shell, SWT.BORDER | SWT.FULL_SELECTION);
		m_baseTypesTable.setHeaderVisible(true);
		m_baseTypesTable.setLinesVisible(true);
		m_baseTypesTable.setBounds(new Rectangle(17, 186, 303, 162));
		double width = m_baseTypesTable.getBounds().width - 2 * m_baseTypesTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_baseTypesTable, SWT.LEFT, 0);
		column.setWidth((int)(0.6 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name Filter");
		column = new TableColumn(m_baseTypesTable, SWT.LEFT, 1);
		column.setWidth((int)(0.4 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Categories");
		m_addBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_addBaseTypeButton.setLocation(new Point(328, 186));
		m_addBaseTypeButton.setText("A&dd filter...");
		m_addBaseTypeButton.setSize(new Point(88, 25));
		m_addBaseTypeButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
			}
		});
		m_editBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_editBaseTypeButton.setLocation(new Point(328, 228));
		m_editBaseTypeButton.setText("&Edit filter...");
		m_editBaseTypeButton.setSize(new Point(88, 25));
		m_removeBaseTypeButton = new Button(m_shell, SWT.NONE);
		m_removeBaseTypeButton.setLocation(new Point(328, 273));
		m_removeBaseTypeButton.setText("&Remove filter");
		m_removeBaseTypeButton.setSize(new Point(88, 25));
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setLocation(new Point(328, 23));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(328, 63));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				m_shell.close();
			}
		});
		new TableItem(m_modifiersList, 0, 0).setText("public");
		new TableItem(m_modifiersList, 0, 1).setText("protected");
		new TableItem(m_modifiersList, 0, 2).setText("private");
		new TableItem(m_modifiersList, 0, 3).setText("abstract");
		new TableItem(m_modifiersList, 0, 4).setText("static");		
		
		switch (ProjectView.getProject().getLanguage())
		{
			case JAVA14:
			case JAVA15:
				new TableItem(m_modifiersList, 0, 5).setText("final");
				new TableItem(m_modifiersList, 0, 6).setText("strictfp");				
				break;
				
			case CSHARP11:
			case CSHARP20:
				new TableItem(m_modifiersList, 0, 5).setText("new");
				new TableItem(m_modifiersList, 0, 6).setText("internal");				
				new TableItem(m_modifiersList, 0, 7).setText("sealed");
		}

		Control[] widgets = m_shell.getChildren();

		for (int i = 0; i < widgets.length; i++) 
		{ 
			widgets[i].addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.keyCode == SWT.ESC)
						m_shell.close();
				}
				
				public void keyReleased(KeyEvent e)
				{				
				}
			});
		}
		
		Util.centerWindow(m_shell, parent);		
		
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
	
	protected class CheckListDblClickMouseAdapter extends MouseAdapter 
	{
		public void mouseDoubleClick(MouseEvent e) 
		{
			if (e.getSource() instanceof Table)
			{
				Table src = (Table)e.getSource();
				
				int sel = src.getSelectionIndex();
	
				if (sel != -1)
				{
					TableItem item = src.getItem(sel); 
					item.setChecked(!item.getChecked());
				}
			}
		}
	}	
}
