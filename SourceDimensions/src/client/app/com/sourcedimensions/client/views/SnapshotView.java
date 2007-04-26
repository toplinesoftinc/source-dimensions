package com.sourcedimensions.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;


public class SnapshotView extends EditorPart 
{
	public static final String ID = "com.sourcedimensions.client.views.SnapshotView";
	
	protected static Map<Integer, List<SnapshotView>> m_viewerTable = new HashMap<Integer, List<SnapshotView>>();

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

	public void createPartControl(Composite parent) 
	{		
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
