package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import com.sourcedimensions.client.forms.TypeFilterDialog.BaseType;
import com.sourcedimensions.client.forms.TypeFilterDialog.TypeCategory;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.dialogs.MessageDialog;


public class SymbolQueryDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-38,-54"
	private Label m_destinationSnapshotLabel;
	private Combo m_comboDestinationSnapshot;
	private Text m_snapshotNameText;
	private Button m_clearSnapshotCheckBox;
	private Label m_snapshotNameLabel;
	private TabFolder m_queryParamsTabFolder;
	private Button m_runQueryButton;
	private Button m_cancelButton;
	private Composite m_namespacesTab;
	private Composite m_typeMembersTab;
	private Composite m_localDeclsTab;
	private Composite m_typesTab;
	private Table m_namespaceFilterTable;
	private Label m_namespaceFilterLabel;
	private Button m_allNamespacesCheckBox;
	private Button m_addNamespaceFilterButton;
	private Button m_removeNamespaceFilterButton;
	private Button m_editNamespaceFilterButton;
	private Button m_allTypesCheckBox;
	private Table m_typeFilterTable;
	private Label m_typeFilterLabel;
	private Button m_addTypeFilterButton;
	private Button m_editTypeFilterButton;
	private Button m_removeTypeFilterButton;
	private ArrayList<TypeFilter> m_typeFilter = new ArrayList<TypeFilter>();
	private Button m_allMembersCheckBox;
	private Label m_memberFilterListLabel;
	private Table m_typeMemberFilterTable;
	private Button m_addMemberFilterButton;
	private Button m_editMemberFilterButton;
	private Button m_removeMemberFilterButton;
	private Label m_queryNameLabel = null;
	private Text m_queryNameText = null;
	private Button m_saveButton = null;
	
	public SymbolQueryDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	private void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Symbol Query");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(645, 536));
		m_shell.setLayout(null);
		m_runQueryButton = new Button(m_shell, SWT.NONE);
		m_saveButton = new Button(getShell(), SWT.NONE);
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_destinationSnapshotLabel = new Label(m_shell, SWT.NONE);
		m_destinationSnapshotLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_destinationSnapshotLabel.setBounds(new Rectangle(122, 17, 118, 14));
		m_destinationSnapshotLabel.setText("&Destination Snapshot:");
		createComboDestinationSnapshot();
		m_clearSnapshotCheckBox = new Button(m_shell, SWT.CHECK | SWT.RIGHT);
		m_snapshotNameLabel = new Label(m_shell, SWT.NONE);
		m_snapshotNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_snapshotNameText.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameText.setSize(new Point(255, 18));
		m_snapshotNameText.setLocation(new Point(370, 35));
		m_clearSnapshotCheckBox.setBounds(new Rectangle(122, 64, 91, 15));
		m_clearSnapshotCheckBox.setEnabled(false);
		m_clearSnapshotCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_clearSnapshotCheckBox.setText("Clear &Snapshot");
		m_clearSnapshotCheckBox.setToolTipText("Delete all contents of the snapshot before putting results of this query");
		m_snapshotNameLabel.setBounds(new Rectangle(370, 20, 111, 14));
		m_snapshotNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameLabel.setText("Ne&w Snapshot Name:");
		m_queryNameLabel = new Label(getShell(), SWT.NONE);
		m_queryNameText = new Text(getShell(), SWT.BORDER);
		m_queryNameText.setBounds(new Rectangle(370, 80, 255, 19));
		createQueryParamsTabFolder();
		m_queryNameLabel.setBounds(new Rectangle(370, 66, 70, 13));
		m_queryNameLabel.setText("&Query Name:");
		m_saveButton.setLocation(new Point(15, 46));
		m_saveButton.setSize(new Point(88, 25));
		m_saveButton.setText("Sa&ve");
		m_cancelButton.setToolTipText("Login");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setLocation(new Point(15, 81));
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
		m_runQueryButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));

		m_runQueryButton.setSize(new Point(88, 25));
		m_runQueryButton.setLocation(new Point(15, 12));
		m_runQueryButton.setText("&Run Query");
		m_runQueryButton.setSelection(true);
		
		m_shell.addShellListener(new ShellListener() 
		{	
			public void shellClosed(ShellEvent event) 
			{
				event.doit = MessageDialog.openQuestion(m_shell, "Close confirmation", "Do you want to close query window?");
				m_cancel = event.doit;
			}
			public void shellActivated(ShellEvent arg0) {}
			public void shellDeactivated(ShellEvent arg0) {}
			public void shellIconified(ShellEvent arg0) {}
			public void shellDeiconified(ShellEvent arg0) {}
			
		});
				
		postCreate(parent);
	}
	
	private void createComboDestinationSnapshot()
	{
		m_comboDestinationSnapshot = new Combo(m_shell, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL);
		m_comboDestinationSnapshot.setLocation(new Point(122, 32));
		m_comboDestinationSnapshot.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_comboDestinationSnapshot.setText("");
		m_comboDestinationSnapshot.setToolTipText("Snapshot where to put results of this query into");
		m_comboDestinationSnapshot.setVisibleItemCount(10);
		m_comboDestinationSnapshot.setSize(new Point(236, 21));
		m_comboDestinationSnapshot.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean newSnapshot = (m_comboDestinationSnapshot.getSelectionIndex() == 0);
				m_clearSnapshotCheckBox.setEnabled(!newSnapshot);
				m_snapshotNameText.setEnabled(newSnapshot);
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		m_comboDestinationSnapshot.add("< New Snapshot >");
		m_comboDestinationSnapshot.select(0);
	}

	private void createQueryParamsTabFolder()
	{
		m_queryParamsTabFolder = new TabFolder(m_shell, SWT.NONE);
		m_queryParamsTabFolder.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_queryParamsTabFolder.setLocation(new Point(15, 118));
		m_queryParamsTabFolder.setSize(new Point(610, 376));
		createNamespacesTab();
		createTypeMembersTab();
		createLocalDeclsTab();
		createTypesTab();
		TabItem tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Namespaces");
		tabItem.setControl(m_namespacesTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Types");
		tabItem.setControl(m_typesTab);		
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Type Members");
		tabItem.setControl(m_typeMembersTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Local Declarations");
		tabItem.setControl(m_localDeclsTab);
	}

	private void createNamespacesTab()
	{
		m_namespacesTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_namespacesTab.setLayout(null);
		m_allNamespacesCheckBox = new Button(m_namespacesTab, SWT.CHECK | SWT.LEFT);
		m_namespaceFilterLabel = new Label(m_namespacesTab, SWT.NONE);
		m_namespaceFilterTable = new Table(m_namespacesTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_namespaceFilterTable.setHeaderVisible(false);
		m_namespaceFilterTable.setLinesVisible(true);
		m_namespaceFilterTable.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_namespaceFilterTable.setBounds(new Rectangle(15, 57, 476, 272));
		m_namespaceFilterTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_namespaceFilterTable.getSelectionIndex() != -1)
				{
					editNamespaceFilter();
				}
			}			
		});
		
		new TableColumn(m_namespaceFilterTable, SWT.LEFT).setWidth(
			m_namespaceFilterTable.getBounds().width - 2 * m_namespaceFilterTable.getBorderWidth());
		m_addNamespaceFilterButton = new Button(m_namespacesTab, SWT.NONE);
		m_addNamespaceFilterButton.setToolTipText("Login");
		m_addNamespaceFilterButton.setSelection(true);
		m_addNamespaceFilterButton.setText("A&dd Filter...");
		m_addNamespaceFilterButton.setLocation(new Point(502, 57));
		m_addNamespaceFilterButton.setSize(new Point(88, 25));
		m_addNamespaceFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_addNamespaceFilterButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
						"Filter", "&Namespace Filter:", "", new NamespaceFilterValidator());
				dialog.open();
				String val = dialog.getValue();
				
				if (val != null)
				{
					new TableItem(m_namespaceFilterTable, SWT.NONE).setText(0, val);
				}
			}
		});
		m_editNamespaceFilterButton = new Button(m_namespacesTab, SWT.NONE);
		m_removeNamespaceFilterButton = new Button(m_namespacesTab, SWT.NONE);
		m_removeNamespaceFilterButton.setToolTipText("Login");
		m_removeNamespaceFilterButton.setSelection(true);
		m_removeNamespaceFilterButton.setText("Re&move Filter");
		m_removeNamespaceFilterButton.setLocation(new Point(502, 152));
		m_removeNamespaceFilterButton.setSize(new Point(88, 25));
		m_removeNamespaceFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_removeNamespaceFilterButton.addSelectionListener(new SelectionAdapter()
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
		m_editNamespaceFilterButton.setToolTipText("Login");
		m_editNamespaceFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_editNamespaceFilterButton.setSize(new Point(88, 25));
		m_editNamespaceFilterButton.setLocation(new Point(502, 104));
		m_editNamespaceFilterButton.setText("&Edit Filter...");
		m_editNamespaceFilterButton.setSelection(true);
		m_editNamespaceFilterButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editNamespaceFilter();
			}
		});
		m_namespaceFilterLabel.setBounds(new Rectangle(15, 41, 115, 16));
		m_namespaceFilterLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_namespaceFilterLabel.setText("Namespace &Filter List:");
		m_allNamespacesCheckBox.setBounds(new Rectangle(15, 12, 93, 16));
		m_allNamespacesCheckBox.setText("&All Namespaces");
		m_allNamespacesCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_allNamespacesCheckBox.setSelection(false);
		m_allNamespacesCheckBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean sel = m_allNamespacesCheckBox.getSelection();
				m_namespaceFilterTable.setEnabled(!sel);
				m_addNamespaceFilterButton.setEnabled(!sel);
				m_editNamespaceFilterButton.setEnabled(!sel);
				m_removeNamespaceFilterButton.setEnabled(!sel);
			}
		});
	}

	private void createTypeMembersTab()
	{
		m_typeMembersTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_typeMembersTab.setLayout(null);
		m_allMembersCheckBox = new Button(m_typeMembersTab, SWT.CHECK | SWT.LEFT);
		m_allMembersCheckBox.setText("&All Type Members");
		m_allMembersCheckBox.setLocation(new Point(15, 12));
		m_allMembersCheckBox.setSize(new Point(105, 16));
		m_allMembersCheckBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean sel = m_allMembersCheckBox.getSelection();

				m_typeMemberFilterTable.setEnabled(!sel);
				m_addMemberFilterButton.setEnabled(!sel);
				m_editMemberFilterButton.setEnabled(!sel);
				m_removeMemberFilterButton.setEnabled(!sel);
			}
		});
		m_memberFilterListLabel = new Label(m_typeMembersTab, SWT.NONE);
		m_memberFilterListLabel.setText("&Type Members Filter List:");
		m_memberFilterListLabel.setLocation(new Point(15, 40));
		m_memberFilterListLabel.setSize(new Point(125, 16));
		m_typeMemberFilterTable = new Table(m_typeMembersTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_typeMemberFilterTable.setHeaderVisible(true);
		m_typeMemberFilterTable.setLinesVisible(true);
		m_typeMemberFilterTable.setBounds(new Rectangle(15, 57, 476, 272));
		double width = m_typeMemberFilterTable.getBounds().width - 2 * m_typeMemberFilterTable.getBorderWidth();		
		TableColumn column = new TableColumn(m_typeMemberFilterTable, SWT.LEFT, 0);
		column.setWidth((int)(0.3 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		column = new TableColumn(m_typeMemberFilterTable, SWT.LEFT, 1);
		column.setWidth((int)(0.3 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type");
		column = new TableColumn(m_typeMemberFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Categories");
		column = new TableColumn(m_typeMemberFilterTable, SWT.LEFT, 3);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");		
		m_addMemberFilterButton = new Button(m_typeMembersTab, SWT.NONE);
		m_addMemberFilterButton.setBounds(new Rectangle(502, 57, 88, 25));
		m_addMemberFilterButton.setText("A&dd Filter...");
		m_addMemberFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				JavaMemberDialog dialog = new JavaMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
				dialog.open();
			}
		});
		m_editMemberFilterButton = new Button(m_typeMembersTab, SWT.NONE);
		m_editMemberFilterButton.setBounds(new Rectangle(502, 104, 88, 25));
		m_editMemberFilterButton.setText("&Edit Filter...");
		m_removeMemberFilterButton = new Button(m_typeMembersTab, SWT.NONE);
		m_removeMemberFilterButton.setBounds(new Rectangle(502, 152, 88, 25));
		m_removeMemberFilterButton.setText("Re&move Filter");
	}

	private void createLocalDeclsTab()
	{
		m_localDeclsTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_localDeclsTab.setLayout(null);
	}

	private void createTypesTab()
	{
		m_typesTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_typesTab.setLayout(null);
		m_allTypesCheckBox = new Button(m_typesTab, SWT.CHECK | SWT.LEFT);
		m_allTypesCheckBox.setSelection(false);
		m_allTypesCheckBox.setText("&All Types");
		m_allTypesCheckBox.setSize(new Point(62, 16));
		m_allTypesCheckBox.setLocation(new Point(15, 12));
		m_allTypesCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_allTypesCheckBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean sel = m_allTypesCheckBox.getSelection();

				m_typeFilterTable.setEnabled(!sel);
				m_addTypeFilterButton.setEnabled(!sel);
				m_editTypeFilterButton.setEnabled(!sel);
				m_removeTypeFilterButton.setEnabled(!sel);
			}
		});
		m_typeFilterTable = new Table(m_typesTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_typeFilterTable.setHeaderVisible(true);
		m_typeFilterTable.setLinesVisible(true);
		m_typeFilterTable.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_typeFilterTable.setBounds(new Rectangle(15, 57, 476, 272));
		m_typeFilterTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_typeFilterTable.getSelectionIndex() != -1)
				{
					editTypeFilter();
				}
			}			
		});		
		double width = m_typeFilterTable.getBounds().width - 2 * m_typeFilterTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_typeFilterTable, SWT.LEFT, 0);
		column.setWidth((int)(0.4 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 1);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Categories");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 3);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Base types");
		m_typeFilterLabel = new Label(m_typesTab, SWT.NONE);
		m_typeFilterLabel.setBounds(new Rectangle(15, 40, 101, 16));
		m_typeFilterLabel.setText("&Type Filter List:");
		m_typeFilterLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_addTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_addTypeFilterButton.setBounds(new Rectangle(502, 57, 88, 25));
		m_addTypeFilterButton.setToolTipText("Login");
		m_addTypeFilterButton.setSelection(true);
		m_addTypeFilterButton.setText("A&dd Filter...");
		m_addTypeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_addTypeFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				TypeFilterDialog dialog = new TypeFilterDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
				dialog.open();	
				
				if (!dialog.isCancelled())
				{
					TypeFilter filter = new TypeFilter();
					
					filter.m_modifiers = dialog.getModifiers();
					filter.m_categories = dialog.getTypeCategories();
					filter.m_baseTypes = dialog.getBaseTypes();
					filter.m_allBaseTypes = dialog.getAllBaseTypes();
					m_typeFilter.add(filter);
					
					TableItem item = new TableItem(m_typeFilterTable, 0);
					item.setText(0, dialog.getTypeName());
					item.setText(1, typeCategoriesToString(filter.m_categories));
					item.setText(2, modifiersToString(filter.m_modifiers));
					item.setText(3, dialog.getAllBaseTypes() ? "<Any>" : baseTypesToString(filter.m_baseTypes));
				}
			}
		});
		m_editTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_editTypeFilterButton.setBounds(new Rectangle(502, 104, 88, 25));
		m_editTypeFilterButton.setToolTipText("Login");
		m_editTypeFilterButton.setSelection(true);
		m_editTypeFilterButton.setText("&Edit Filter...");
		m_editTypeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_editTypeFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{	
				editTypeFilter();
			}
		});
		m_removeTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_removeTypeFilterButton.setBounds(new Rectangle(502, 152, 88, 25));
		m_removeTypeFilterButton.setToolTipText("Login");
		m_removeTypeFilterButton.setSelection(true);
		m_removeTypeFilterButton.setText("Re&move Filter");
		m_removeTypeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_removeTypeFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_typeFilterTable.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
				}
				else
				{
					if (MessageDialog.openQuestion(m_shell, "Deletion confirmation", 
						"Are you sure you want to delete selected filter?"))
					{
						m_typeFilterTable.remove(sel);
					}
				}				
			}
		});
	}

	protected String typeCategoriesToString(int flags)
	{
		if ((flags & TypeCategory.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
				
		for (TypeCategory f : TypeCategory.values())
		{
			if ((flags & f.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += f.name();
			}
		}
		
		return str;
	}

	protected String modifiersToString(int flags)
	{
		if ((flags & Modifier.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
		
		for (Modifier f : Modifier.values())
		{
			if ((flags & f.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += f.name().toLowerCase();
			}						
		}
		
		return str;
	}

	protected String baseTypesToString(ArrayList<BaseType> types)
	{
		String str = "";
		
		for (BaseType t : types)
		{
			if (str.length() > 0)
				str += ",";
			
			str += t.m_name;
		}
		
		return str;
	}
	
	protected void editNamespaceFilter()
	{
		int sel = m_namespaceFilterTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
		}
		else
		{
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, 
				"Filter", "&Namespace Filter:", m_namespaceFilterTable.getItem(sel).getText(), new NamespaceFilterValidator());
			dialog.open();
			String val = dialog.getValue();
			
			if (val != null)
			{
				m_namespaceFilterTable.getItem(sel).setText(val);
			}							
		}		
	}

	protected void editTypeFilter()
	{
		int sel = m_typeFilterTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");
		}
		else
		{
			TypeFilter filter = m_typeFilter.get(sel);
			TableItem item = m_typeFilterTable.getItem(sel);
			
			TypeFilterDialog dialog = new TypeFilterDialog(PlatformUI.getWorkbench().getDisplay(), m_shell,
				item.getText(0), filter.m_categories, filter.m_modifiers, filter.m_allBaseTypes, filter.m_baseTypes);
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.m_modifiers = dialog.getModifiers();
				filter.m_categories = dialog.getTypeCategories();
				filter.m_baseTypes = dialog.getBaseTypes();
				filter.m_allBaseTypes = dialog.getAllBaseTypes();						

				item.setText(0, dialog.getTypeName());
				item.setText(1, typeCategoriesToString(filter.m_categories));
				item.setText(2, modifiersToString(filter.m_modifiers));
				item.setText(3, dialog.getAllBaseTypes() ? "<Any>" : baseTypesToString(filter.m_baseTypes));
			}
		}
		
	}
	
	protected class TypeFilter
	{
		public int m_categories;
		public int m_modifiers;
		public boolean m_allBaseTypes;
		public ArrayList<BaseType> m_baseTypes;
	}
	
	protected class NamespaceFilterValidator extends InputDialog.MandatoryFieldValidator
	{
		public NamespaceFilterValidator()
		{
			super("Please enter filter");
		}
		
		public boolean validate(Shell shell, String value)
		{
			if (!super.validate(shell, value))
			{
				return false;
			}
			else
			{
				String[] names = value.split("/");
				
				for (String name : names)
				{
					if (name.trim().length() == 0)
					{
						MessageDialog.openError(shell, "Incorrect input", "Namespace section cannot be empty (like \"com//abc\")");
						return false;
					}
					
					if (name.equals("**"))
					{
						continue;
					}
					
					try
					{
						Pattern.compile(name);
					}
					catch(PatternSyntaxException e)
					{
						MessageDialog.openError(shell, "Incorrect input",
							"Pattern \"" + name + "\" has the following error: " + e.getMessage());
						return false;
					}
				}
				
				return true;
			}
		}
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
