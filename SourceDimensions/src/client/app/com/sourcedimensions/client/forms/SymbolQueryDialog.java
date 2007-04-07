package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Project.Language;
import com.sourcedimensions.client.views.ProjectView;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
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
import com.sourcedimensions.client.model.*;
import com.sourcedimensions.ws.consumer.WSConsumer;


public class SymbolQueryDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="-45,-79"
	private Text m_snapshotNameText;
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
	private List<TypeFilter> m_typeFilter = new ArrayList<TypeFilter>();  //  @jve:decl-index=0:
	private List<MemberFilter> m_memberFilter = new ArrayList<MemberFilter>();  //  @jve:decl-index=0:
	private List<LocalDeclFilter> m_localDeclFilter = new ArrayList<LocalDeclFilter>();
	private Button m_allMembersCheckBox;
	private Label m_memberFilterListLabel;
	private Table m_memberFilterTable;
	private Button m_addMemberFilterButton;
	private Button m_editMemberFilterButton;
	private Button m_removeMemberFilterButton;
	private Label m_queryNameLabel;
	private Text m_queryNameText;
	private Button m_saveButton;
	private Composite m_localDeclarationsTab;
	private Button m_allLocalDeclCheckBox;
	private Table m_localDeclFilterTable;
	private Label m_localDeclFilterLabel;
	private Button m_addFilterButton;
	private Button m_editFilterButton;
	private Button m_removeFilterButton;
	private Button m_snapshotBrowseButton;
	private Button m_queryBrowseButton;
	private boolean m_forceClose;

	public SymbolQueryDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);
	}

	public SymbolQueryDialog(Display display, Shell parent, SymbolQuery query)
	{
		m_display = display;
		createShell(parent);
		
		TableItem item;
		
		m_queryNameText.setText(query.getName());
		
		for (String namespace : query.getNamespaceFilter())
		{
			new TableItem(m_namespaceFilterTable, SWT.NONE).setText(0, namespace);
		}
		
		for (TypeFilter type : query.getTypeFilter())
		{
			item = new TableItem(m_typeFilterTable, SWT.NONE);
			fillTypeFilterItem(item, type);
			m_typeFilter.add(type);
		}
		
		for (MemberFilter member : query.getMemberFilter())
		{
			item = new TableItem(m_memberFilterTable, SWT.NONE);
			fillMemberFilterItem(item, member);
			m_memberFilter.add(member);
		}
		
		for (LocalDeclFilter local : query.getLocalDeclFilter())
		{
			item = new TableItem(m_localDeclFilterTable, SWT.NONE);
			fillLocalDeclFilterItem(item, local);
			m_localDeclFilter.add(local);
		}	
	}
	
	public void setSnapshotName(String name)
	{
		m_snapshotNameText.setText(name);
	}
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Symbol Query");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(680, 494));
		m_shell.setLayout(null);
		m_runQueryButton = new Button(m_shell, SWT.NONE);
		m_saveButton = new Button(getShell(), SWT.NONE);
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_snapshotNameLabel = new Label(m_shell, SWT.NONE);
		m_snapshotNameText = new Text(m_shell, SWT.BORDER | SWT.LEFT);
		m_snapshotNameText.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameText.setSize(new Point(251, 18));
		m_snapshotNameText.setLocation(new Point(334, 23));
		m_snapshotNameLabel.setBounds(new Rectangle(334, 8, 148, 14));
		m_snapshotNameLabel.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotNameLabel.setText("&Destination Snapshot Name:");
		m_snapshotBrowseButton = new Button(getShell(), SWT.NONE);
		m_snapshotBrowseButton.setSize(new Point(72, 23));
		m_snapshotBrowseButton.setText("Browse...");
		m_snapshotBrowseButton.setLocation(new Point(587, 20));
		m_queryNameLabel = new Label(getShell(), SWT.NONE);
		m_queryNameText = new Text(getShell(), SWT.BORDER);
		m_queryNameText.setBounds(new Rectangle(334, 62, 251, 19));
		m_queryBrowseButton = new Button(getShell(), SWT.NONE);
		m_queryBrowseButton.setLocation(new Point(587, 59));
		m_queryBrowseButton.setText("Browse...");
		m_queryBrowseButton.setSize(new Point(72, 23));
		createQueryParamsTabFolder();
		m_queryNameLabel.setText("&Query Name:");
		m_queryNameLabel.setLocation(new Point(334, 48));
		m_queryNameLabel.setSize(new Point(69, 13));
		m_saveButton.setLocation(new Point(110, 16));
		m_saveButton.setSize(new Point(88, 25));
		m_saveButton.setText("Sa&ve");
		m_saveButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String fullName = m_queryNameText.getText().trim();
				
				if (!validatePath(true))
					return;
				
				Object found = DbAdapter.findObject(ProjectView.getProject().getId(), fullName, true);
				
				if (!validateFoundObject(found, true))
					return;
				
				Integer queryId = null;
							
				if (found != null)
				{		
					queryId = ((QueryNode)found).m_id;
					
					DbAdapter.deleteQuery(queryId);						
					ProjectView.getQueryGroup().deleteObject(fullName.split(Folder.DIVIDER_REGEX));
				}
				
				ProjectView.getQueryGroup().addQueryNode(createSymbolQuery(), queryId, fullName);				
			}
		});
		m_cancelButton.setToolTipText("Login");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setLocation(new Point(205, 16));
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
		m_runQueryButton.setLocation(new Point(15, 16));
		m_runQueryButton.setText("&Run Query");
		m_runQueryButton.setSelection(true);
		m_runQueryButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) 
			{
				String fullName = m_snapshotNameText.getText().trim();
				
				if (!validatePath(false))
					return;
				
				Object found = DbAdapter.findObject(ProjectView.getProject().getId(), fullName, false);
				
				if (!validateFoundObject(found, false))
					return;
				
				SymbolQuery query = createSymbolQuery();				
				WSConsumer consumer = new WSConsumer();
				SnapshotNode node;
				
				try
				{
					node = (SnapshotNode)consumer.invokeWebService(PlatformUI.getWorkbench().getDisplay(), 
						m_shell, "runSymbolQuery", new Object[] { query });			
				}
				catch (Exception ex)
				{
					MessageDialog.openError(m_shell, "Web Service Error", ex.getMessage());
					return;
				}
				
				if (consumer.wasCancelled())
				{
					m_forceClose = true;
					m_shell.close();

					return;
				}
				
				if (node == null)
				{
					MessageDialog.openInformation(m_shell, "Query Results", "No item found for the specified query");
					return;
				}
				else
				{
					String[] sections = fullName.split(Folder.DIVIDER_REGEX);
					String name = sections[sections.length - 1];
					
					node.setName(name);

					if (found != null)
					{
						DbAdapter.deleteSnapshot(((SnapshotNode)found).m_id);						
						ProjectView.getSnapshotGroup().deleteObject(sections);
					}
					
					ProjectView.getSnapshotGroup().addSnapshotNode(node, fullName);
				}
				
				m_forceClose = true;
				m_shell.close();
			}
			
		});
		
		m_shell.addShellListener(new ShellAdapter() 
		{	
			public void shellClosed(ShellEvent event) 
			{
				if (!m_forceClose)
				{
					event.doit = MessageDialog.openQuestion(m_shell, "Close confirmation", "Do you want to close query window?");
					m_cancel = event.doit;
				}
				else
				{
					event.doit = true;
				}
			}
		});
				
		m_shell.setDefaultButton(m_runQueryButton);
		super.createShell(parent);
	}
	
	private void createQueryParamsTabFolder()
	{
		m_queryParamsTabFolder = new TabFolder(m_shell, SWT.NONE);
		m_queryParamsTabFolder.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_queryParamsTabFolder.setLocation(new Point(15, 73));
		m_queryParamsTabFolder.setSize(new Point(644, 376));
		createNamespacesTab();
		createMembersTab();
		createTypesTab();
		createLocalDeclarationsTab();
		TabItem tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Namespaces");
		tabItem.setControl(m_namespacesTab);
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Types");
		tabItem.setControl(m_typesTab);		
		tabItem = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem.setText("Members");
		tabItem.setControl(m_membersTab);
		TabItem tabItem4 = new TabItem(m_queryParamsTabFolder, SWT.NONE);
		tabItem4.setText("Local Declarations");
		tabItem4.setControl(m_localDeclarationsTab);
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
		m_namespaceFilterLabel.setBounds(new Rectangle(15, 41, 115, 15));
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
		
		Language lang = ProjectView.getProject().language();
		
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
				
				Language lang = ProjectView.getProject().language();
				
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
					
					filter.setCategories(dialog.getMemberCategories());
					filter.setModifiers(dialog.getModifiers());
					filter.setAnyParams(dialog.getAnyParams());
					filter.setType(dialog.getType());
					filter.setParamList(dialog.getParams());
					filter.setName(dialog.getMemberName());
					
					if (dialog instanceof JavaMemberDialog)
					{
						JavaMemberDialog javaDialog = (JavaMemberDialog)dialog;
						
						filter.setAnyThrows(javaDialog.getAnyThrow());
						filter.setThrowList(javaDialog.getThrowList());
					}
					
					TableItem item = new TableItem(m_memberFilterTable, SWT.NONE);
					
					fillMemberFilterItem(item, filter);
										
			 		if (lang == Language.CSHARP11 || lang == Language.CSHARP20)
			  		{
						filter.setOperators(dialog.getOperators());			 			
			 			item.setText(5, operatorsToString(filter.getOperators()));
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
		column.setWidth((int)(0.18 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Modifiers");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.30 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");		
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 3);
		column.setWidth((int)(0.20 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Base types");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 4);
		column.setWidth((int)(0.12 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Inner");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 5);
		column.setWidth((int)(0.12 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Super");
		column = new TableColumn(m_typeFilterTable, SWT.LEFT, 6);
		column.setWidth((int)(0.13 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Subtypes");		
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
					
					filter.setModifiers(dialog.getModifiers());
					filter.setCategories(dialog.getTypeCategories());
					filter.setBaseTypes(dialog.getBaseTypes());
					filter.setAllBaseTypes(dialog.getAllBaseTypes());
					filter.setInnerTypes(dialog.getInnerTypes());
					filter.setSupertypes(dialog.getSupertypes());
					filter.setSubtypes(dialog.getSubtypes());					
					filter.setDelegate(dialog.getDelegate());
					filter.setName(dialog.getTypeName());
					m_typeFilter.add(filter);
					
					TableItem item = new TableItem(m_typeFilterTable, 0);
					
					fillTypeFilterItem(item, filter);
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
				
				str += t.toString();
			}
		}
		
		return str;
	}
	
	protected String memberCategoriesToString(int flags)
	{
		if ((flags & MemberCategory.ALL.value()) != 0)
			return "<All>";
		
		String str = "";
		
		for (MemberCategory m : MemberCategory.values())
		{
			if ((flags & m.value()) != 0)
			{
				if (str.length() > 0)
					str += ",";
				
				str += m.toString();
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
		
		for (Modifier m : Modifier.values())
		{
			switch (mask.getMask(m.value()))
			{
				case TRUE:
					if (str.length() > 0)
						str += ",";
					
					str += m.toString();
					break;
					 
				case EITHER:
					if (str.length() > 0)
						str += ",";
					
					str += "(" + m.toString() + ")";
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
			
			str += t.getName();
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
				filter.getName(), filter.getCategories(), filter.getModifiers(), filter.getInnerTypes(), filter.getSupertypes(),
				filter.getSubtypes(), filter.getAllBaseTypes(), filter.getBaseTypes(), filter.getDelegate());
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.setModifiers(dialog.getModifiers());
				filter.setCategories(dialog.getTypeCategories());
				filter.setBaseTypes(dialog.getBaseTypes());
				filter.setAllBaseTypes(dialog.getAllBaseTypes());
				filter.setInnerTypes(dialog.getInnerTypes());
				filter.setSupertypes(dialog.getSupertypes());
				filter.setSubtypes(dialog.getSubtypes());
				filter.setDelegate(dialog.getDelegate());
				filter.setName(dialog.getTypeName());

				fillTypeFilterItem(item, filter);
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

			Language lang = ProjectView.getProject().language();			
			
	 		switch (lang)
	  		{
	  			case JAVA14:
	  			case JAVA15:
	  				dialog = new JavaMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, filter.getName(), filter.getCategories(), 
	  					filter.getModifiers(), filter.getType(), filter.getAnyParams(), filter.getParamList(), filter.getAnyThrows(), filter.getThrowList());
	  				break;
	  				
	  			case CSHARP11:
	  			case CSHARP20:
	  				dialog = new CSharpMemberDialog(PlatformUI.getWorkbench().getDisplay(), m_shell, filter.getName(), 
	  					filter.getCategories(), filter.getModifiers(), filter.getType(), filter.getAnyParams(), filter.getOperators(), filter.getParamList());
	  		}
			
			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.setModifiers(dialog.getModifiers());
				filter.setCategories(dialog.getMemberCategories());
				filter.setAnyParams(dialog.m_anyParams);
				filter.setType(dialog.getType());
				filter.setParamList(dialog.getParams());
				filter.setName(dialog.getMemberName());
				
				if (dialog instanceof JavaMemberDialog)
				{
					JavaMemberDialog javaDialog = (JavaMemberDialog)dialog;
					
					filter.setAnyThrows(javaDialog.getAnyThrow());
					filter.setThrowList(javaDialog.getThrowList());
				}
				
				fillMemberFilterItem(item, filter);
				
		 		if (lang == Language.CSHARP11 || lang == Language.CSHARP20)
		  		{
		 			filter.setOperators(dialog.getOperators());
		 			item.setText(5, operatorsToString(filter.getOperators()));
				}
			}
		}
	}
	
	protected void editLocalDeclFilter()
	{
		int sel = m_localDeclFilterTable.getSelectionIndex();
		
		if (sel == -1)
		{
			MessageDialog.openWarning(m_shell, "Selection", "Please select filter");			
		}
		else
		{
			LocalDeclFilter filter = m_localDeclFilter.get(sel);
			TableItem item = m_localDeclFilterTable.getItem(sel);

			LocalDeclDialog  dialog = new LocalDeclDialog(PlatformUI.getWorkbench().getDisplay(), m_shell,
				filter.getType(), filter.getName(), filter.getFinal());

			dialog.open();
			
			if (!dialog.isCancelled())
			{
				filter.setType(dialog.getType());
				filter.setName(dialog.getName());
				filter.setFinal(dialog.getFinal());
				
				fillLocalDeclFilterItem(item, filter);
			}			
		}
	}

	protected boolean validatePath(boolean isQuery)
	{		
		String fullName;
		String[] segments;
		String entityName = isQuery ? "Query Name" : "Snapshot Name"; 
		
		if (isQuery)
			fullName = m_queryNameText.getText().trim();
		else
			fullName = m_snapshotNameText.getText().trim();
		
		StringBuffer buf = new StringBuffer(fullName);
		buf.reverse();
		
		segments = buf.toString().split(Folder.DIVIDER_REGEX);
		
		if (fullName.length() == 0 || segments.length == 0)
		{
			MessageDialog.openError(m_shell, "Incorrect Input", "Please enter " + entityName);
			return false;
		}
		
		for (String s : segments)
		{
			if (s.trim().length() == 0)
			{
				MessageDialog.openError(m_shell, "Incorrect Input", entityName +	" is incorrect. The name should have " +
					"path components separated by path divider characters " + Folder.DIVIDER_REGEX.replaceAll("[\\x5B\\x5D]", "\"") +
					". Terminating and several consecutive path divider characters are not allowed.");
				
				return false;
			}
		}
						
		return true;
	}
	
	protected boolean validateFoundObject(Object found, boolean isQuery)
	{
		String entityName = isQuery ? "query" : "snapshot";
		
		if (found != null)
		{
			if (MessageDialog.openQuestion(m_shell, "Overwrite confirmation", "There is a " + entityName + 
					" with the same name which will be deleted and re-created with new contents. " + 
					"Do you want to continue?"))
				return true;
			else
				return false;
		}
		else
			return true;
	}
	
	protected SymbolQuery createSymbolQuery()
	{
		SymbolQuery query = new SymbolQuery();
		
		String[] segments = m_queryNameText.getText().split(Folder.DIVIDER_REGEX);
		
		if (segments.length > 0)
			query.setName(segments[segments.length-1]);
		
		query.setAllNamespaces(m_allNamespacesCheckBox.getSelection());
		
		if (!query.getAllNamespaces())
		{
			List<String> list = new ArrayList<String>();
			query.setNamespaceFilter(list);
			
			for (TableItem item : m_namespaceFilterTable.getItems())
			{
				list.add(item.getText(0));
			}
		}
		
		query.setAllTypes(m_allTypesCheckBox.getSelection());
		
		if (!query.getAllTypes())
		{
			query.setTypeFilter(m_typeFilter);
		}
		
		query.setAllMembers(m_allMembersCheckBox.getSelection());
		
		if (!query.getAllMembers())
		{
			query.setMemberFilter(m_memberFilter);
		}
		
		query.setAllLocalDecls(m_allLocalDeclCheckBox.getSelection());
		
		if (!query.getAllLocalDecls())
		{
			query.setLocalDeclFilter(m_localDeclFilter);
		}

		return query;
	}
		
	protected Shell getShell()
	{
		return m_shell;
	}	

	protected void fillMemberFilterItem(TableItem item, MemberFilter filter)
	{
		item.setText(0, memberCategoriesToString(filter.getCategories()));
		item.setText(1, modifiersToString(filter.getModifiers()));
		item.setText(2, filter.getName());
		item.setText(3, filter.getType().getName());
		item.setText(4, filter.getType().typePropsToString());		
	}
	
	protected void fillTypeFilterItem(TableItem item, TypeFilter filter)
	{
		item.setText(0, typeCategoriesToString(filter.getCategories()));
		item.setText(1, modifiersToString(filter.getModifiers()));
		item.setText(2, filter.getName());					
		item.setText(3, filter.getAllBaseTypes() ? "<Any>" : baseTypesToString(filter.getBaseTypes()));
		item.setText(4, m_triStateText[filter.getInnerTypes().value()]);
		item.setText(5, m_triStateText[filter.getSupertypes().value()]);
		item.setText(6, m_triStateText[filter.getSubtypes().value()]);		
	}
	
	protected void fillLocalDeclFilterItem(TableItem item, LocalDeclFilter filter)
	{
		item.setText(0, filter.getType().getName());
		item.setText(1, filter.getType().typePropsToString());
		item.setText(2, filter.getName());		
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
				try
				{
					Type.validateTypeName(value);
				}
				catch (Type.EmptyNameSectionException e)
				{
					MessageDialog.openError(shell, "Incorrect Input", "Namespace is incorrect. The string should have " +
							"namespace components separated by divider character \"/\" " +
							"Starting/terminating and several consecutive divider characters are not allowed.");

					return false;					
				}
				catch (PatternSyntaxException e)
				{
					MessageDialog.openError(shell, "Incorrect input",
							"Pattern \"" + e.getPattern() + "\" has the following error: " + e.getMessage());
					
					return false;
				}
				
				return true;
			}
		}
	}

	private void createLocalDeclarationsTab() 
	{
		m_localDeclarationsTab = new Composite(m_queryParamsTabFolder, SWT.NONE);
		m_allLocalDeclCheckBox = new Button(m_localDeclarationsTab, SWT.CHECK);
		m_allLocalDeclCheckBox.setSize(new Point(124, 16));
		m_allLocalDeclCheckBox.setText("&All Local Declarations");
		m_allLocalDeclCheckBox.setLocation(new Point(15, 12));
		m_allLocalDeclCheckBox.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean sel = m_allLocalDeclCheckBox.getSelection();
				
				m_localDeclFilterTable.setEnabled(!sel);
				m_addFilterButton.setEnabled(!sel);
				m_editFilterButton.setEnabled(!sel);
				m_removeFilterButton.setEnabled(!sel);
			}
		});
		m_localDeclFilterTable = new Table(m_localDeclarationsTab, SWT.BORDER | SWT.FULL_SELECTION);
		m_localDeclFilterTable.setHeaderVisible(true);
		m_localDeclFilterTable.setLinesVisible(true);
		m_localDeclFilterTable.setLocation(new Point(15, 57));
		m_localDeclFilterTable.setSize(new Point(506, 272));
		m_localDeclFilterTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				if (m_localDeclFilterTable.getSelectionIndex() != -1)
				{
					editLocalDeclFilter();
				}
			}			
		});
		
		double width = m_localDeclFilterTable.getBounds().width - 2 * m_typeFilterTable.getBorderWidth(); 
		TableColumn column = new TableColumn(m_localDeclFilterTable, SWT.LEFT, 0);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type");		
		column = new TableColumn(m_localDeclFilterTable, SWT.LEFT, 1);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Type Properties");		
		column = new TableColumn(m_localDeclFilterTable, SWT.LEFT, 2);
		column.setWidth((int)(0.5 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Name");		
		m_localDeclFilterLabel = new Label(m_localDeclarationsTab, SWT.NONE);
		m_localDeclFilterLabel.setText("&Local Declarations Filter:");
		m_localDeclFilterLabel.setLocation(new Point(15, 40));
		m_localDeclFilterLabel.setSize(new Point(129, 15));
		m_addFilterButton = new Button(m_localDeclarationsTab, SWT.NONE);
		m_addFilterButton.setLocation(new Point(535, 57));
		m_addFilterButton.setText("A&dd Filter...");
		m_addFilterButton.setSize(new Point(88, 25));
		m_addFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				LocalDeclDialog dialog = new LocalDeclDialog(PlatformUI.getWorkbench().getDisplay(), m_shell);
				dialog.open();	
				
				if (!dialog.isCancelled())
				{
					LocalDeclFilter filter = new LocalDeclFilter();
					
					filter.setType(dialog.getType());
					filter.setName(dialog.getName());
					filter.setFinal(dialog.getFinal());
					m_localDeclFilter.add(filter);
					
					TableItem item = new TableItem(m_localDeclFilterTable, 0);
					
					fillLocalDeclFilterItem(item, filter);
				}
			}
		});
		m_editFilterButton = new Button(m_localDeclarationsTab, SWT.NONE);
		m_editFilterButton.setLocation(new Point(535, 104));
		m_editFilterButton.setText("&Edit Filter...");
		m_editFilterButton.setSize(new Point(88, 25));
		m_editFilterButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				editLocalDeclFilter();
			}
		});
		m_removeFilterButton = new Button(m_localDeclarationsTab, SWT.NONE);
		m_removeFilterButton.setLocation(new Point(535, 152));
		m_removeFilterButton.setText("Re&move Filter");
		m_removeFilterButton.setSize(new Point(88, 25));
		m_removeFilterButton.addSelectionListener(
			new RemoveFilterAdapter(m_shell, m_localDeclFilterTable, m_localDeclFilter)); 
	}
	
	public void open()
	{
		m_forceClose = false;
		super.open();
	}
}
