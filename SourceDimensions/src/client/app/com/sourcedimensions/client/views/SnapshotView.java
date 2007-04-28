package com.sourcedimensions.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;



public class SnapshotView extends EditorPart 
{
	public static final String ID = "com.sourcedimensions.client.views.SnapshotView";
	
	protected static Map<Integer, List<SnapshotView>> m_viewerTable = new HashMap<Integer, List<SnapshotView>>();
	protected TreeViewer m_viewer;
	protected SnapshotNodeObject[] m_root = new SnapshotNodeObject[0];
	protected Snapshot m_snapshot;

	public void doSave(IProgressMonitor monitor) 
	{
	}

	public void doSaveAs() 
	{
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException 
	{
		setSite(site);
		setInput(input);
		
		Integer id = ((Input)input).getID();
		List<SnapshotView> list = m_viewerTable.get(id);
		
		if (list == null)
		{
			list = new ArrayList<SnapshotView>();
			m_viewerTable.put(id, list);
			setPartName(input.getName());
		}
		else
		{
			setPartName(input.getName() + ":" + Integer.toString(list.size() + 1));
		}
		
		list.add(this);
	}

	public boolean isDirty() 
	{
		return false;
	}

	public boolean isSaveAsAllowed() 
	{
		return false;
	}

	
	public class SnapshotNodeObject implements IAdaptable 
	{
		protected String m_name;
		protected SnapshotNodeObject m_parent;
		protected List<SnapshotNodeObject> m_children;
		protected Integer m_id;
		protected int m_type;

		public SnapshotNodeObject(int id, int type, String name) 
		{
			m_id = id;
			m_type = type;
			m_name = name;
		}

		public Integer getID()
		{
			return m_id;
		}

		public int getType()
		{
			return m_type;
		}
		
		public String getName() 
		{
			return m_name;
		}
				
		public void setName(String name)
		{
			m_name = name;
		}
		
		public SnapshotNodeObject getParent() 
		{
			return m_parent;
		}
		
		public void setParent(SnapshotNodeObject parent)
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
		
		public Image getImage()
		{
			// TODO
			
			return Util.getSharedImage(IImageKeys.IMG_SNAPSHOT);
		}
		
		public void addChild(SnapshotNodeObject object)
		{
			if (m_children == null)
			{
				m_children = new ArrayList<SnapshotNodeObject>();
				load();
			}
			
			m_children.add(object);
			object.setParent(this);		
		}
		
		public void deleteChild(SnapshotNodeObject object)
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
		
		public SnapshotNodeObject[] getChildren() 
		{
			if (m_children == null)
			{
				m_children = new ArrayList<SnapshotNodeObject>();
				load();
			}
			
			return m_children.toArray(new SnapshotNodeObject[0]);
		}
		
		public boolean hasChildren() 
		{
			if (m_children == null)
				return true;
			else
				return m_children.size() > 0;
		}
		
		protected void load()
		{		
			if (m_snapshot == null)
				return;

			List<SnapshotNode> list;			
			
			try
			{
				list = DbAdapter.getSnapshotNodeList(m_snapshot.m_id, m_id);
			}
			catch (Exception e)
			{
				return;
			}
			
			m_children = new ArrayList<SnapshotNodeObject>();
			
			for (SnapshotNode node : list)
			{
				SnapshotNodeObject obj = new SnapshotNodeObject(node.m_id,
					node.getType().value(), node.getLabel());
				
				m_children.add(obj);
			}
		}
	}
	
	public void setSnapshot(Snapshot snapshot)
	{
		List<SnapshotNode> root;
		
		m_snapshot = snapshot;
		
		try
		{
			root = DbAdapter.getSnapshotNodeList(m_snapshot.m_id, null);
		}
		catch (Exception e)
		{
			return;
		}
		
		if (root.size() > 0)
		{
			List<SnapshotNode> list;

			try
			{
				list = DbAdapter.getSnapshotNodeList(m_snapshot.m_id, root.get(0).m_id);
			}
			catch (Exception e)
			{
				return;
			}
			
			m_root = new SnapshotNodeObject[list.size()];
			
			for (int i = 0; i < list.size(); i++)
			{
				SnapshotNode node = list.get(i);
				
				m_root[i] = new SnapshotNodeObject(node.m_id,
					node.getType().value(), node.getLabel());
			}
		}
				
		m_viewer.refresh();
	}
	
	
	public class SnapshotContentProvider implements ITreeContentProvider 
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
		{
		}
		
		public void dispose() 
		{
		}
		
		public Object[] getElements(Object parent) 
		{
			if (parent.equals(getEditorSite())) 
			{
				return m_root;
			}
			
			return getChildren(parent);
		}
	
		public Object getParent(Object child) 
		{
			if (child instanceof SnapshotNodeObject) 
			{
				return ((SnapshotNodeObject)child).getParent();
			}
			
			return null;
		}

		public Object[] getChildren(Object parent) 
		{
			if (parent instanceof SnapshotNodeObject) 
			{
				return ((SnapshotNodeObject)parent).getChildren();
			}
			
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) 
		{
			if (parent instanceof SnapshotNodeObject)
				return ((SnapshotNodeObject)parent).hasChildren();
			else
				return false;
		}
	}

	
	public class SnapshotLabelProvider extends LabelProvider 
	{
		public String getText(Object obj) 
		{
			return obj.toString();
		}
		
		public Image getImage(Object obj) 
		{
			if (obj instanceof SnapshotNodeObject)
				return ((SnapshotNodeObject)obj).getImage();
			else
				return null;
		}
	}
	
	
	public void createPartControl(Composite parent) 
	{	
		m_viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		
		m_viewer.setContentProvider(new SnapshotContentProvider());
		getSite().setSelectionProvider(m_viewer);
		m_viewer.setLabelProvider(new SnapshotLabelProvider());
		m_viewer.setInput(getEditorSite());
	}

	public void setFocus() 
	{
	}

	public static void renameSnapshot(Integer id, String name)
	{
		List<SnapshotView> list = m_viewerTable.get(id);
		
		if (list != null)
		{
			for (int i = 0; i < list.size(); i++)
			{
				SnapshotView view = list.get(i);
				
				if (i > 0)
					view.setPartName(name + ":" + Integer.toString(i + 1));
				else
					view.setPartName(name);
			}
		}
	}
	
	public static void closeSnapshot(Integer id)
	{
		List<SnapshotView> list = m_viewerTable.get(id);
		
		if (list != null)
		{
			for (SnapshotView view : list)
			{
				view.getSite().getPage().closeEditor(view, false);
			}
		}
		
		m_viewerTable.remove(id);
	}
	
	public static Map<Integer, List<SnapshotView>> getViewerTable()
	{
		return m_viewerTable;
	}
	
	public void dispose()
	{
		m_viewerTable.remove(((Input)getEditorInput()).getID());
	}
	
	public static class Input implements IEditorInput
	{
		protected SnapshotObject m_node;
		
		public Input(SnapshotObject node)
		{
			m_node = node;
		}

		public SnapshotObject getNode()
		{
			return m_node;
		}
		
		public boolean exists() 
		{
			return false;
		}

		public ImageDescriptor getImageDescriptor() 
		{
			return null;
		}

		public String getName() 
		{
			return m_node.getName();
		}

		public Integer getID()
		{
			return m_node.getID();
		}
		
		public IPersistableElement getPersistable() 
		{
			return null;
		}

		public String getToolTipText() 
		{
			return "";
		}

		public Object getAdapter(Class obj) 
		{
			return null;
		}
		
		public boolean equals(Object obj)
		{
			if (super.equals(obj))
				return true;
			
			if (!(obj instanceof Input))
				return false;
			
			Input other = (Input) obj;
			return (other.getNode().getID()== getNode().getID());
		}
	}
}
