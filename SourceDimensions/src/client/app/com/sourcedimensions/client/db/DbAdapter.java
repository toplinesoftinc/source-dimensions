package com.sourcedimensions.client.db;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.client.model.SnapshotNode;


public class DbAdapter 
{

	static
	{
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		}
		catch (ClassNotFoundException e)
		{
			MessageDialog.openError(null, "Error", "Error initializing database JDBC driver: " + e.getMessage());
		}
	}

	public static void tryConnection() throws SQLException, IOException
	{
		Connection c = getConnection();
		c.close();
	}
	
	public static List<Project> getProjectList()
	{
		List<Project> list = new ArrayList<Project>();
		
		try
		{
			Connection c = getConnection();
			
			ResultSet rs = c.createStatement().executeQuery("SELECT * FROM project");

			while (rs.next())
			{
				Project p = new Project();

				p.setId(rs.getString("ext_id"));
				p.setName(rs.getString("name"));
				p.setLanguage(rs.getInt("language"));
				p.setReadOnly(rs.getShort("readonly") > 0);
				
				list.add(p);
			}
			
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
		
		return list;
	}

	
	public static Project getProject(String id)
	{
		Project p = null;
		
		try
		{
			Connection c = getConnection();
			PreparedStatement s = c.prepareStatement("SELECT * FROM project WHERE id = ?");
			s.setString(1, id);
			
			ResultSet rs = s.executeQuery();

			if (rs.next())
			{
				p = new Project();

				p.setId(rs.getString("ext_id"));
				p.setName(rs.getString("name"));
				p.setLanguage(rs.getInt("language"));
				p.setReadOnly(rs.getShort("readonly") > 0);
			}
			
			s = c.prepareStatement("SELECT * FROM dependent_project WHERE parent_id = ?");
			s.setString(1, id);
			
			rs = s.executeQuery();
			
			while (rs.next())
			{
				Project dp = new Project();

				dp.setId(rs.getString("ext_id"));
				dp.setName(rs.getString("name"));
				dp.setLanguage(rs.getInt("language"));
				dp.setReadOnly(rs.getShort("readonly") > 0);
				
				p.getParents().add(dp);
			}		
			
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
		
		return p;
	}
	
	
	public static void saveProject(Project prj)
	{
		try
		{
			int id = 0;
			Connection c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM project WHERE ext_id = ?", 
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			
			ps.setString(1, prj.getId());
			
			ResultSet rs = ps.executeQuery();
			boolean update = false;
			
			if (rs.next())
			{
				id = rs.getInt("id");
				update = true;
			}
			else
				rs.moveToInsertRow();			
				
			rs.updateString("ext_id", prj.getId());
			rs.updateString("name", prj.getName());
			rs.updateInt("language", prj.getLanguage());
			rs.updateShort("readonly", (short)(prj.getReadOnly() ? 1 : 0));
				
			if (update)
			{
				rs.updateRow();				
				
				ps = c.prepareStatement("SELECT * FROM dependent_project WHERE parent_id = ?", 
						ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

				ps.setInt(1, id);
				rs = ps.executeQuery();
				
				while (rs.next())
				{
					rs.deleteRow();
				}
				
				for (Project p : prj.getParents())
				{
					rs.moveToInsertRow();
					
					rs.updateString("ext_id", p.getId());
					rs.updateString("name", p.getName());
					rs.updateInt("language", p.getLanguage());
					
					rs.insertRow();
				}				
			}
			else
			{
				rs.insertRow();
				rs.moveToCurrentRow();
			}
		
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
	}
	
	
	public static List<Folder> getFolderList(Integer parent_id, String projectId, boolean query)
	{
		List<Folder> list = new ArrayList<Folder>();
		int p_id;

		try
		{
			Connection c = getConnection();
			
			if (parent_id == null)
				p_id = getRootFolderID(c, projectId, query);
			else
				p_id = parent_id.intValue();

			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");

			ps = c.prepareStatement("SELECT * FROM folder WHERE query_or_folder = ? AND project_id = ? AND parent_id = ?");
			
			ps.setShort(1, (short)(query ? 1 : 0));
			ps.setInt(2, prjId);
			ps.setInt(3, p_id);
			
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				Folder folder = new Folder();
				folder.m_id = rs.getInt("id");
				folder.m_name = rs.getString("name");
				
				list.add(folder);
			}
			
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}		
				
		return list;
	}
	
	
	public static Folder addFolder(String name, Integer parent_id, String projectId, boolean queryFolder) throws DupFolderNameException
	{
		try
		{
			Connection c = getConnection();			
			int real_id;
			
			if (parent_id == null)
			{
				real_id = getRootFolderID(c, projectId, queryFolder);
			}
			else
				real_id = parent_id.intValue();

			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		
			
			ps = c.prepareStatement("SELECT * FROM folder WHERE project_id = ? AND parent_id = ? AND name = ?");
			
			ps.setInt(1, prjId);
			ps.setInt(2, real_id);
			ps.setString(3, name);
			
			rs = ps.executeQuery();
			
			if (rs.next())
			{
				throw new DupFolderNameException();
			}
			
			ps = c.prepareStatement("INSERT INTO folder(project_id, parent_id, name, query_or_folder) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, prjId);
			ps.setInt(2, real_id);
			ps.setString(3, name);
			ps.setShort(4, (short)(queryFolder ? 1 : 0));
			
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			rs.next();
			
			Folder folder = new Folder();
			folder.m_id = rs.getInt(1);
			folder.m_name = name;
			
			c.commit();
			c.close();
			
			return folder;
		}
		catch (DupFolderNameException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			return null;
		}		
	}

	
	public static void updateFolder(String name, int id) throws DupFolderNameException
	{
		try
		{
			Connection c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT project_id, parent_id FROM folder WHERE id = ?");
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			int parent_id = rs.getInt("parent_id");
			int prjId = rs.getInt("project_id");

			ps = c.prepareStatement("SELECT * FROM folder WHERE project_id = ? AND parent_id = ? and name = ? and id <> ?");
			
			ps.setInt(1, prjId);
			ps.setInt(2, parent_id);			
			ps.setString(3, name);
			ps.setInt(4, id);
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DupFolderNameException();
			
			ps = c.prepareStatement("UPDATE folder SET name = ? WHERE id = ?");
			
			ps.setString(1, name);
			ps.setInt(2, id);
			
			ps.executeUpdate();
					
			c.commit();
			c.close();
		}
		catch (DupFolderNameException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}				
	}
	
	
	public static void deleteFolder(int id)
	{
		try
		{
			Connection c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("DELETE FROM snapshot_node WHERE folder_id = ?");
			
			ps.setInt(1, id);
			ps.executeUpdate();
			
			deleteFolder(c, id);
			
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}						
	}
	
	
	protected static void deleteFolder(Connection c, int id) throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("SELECT id FROM folder WHERE parent_id = ?");
		
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		
		while (rs.next())
		{
			deleteFolder(c, rs.getInt("id"));
		}
		
		ps = c.prepareStatement("DELETE FROM snapshot_node WHERE folder_id = ?");
		
		ps.setInt(1, id);
		ps.executeUpdate();
		
		ps = c.prepareStatement("DELETE FROM folder WHERE id = ?");
		
		ps.setInt(1, id);
		ps.executeUpdate();
	}
	
	
	public static void saveSnapshotTree(String projectId, Integer folderId, SnapshotNode root) throws SQLException, IOException
	{
		try
		{
			Connection c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		

			addSnapshotNode(c, prjId, null, folderId, root);
			
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
	}

	protected static void addSnapshotNode(Connection c, int projectId, Integer parentId, int folderId, SnapshotNode node) throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("INSERT INTO snapshot_node(folder_id, parent_id, project_id, type, origin_id, name) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		
		ps.setInt(1, folderId);
		ps.setInt(2, parentId);
		ps.setInt(3, projectId);
		ps.setInt(4, node.getType().value());
		ps.setString(5, node.getOriginID());
		ps.setString(6, node.getName());

		ps.executeUpdate();
		
		ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		
		for (SnapshotNode n : node.getChildren())
		{
			addSnapshotNode(c, projectId, rs.getInt(1), folderId, n);
		}
	}
	
	public static List<SnapshotNode> getSnapshotNodeList(String projectId, Integer parentId, int folderId)
	{
		List<SnapshotNode> list = new ArrayList<SnapshotNode>();
		
		try
		{
			Connection c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		

			if (parentId == null)
			{
				ps = c.prepareStatement("SELECT * FROM snapshot_node WHERE projectId = ? AND parent_id IS NULL AND folderId = ?");
				
				ps.setInt(1, prjId);
				ps.setInt(2, folderId);
			}
			else
			{
				ps = c.prepareStatement("SELECT * FROM snapshot_node WHERE projectId = ? AND parent_id = ? AND folderId = ?");
				
				ps.setInt(1, prjId);
				ps.setInt(2, parentId);
				ps.setInt(3, folderId);
			}
			
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				SnapshotNode node = new SnapshotNode();
				
				node.m_id = rs.getInt("id");
				node.setType(SnapshotNode.Type.values()[rs.getInt("type")]);
				node.setOriginID(rs.getString("origin_id"));
				node.setName(rs.getString("name"));
				
				list.add(node);
			}
			
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
		
		return list;
	}

	public static void deleteSnapshotTree(int id)
	{
		try
		{
			Connection c = getConnection();			
	
			deleteSnapshotNode(c, id);
			
			c.commit();
			c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}		
	}
	
	protected static void deleteSnapshotNode(Connection c, int id) throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("SELECT id FROM snapshot_node WHERE parent_id = ?");
		
		ps.setInt(1, id);

		ResultSet rs = ps.executeQuery();
		
		while (rs.next())
		{
			deleteSnapshotNode(c, rs.getInt("id"));
		}
		
		ps = c.prepareStatement("DELETE FROM snapshot_node WHERE id = ?");
		
		ps.executeUpdate();
	}
	
	protected static int getRootFolderID(Connection c, String projectId, boolean query) throws SQLException, IOException
	{
		short iqry = (short)(query ? 1 : 0);
		
		PreparedStatement ps = c.prepareStatement("SELECT * FROM folder WHERE query_or_folder = ? AND parent_id IS NULL");
		
		ps.setShort(1, iqry);
		ResultSet rs = ps.executeQuery();
		
		if (!rs.next())
		{
			ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");
			
			ps = c.prepareStatement("INSERT INTO folder(project_id, parent_id, query_or_folder) VALUES (?, NULL, ?)");
			
			ps.setInt(1, prjId);
			ps.setShort(2, iqry);
			ps.executeUpdate();
			
			ps = c.prepareStatement("SELECT * FROM folder WHERE project_id = ? AND query_or_folder = ? AND parent_id IS NULL");
			
			ps.setInt(1, prjId);
			ps.setShort(2, iqry);
			rs = ps.executeQuery();
			
			rs.next();
		}
		
		return rs.getInt("id");
	}
	
	
	protected static Connection getConnection() throws SQLException, IOException
	{	
		String url = "jdbc:derby:%s/cachedb";
		String path = Platform.getInstallLocation().getURL().getPath();
		
		if (path.startsWith("/"))
			path = path.substring(1);
		
		path += "db";
			
		File f = new File(path);
		
		if (!f.exists())
		{
			f.mkdir();
			Connection c = DriverManager.getConnection(String.format(url + ";create=true", path));
			
			createDb(c);
			
			return c;
		}
		else
			return DriverManager.getConnection(String.format(url, path));
	}
	
	
	protected static void createDb(Connection c) throws SQLException
	{
		String[][] spec = 
		{
			{
				"PROJECT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"ext_id VARCHAR(36)",
				"name VARCHAR(256) NOT NULL",
				"language INT NOT NULL",
				"readonly SMALLINT NOT NULL",
				"PRIMARY KEY (id)"
			},
			{
				"DEPENDENT_PROJECT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"parent_id INT",
				"ext_id VARCHAR(36)",
				"name VARCHAR(256) NOT NULL",
				"language INTEGER NOT NULL",				
				"PRIMARY KEY (id)",
				"FOREIGN KEY (parent_id) REFERENCES PROJECT"
			},
			{
				"FOLDER",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"parent_id INT",
				"project_id INT NOT NULL",
				"query_or_folder SMALLINT NOT NULL",
				"name VARCHAR(256)",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (parent_id) REFERENCES FOLDER",
				"FOREIGN KEY (project_id) REFERENCES PROJECT"
			},
			{
				"SNAPSHOT_NODE",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"folder_id INT",
				"parent_id INT",
				"project_id INT NOT NULL",
				"type INT NOT NULL",
				"origin_id VARCHAR(36) NOT NULL",				
				"name VARCHAR(256) NOT NULL",				
				"PRIMARY KEY (id)",
				"FOREIGN KEY (folder_id) REFERENCES FOLDER",
				"FOREIGN KEY (parent_id) REFERENCES SNAPSHOT_NODE",				
				"FOREIGN KEY (project_id) REFERENCES PROJECT"				
			}
		};
	
		for (String[] stmt : spec)
		{
			String sql = "CREATE TABLE " + stmt[0] + " (";
			
			for (int i = 1; i < stmt.length; i++)
			{
				sql += stmt[i];
				
				if (i < stmt.length - 1)
					sql += ",";
			}
			
			sql += ")";
			
			Statement s = c.createStatement();
			s.execute(sql);
			s.close();
		}
		
		c.commit();
	}
}
