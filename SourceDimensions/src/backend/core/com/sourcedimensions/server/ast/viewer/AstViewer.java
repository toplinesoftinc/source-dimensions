package com.sourcedimensions.server.ast.viewer;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.hibernate.Session;

import com.sourcedimensions.server.ast.AstNode;
import com.sourcedimensions.server.ast.CompilationUnit;
import com.sourcedimensions.server.sys.Folder;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.SourceFile;
import com.sourcedimensions.server.sys.profile.Database;

public class AstViewer extends JFrame
{

	private static final long serialVersionUID = 1L;

	private JPanel m_contentPane = null;
	private JSplitPane m_splitPane = null;
	private JScrollPane m_leftScrollPane = null;
	private JScrollPane m_rightScrollPane = null;
	private JTree m_treeView = null;
	private JTextPane m_textPane = null;
	private String m_curFilePath = "";

	private static ImageIcon m_dbIcon;
	private static ImageIcon m_prjIcon;
	private static ImageIcon m_folderIcon;
	private static ImageIcon m_fileIcon;
	private static ImageIcon m_astIcon;
	
	static
	{
		m_dbIcon = createIcon("icons/database.png");
		m_prjIcon = createIcon("icons/project.png");
		m_folderIcon = createIcon("icons/folder.png");
		m_fileIcon = createIcon("icons/file.png");
		m_astIcon = createIcon("icons/astnode.png");
	}
	
	private static ImageIcon createIcon(String path)
	{
		URL url = AstViewer.class.getResource(path);
		
		if (url != null)
			return new ImageIcon(url);
		else
			return null;
	}
	
	public static void main(String[] args)
	{
		AstViewer viewer = new AstViewer();
		
		// This call is to diminish bug causing artifacts of incorrect screen area redrawing
		// under moving mouse cursor.
		javax.swing.RepaintManager.currentManager(viewer).setDoubleBufferingEnabled(false);
		viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		viewer.setVisible(true);
		viewer.pack();
		viewer.setExtendedState(viewer.getExtendedState() | JFrame.MAXIMIZED_BOTH);		
	}
	
	/**
	 * This is the default constructor
	 */
	public AstViewer() 
	{
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() 
	{
		this.setSize(732, 550);
		this.setContentPane(getJContentPane());
		this.setTitle("AST Viewer");
	}

	/**
	 * This method initializes m_contentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() 
	{
		if (m_contentPane == null) 
		{
			m_contentPane = new JPanel();
			m_contentPane.setLayout(new CardLayout());
			m_contentPane.add(getSplitPane(), getSplitPane().getName());
		}
		return m_contentPane;
	}

	/**
	 * This method initializes m_splitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getSplitPane() 
	{
		if (m_splitPane == null) 
		{
			m_splitPane = new JSplitPane();
			m_splitPane.setName("SplitPane");
			m_splitPane.setResizeWeight(0.5D);
			m_splitPane.setDividerSize(8);
			m_splitPane.setLeftComponent(getLeftScrollPane());
			m_splitPane.setRightComponent(getRightScrollPane());
			m_splitPane.setOneTouchExpandable(true);
		}
		return m_splitPane;
	}

	/**
	 * This method initializes m_leftScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getLeftScrollPane() 
	{
		if (m_leftScrollPane == null) 
		{
			m_leftScrollPane = new JScrollPane();
			m_leftScrollPane.setViewportView(getTreeView());
		}
		return m_leftScrollPane;
	}

	/**
	 * This method initializes m_rightScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getRightScrollPane() 
	{
		if (m_rightScrollPane == null) 
		{
			m_rightScrollPane = new JScrollPane();
			m_rightScrollPane.setViewportView(getTextPane());
		}
		return m_rightScrollPane;
	}

	/**
	 * This method initializes m_treeView	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getTreeView() 
	{
		if (m_treeView == null) 
		{
			m_treeView = new JTree(new RootNode());
			m_treeView.setRootVisible(false);
			m_treeView.setShowsRootHandles(true);
			m_treeView.setCellRenderer(new AstTreeRenderer());
			m_treeView.setLargeModel(true);
			ToolTipManager.sharedInstance().setDismissDelay(60000);
			ToolTipManager.sharedInstance().registerComponent(m_treeView);
			m_treeView.addTreeSelectionListener(new TreeSelectionListener()
			{
				public void valueChanged(TreeSelectionEvent e)
				{
					Object sel = e.getPath().getLastPathComponent();
					
					if (sel instanceof AstTreeNode)
					{
						String filePath = ((AstTreeNode)sel).getFilePath();
						
						if (!filePath.equals(m_curFilePath))
						{
							m_curFilePath = filePath;
							
							try
							{
								FileInputStream reader = new FileInputStream(filePath);
								byte[] buf = new byte[reader.available()];
								int offset = 0;
								String bom = "";
								
								reader.read(buf);
								
								for (int i = 0; i < Math.min(buf.length, 4); i++)
								{
									bom += String.format("%02X", buf[i]);
								}

								if (bom.startsWith("0000FEFF") || bom.startsWith("FFFE0000"))
									offset = 4;
								else if (bom.startsWith("EFBBBF"))
									offset = 3;
								else if (bom.startsWith("FEFF") || bom.startsWith("FFFE"))
									offset = 2;
																
								m_textPane.setText(new String(buf).replaceAll("\r\n", " \n").substring(offset));							

								reader.close();
							}
							catch (Exception ex)
							{
								JOptionPane.showMessageDialog(null, "Error opening file '" + filePath + "':" + ex.getMessage(), 
										"Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						
						SimpleAttributeSet attrs = new SimpleAttributeSet();
						StyleConstants.setBold(attrs, false);
						m_textPane.selectAll();
						m_textPane.setCharacterAttributes(attrs, true);
						
						AstNode node = ((AstTreeNode)sel).getAstNode();
						StyleConstants.setBold(attrs, true);
						m_textPane.select(node.m_left, node.m_right);
						m_textPane.setCharacterAttributes(attrs, true);
					}
					else
					{
						m_textPane.setText("");
						m_curFilePath = "";
					}
				}
			});
		}
		return m_treeView;
	}

	/**
	 * This method initializes m_textArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextPane getTextPane() 
	{
		if (m_textPane == null) 
		{
			m_textPane = new JTextPane();
			m_textPane.setEditable(false);
			m_textPane.setMargin(new Insets(5,15,0,0));
		}
		return m_textPane;
	}
	
	protected class AstTreeNode extends AstNodeBase
	{		
		public AstTreeNode(AstNodeBase parent, Database db, AstNode node, String filePath)
		{
			m_parent = parent;
			m_db = db;
			m_node = node;
			m_filePath = filePath;
		}
		
		public String toString()
		{
			return m_node.toString();
		}
		
		protected void loadChildren()
		{
			Session session = m_db.getDbSessionFactory().openSession();

			session.beginTransaction();
			
			m_children = new ArrayList<TreeNode>();
			List list = session.createQuery("FROM AstNode WHERE m_parent = :parent ORDER BY m_left").setEntity("parent", m_node).list();
			
			for (Object node : list)
			{
				m_children.add(new AstTreeNode(this, m_db, (AstNode)node, m_filePath));
			}
			
			session.getTransaction().commit();			
		}
		
		public String getFilePath()
		{
			return m_filePath;
		}
		
		public AstNode getAstNode()
		{
			return m_node;
		}
		
		protected Database m_db;
		protected AstNode m_node;
		protected String m_filePath;
	}
	
	protected class SourceFileNode extends AstNodeBase
	{
		public SourceFileNode(FolderNode parent, Database db, SourceFile file)
		{
			m_parent = parent;
			m_db = db;
			m_file = file;
		}
		
		public String toString()
		{
			return m_file.m_name;
		}
		
		protected void loadChildren()
		{
			Session session = m_db.getDbSessionFactory().openSession();

			session.beginTransaction();
			
			m_children = new ArrayList<TreeNode>();
			CompilationUnit unit = ((SourceFile)session.get(SourceFile.class, m_file.getID())).getCompilationUnit();
			
			if (unit != null)
			{
				String filePath = m_file.m_name;
				TreeNode p;
				
				for (p = getParent(); p instanceof FolderNode; p = p.getParent())
				{
					filePath = p.toString() + File.separator + filePath; 
				}
				
				filePath = ((ProjectNode)p).getProject().m_rootPath + filePath;
				
				m_children.add(new AstTreeNode(this, m_db, unit, filePath));
			}
				
			session.getTransaction().commit();							
		}
		
		protected Database m_db;
		protected SourceFile m_file;
	}

	protected class FolderNode extends AstNodeBase
	{
		public FolderNode(AstNodeBase parent, Database db, Folder folder)
		{
			m_parent = parent;
			m_db = db;
			m_folder = folder;
		}

		public String toString() 
		{
			String name = m_folder.m_name;
			
			if (name == null || name.length() == 0)
				return File.separator;
			else
				return name;
		}		

		protected void loadChildren()
		{
			Session session = m_db.getDbSessionFactory().openSession();

			session.beginTransaction();
			
			m_children = new ArrayList<TreeNode>();
			List list = session.createQuery("FROM Folder WHERE m_parentFolder = :parent ORDER BY m_name").setEntity("parent", m_folder).list();
			
			for (Object folder : list)
			{
				m_children.add(new FolderNode(this, m_db, (Folder)folder));
			}
			
			list = session.createQuery("FROM SourceFile WHERE m_parentFolder = :parent ORDER BY m_name").setEntity("parent", m_folder).list();

			for (Object file : list)
			{
				m_children.add(new SourceFileNode(this, m_db, (SourceFile)file));
			}			
			
			session.getTransaction().commit();			
		}
				
		protected Database m_db;
		protected Folder m_folder;
	}
	
	protected class ProjectNode extends AstNodeBase
	{
		public ProjectNode(DatabaseNode parent, Project prj)
		{
			m_parent = parent;
			m_prj = prj;
		}

		public String toString() 
		{
			return m_prj.m_name;
		}		

		protected void loadChildren()
		{
			Session session = ((DatabaseNode)m_parent).getDatabase().getDbSessionFactory().openSession();

			session.beginTransaction();
			
			m_children = new ArrayList<TreeNode>();
			Folder folder = ((Project)session.get(Project.class, m_prj.getID())).getRoot();
			
			if (folder != null)
			{
				m_children.add(new FolderNode(this, ((DatabaseNode)m_parent).getDatabase(), folder));
			}
			
			session.getTransaction().commit();							
		}
		
		
		public Project getProject()
		{
			return m_prj;
		}
		
		protected Project m_prj;
	}
	
	protected class DatabaseNode extends AstNodeBase
	{
		public DatabaseNode(RootNode parent, Database db)
		{
			m_parent = parent;
			m_db = db;
		}

		protected void loadChildren()
		{
			try
			{
				Session session = m_db.getDbSessionFactory().openSession();

				session.beginTransaction();
				
				m_children = new ArrayList<TreeNode>();
				List list = session.createQuery("FROM Project ORDER BY m_name").list();
				
				for (Object prj : list)
				{
					m_children.add(new ProjectNode(this, (Project)prj));					
				}
				
				session.getTransaction().commit();				
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Error loading projects: " + e.getMessage(), "Error", 
						JOptionPane.ERROR_MESSAGE);
			}
		}

		public String toString() 
		{
			return m_db.getDatabaseName();
		}
		
		public Database getDatabase()
		{
			return m_db;
		}
		
		protected Database m_db;
	}
	
	protected class RootNode extends AstNodeBase
	{
		protected void loadChildren()
		{
			Session session = Database.getProfileSessionFactory().getCurrentSession();
			
			session.beginTransaction();
			
			m_children = new ArrayList<TreeNode>();
			List list = session.createQuery("FROM Database ORDER BY m_databaseName").list();
			
			for (Object db : list)
			{
				m_children.add(new DatabaseNode(this, (Database)db));					
			}
			
			session.getTransaction().commit();
		}
	}
	
	protected abstract class AstNodeBase implements TreeNode 
	{		
		public Enumeration children() 
		{
			if (m_children == null)
				loadChildren();
	
			return new IterEnumeration<TreeNode>(m_children.iterator());
		}

		public boolean getAllowsChildren() 
		{
			return true;
		}

		public TreeNode getChildAt(int childIndex) 
		{
			if (m_children == null)
				loadChildren();

			return m_children.get(childIndex);
		}

		public int getChildCount() 
		{
			if (m_children == null)
				loadChildren();

			return m_children.size();
		}

		public int getIndex(TreeNode node) 
		{
			if (m_children == null)
				loadChildren();
			
			for (int i = 0; i < m_children.size(); i++)
			{
				if (m_children.get(i) == node)
					return i;
			}
			
			return -1;
		}

		public boolean isLeaf() 
		{
			if (m_children == null)
				loadChildren();

			return m_children.size() == 0;
		}

		public TreeNode getParent() 
		{
			return m_parent;
		}				
		
		protected abstract void loadChildren();
		
		protected ArrayList<TreeNode> m_children = null;
		protected TreeNode m_parent = null;
	}
	
	protected class IterEnumeration<T> implements Enumeration<T>
	{
		public IterEnumeration(Iterator<T> iter)
		{
			m_iter = iter;
		}

		public boolean hasMoreElements() 
		{
			return m_iter.hasNext();
		}

		public T nextElement() 
		{
			return m_iter.next();
		}

		protected Iterator<T> m_iter;
	}
	
	protected class AstTreeRenderer extends DefaultTreeCellRenderer 
	{
		protected static final long serialVersionUID = 1L;
		
	    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
	    	boolean expanded, boolean leaf, int row, boolean hasFocus)
	    {
	    	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	    	
	    	if (value instanceof DatabaseNode)
	    		setIcon(m_dbIcon);
	    	else if (value instanceof ProjectNode)
	    		setIcon(m_prjIcon);
	    	else if (value instanceof FolderNode)
	    		setIcon(m_folderIcon);
	    	else if (value instanceof SourceFileNode)
	    		setIcon(m_fileIcon);
	    	else if (value instanceof AstTreeNode)
	    	{
	    		setToolTipText("ID = " + ((AstTreeNode)value).getAstNode().getID());
	    		setIcon(m_astIcon);
	    	}
	    	
	        return this;
	    }
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
