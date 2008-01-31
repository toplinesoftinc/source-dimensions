package com.sourcedimensions.client.views;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.compare.Splitter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.actions.ShowSourceAction;
import com.sourcedimensions.client.actions.SubqueryAction;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.client.model.SnapshotNode.Reference;
import com.sourcedimensions.client.views.ProjectView.QueryObject;
import com.sourcedimensions.client.views.ProjectView.SnapshotObject;



public class SnapshotView extends EditorPart 
{
	public static final String ID = "com.sourcedimensions.client.views.SnapshotView";
	
	protected static Map<Integer, List<SnapshotView>> m_viewerTable = new HashMap<Integer, List<SnapshotView>>();
	protected TreeViewer m_treeViewer;
	protected SnapshotNodeTreeItem[] m_root = new SnapshotNodeTreeItem[0];
	protected Snapshot m_snapshot;
	protected TextViewer m_textViewer;
	protected Splitter m_topSplitter;
	protected Splitter m_textAreaSplitter;
	protected org.eclipse.swt.widgets.List m_fileList;
	protected Map<String, Reference> m_refMap;
	protected List<String> m_fileIDs;
	
	
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

	
	public class SnapshotNodeTreeItem implements IAdaptable 
	{
		protected Integer m_id;
		protected String m_name;
		protected SnapshotNode.Type m_type;
		protected SnapshotNodeTreeItem m_parent;
		protected List<SnapshotNodeTreeItem> m_children;

		public SnapshotNodeTreeItem(SnapshotNode node) 
		{
			m_id = node.getID();
			m_name = node.getLabel();
			m_type = node.getType();
		}

		public Integer getID()
		{
			return m_id;
		}

		public SnapshotNode.Type getType()
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
		
		public TreeViewer getTreeViewer()
		{
			return m_treeViewer;
		}
		
		public SnapshotView getSnapshotView()
		{
			return SnapshotView.this;
		}
		
		public SnapshotNodeTreeItem getParent() 
		{
			return m_parent;
		}
		
		public void setParent(SnapshotNodeTreeItem parent)
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
			switch (m_type)
			{
				case GLOBALNAMESPACE:	return Util.getSharedImage(IImageKeys.IMG_GLOBAL_NAMESPACE_DECL);			
				case NAMESPACE:			return Util.getSharedImage(IImageKeys.IMG_NAMESPACE_DECL);
				case CLASS:				return Util.getSharedImage(IImageKeys.IMG_CLASS_DECL);
				case ANONYMCLASS:		return Util.getSharedImage(IImageKeys.IMG_ANONYM_CLASS_DECL);
				case INTERFACE:			return Util.getSharedImage(IImageKeys.IMG_INTERFACE_DECL);
				case DELEGATE:			return Util.getSharedImage(IImageKeys.IMG_DELEGATE_DECL);
				case ENUM:				return Util.getSharedImage(IImageKeys.IMG_ENUM_DECL);
				case STRUCT:			return Util.getSharedImage(IImageKeys.IMG_STRUCT_DECL);
				case ANNOT:				return Util.getSharedImage(IImageKeys.IMG_ANNOT_DECL);
				case ANONYMMETHOD:		return Util.getSharedImage(IImageKeys.IMG_ANONYM_METHOD_DECL);
				case CONST:				return Util.getSharedImage(IImageKeys.IMG_CONST_DECL);
				case CONSTRUCTOR:		return Util.getSharedImage(IImageKeys.IMG_CONSTRUCTOR_DECL);
				case DESTRUCTOR:		return Util.getSharedImage(IImageKeys.IMG_DESTRUCTOR_DECL);
				case ENUMCONST:			return Util.getSharedImage(IImageKeys.IMG_ENUM_CONST_DECL);
				case EVENT:				return Util.getSharedImage(IImageKeys.IMG_EVENT_DECL);
				case EVENTADD:			return Util.getSharedImage(IImageKeys.IMG_EVENT_ADD_DECL); 
				case EVENTREMOVE:		return Util.getSharedImage(IImageKeys.IMG_EVENT_REMOVE_DECL);
				case FIELD:				return Util.getSharedImage(IImageKeys.IMG_FIELD_DECL);
				case INDEXER:			return Util.getSharedImage(IImageKeys.IMG_INDEXER_DECL);
				case INDEXERGET:		return Util.getSharedImage(IImageKeys.IMG_INDEXER_GET_DECL);
				case INDEXERSET:		return Util.getSharedImage(IImageKeys.IMG_INDEXER_SET_DECL);
				case METHOD:			return Util.getSharedImage(IImageKeys.IMG_METHOD_DECL);
				case OPERATOR:			return Util.getSharedImage(IImageKeys.IMG_OPERATOR_DECL);
				case PROPERTY:			return Util.getSharedImage(IImageKeys.IMG_PROPERTY_DECL);
				case PROPERTYGET:		return Util.getSharedImage(IImageKeys.IMG_PROPERTY_GET_DECL);
				case PROPERTYSET:		return Util.getSharedImage(IImageKeys.IMG_PROPERTY_SET_DECL);
				case BASECLASS:			return Util.getSharedImage(IImageKeys.IMG_BASE_CLASS);
				case BASEINTERFACE:		return Util.getSharedImage(IImageKeys.IMG_BASE_INTERFACE);
				case LOCAL:				return Util.getSharedImage(IImageKeys.IMG_LOCAL_DECL);
				case CLASSREF:			return Util.getSharedImage(IImageKeys.IMG_CLASS_REF);
				case INTERFACEREF:		return Util.getSharedImage(IImageKeys.IMG_INTERFACE_REF);
				case STRUCTREF:			return Util.getSharedImage(IImageKeys.IMG_STRUCT_REF);
				case ENUMREF:			return Util.getSharedImage(IImageKeys.IMG_ENUM_REF);
				case DELEGATEREF:		return Util.getSharedImage(IImageKeys.IMG_DELEGATE_REF);
				case FIXEDSIZEBUFFER:	return Util.getSharedImage(IImageKeys.IMG_FIXEDSIZEBUF_DECL);
				case INITBLOCK:			return Util.getSharedImage(IImageKeys.IMG_INIT_BLOCK);
			
				default:
					return null;
			}
		}
		
		public void addChild(SnapshotNodeTreeItem object)
		{
			if (m_children == null)
				m_children = new ArrayList<SnapshotNodeTreeItem>();
			
			m_children.add(object);
			object.setParent(this);		
		}
		
		public void deleteChild(SnapshotNodeTreeItem object)
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
		
		public void invalidate()
		{
			m_children = null;
		}
		
		public SnapshotNodeTreeItem[] getChildren() 
		{
			if (m_children == null)
			{
				m_children = new ArrayList<SnapshotNodeTreeItem>();
				load();
			}
			
			return m_children.toArray(new SnapshotNodeTreeItem[0]);
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
				list = DbAdapter.getSnapshotNodeChildList(m_snapshot.m_id, getID());
			}
			catch (Exception e)
			{
				return;
			}
			
			for (SnapshotNode node : list)
			{
				SnapshotNodeTreeItem obj = new SnapshotNodeTreeItem(node);
				
				addChild(obj);
			}
		}
	}
	
	
	public void setSnapshot(Snapshot snapshot)
	{
		m_snapshot = snapshot;
		refreshView();
	}

	
	public void refreshView()
	{
		List<SnapshotNode> root;
		
		try
		{
			root = DbAdapter.getSnapshotNodeChildList(m_snapshot.m_id, null);
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
				list = DbAdapter.getSnapshotNodeChildList(m_snapshot.m_id, root.get(0).getID());
			}
			catch (Exception e)
			{
				return;
			}
			
			m_root = new SnapshotNodeTreeItem[list.size()];
			
			for (int i = 0; i < list.size(); i++)
			{
				SnapshotNode node = list.get(i);
				
				m_root[i] = new SnapshotNodeTreeItem(node);
			}
		}
				
		m_treeViewer.refresh();		
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
			if (child instanceof SnapshotNodeTreeItem) 
			{
				return ((SnapshotNodeTreeItem)child).getParent();
			}
			
			return null;
		}

		public Object[] getChildren(Object parent) 
		{
			if (parent instanceof SnapshotNodeTreeItem) 
			{
				return ((SnapshotNodeTreeItem)parent).getChildren();
			}
			
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) 
		{
			if (parent instanceof SnapshotNodeTreeItem)
				return ((SnapshotNodeTreeItem)parent).hasChildren();
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
			if (obj instanceof SnapshotNodeTreeItem)
				return ((SnapshotNodeTreeItem)obj).getImage();
			else
				return null;
		}
	}
	
	
	public void createPartControl(Composite parent) 
	{	
		m_topSplitter = new Splitter(parent, SWT.BORDER);
		m_topSplitter.setOrientation(SWT.HORIZONTAL);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		m_topSplitter.setLayout(layout);

		m_treeViewer = new TreeViewer(m_topSplitter, SWT.MULTI);
		
		m_treeViewer.setContentProvider(new SnapshotContentProvider());
		getSite().setSelectionProvider(m_treeViewer);
		m_treeViewer.setLabelProvider(new SnapshotLabelProvider());
		m_treeViewer.setInput(getEditorSite());
		
		Tree tree = m_treeViewer.getTree();

		tree.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				setTextAreaVisible(false);				
			}
		});	
		
		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e) 
			{
				TreeSelection selection = (TreeSelection)m_treeViewer.getSelection();
				
				if (selection.size() > 0)
				{
					SnapshotNodeTreeItem item = (SnapshotNodeTreeItem)selection.getFirstElement();
					Shell shell = getSite().getShell();
					
					try
					{
						ShowSourceAction.showSource(shell, item);
					}
					catch (Exception ex)
					{
						MessageDialog.openError(shell, "Show Source Error", ex.getMessage());
					}
				}
			}
		});
		
		m_treeViewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { LocalSelectionTransfer.getTransfer()}, new DropTargetAdapter()
		{
			public void dragOver(DropTargetEvent event)
			{
				TreeSelection selection = (TreeSelection)LocalSelectionTransfer.getTransfer().getSelection();
				
				if (selection.size() == 1)
				{
					if (selection.toArray()[0] instanceof QueryObject)
						event.detail = DND.DROP_MOVE;
					else
						event.detail = DND.DROP_NONE;
				}
				else
					event.detail = DND.DROP_NONE;
			}
			
			public void drop(DropTargetEvent event)
			{
				TreeSelection selection = (TreeSelection)event.data;
				QueryObject obj = (QueryObject)selection.toArray()[0];
				SnapshotNodeTreeItem item = (SnapshotNodeTreeItem)event.item.getData();
				SymbolQuery query;
				
				try
				{
					query = DbAdapter.getSymbolQuery(obj.m_id);
				}
				catch (Exception e)
				{
					return;
				}
				
				Shell shell = getSite().getShell();
				
				if (MessageDialog.openQuestion(shell, "Subquery confirmation", "Do you want to run query \"" +
						query.getFullName() + "\" on item \"" + item.getName() + "\"?"))
				{
					List<SnapshotNodeTreeItem> sel = new ArrayList<SnapshotNodeTreeItem>();
					sel.add(item);
					
					SubqueryAction.executeQuery(shell, sel, query);
				}
			}
		});
		
		m_textAreaSplitter = new Splitter(m_topSplitter, SWT.NONE);
		m_textAreaSplitter.setOrientation(SWT.VERTICAL);
		m_textAreaSplitter.setLayout(layout);
		
		setTextAreaVisible(false);
		
		m_textViewer = new TextViewer(m_textAreaSplitter, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		m_textViewer.setEditable(false);
		
		m_fileList = new org.eclipse.swt.widgets.List(m_textAreaSplitter, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		m_fileList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					loadFileItem(m_fileList.getSelectionIndex());
				}
				catch (Exception ex)
				{
					MessageDialog.openError(getSite().getShell(), "Error", "Error loading file item: " + ex.getMessage());
				}
			}
		});
		
		m_textAreaSplitter.setWeights(new int[] {80, 20});
		
		MenuManager menuMgr = new MenuManager("");
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS + "_subquery"));
		menuMgr.add(new Separator());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS + "_object"));		
		Menu menu = menuMgr.createContextMenu(m_treeViewer.getControl());
		m_treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, m_treeViewer);
	}
	
	public void setSelectedItem(SnapshotNodeTreeItem item, Map<String,String> fileNames)
	{
		if (fileNames.size() == 0)
			return;
		
		SnapshotNode node;
		
		try
		{
			node = DbAdapter.getSnapshotNode(item.getID());
		}
		catch(Exception e)
		{
			return;
		}
		
		if (m_refMap == null)
			m_refMap = new HashMap<String, Reference>();
		else
			m_refMap.clear();
		
		for (Reference ref : node.getRefs())
			m_refMap.put(ref.getFileId(), ref);
		
		if (m_fileIDs == null)
			 m_fileIDs = new ArrayList<String>();
		else
			m_fileIDs.clear();
		
		m_fileList.removeAll();
		
		for (Map.Entry<String,String> entry : fileNames.entrySet())
		{
			m_fileIDs.add(entry.getKey());
			m_fileList.add(entry.getValue());
		}

		m_fileList.select(0);
		
		try
		{
			loadFileItem(0);
		}
		catch (Exception ex)
		{
			MessageDialog.openError(getSite().getShell(), "Error", "Error loading file item: " + ex.getMessage());
		}		
		
		setTextAreaVisible(true);
		setFileListVisible(fileNames.size() > 1);
	}
		
	
	protected void loadFileItem(int pos) throws Exception
	{
		String fileID = m_fileIDs.get(pos);
		ByteArrayOutputStream contents;
		
		try
		{
			contents = DbAdapter.getSourceFile(fileID);
		}
		catch (Exception e)
		{
			return;
		}
		
		if (m_textViewer.getDocument() == null)
			m_textViewer.setDocument(new Document());		

		Reference ref = m_refMap.get(fileID);
		int start = ref.getStartPos();
		int length = (ref.getEndPos() - start) + 1;
				
		m_textViewer.getDocument().set(contents.toString("UTF-8"));
		m_textViewer.revealRange(start, length);
		m_textViewer.setSelectedRange(start, length);
	}
	
	protected void setTextAreaVisible(boolean visible)
	{
		m_topSplitter.setVisible(m_textAreaSplitter, visible);
	}

	protected void setFileListVisible(boolean visible)
	{
		m_textAreaSplitter.setVisible(m_fileList, visible);
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
	
	public static void closeAll()
	{
		Set<Integer> idSet = m_viewerTable.keySet();
		
		for (Integer id : idSet)
			closeSnapshot(id);
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
