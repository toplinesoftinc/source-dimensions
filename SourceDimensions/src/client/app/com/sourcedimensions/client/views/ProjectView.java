package com.sourcedimensions.client.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.actions.EditQueryAction;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.db.DuplicateNameException;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.client.model.QueryNode;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SymbolQuery;


public class ProjectView extends ViewPart 
{
	public final static String ID = "com.sourcedimensions.client.views.ProjectView";
	
	private static TreeViewer m_viewer;
	//private DrillDownAdapter m_drillDownAdapter;
	private static Project m_project;
	private static TreeGroup m_root;
	private static SnapshotGroup m_snapshotGroup;
	private static QueryGroup m_queryGroup;
	
	public abstract static class TreeObject implements IAdaptable 
	{
		protected String m_name;
		protected TreeGroup m_parent;
		protected boolean m_faded = false;

		public TreeObject(String name) 
		{
			m_name = name;
		}

		public String getName() 
		{
			return m_name;
		}
				
		public void setName(String name)
		{
			m_name = name;
		}
		
		public TreeGroup getParent() 
		{
			return m_parent;
		}
		
		public void setParent(TreeGroup parent)
		{
			m_parent = parent; 
		}
		
		public String toString() 
		{
			return getName();
		}
		
		public Object getAdapter(Class key) 
		{
			return null;
		}
		
		public Integer getID()
		{
			return null;
		}
		
		public void setID(int id)
		{			
		}
		
		abstract public Image getImage();
		
		public Boolean isQueryGroup()
		{
			return null;
		}
		
		protected Image getImage(String key)
		{
			if (m_faded)
				return Util.getFadedImage(key);
			else
				return Util.getSharedImage(key);
		}
		
		public void setFading(boolean faded)
		{
			m_faded = faded;
			m_viewer.refresh(this);
		}
	}

	
	public abstract static class TreeGroup extends TreeObject
	{
		protected ArrayList<TreeObject> m_children;

		public TreeGroup(String name)
		{
			super(name);
		}
	
		public boolean addDbChild(TreeObject object)
		{
			if (m_children == null)
			{
				m_children = new ArrayList<TreeObject>();
				load();
				return false;
			}
			
			m_children.add(object);
			object.setParent(this);
			
			return true;
		}
		
		public void addChild(TreeObject object)
		{
			if (m_children == null)
			{
				m_children = new ArrayList<TreeObject>();
				load();
				
				for (TreeObject o : m_children)
					o.setFading(m_faded);				
			}
			
			m_children.add(object);
			object.setFading(m_faded);
			object.setParent(this);		
		}
		
		public void deleteChild(TreeObject object)
		{
			if (m_children == null)
				return;
			
			m_children.remove(object);
		}
		
		public void deleteAllChildren()
		{
			if (m_children == null)
				return;

			m_children.clear();
		}
		
		public TreeObject[] getChildren() 
		{
			if (m_children == null)
			{
				m_children = new ArrayList<TreeObject>();
				load();
				
				for (TreeObject o : m_children)
					o.setFading(m_faded);
			}
			
			return m_children.toArray(new TreeObject[0]);
		}
		
		public boolean hasChildren() 
		{
			if (m_children == null)
				return true;
			else
				return m_children.size() > 0;
		}

		public void deleteObject(String[] path)
		{
			TreeObject obj = findObject(path);
			
			if (obj != null)
			{
				obj.getParent().deleteChild(obj);				
			}
		}
		
		public TreeObject findObject(String[] path)
		{
			TreeObject cur = this;
			
			for (int i = 0; i < path.length; i++)
			{
				boolean found = false;
				
				if (cur instanceof TreeGroup)
				{
					TreeGroup group = (TreeGroup)cur;
					
					for (TreeObject o : group.getChildren())
					{
						if (o.getName().equals(path[i]))
						{
							cur = o;
							found = true;
							break;
						}
					}
										
					if (!found)
						return null;					
				}
				else
					return null;
			}
			
			return cur;
		}
		
		protected abstract void load();
		
		protected List<FolderObject> makeFolderPath(String path, boolean isQuery)
		{
			List<FolderObject> segments = new ArrayList<FolderObject>();
			
			String[] names = path.split(Folder.DIVIDER_REGEX);
			
			if (names.length == 1)
				return segments;
			
			TreeGroup cur = this;
			boolean found = false;
			
			for (int i = 0; i < names.length - 1; i++, found = false)
			{			
				for (Object o : cur.getChildren())
				{
					if (o instanceof FolderObject)
					{
						FolderObject folder = (FolderObject)o;
						
						if (folder.getName().equals(names[i]))
						{
							segments.add(folder);
							cur = folder;
							found = true;
							break;
						}
					}
				}
				
				if (!found)
				{
					for (int j = i; j < names.length - 1; j++)
					{
						Folder folder = null;
						
						try
						{
							Integer parentId;
							
							if (cur instanceof FolderObject)
								parentId = ((FolderObject)cur).getID();
							else
								parentId = null;
							
							folder = DbAdapter.addFolder(names[j], parentId, getProject().getId(), isQuery);
						}
						catch (Exception e)
						{
						}
						
						FolderObject folderObject = new FolderObject(folder.m_name, folder.m_id, isQuery);
						folderObject.initNew();
						cur.addChild(folderObject);
						
						segments.add(folderObject);
						cur = folderObject;				
					}					

					break;
				}
			}
			
			return segments;			
		}
		
		public void setFading(boolean faded)
		{
			super.setFading(faded);
			
			if (m_children != null)
			{
				for (TreeObject o : m_children)
					o.setFading(faded);
			}
		}
	}


	public static class ProjectObject extends TreeObject
	{
		public ProjectObject(Project prj)
		{
			super(prj.getName() + " (" + prj.langName() + ")");
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_PROJECT);
		}
	}

	
	public static class FolderObject extends TreeGroup
	{
		protected Integer m_id;
		boolean m_isQuery;
		
		public FolderObject(String name, Integer id, boolean isQuery)
		{
			super(name);
			m_id = id;
			m_isQuery = isQuery;
		}
		
		protected void load()
		{
			try
			{
				List<Folder> folderList = DbAdapter.getFolderList(m_id, m_project.getId(), m_isQuery);
				
				for (Folder f : folderList)
				{
					FolderObject o = new FolderObject(f.m_name, f.m_id, m_isQuery);
					addChild(o);
				}
				
				if (m_isQuery)
				{
					List<QueryNode> queryList = DbAdapter.getQueryList(m_project.getId(), m_id);
					
					for (QueryNode q : queryList)
					{
						addChild(new QueryObject(q.m_name, q.m_id, m_id));
					}				
				}
				else
				{
					List<Snapshot> snapshotList = DbAdapter.getSnapshotList(m_project.getId(), m_id);
					
					for (Snapshot s : snapshotList)
					{
						addChild(new SnapshotObject(s.getName(), s.m_id, m_id));
					}
				}
			}
			catch (Exception e)
			{				
			}
		}

		public void initNew()
		{
			if (m_children == null)
				m_children = new ArrayList<TreeObject>();
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_FOLDER);
		}
		
		public Integer getID()
		{
			return m_id;
		}
		
		public void setID(int id)
		{
			m_id = id;
		}
		
		public Boolean isQueryGroup()
		{
			return m_isQuery;
		}
	}

	
	public static class RootGroup extends TreeGroup
	{
		public RootGroup()
		{
			super(m_project.getName() + " (" + m_project.langName() + ")");			
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_PROJECT);
		}
		
		protected void load()
		{			
		}
	}
	
	
	public static class ParentPrjGroup extends TreeGroup
	{
		public ParentPrjGroup()
		{
			super("Parent Projects");
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_PARENT);
		}
		
		protected void load()
		{			
		}
	}

	
	public static class QueryGroup extends TreeGroup
	{
		public QueryGroup()
		{
			super("Queries");
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_QUERY_GROUP);
		}

		protected void load()
		{
			try
			{
				List<Folder> list = DbAdapter.getFolderList(null, m_project.getId(), true);
				
				for (Folder f : list)
				{
					FolderObject o = new FolderObject(f.m_name, f.m_id, true);
					addChild(o);
				}			
				
				List<QueryNode> queryList = DbAdapter.getQueryList(m_project.getId(), null);
				
				for (QueryNode q : queryList)
				{
					addChild(new QueryObject(q.m_name, q.m_id, null));
				}
			}
			catch (Exception e)
			{				
			}
		}
		
		public void addQueryNode(SymbolQuery query, Integer queryId, String fullName)
		{		
			List<FolderObject> segments = makeFolderPath(fullName, true);
			Integer folderId = (segments.size() == 0) ? null : segments.get(segments.size() - 1).getID();						
			Integer id = null;
			
			try
			{
				id = DbAdapter.addSymbolQuery(m_project.getId(), folderId, query);
			}
			catch (DuplicateNameException e)
			{				
			}
			catch (Exception e)
			{
				return;
			}
			
			if (id != null)
			{
				QueryObject item = new QueryObject(query.getName(), id, folderId);
				
				TreeGroup target;
				
				if (segments.size() == 0)
					target = this;
				else
					target = segments.get(segments.size() - 1);
				
				if (!target.addDbChild(item))
				{
					for (TreeObject o : target.getChildren())
					{
						if (o instanceof QueryObject)
						{
							QueryObject q = (QueryObject)o;
							
							if (q.getID() == item.getID())
							{
								item = q;
								break;
							}
						}
					}
				}
				
				m_viewer.refresh(this, true);
				m_viewer.setSelection(new TreeSelection(new TreePath(segments.toArray()).createChildPath(item)), true);				
			}			
		}

		public Boolean isQueryGroup()
		{
			return true;
		}		
	}	

	
	public static class SnapshotGroup extends TreeGroup
	{
		public SnapshotGroup()
		{
			super("Snapshots");
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_SNAPSHOT_GROUP);
		}
		
		protected void load()
		{
			try
			{
				List<Folder> list = DbAdapter.getFolderList(null, m_project.getId(), false);
				
				for (Folder f : list)
				{
					addChild(new FolderObject(f.m_name, f.m_id, false));
				}
				
				List<Snapshot> snapshotList = DbAdapter.getSnapshotList(m_project.getId(), null);
				
				for (Snapshot s : snapshotList)
				{
					addChild(new SnapshotObject(s.getName(), s.m_id, null));
				}
			}
			catch (Exception e)
			{				
			}
		}
		
		public void addSnapshotNode(Snapshot node, String name)
		{		
			List<FolderObject> segments = makeFolderPath(name, false);
			Integer folderId = (segments.size() == 0) ? null : segments.get(segments.size() - 1).getID();			
			Integer id = null;
			
			try
			{
				id = DbAdapter.addSnapshot(m_project.getId(), folderId, node);
			}
			catch (DuplicateNameException e)
			{				
			}
			catch (Exception e)
			{
				return;
			}
			
			if (id != null)
			{
				SnapshotObject item = new SnapshotObject(node.getName(), id, folderId);

				TreeGroup target;
				
				if (segments.size() == 0)
					target = this;
				else
					target = segments.get(segments.size() - 1);
				
				if (!target.addDbChild(item))
				{
					for (TreeObject o : target.getChildren())
					{
						if (o instanceof SnapshotObject)
						{
							SnapshotObject s = (SnapshotObject)o;
							
							if (s.getID() == item.getID())
							{
								item = s;
								break;
							}
						}
					}
				}

				m_viewer.refresh(this, true);
				m_viewer.setSelection(new TreeSelection(new TreePath(segments.toArray()).createChildPath(item)), true);				
			}			
		}
		
		public Boolean isQueryGroup()
		{
			return false;
		}		
	}

	
	public static class SnapshotObject extends TreeObject
	{
		protected int m_id;
		protected Integer m_folderId;
		
		public SnapshotObject(String name, int id, Integer folderId)
		{
			super(name);
			m_id = id;
			m_folderId = folderId;
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_SNAPSHOT);
		}
		
		public Integer getID()
		{
			return m_id;
		}
		
		public void setID(int id)
		{
			m_id = id;
		}
		
		public Boolean isQueryGroup()
		{
			return false;
		}
	}

	public static class QueryObject extends TreeObject
	{
		protected int m_id;
		protected Integer m_folderId;
		
		public QueryObject(String name, int id, Integer folderId)
		{
			super(name);
			m_id = id;
			m_folderId = folderId;
		}
		
		public Image getImage()
		{
			return getImage(IImageKeys.IMG_SYMBOL_QUERY);
		}
		
		public Integer getID()
		{
			return m_id;
		}
		
		public void setID(int id)
		{
			m_id = id;
		}		
		
		public Boolean isQueryGroup()
		{
			return true;
		}		
	}

	
	public class ProjectContentProvider implements ITreeContentProvider 
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
		{
		}
		
		public void dispose() 
		{
		}
		
		public Object[] getElements(Object parent) 
		{
			if (parent.equals(getViewSite())) 
			{
				if (m_root != null)
					return new Object[] { m_root };
				else
					return new Object[] {};
			}
			
			return getChildren(parent);
		}
	
		public Object getParent(Object child) 
		{
			if (child instanceof TreeObject) 
			{
				return ((TreeObject)child).getParent();
			}
			
			return null;
		}

		public Object[] getChildren(Object parent) 
		{
			if (parent instanceof TreeGroup) 
			{
				return ((TreeGroup)parent).getChildren();
			}
			
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) 
		{
			if (parent instanceof TreeGroup)
				return ((TreeGroup)parent).hasChildren();
			else
				return false;
		}
	}

	
	public class ProjectLabelProvider extends LabelProvider 
	{
		public String getText(Object obj) 
		{
			return obj.toString();
		}
		
		public Image getImage(Object obj) 
		{
			if (obj instanceof TreeObject)
				return ((TreeObject)obj).getImage();
			else
				return null;
		}
	}

	
	public void setProject(Project project)
	{
		m_project = project;
		
		if (project == null)
			m_root = null;
		else
		{
			initializeTree();
			m_viewer.expandToLevel(2);
		}
				
		m_viewer.refresh();
	}
	
	protected void initializeTree()
	{
		m_root = new RootGroup();	
		
		TreeGroup parents = new ParentPrjGroup();
		m_root.addChild(parents);
		
		if (m_project != null)
		{
			for (Project p : m_project.getParents())
			{
				parents.addChild(new ProjectObject(p));
			}
		}

		m_snapshotGroup = new SnapshotGroup();
		m_root.addChild(m_snapshotGroup);
		
		m_queryGroup = new QueryGroup();
		m_root.addChild(m_queryGroup);
	}
		
	public void createPartControl(Composite parent) 
	{
		m_viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		m_viewer.addOpenListener(new IOpenListener() 
		{
			public void open(OpenEvent event) 
			{
				Object selection = ((StructuredSelection)event.getSelection()).getFirstElement();
				
				if (selection instanceof QueryObject)
				{
					EditQueryAction.runQueryEdit(getSite().getShell(), (QueryObject)selection);
				}
			} 
		});
		
		//m_drillDownAdapter = new DrillDownAdapter(m_viewer);
		m_viewer.setContentProvider(new ProjectContentProvider());
		getSite().setSelectionProvider(m_viewer);
		m_viewer.setLabelProvider(new ProjectLabelProvider());
		m_viewer.setInput(getViewSite());
		
		m_viewer.setComparator(new ViewerComparator()
		{
			public int category(Object element)
			{
				if (element instanceof FolderObject)
					return 1;
				else if (element instanceof SnapshotObject || 
						 element instanceof QueryObject)
					return 2;
				else
					return 0;
			}
		});
		
		MenuManager menuMgr = new MenuManager("");
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS + "_folder"));
		menuMgr.add(new Separator());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS + "_query"));
		Menu menu = menuMgr.createContextMenu(m_viewer.getControl());
		m_viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, m_viewer);
	}

	public void setFocus() 
	{
		m_viewer.getControl().setFocus();
	}
	
	public static Project getProject()
	{
		return m_project;
	}
	
	public static SnapshotGroup getSnapshotGroup()
	{
		return m_snapshotGroup;
	}
	
	public static QueryGroup getQueryGroup()
	{
		return m_queryGroup;
	}
	
	public TreeViewer getViewer()
	{
		return m_viewer;
	}
}