package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.sourcedimensions.client.TriStateBoolean;
import com.sourcedimensions.client.forms.TypeFilterDialog.BaseType;
import com.sourcedimensions.client.forms.TypeFilterDialog.TypeCategory;
import com.sourcedimensions.client.model.Project.Language;
import com.sourcedimensions.client.views.ProjectView;

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
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-36,-68"
	private Label m_destinationSnapshotLabel;
	private Combo m_comboDestinationSnapshot;
	private Text m_snapshotNameText;
	private Button m_clearSnapshotCheckBox;
	private Label m_snapshotNameLabel;
	private TabFolder m_queryParamsTabFolder;
	private Button m_runQueryButton;
	private Button m_cancelButton;
	private Composite m_namespacesTab;
	private Composite m_membersTab;
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
	private List<TypeFilter> m_typeFilter = new ArrayList<TypeFilter>();
	private List<MemberFilter> m_memberFilter = new ArrayList<MemberFilter>();  //  @jve:decl-index=0:
	private Button m_allMembersCheckBox;
	private Label m_memberFilterListLabel;
	private Table m_memberFilterTable;
	private Button m_addMemberFilterButton;
	private Button m_editMemberFilterButton;
	private Button m_removeMemberFilterButton;
	private Label m_queryNameLabel;
	private Text m_queryNameText;
	private Button m_saveButton;
	
	public SymbolQueryDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Symbol Query");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(680, 536));
		m_shell.setLayout(null);
		m_runQueryButton = new Button(m_shell, SWT.NONE);
		m_saveButton = new Button(getShell(), SWT.NONE);
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_destinationSnapshotLabel = new Label(m_shell, SWT.NONE);
		m_destinationSnapshotLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_destinationSnapshotLabel.setBounds(new Rectangle(122, 17, 115, 14));
		m_destinationSnapshotLabel.setText("&Destination Snapshot:");
		createComboDestinationSnapshot();
		m_clearSnapshotCheckBox = new Button(m_shell, SWT.CHECK | SWT.RIGHT);
		m_snapshotNameLabel = new Label(m_shell, SWT.NONE);
		m_snapshotNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_snapshotNameText.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameText.setSize(new Point(275, 18));
		m_snapshotNameText.setLocation(new Point(385, 32));
		m_clearSnapshotCheckBox.setBounds(new Rectangle(122, 64, 91, 15));
		m_clearSnapshotCheckBox.setEnabled(false);
		m_clearSnapshotCheckBox.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_clearSnapshotCheckBox.setText("Clear &Snapshot");
		m_clearSnapshotCheckBox.setToolTipText("Delete all contents of the snapshot before putting results of this query");
		m_snapshotNameLabel.setBounds(new Rectangle(385, 17, 109, 14));
		m_snapshotNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameLabel.setText("Ne&w Snapshot Name:");
		m_queryNameLabel = new Label(getShell(), SWT.NONE);
		m_queryNameText = new Text(getShell(), SWT.BORDER);
		m_queryNameText.setBounds(new Rectangle(385, 80, 275, 19));
		createQueryParamsTabFolder();
		m_queryNameLabel.setBounds(new Rectangle(385, 66, 69, 13));
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
		
		m_shell.addShellListener(new ShellAdapter() 
		{	
			public void shellClosed(ShellEvent event) 
			{
				event.doit = MessageDialog.openQuestion(m_shell, "Close confirmation", "Do you want to close query window?");
				m_cancel = event.doit;
			}
		});
				
		m_shell.setDefaultButton(m_runQueryButton);
		super.createShell(parent);
	}
	
	private void createComboDestinationSnapshot()
	{
		m_comboDestinationSnapshot = new Combo(m_shell, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL);
		m_comboDestinationSnapshot.setLocation(new Point(122, 32));
		m_comboDestinationSnapshot.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_comboDestinationSnapshot.setText("");
		m_comboDestinationSnapshot.setToolTipText("Snapshot where to put results of this query into");
		m_comboDestinationSnapshot.setVisibleItemCount(10);
		m_comboDestinationSnapshot.setSize(new Point(247, 21));
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
		m_queryParamsTabFolder.setSize(new Point(644, 376));
		createNamespacesTab();
		createMembersTab();
		createTypesTab();
		TabItem tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Namespaces");
		tabItem.setControl(m_namespacesTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Types");
		tabItem.setControl(m_typesTab);		
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Members");
		tabItem.setControl(m_membersTab);
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
		m_namespaceFilterTable.setLocation(new Point(15, 57));
		m_namespaceFilterTable.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_namespaceFilterTable.setSize(new Point(506, 272));
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
		m_addNamespaceFilterButton.setLocation(new Point(535, 57));
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
		m_removeNamespaceFilterButton.setLocation(new Point(535, 152));
		m_removeNamespaceFilterButton.setSize(new Point(88, 25));
		m_removeNamespaceFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_removeNamespaceFilterButton.addSelectionListener(new RemoveFilterAdapter(m_shell, m_namespaceFilterTable));
		m_editNamespaceFilterButton.setToolTipText("Login");
		m_editNamespaceFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_editNamespaceFilterButton.setSize(new Point(88, 25));
		m_editNamespaceFilterButton.setLocation(new Point(535, 104));
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

	private void createMembersTab()
	{
		m_membersTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_membersTab.setLayout(null);
		m_allMembersCheckBox = new Button(m_membersTab, SWT.CHECK | SWT.LEFT);
		m_allMembersCheckBox.setText("&All Members");
		m_allMembersCheckBox.setLocation(new Point(15, 12));
		m_allMembersCheckBox.setSize(new Point(105, 16));
		m_allMembersCheckBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean sel = m_allMembersCheckBox.getSelection();

				m_memberFilterTable.setEnabled(!sel);
				m_addMemberFilterButton.setEnabled(!sel);
				m_editMemberFilterButton.setEnabled(!sel);
				m_removeMemberFilterButton.setEnabled(!sel);
			}
		});
		m_memberFilterListLabel = new Label(m_membersTab, SWT.NONE);
		m_memberFilterListLabel.setText("&Members Filter &List:");
		m_memberFilterListLabel.setLocation(new Point(15, 40));
		m_memberFilterListLabel.setSize(new Point(108, 15));
		m_memberFilterTable = new Table(m_membersTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_memberFilterTable.setHeaderVisible(true);
		m_memberFilterTable.setLocation(new Point(15, 57));
		m_memberFilterTable.setLinesVisible(true);
		m_memberFilterTable.setSize(new Point(506, 272));
		m_memberFilterTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_memberFilterTable.getSelectionIndex() != -1)
				{
					editMemberFilter();
				}
			}			
		});		
		
		double width = m_memberFilterTable.getBounds().width - 2 * m_memberFilterTable.getBorderWidth();		
		TableColumn column = new TableColumn(m_memberFilterTable, SWT.LEFT, 0);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Categories");
		column = new TableColumn(m_memberFilterTable, SWT.LEFT, 1);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");
		column = new TableColumn(m_memberFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.35 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");
		column = new TableColumn(m_memberFilterTable, SWT.LEFT, 3);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type/Return type");
		column = new TableColumn(m_memberFilterTable, SWT.LEFT, 4);
		column.setWidth((int)(0.2 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type Properties");
		
		Language lang = ProjectView.getProject().getLanguage();
		
 		if (lang == Language.CSHARP11 || lang == Language.CSHARP20)
  		{
			column = new TableColumn(m_memberFilterTable, SWT.LEFT, 5);
			column.setWidth((int)(0.2 * width));
			column.setResizable(true);
			column.setMoveable(true);
			column.setText("Operators");			
  		}
		
		m_addMemberFilterButton = new Button(m_membersTab, SWT.NONE);
		m_addMemberFilterButton.setText("A&dd Filter...");
		m_addMemberFilterButton.setLocation(new Point(535, 57));
		m_addMemberFilterButton.setSize(new Point(88, 25));
		m_addMemberFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				TypeMemberDialogBase dialog = null;
				
				Language lang = ProjectView.getProject().getLanguage();
				
		 		switch (lang)
		  		{
		  			case JAVA14:
		  			case JAVA15:
		  				dialog = new JavaMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
		  				break;
		  				
		  			case CSHARP11:
		  			case CSHARP20:
		  				dialog = new CSharpMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell); 
		  		}
				
				dialog.open();
				
				if (!dialog.isCancelled())
				{
					MemberFilter filter = new MemberFilter();
					m_memberFilter.add(filter);
					
					filter.m_categories = dialog.getMemberCategories();
					filter.m_modifiers = dialog.getModifiers();
					filter.m_anyParams = dialog.getAnyParams();
					filter.m_type = dialog.getType();
					if (!filter.m_anyParams)
						filter.m_paramList = dialog.getParams();
					
					TableItem item = new TableItem(m_memberFilterTable, SWT.NONE);
					item.setText(0, memberCategoriesToString(filter.m_categories));
					item.setText(1, modifiersToString(filter.m_modifiers));
					item.setText(2, dialog.getMemberName());
					item.setText(3, filter.m_type.m_name);
					item.setText(4, Type.typePropsToString(filter.m_type.m_typeProps));
										
			 		if (lang == Language.CSHARP11 || lang == Language.CSHARP20)
			  		{
						filter.m_operators = dialog.getOperators();			 			
			 			item.setText(5, operatorsToString(filter.m_operators));
					}		
				}			
			}
		});
		m_editMemberFilterButton = new Button(m_membersTab, SWT.NONE);
		m_editMemberFilterButton.setText("&Edit Filter...");
		m_editMemberFilterButton.setLocation(new Point(535, 104));
		m_editMemberFilterButton.setSize(new Point(88, 25));
		m_editMemberFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editMemberFilter();
			}
		});
		m_removeMemberFilterButton = new Button(m_membersTab, SWT.NONE);
		m_removeMemberFilterButton.setText("Re&move Filter");
		m_removeMemberFilterButton.setLocation(new Point(535, 152));
		m_removeMemberFilterButton.setSize(new Point(88, 25));
		m_removeMemberFilterButton.addSelectionListener(
			new RemoveFilterAdapter(m_shell, m_memberFilterTable, m_memberFilter));
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
		m_typeFilterTable.setLocation(new Point(15, 57));
		m_typeFilterTable.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_typeFilterTable.setSize(new Point(506, 272));
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
		column.setWidth((int)(0.20 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Categories");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 1);
		column.setWidth((int)(0.20 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.35 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");		
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 3);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Base types");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 4);
		column.setWidth((int)(0.15 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Internal");
		m_typeFilterLabel = new Label(m_typesTab, SWT.NONE);
		m_typeFilterLabel.setBounds(new Rectangle(15, 40, 101, 16));
		m_typeFilterLabel.setText("&Type Filter List:");
		m_typeFilterLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_addTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_addTypeFilterButton.setToolTipText("Login");
		m_addTypeFilterButton.setSelection(true);
		m_addTypeFilterButton.setText("A&dd Filter...");
		m_addTypeFilterButton.setSize(new Point(88, 25));
		m_addTypeFilterButton.setLocation(new Point(535, 57));
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
					filter.m_internalType = dialog.getInternalType();
					m_typeFilter.add(filter);
					
					TableItem item = new TableItem(m_typeFilterTable, 0);
					item.setText(0, typeCategoriesToString(filter.m_categories));
					item.setText(1, modifiersToString(filter.m_modifiers));
					item.setText(2, dialog.getTypeName());					
					item.setText(3, dialog.getAllBaseTypes() ? "<Any>" : baseTypesToString(filter.m_baseTypes));
					item.setText(4, m_triStateText[dialog.getInternalType().value()]);
				}
			}
		});
		m_editTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_editTypeFilterButton.setToolTipText("Login");
		m_editTypeFilterButton.setSelection(true);
		m_editTypeFilterButton.setText("&Edit Filter...");
		m_editTypeFilterButton.setSize(new Point(88, 25));
		m_editTypeFilterButton.setLocation(new Point(535, 104));
		m_editTypeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_editTypeFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{	
				editTypeFilter();
			}
		});
		m_removeTypeFilterButton = new Button(m_typesTab, SWT.NONE);
		m_removeTypeFilterButton.setToolTipText("Login");
		m_removeTypeFilterButton.setSelection(true);
		m_removeTypeFilterButton.setText("Re&move Filter");
		m_removeTypeFilterButton.setSize(new Point(88, 25));
		m_removeTypeFilterButton.setLocation(new Point(535, 152));
		m_removeTypeFilterButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_removeTypeFilterButton.addSelectionListener(new RemoveFilterAdapter(m_shell, m_typeFilterTable, m_typeFilter)); 
	}

	protected String typeCategoriesToString(int flags)
	{
		if ((flags & TypeCategory.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
				
		for (TypeCategory t : TypeCategory.values())
		{
			if ((flags & t.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += t.name().replace("_", ".");
			}
		}
		
		return str;
	}
	
	protected String memberCategoriesToString(int flags)
	{
		if ((flags & TypeMemberDialogBase.MemberCategory.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
		
		for (TypeMemberDialogBase.MemberCategory m : TypeMemberDialogBase.MemberCategory.values())
		{
			if ((flags & m.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += m.name().replace("ANONYM_METHOD", "ANONYM.METHOD").replace("_", " ");
			}
		}
		
		return str;
	}
	
	protected String modifiersToString(TriStateMask mask)
	{
		switch (mask.getMask(Modifier.ALL.value()))
		{
			case TRUE:
				return "<ALL>";
				
			case EITHER:
				return "<(ALL)>";		
		}
		
		String str = "";
		
		for (Modifier f : Modifier.values())
		{
			switch (mask.getMask(f.value()))
			{
				case TRUE:
					if (str.length() > 0)
						str += ",";
					
					str += f.name().toLowerCase();
					break;
					 
				case EITHER:
					if (str.length() > 0)
						str += ",";
					
					str += "(" + f.name().toLowerCase() + ")";
			}
		}
		
		return str;		
	}

	protected String operatorsToString(int flags)
	{
		if ((flags & Operator.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
		
		for (Operator op : Operator.values())
		{
			if ((flags & op.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += CSharpMemberDialog.getOperatorName(op);
			}
		}
		
		return str;
	}
	
	protected String baseTypesToString(List<BaseType> types)
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
				item.getText(2), filter.m_categories, filter.m_modifiers, filter.m_internalType, filter.m_allBaseTypes, filter.m_baseTypes);
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.m_modifiers = dialog.getModifiers();
				filter.m_categories = dialog.getTypeCategories();
				filter.m_baseTypes = dialog.getBaseTypes();
				filter.m_allBaseTypes = dialog.getAllBaseTypes();
				filter.m_internalType = dialog.getInternalType();

				item.setText(0, typeCategoriesToString(filter.m_categories));			
				item.setText(1, modifiersToString(filter.m_modifiers));
				item.setText(2, dialog.getTypeName());				
				item.setText(3, filter.m_allBaseTypes ? "<Any>" : baseTypesToString(filter.m_baseTypes));
				item.setText(4, m_triStateText[dialog.getInternalType().value()]);
			}
		}
		
	}
	
	protected void editMemberFilter()
	{
		int sel = m_memberFilterTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");			
		}
		else
		{
			MemberFilter filter = m_memberFilter.get(sel);
			TableItem item = m_memberFilterTable.getItem(sel);
			TypeMemberDialogBase dialog = null;

			Language lang = ProjectView.getProject().getLanguage();			
			
	 		switch (lang)
	  		{
	  			case JAVA14:
	  			case JAVA15:
	  				dialog = new JavaMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell,
	  					item.getText(2), filter.m_categories, filter.m_modifiers, filter.m_type, filter.m_anyParams, filter.m_paramList);
	  				break;
	  				
	  			case CSHARP11:
	  			case CSHARP20:
	  				dialog = new CSharpMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell,
		  				item.getText(2), filter.m_categories, filter.m_modifiers, filter.m_type, filter.m_anyParams, filter.m_operators, filter.m_paramList);
	  		}
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.m_modifiers = dialog.getModifiers();
				filter.m_categories = dialog.getMemberCategories();
				filter.m_anyParams = dialog.m_anyParams;
				filter.m_type = dialog.getType();
				if (!dialog.m_anyParams)
					filter.m_paramList = dialog.getParams();
				
				item.setText(0, memberCategoriesToString(filter.m_categories));
				item.setText(1, modifiersToString(filter.m_modifiers));
				item.setText(2, dialog.getMemberName());
				item.setText(3, dialog.getType().m_name);
				item.setText(4, Type.typePropsToString(dialog.getType().m_typeProps));				
				
		 		if (lang == Language.CSHARP11 || lang == Language.CSHARP20)
		  		{
		 			filter.m_operators = dialog.getOperators();
		 			item.setText(5, operatorsToString(filter.m_operators));
				}
			}
		}
	}
	
	protected class TypeFilter
	{
		public int m_categories;
		public TriStateMask m_modifiers;
		public boolean m_allBaseTypes;
		public TriStateBoolean m_internalType;
		public List<BaseType> m_baseTypes;
	}
	
	protected class MemberFilter
	{
		public int m_categories;
		public TriStateMask m_modifiers;
		public int m_operators;
		public boolean m_anyParams;
		public Type m_type;
		public List<Parameter> m_paramList;
	}

	protected Shell getShell()
	{
		return m_shell;
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
}
