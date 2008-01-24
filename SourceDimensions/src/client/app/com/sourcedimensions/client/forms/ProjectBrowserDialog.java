package com.sourcedimensions.client.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.QueryNode;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.views.ProjectView;


public class ProjectBrowserDialog extends DialogBase 
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,9"
	private Button m_selectButton;
	private Button m_cancelButton;
	private boolean m_isQuery;
	private Map<TreeItem,ItemProps> m_itemMap = new HashMap<TreeItem,ItemProps>();  //  @jve:decl-index=0:
	private String m_resultPath;
	private Integer m_resultID;
	private Tree m_viewer;
	
	
	public ProjectBrowserDialog(Shell parent, boolean isQuery)
	{
		m_isQuery = isQuery;
		createShell(parent);
	}
	
	public ProjectBrowserDialog(Shell parent, boolean isQuery, String path)
	{
		m_isQuery = isQuery;
		createShell(parent);

		String projectId = ProjectView.getProject().getId();
		
		try
		{
			Object sel = DbAdapter.findObject(projectId, path, isQuery);
			
			if (sel != null)
			{
				String[] segments = path.split(Folder.DIVIDER_REGEX);
	
				TreeItem curItem = m_viewer.getItems()[0];
				
				for (int i = 0; i < segments.length; i++)
				{
					m_itemMap.get(curItem).setLoaded();
					curItem.setItemCount(0);
	
					List<Folder> folderList = DbAdapter.getFolderList(m_itemMap.get(curItem).m_id, projectId, isQuery);
					TreeItem cur = null;
					
					for (Folder f : folderList)
					{
						TreeItem item = addItem(curItem, f.m_id, f.m_name, ItemType.FOLDER, Util.getSharedImage(IImageKeys.IMG_FOLDER));
						
						if (f.m_name.equals(segments[i]) && ((i < segments.length - 1) || (sel instanceof Folder)))
							cur = item;
					}
	
					if (m_isQuery)
					{
						List<QueryNode> queryList = DbAdapter.getQueryList(projectId, m_itemMap.get(curItem).m_id);
						
						for (QueryNode q : queryList)
						{
							TreeItem item = addItem(curItem, q.m_id, q.m_name, ItemType.LEAF, Util.getSharedImage(IImageKeys.IMG_SYMBOL_QUERY));
							
							if (q.m_name.equals(segments[i]) && i == segments.length - 1 && !(sel instanceof Folder))
								cur = item; 
						}	
					}
					else
					{
						List<Snapshot> snapshotList = DbAdapter.getSnapshotList(projectId, m_itemMap.get(curItem).m_id);
						
						for (Snapshot s : snapshotList)
						{
							TreeItem item = addItem(curItem, s.m_id, s.getName(), ItemType.LEAF, Util.getSharedImage(IImageKeys.IMG_SNAPSHOT));
							
							if (s.getName().equals(segments[i]) && i == segments.length - 1 && !(sel instanceof Folder))
								cur = item; 
						}
					}
					
					curItem = cur;
				}
	
				m_viewer.showItem(curItem);
				m_viewer.setSelection(curItem);
			}
		}
		catch (Exception e)
		{
		}			
	}

	public String getResultPath()
	{
		return m_resultPath;
	}

	public Integer getResultID()
	{
		return m_resultID;
	}
	
	protected void createShell(Shell parent) 
	{
		m_shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("");
		m_shell.setLayout(null);
		m_shell.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_shell.setVisible(false);
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(373, 388));
		m_selectButton = new Button(m_shell, SWT.NONE);
		m_selectButton.setBounds(new Rectangle(68, 324, 88, 25));
		m_selectButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_selectButton.setSelection(true);
		m_selectButton.setToolTipText("Login");
		m_selectButton.setText("&Select");
		m_shell.setDefaultButton(m_selectButton);
		m_selectButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_viewer.getSelectionCount() == 0)
					MessageDialog.openWarning(m_shell, "Selection", "Please make selection");
				else
					makeSelection();
			}
		});

		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setLocation(new Point(211, 324));
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setFont(new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL));
		m_viewer = new Tree(getShell(), SWT.VIRTUAL | SWT.BORDER);
		m_viewer.setBounds(new Rectangle(16, 13, 333, 299));
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});

		if (m_isQuery)
			m_shell.setText("Query Selection");
		else
			m_shell.setText("Snapshot Selection");
		
		TreeItem item = new TreeItem(m_viewer, SWT.NONE);
		
		if (m_isQuery)
		{
			m_shell.setText("Query Selection");			
			item.setText("Queries");
			item.setImage(Util.getSharedImage(IImageKeys.IMG_QUERY_GROUP));
		}
		else
		{
			m_shell.setText("Snapshot Selection");			
			item.setText("Snapshots");
			item.setImage(Util.getSharedImage(IImageKeys.IMG_SNAPSHOT_GROUP));
		}

		item.setItemCount(1);
		m_itemMap.put(item, new ItemProps());

		m_viewer.addSelectionListener(new SelectionAdapter()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				makeSelection();
			}
		});

		m_viewer.addTreeListener(new TreeAdapter()
		{
			public void treeExpanded(TreeEvent e)
			{
				TreeItem source = (TreeItem)e.item;				
				ItemProps props = m_itemMap.get(source);
				
				if (!props.isLoaded())
				{
					String projectId = ProjectView.getProject().getId();
				
					try
					{
						List<Folder> folderList = DbAdapter.getFolderList(props.getID(), projectId, m_isQuery);
						
						source.setItemCount(0);
						
						for (Folder f : folderList)
						{
							addItem(source, f.m_id, f.m_name, ItemType.FOLDER, Util.getSharedImage(IImageKeys.IMG_FOLDER));
						}
						
						if (m_isQuery)
						{
							List<QueryNode> queryList = DbAdapter.getQueryList(projectId, props.m_id);
							
							for (QueryNode q : queryList)
							{
								addItem(source, q.m_id, q.m_name, ItemType.LEAF, Util.getSharedImage(IImageKeys.IMG_SYMBOL_QUERY));
							}	
						}
						else
						{
							List<Snapshot> snapshotList = DbAdapter.getSnapshotList(projectId, props.m_id);
							
							for (Snapshot s : snapshotList)
							{
								addItem(source, s.m_id, s.getName(), ItemType.LEAF, Util.getSharedImage(IImageKeys.IMG_SNAPSHOT));
							}
						}
						
						props.setLoaded();
					}
					catch (Exception ex)
					{
						return;
					}
				}
			}
		});
		
		super.createShell(parent);
	}
	
	protected TreeItem addItem(TreeItem parent, int id, String name, ItemType type, Image image)
	{
		TreeItem item = new TreeItem(parent, SWT.NONE);
		
		m_itemMap.put(item, new ItemProps(parent, id, type));
		item.setText(name);		
		item.setImage(image);
		
		if (type == ItemType.FOLDER)
		{
			item.setItemCount(1);
		}
		
		return item;
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
	
	protected void makeSelection()
	{
		TreeItem selected = m_viewer.getSelection()[0];
		ItemProps props = m_itemMap.get(selected);
		
		m_resultID = props.getID();
		m_resultPath = "";
		
		while (props.getType() != ItemType.ROOT)
		{					
			if (props.getType() == ItemType.FOLDER)
				m_resultPath = Folder.DIVIDER + m_resultPath;
		
			m_resultPath = selected.getText() + m_resultPath;
			
			selected = props.getParent();
			props = m_itemMap.get(selected);
		}
				
		m_cancel = false;
		getShell().close();			
	}
	
	
	protected enum ItemType
	{
		ROOT,
		FOLDER,
		LEAF
	}
	
	
	protected class ItemProps
	{
		protected Integer m_id;
		protected ItemType m_type;
		protected TreeItem m_parent;
		protected boolean m_isLoaded = false;
		
		public ItemProps()
		{
			m_id = null;
			m_parent = null;
			m_type = ItemType.ROOT;
		}
		
		public ItemProps(TreeItem parent, int id, ItemType type)
		{
			m_id = id;
			m_parent = parent;
			m_type = type;
		}
		
		public Integer getID()
		{
			return m_id;
		}
		
		public ItemType getType()
		{
			return m_type;
		}
		
		public boolean isLoaded()
		{
			return m_isLoaded;
		}
		
		public void setLoaded()
		{
			m_isLoaded = true;
		}
		
		public TreeItem getParent()
		{
			return m_parent;
		}
	}
}
