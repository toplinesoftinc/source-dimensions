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
			switch (SnapshotNode.Type.values()[m_type])
			{
				case NAMESPACE:			return Util.getSharedImage(IImageKeys.IMG_NAMESPACE_DECL);
				case CLASS:				return Util.getSharedImage(IImageKeys.IMG_CLASS_DECL);
				case ANONYMOUS_CLASS:	return Util.getSharedImage(IImageKeys.IMG_ANONYM_CLASS_DECL);
				case INTERFACE:			return Util.getSharedImage(IImageKeys.IMG_INTERFACE_DECL);
				case DELEGATE:			return Util.getSharedImage(IImageKeys.IMG_DELEGATE_DECL);
				case ENUM:				return Util.getSharedImage(IImageKeys.IMG_ENUM_DECL);
				case STRUCT:			return Util.getSharedImage(IImageKeys.IMG_STRUCT_DECL);
				case ANNOTATION:		return Util.getSharedImage(IImageKeys.IMG_ANNOT_DECL);
				case ANONYMOUS_METHOD:	return Util.getSharedImage(IImageKeys.IMG_ANONYM_METHOD_DECL);
				case CONSTANT:			return Util.getSharedImage(IImageKeys.IMG_CONST_DECL);
				case CONSTRUCTOR:		return Util.getSharedImage(IImageKeys.IMG_CONSTRUCTOR_DECL);
				case DESTRUCTOR:		return Util.getSharedImage(IImageKeys.IMG_DESTRUCTOR_DECL);
				case ENUM_CONST:		return Util.getSharedImage(IImageKeys.IMG_ENUM_CONST_DECL);
				case EVENT:				return Util.getSharedImage(IImageKeys.IMG_EVENT_DECL);
				case EVENT_ADD:			return Util.getSharedImage(IImageKeys.IMG_EVENT_ADD_DECL); 
				case EVENT_REMOVE:		return Util.getSharedImage(IImageKeys.IMG_EVENT_REMOVE_DECL);
				case FIELD:				return Util.getSharedImage(IImageKeys.IMG_FIELD_DECL);
				case INDEXER:			return Util.getSharedImage(IImageKeys.IMG_INDEXER_DECL);
				case INDEXER_GET:		return Util.getSharedImage(IImageKeys.IMG_INDEXER_GET_DECL);
				case INDEXER_SET:		return Util.getSharedImage(IImageKeys.IMG_INDEXER_SET_DECL);
				case METHOD:			return Util.getSharedImage(IImageKeys.IMG_METHOD_DECL);
				case OPERATOR:			return Util.getSharedImage(IImageKeys.IMG_OPERATOR_DECL);
				case PROPERTY:			return Util.getSharedImage(IImageKeys.IMG_PROPERTY_DECL);
				case PROPERTY_GET:		return Util.getSharedImage(IImageKeys.IMG_PROPERTY_GET_DECL);
				case PROPERTY_SET:		return Util.getSharedImage(IImageKeys.IMG_PROPERTY_SET_DECL);
				case BASE_CLASS:		return Util.getSharedImage(IImageKeys.IMG_BASE_CLASS);
				case BASE_INTERFACE:	return Util.getSharedImage(IImageKeys.IMG_BASE_INTERFACE);
				case LOCAL_DECL:		return Util.getSharedImage(IImageKeys.IMG_LOCAL_DECL);
				case CLASS_REF:			return Util.getSharedImage(IImageKeys.IMG_CLASS_REF);
				case INTERFACE_REF:		return Util.getSharedImage(IImageKeys.IMG_INTERFACE_REF);
				case STRUCT_REF:		return Util.getSharedImage(IImageKeys.IMG_STRUCT_REF);
				case ENUM_REF:			return Util.getSharedImage(IImageKeys.IMG_ENUM_REF);
				case DELEGATE_REF:		return Util.getSharedImage(IImageKeys.IMG_DELEGATE_REF);
			
				default:
					return null;
			}
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
