package com.sourcedimensions.client.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
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

		protected TreeObject()
		{			
		}
		
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

	
	class PrjObject extends TreeObject
	{
		public PrjObject(Project prj)
		{
			super(prj.m_name);
		}
		
		public Image getImage()
		{
			return Util.getSharedImage(IImageKeys.IMG_PROJECT);
		}
	}
	
	
	public enum GroupType 
	{
		ROOT,
		PARENT_PROJECTS,
		SNAPSHOTS
	}
	
	
	class TreeGroup extends TreeObject
	{
		protected ArrayList<TreeObject> m_children = new ArrayList<TreeObject>();
		protected GroupType m_type;

		public TreeGroup(GroupType type) 
		{
			m_type = type;
			
			switch (type)
			{
				case ROOT:
					if (m_project != null)
						m_name = m_project.m_name;
					break;
					
				case PARENT_PROJECTS:
					m_name = "Parent projects";
					break;
					
				case SNAPSHOTS:
					m_name = "Snapshots";
					break;
			}
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
		
		public GroupType getGroupType()
		{
			return m_type;
		}
		
		public Image getImage()
		{
			String key;
			
			switch (m_type)
			{
				case ROOT:
					key = IImageKeys.IMG_PROJECT;
					break;

				case PARENT_PROJECTS:
					key = IImageKeys.IMG_PARENT;
					break;

				default:
					return null;
			}
			
			return Util.getSharedImage(key);
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
		initializeTree();
		m_viewer.expandToLevel(2);
	}
	
	protected void initializeTree()
	{
		m_root = new TreeGroup(GroupType.ROOT);	
		TreeGroup parents = new TreeGroup(GroupType.PARENT_PROJECTS);
		
		m_root.addChild(parents);
		
		if (m_project != null)
		{
			for (Project p : m_project.m_parents)
			{
				parents.addChild(new PrjObject(p));
			}
		}
		
		m_viewer.refresh();
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