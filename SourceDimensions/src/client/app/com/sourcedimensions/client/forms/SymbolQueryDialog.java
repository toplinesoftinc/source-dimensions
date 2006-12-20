package com.sourcedimensions.client.forms;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import com.sourcedimensions.client.Util;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;


public class SymbolQueryDialog
{

	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="0,-13"
	private Display m_display;  //  @jve:decl-index=0:
	private Label m_destinationSnapshotLabel = null;
	private Combo m_comboDestinationSnapshot = null;
	private Text m_snapshotNameText = null;
	private Button m_clearSnapshotCheckBox = null;
	private Label m_snapshotNameLabel = null;
	private TabFolder m_queryParamsTabFolder = null;
	private Button m_runQueryButton = null;
	private Button m_cancelButton = null;
	private Composite m_namespaceScopeTab = null;
	private Composite m_memberScopeTab = null;
	private Composite m_localScopeTab = null;
	private Composite m_classScopeTab = null;
	private Table m_namespaceFilterTable = null;
	private Label m_namespaceFilterLabel = null;
	private Button m_allNamespacesCheckBox = null;
	private Button m_addFilterButton = null;
	private Button m_removeFilterButton = null;
	private Button m_editFilterButton = null;
	public SymbolQueryDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
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
	
	private void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		m_shell.setText("Symbol Query");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(646, 508));
		m_shell.setLayout(null);
		m_runQueryButton = new Button(m_shell, SWT.NONE);
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_destinationSnapshotLabel = new Label(m_shell, SWT.NONE);
		m_destinationSnapshotLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_destinationSnapshotLabel.setBounds(new Rectangle(144, 8, 147, 17));
		m_destinationSnapshotLabel.setText("&Destination Snapshot:");
		createComboDestinationSnapshot();
		m_clearSnapshotCheckBox = new Button(m_shell, SWT.CHECK | SWT.RIGHT);
		m_snapshotNameLabel = new Label(m_shell, SWT.NONE);
		m_snapshotNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_snapshotNameText.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_snapshotNameText.setSize(new Point(199, 21));
		m_snapshotNameText.setLocation(new Point(426, 25));
		m_clearSnapshotCheckBox.setBounds(new Rectangle(145, 56, 119, 15));
		m_clearSnapshotCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_clearSnapshotCheckBox.setText("Clear &Snapshot");
		m_clearSnapshotCheckBox.setToolTipText("Delete all contents of the snapshot before putting results of this query");
		m_snapshotNameLabel.setBounds(new Rectangle(426, 8, 144, 16));
		m_snapshotNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_snapshotNameLabel.setText("&New Snapshot Name:");
		createQueryParamsTabFolder();
		m_cancelButton.setToolTipText("Login");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setLocation(new Point(15, 48));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSelection(true);
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				cancelClose();
			}
		});
		m_runQueryButton.setToolTipText("Login");
		m_runQueryButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));

		m_runQueryButton.setSize(new Point(88, 25));
		m_runQueryButton.setLocation(new Point(15, 12));
		m_runQueryButton.setText("&Run Query");
		m_runQueryButton.setSelection(true);
		
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
		
		Util.centerWindow(m_shell, parent);		
	}
	
	protected void cancelClose()
	{
		m_shell.close();
	}

	private void createComboDestinationSnapshot()
	{
		m_comboDestinationSnapshot = new Combo(m_shell, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL);
		m_comboDestinationSnapshot.setLocation(new Point(145, 25));
		m_comboDestinationSnapshot.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_comboDestinationSnapshot.setText("");
		m_comboDestinationSnapshot.setToolTipText("Snapshot where to put results of this query into");
		m_comboDestinationSnapshot.setVisibleItemCount(10);
		m_comboDestinationSnapshot.setSize(new Point(253, 24));
		m_comboDestinationSnapshot.add("< New Snapshot >");
		m_comboDestinationSnapshot.select(0);
	}

	private void createQueryParamsTabFolder()
	{
		m_queryParamsTabFolder = new TabFolder(m_shell, SWT.NONE);
		m_queryParamsTabFolder.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		createNamespaceScopeComposite();
		createMemberScopeComposite();
		createLocalScopePage();
		createClassScopeTab();
		m_queryParamsTabFolder.setBounds(new Rectangle(15, 87, 610, 376));
		TabItem tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Namespace Scope");
		tabItem.setControl(m_namespaceScopeTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Class Scope");
		tabItem.setControl(m_classScopeTab);		
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Member Scope");
		tabItem.setControl(m_memberScopeTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Local Scope");
		tabItem.setControl(m_localScopeTab);
	}

	private void createNamespaceScopeComposite()
	{
		m_namespaceScopeTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_namespaceScopeTab.setLayout(null);
		m_allNamespacesCheckBox = new Button(m_namespaceScopeTab, SWT.CHECK | SWT.RIGHT);
		m_namespaceFilterLabel = new Label(m_namespaceScopeTab, SWT.NONE);
		m_namespaceFilterTable = new Table(m_namespaceScopeTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_namespaceFilterTable.setHeaderVisible(false);
		m_namespaceFilterTable.setLinesVisible(true);
		m_namespaceFilterTable.setBounds(new Rectangle(15, 57, 476, 272));
		new TableColumn(m_namespaceFilterTable, SWT.LEFT)
			.setWidth(m_namespaceFilterTable.getBounds().width - 2 * m_namespaceFilterTable.getBorderWidth());
		m_addFilterButton = new Button(m_namespaceScopeTab, SWT.NONE);
		m_addFilterButton.setToolTipText("Login");
		m_addFilterButton.setSelection(true);
		m_addFilterButton.setText("A&dd filter...");
		m_addFilterButton.setLocation(new Point(502, 57));
		m_addFilterButton.setSize(new Point(88, 25));
		m_addFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_addFilterButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				InputDialog input = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
						"Filter", "&Namespace Filter:", "", false);
				input.open();
				String val = input.getValue();
				
				if (val != null)
				{
					new TableItem(m_namespaceFilterTable, SWT.NONE).setText(0, val);
				}
			}
		});
		m_editFilterButton = new Button(m_namespaceScopeTab, SWT.NONE);
		m_removeFilterButton = new Button(m_namespaceScopeTab, SWT.NONE);
		m_removeFilterButton.setToolTipText("Login");
		m_removeFilterButton.setSelection(true);
		m_removeFilterButton.setText("Re&move filter");
		m_removeFilterButton.setLocation(new Point(502, 152));
		m_removeFilterButton.setSize(new Point(88, 25));
		m_removeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_removeFilterButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_namespaceFilterTable.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
				}
				else
				{
					if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
						"Are you sure you want to delete selected filter?"))
					{
						m_namespaceFilterTable.remove(sel);
					}
				}				
			}
		});
		m_editFilterButton.setToolTipText("Login");
		m_editFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_editFilterButton.setSize(new Point(88, 25));
		m_editFilterButton.setLocation(new Point(502, 104));
		m_editFilterButton.setText("&Edit filter...");
		m_editFilterButton.setSelection(true);
		m_editFilterButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_namespaceFilterTable.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
				}
				else
				{
					InputDialog input = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
						"Filter", "&Namespace Filter:", m_namespaceFilterTable.getItem(sel).getText() ,false);
					input.open();
					String val = input.getValue();
					
					if (val != null)
					{
						m_namespaceFilterTable.getItem(sel).setText(val);
					}							
				}
			}
		});
		m_namespaceFilterLabel.setBounds(new Rectangle(15, 41, 140, 16));
		m_namespaceFilterLabel.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_namespaceFilterLabel.setText("Namespace &filter list:");
		m_allNamespacesCheckBox.setBounds(new Rectangle(15, 12, 121, 16));
		m_allNamespacesCheckBox.setText("&All Namespaces");
		m_allNamespacesCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.BOLD));
		m_allNamespacesCheckBox.setSelection(false);
		m_allNamespacesCheckBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean sel = !m_allNamespacesCheckBox.getSelection();
				m_namespaceFilterTable.setEnabled(sel);
				m_addFilterButton.setEnabled(sel);
				m_editFilterButton.setEnabled(sel);
				m_removeFilterButton.setEnabled(sel);
			}
		});
	}

	private void createMemberScopeComposite()
	{
		m_memberScopeTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_memberScopeTab.setLayout(null);
	}

	private void createLocalScopePage()
	{
		m_localScopeTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_localScopeTab.setLayout(null);
	}

	private void createClassScopeTab()
	{
		m_classScopeTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_classScopeTab.setLayout(new GridLayout());
	}
}
