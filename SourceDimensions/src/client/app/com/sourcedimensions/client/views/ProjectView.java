package com.sourcedimensions.client.views;

import java.util.ArrayList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.model.Project;


public class ProjectView extends ViewPart 
{
	public final static String ID = "com.sourcedimensions.client.views.ProjectView";
	
	private TreeViewer m_viewer;
	private DrillDownAdapter m_drillDownAdapter;
	private Project m_project;
	private TreeGroup m_root;

	
	abstract class TreeObject implements IAdaptable 
	{
		protected String m_name;
		protected TreeGroup m_parent;

		public TreeObject(String name) 
		{
			m_name = name;
		}

		public String getName() 
		{
			return m_name;
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
		
		abstract public Image getImage();
	}

	
	abstract class TreeGroup extends TreeObject
	{
		protected ArrayList<TreeObject> m_children = new ArrayList<TreeObject>();

		public TreeGroup(String name)
		{
			super(name);
		}
	
		public void addChild(TreeObject object)
		{
			m_children.add(object);
			object.setParent(this);
		}
		
		public Object[] getChildren() 
		{
			return m_children.toArray();
		}
		
		public boolean hasChildren() 
		{
			return m_children.size()>0;
		}
	}


	public class ProjectObject extends TreeObject
	{
		public ProjectObject(Project prj)
		{
			super(prj.m_name);
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_PROJECT);
		}
	}

	
	public class FolderObject extends TreeObject
	{
		public FolderObject(String name)
		{
			super(name);
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_FOLDER);
		}
	}

	
	public class RootGroup extends TreeGroup
	{
		public RootGroup()
		{
			super(m_project.m_name);			
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_PROJECT);
		}
	}
	
	
	public class ParentPrjGroup extends TreeGroup
	{
		public ParentPrjGroup()
		{
			super("Parent Projects");
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_PARENT);
		}
	}
	

	public class SnapshotGroup extends TreeGroup
	{
		public SnapshotGroup()
		{
			super("Snapshots");
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_SNAPSHOT);
		}
	}
		
	
	public class FolderGroup extends TreeGroup
	{
		public FolderGroup(String name)
		{
			super(name);
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_FOLDER);
		}
	}
	
	
	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider 
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
		
			return false;
		}
	}

	
	class ViewLabelProvider extends LabelProvider 
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
			for (Project p : m_project.m_parents)
			{
				parents.addChild(new ProjectObject(p));
			}
		}
		
		TreeGroup snapshot = new SnapshotGroup();
		m_root.addChild(snapshot);
	}
	
	public void createPartControl(Composite parent) 
	{
		m_viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		m_drillDownAdapter = new DrillDownAdapter(m_viewer);
		m_viewer.setContentProvider(new ViewContentProvider());
		m_viewer.setLabelProvider(new ViewLabelProvider());
		m_viewer.setInput(getViewSite());
	}

	public void setFocus() 
	{
		m_viewer.getControl().setFocus();
	}
}