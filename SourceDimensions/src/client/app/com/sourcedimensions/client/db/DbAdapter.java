package com.sourcedimensions.client.db;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import com.sourcedimensions.client.model.*;
import com.sourcedimensions.client.model.SnapshotNode.Reference;


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
		closeConn(c);
	}
	
	public static List<Project> getProjectList() throws Exception
	{
		Connection c = null;
		List<Project> list = new ArrayList<Project>();
		
		try
		{
			c = getConnection();
			
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
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return list;
	}

	
	public static Project getProject(String id) throws Exception
	{
		Connection c = null;
		Project p = null;
		
		try
		{
			c = getConnection();
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
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return p;
	}
	
	
	public static void saveProject(Project prj) throws Exception
	{
		Connection c = null;
		
		try
		{
			int id = 0;
			c = getConnection();
			
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
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;			
		}
		finally
		{
			closeConn(c);
		}
	}
	
	
	public static List<Folder> getFolderList(Integer parentId, String projectId, boolean isQuery) throws Exception
	{
		Connection c = null;
		List<Folder> list = new ArrayList<Folder>();

		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");

			ps = c.prepareStatement("SELECT * FROM folder WHERE query_or_folder = ? AND project_id = ? AND " + 
				"(parent_id = ? OR (parent_id IS NULL AND ? IS NULL))");
			
			ps.setShort(1, (short)(isQuery ? 1 : 0));
			ps.setInt(2, prjId);
			
			if (parentId == null)
			{
				ps.setNull(3, Types.INTEGER);
				ps.setNull(4, Types.INTEGER);
			}
			else
			{
				ps.setInt(3, parentId);
				ps.setInt(4, parentId);
			}
			
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				Folder folder = new Folder();
				folder.m_id = rs.getInt("id");
				folder.m_name = rs.getString("name");
				
				list.add(folder);
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
				
		return list;
	}
	
	
	public static Folder addFolder(String name, Integer parentId, String projectId, boolean isQuery) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			

			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");
			short queryFlag = (short)(isQuery ? 1 : 0);
			
			ps = c.prepareStatement("SELECT * FROM folder WHERE query_or_folder = ? AND project_id = ? AND " + 
				"(parent_id = ? OR (parent_id IS NULL AND ? IS NULL)) AND name = ?");
			
			ps.setShort(1, queryFlag);
			ps.setInt(2, prjId);
			
			if (parentId == null)
			{
				ps.setNull(3, Types.INTEGER);
				ps.setNull(4, Types.INTEGER);
			}
			else
			{
				ps.setInt(3, parentId);
				ps.setInt(4, parentId);
			}
			
			ps.setString(5, name);
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("INSERT INTO folder(project_id, parent_id, name, query_or_folder) " +
				"VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, prjId);
			
			if (parentId == null)
				ps.setNull(2, Types.INTEGER);
			else
				ps.setInt(2, parentId);
			
			ps.setString(3, name);
			ps.setShort(4, queryFlag);
			
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			rs.next();
			
			Folder folder = new Folder();
			folder.m_id = rs.getInt(1);
			folder.m_name = name;
			
			c.commit();
			
			return folder;
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}

	
	public static void updateFolder(int id, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT project_id, parent_id FROM folder WHERE id = ?");
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			Integer parentId = rs.getInt("parent_id");
			
			if (rs.wasNull())
				parentId = null;
			
			ps = c.prepareStatement("SELECT * FROM folder WHERE (parent_id = ? OR " +
				"(parent_id IS NULL AND ? IS NULL)) AND name = ? and id <> ?");
					
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);
			}
			
			ps.setString(3, name);
			ps.setInt(4, id);
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE folder SET name = ? WHERE id = ?");
			
			ps.setString(1, name);
			ps.setInt(2, id);
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}
	
	public static void moveFolder(int id, Integer parentId, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM folder WHERE (parent_id = ? " +
				"OR (parent_id IS NULL AND ? IS NULL)) AND name = ? AND id <> ?");
						
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);
			}
			
			ps.setString(3, name);
			ps.setInt(4, id);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE folder SET parent_id = ?, name = ? WHERE id = ?");
			
			if (parentId == null)
				ps.setNull(1, Types.INTEGER);
			else
				ps.setInt(1, parentId);
			
			ps.setString(2, name);
			ps.setInt(3, id);			
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}
	
	
	public static void deleteFolder(int id) throws Exception
	{
		Connection c = null;
		
		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("DELETE FROM folder WHERE id = ?");
			
			ps.setInt(1, id);
			ps.executeUpdate();
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}
	
	
	public static Integer addSnapshot(String projectId, Integer folderId, Snapshot snapshot) throws Exception, DuplicateNameException
	{
		Connection c = null;

		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		
		
			ps = c.prepareStatement("SELECT * FROM snapshot WHERE project_id = ? AND " + 
				"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL)) AND name = ?");
			
			ps.setInt(1, prjId);
			
			if (folderId == null)
			{
				ps.setNull(2, Types.INTEGER);
				ps.setNull(3, Types.INTEGER);
			}
			else
			{
				ps.setInt(2, folderId);
				ps.setInt(3, folderId);
			}
			
			ps.setString(4, snapshot.getName());
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("INSERT INTO snapshot(folder_id, project_id, name) "+ 
					"VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			if (folderId == null)
				ps.setNull(1, Types.INTEGER);
			else
				ps.setInt(1, folderId);
			
			ps.setInt(2, prjId);
			ps.setString(3, snapshot.getName());

			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			rs.next();
			
			int id = rs.getInt(1);
			
			if (snapshot.getRoot() != null)
			{
				saveSnapshotNode(c, prjId, id, null, snapshot.getRoot());
			}
			
			c.commit();
			
			return id;
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}

	
	protected static void saveSnapshotNode(Connection c, int projectId, int snapshotId, Integer parentId, SnapshotNode node) throws Exception
	{
		PreparedStatement ps = c.prepareStatement("INSERT INTO snapshot_node(project_id, snapshot_id, parent_id, label, type)" +
			" VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		
		ps.setInt(1, projectId);
		ps.setInt(2, snapshotId);
		
		if (parentId == null)
			ps.setNull(3, Types.INTEGER);
		else
			ps.setInt(3, parentId);
		
		ps.setString(4, node.getLabel());
		ps.setInt(5, node.getType().value());
		
		ps.executeUpdate();

		ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		
		int id = rs.getInt(1);

		if (node.getChildren() != null)
		{
			for (SnapshotNode s : node.getChildren())
			{
				saveSnapshotNode(c, projectId, snapshotId, id, s);
			}
		}
		
		if (node.getRefs() != null)
		{
			for (Reference ref : node.getRefs())
			{
				ps = c.prepareStatement("INSERT INTO REFERENCE(snapshot_node_id, ext_id, file_id, " +
					"start_pos, end_pos) VALUES (?,?,?,?,?)");
				
				ps.setInt(1, snapshotId);
				ps.setString(2, ref.getId());
				ps.setString(3,	ref.getFileId());
				ps.setInt(4, ref.getStartPos());
				ps.setInt(5, ref.getEndPos());
				
				ps.executeUpdate();
			}
		}
	}


	public static List<Reference> getSnapshotNodeRefs(int snapshotNodeId) throws Exception
	{
		Connection c = null;
		List<Reference> list = new ArrayList<Reference>();
		
		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM reference WHERE snapshot_node_id = ?");
			
			ps.setInt(1, snapshotNodeId);
						
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
			{				
				list.add(new Reference(rs.getString("ext_id"), rs.getString("file_id"), 
					rs.getInt("start_pos"), rs.getInt("end_pos")));
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return list;
	}
	
	
	public static List<SnapshotNode> getSnapshotNodeList(int snapshotId, Integer parentId) throws Exception
	{
		Connection c = null;
		List<SnapshotNode> list = new ArrayList<SnapshotNode>();
		
		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM snapshot_node " +
				"WHERE snapshot_id = ? AND (parent_id = ? OR (parent_id IS NULL AND ? IS NULL))");
			
			ps.setInt(1, snapshotId);
			
			if (parentId == null)
			{
				ps.setNull(2, Types.INTEGER);
				ps.setNull(3, Types.INTEGER);
			}
			else
			{
				ps.setInt(2, parentId);
				ps.setInt(3, parentId);
			}
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
			{
				SnapshotNode node = new SnapshotNode();
				
				node.m_id = rs.getInt("id");
				node.setLabel(rs.getString("label"));
				node.setType(SnapshotNode.Type.values()[rs.getInt("type")]);
				
				list.add(node);
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return list;
	}
	
	
	public static int copySnapshot(int snapshotId, Integer folderId, int destId, String name) throws Exception
	{
		Connection c = null;
		int id;
		
		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT project_id FROM snapshot WHERE id = ?");
			
			ps.setInt(1, snapshotId);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			int prjId = rs.getInt("project_id");
			
			ps = c.prepareStatement("INSERT INTO snapshot(project_id, folder_id, name) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setInt(1, prjId);
			ps.setInt(2, folderId);
			ps.setString(3, name);
			
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			rs.next();
			
			id = rs.getInt(1);			
			
			ps = c.prepareStatement("SELECT id FROM snapshot_node WHERE snapshot_id = ? AND parent_id IS NULL");
			
			ps.setInt(1, snapshotId);
			
			rs = ps.executeQuery();
			
			if (rs.next())
			{
				copySnapshotContents(c, id, rs.getInt("id"), null);
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}

		return id;	
	}
	
	
	protected static void copySnapshotContents(Connection c, int snapshotId, int sourceId, Integer destId) throws Exception
	{
		PreparedStatement ps = c.prepareStatement("SELECT * FROM snapshot_node WHERE id = ?");
		
		ps.setInt(1, sourceId);
		
		ResultSet rs = ps.executeQuery();
		
		rs.next();
		
		int projectId = rs.getInt("project_id");
		String label = rs.getString("label");
		int type = rs.getInt("type");
		
		ps = c.prepareStatement("INSERT INTO snapshot_node(project_id, snapshot_id, parent_id, label, type) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		
		ps.setInt(1, projectId);
		ps.setInt(2, snapshotId);
		
		if (destId == null)
			ps.setNull(3, Types.INTEGER);
		else
			ps.setInt(3, destId);
		
		ps.setString(4, label);
		ps.setInt(5, type);
		
		ps.executeUpdate();
		
		rs = ps.getGeneratedKeys();
		rs.next();
		
		int id = rs.getInt(1);

		ps = c.prepareStatement("SELECT id FROM snapshot_node WHERE parent_id = ?");
		
		ps.setInt(1, sourceId);
		
		rs = ps.executeQuery();
		
		while (rs.next())
		{
			copySnapshotContents(c, snapshotId, rs.getInt("id"), id);
		}
	}
	
		
	public static void updateSnapshot(int id, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT folder_id FROM snapshot WHERE id = ?");
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
						
			Integer parentId = rs.getInt("folder_id");
			
			if (rs.wasNull())
				parentId = null;

			ps = c.prepareStatement("SELECT * FROM snapshot WHERE " +
				"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL)) AND name = ? and id <> ?");
			
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);				
			}
			ps.setString(3, name);
			ps.setInt(4, id);
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE snapshot SET name = ? WHERE id = ?");
			
			ps.setString(1, name);
			ps.setInt(2, id);
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}


	public static Snapshot getSnapshot(int id) throws Exception
	{
		Connection c = null;
		Snapshot snapshot = null;
		
		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM snapshot WHERE id = ?");
			
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			snapshot = new Snapshot();
			snapshot.m_id = id;
			snapshot.setName(rs.getString("name"));
			
			Integer folderId = rs.getInt("folder_id");
			
			if (rs.wasNull())
				snapshot.setFullName(snapshot.getName());
			else
				snapshot.setFullName(getFolderPath(folderId) + snapshot.getName());
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return snapshot;
	}
	
		
	public static void moveSnapshot(int id, Integer parentId, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM snapshot WHERE (folder_id = ? " +
				"OR (folder_id IS NULL AND ? IS NULL)) AND name = ? AND id <> ?");
						
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);
			}
			
			ps.setString(3, name);
			ps.setInt(4, id);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE snapshot SET folder_id = ?, name = ? WHERE id = ?");
			
			if (parentId == null)
				ps.setNull(1, Types.INTEGER);
			else
				ps.setInt(1, parentId);

			ps.setString(2, name);			
			ps.setInt(3, id);
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}	
	
	
	public static List<Snapshot> getSnapshotList(String projectId, Integer folderId) throws Exception
	{
		Connection c = null;
		List<Snapshot> list = new ArrayList<Snapshot>();
		
		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		

			ps = c.prepareStatement("SELECT * FROM snapshot WHERE project_id = ? AND "+
					"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL))");

			ps.setInt(1, prjId);

		
			if (folderId == null)
			{
				ps.setNull(2, Types.INTEGER);
				ps.setNull(3, Types.INTEGER);
			}
			else
			{
				ps.setInt(2, folderId);
				ps.setInt(3, folderId);
			}

			rs = ps.executeQuery();
			
			while (rs.next())
			{
				Snapshot snapshot = new Snapshot();
				
				snapshot.m_id = rs.getInt("id");
				snapshot.setName(rs.getString("name"));
				
				list.add(snapshot);
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return list;
	}

	
	public static List<QueryNode> getQueryList(String projectId, Integer folderId) throws Exception
	{
		Connection c = null;
		List<QueryNode> list = new ArrayList<QueryNode>();
		
		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		

			ps = c.prepareStatement("SELECT * FROM query WHERE project_id = ? AND "+
					"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL))");

			ps.setInt(1, prjId);
		
			if (folderId == null)
			{
				ps.setNull(2, Types.INTEGER);
				ps.setNull(3, Types.INTEGER);
			}
			else
			{
				ps.setInt(2, folderId);
				ps.setInt(3, folderId);
			}

			rs = ps.executeQuery();
			
			while (rs.next())
			{
				QueryNode node = new QueryNode();
				
				node.m_id = rs.getInt("id");
				node.m_name = rs.getString("name");
				
				list.add(node);
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return list;
	}

	
	public static Object findObject(String projectId, String path, boolean isQuery) throws Exception
	{
		Connection c = null;
		Integer id = null;
				
		String[] segments = path.split(Folder.DIVIDER_REGEX);

		try
		{
			c = getConnection();			
						
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		
			
			for (int i = 0; i < segments.length; i++)
			{
				ps = c.prepareStatement("SELECT id FROM folder WHERE query_or_folder = ? AND project_id = ? AND " +
					"(parent_id = ? OR (parent_id IS NULL AND ? IS NULL)) AND name = ?");					
				
				ps.setShort(1, (short)(isQuery ? 1 : 0));
				ps.setInt(2, prjId);
				
				if (id == null)
				{
					ps.setNull(3, Types.INTEGER);
					ps.setNull(4, Types.INTEGER);
				}
				else
				{
					ps.setInt(3, id);
					ps.setInt(4, id);
				}
				
				ps.setString(5, segments[i]);
				
				rs = ps.executeQuery();
			
				if (i == segments.length - 1)
				{				
					if (Pattern.matches(".*" + Folder.DIVIDER_REGEX, path))
					{
						if (rs.next())
						{
							Folder folder = new Folder();
							folder.m_id = rs.getInt("id");
							folder.m_name = segments[i];
							
							c.commit();
							return folder;
						}
						else
						{
							c.commit();
							return null;
						}
					}
					else
					{
						if (isQuery)
						{
							ps = c.prepareStatement("SELECT id FROM query WHERE project_id = ? AND " +
								"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL)) AND name = ?");
						}
						else
						{
							ps = c.prepareStatement("SELECT id FROM snapshot WHERE project_id = ? AND " +
								"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL)) AND name = ?");						
						}
						
						ps.setInt(1, prjId);
						
						if (id == null)
						{
							ps.setNull(2, Types.INTEGER);
							ps.setNull(3, Types.INTEGER);
						}
						else
						{
							ps.setInt(2, id);
							ps.setInt(3, id);
						}
						
						ps.setString(4, segments[i]);
						
						rs = ps.executeQuery();

						if (rs.next())
						{
							if (isQuery)
							{
								QueryNode node = new QueryNode();
								node.m_id = rs.getInt("id");
								node.m_name = segments[i];
								
								c.commit();
								return node;
							}
							else
							{
								Snapshot snapshot = new Snapshot();
								snapshot.m_id = rs.getInt("id");
								snapshot.setName(segments[i]);
								
								c.commit();
								return snapshot;
							}
						}
						else
						{
							c.commit();
							return null;
						}
					}
				}

				if (rs.next())
				{
					id = rs.getInt("id");
				}
				else
				{
					c.commit();
					return null;
				}
			}
			
			c.commit();		
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return null;
	}
	
	
	public static void deleteSnapshot(int id) throws Exception
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("DELETE FROM snapshot WHERE id = ?");
			
			ps.setInt(1, id);			
			ps.executeUpdate();
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}
		
	
	protected static Connection getConnection() throws SQLException, IOException
	{	
		String url = "jdbc:derby:%s/cachedb";
		String path = Platform.getInstallLocation().getURL().getPath();
		Connection c = null;
		
		if (path.startsWith(Folder.DIVIDER))
			path = path.substring(1);
		
		path += "db";
			
		File f = new File(path);
		
		if (!f.exists())
		{
			f.mkdir();
			c = DriverManager.getConnection(String.format(url + ";create=true", path));

			c.setAutoCommit(false);
			createDb(c);
			c.close();
		}

		c = DriverManager.getConnection(String.format(url, path));	
		c.setAutoCommit(false);
			
		return c;
	}

	
	public static SymbolQuery getSymbolQuery(int queryId) throws Exception
	{
		Connection c = null;
		SymbolQuery query = new SymbolQuery();

		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("SELECT * FROM query WHERE id = ?");

			ps.setInt(1, queryId);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next())
			{
				c.commit();
				return null;
			}
			
			query.setName(rs.getString("name"));
			query.setDestination(rs.getString("destination"));
			query.setAllNamespaces(rs.getShort("all_namespaces") != 0);
			query.setAllTypes(rs.getShort("all_types") != 0);
			query.setAllMembers(rs.getShort("all_members") != 0);
			query.setAllLocalDecls(rs.getShort("all_local_decls") != 0);
			query.setGlobalNamespace(rs.getShort("global_namespace") != 0) ;
			
			Integer parentId = rs.getInt("folder_id");
			
			if (rs.wasNull())
				query.setFullName(query.getName());
			else
				query.setFullName(getFolderPath(parentId) + query.getName());
			
			while (!rs.wasNull())
			{
				ps = c.prepareStatement("SELECT parent_id, name FROM folder WHERE id = ?");
				ps.setInt(1, parentId);
				
				rs = ps.executeQuery();
				rs.next();
				
				query.setName(rs.getString("name") + Folder.DIVIDER + query.getName());
				
				parentId = rs.getInt("parent_id");
			}
			
			if (!query.getAllNamespaces())
			{
				query.setNamespaceFilter(new ArrayList<String>());
				
				ps = c.prepareStatement("SELECT name FROM namespace_filter WHERE query_id = ?");
				
				ps.setInt(1, queryId);
				rs = ps.executeQuery();
				
				while (rs.next())
				{
					query.getNamespaceFilter().add(rs.getString("name"));
				}
			}
			
			if (!query.getAllTypes())
			{
				query.setTypeFilter(new ArrayList<TypeFilter>());
				
				ps = c.prepareStatement("SELECT * FROM type_filter WHERE query_id = ?");
				
				ps.setInt(1, queryId);
				rs = ps.executeQuery();
				
				while (rs.next())
				{
					TypeFilter filter = new TypeFilter();
					query.getTypeFilter().add(filter);
					
					filter.setCategories(rs.getInt("categories"));
					filter.setModifiers(new TriStateMask(rs.getLong("modifiers")));
					filter.setAllBaseTypes(rs.getShort("all_types") != 0);
					filter.setInnerTypes(rs.getShort("inner_types") != 0);
					filter.setName(rs.getString("name"));
					
					int filterId = rs.getInt("id");
					
					if (!filter.getAllBaseTypes())
					{
						filter.setBaseTypes(new ArrayList<BaseType>());
						
						PreparedStatement ps2 = c.prepareStatement("SELECT * FROM base_type WHERE filter_id = ?");
						
						ps2.setInt(1, filterId);
						ResultSet rs2 = ps2.executeQuery();
						
						while (rs2.next())
						{
							BaseType bt = new BaseType();
							filter.getBaseTypes().add(bt);
							
							bt.setCategory(rs2.getInt("category"));
							bt.setName(rs2.getString("name"));
						}
					}
					
					PreparedStatement ps2 = c.prepareStatement("SELECT * FROM delegate WHERE filter_id = ?");
					
					ps2.setInt(1, filterId);
					ResultSet rs2 = ps2.executeQuery();
					
					if (rs2.next())
					{
						Delegate delegate = new Delegate();
						Type type = new Type();
						
						filter.setDelegate(delegate);
						delegate.setType(type);
						
						delegate.setAnyParams(rs2.getShort("any_params") != 0);
						type.setTypeProps(new TriStateMask(rs2.getLong("type_props")));
						type.setName(rs2.getString("type_name"));
						
						if (!delegate.getAnyParams())
						{
							int delegateId = rs2.getInt("id");
							delegate.setParamList(new ArrayList<Parameter>());
							
							ps2 = c.prepareStatement("SELECT * FROM delegate_param WHERE delegate_id = ?");
							
							ps2.setInt(1, delegateId);
							
							rs2 = ps2.executeQuery();
							
							while (rs2.next())
							{
								Parameter param = new Parameter();
								Type t = new Type();
								
								delegate.getParamList().add(param);
								param.setType(t);
								
								param.setModifiers(new TriStateMask(rs2.getLong("modifiers")));
								param.setName(rs2.getString("name"));
								param.setPosType(Parameter.Position.values()[rs2.getInt("pos_type")]);
								param.setPosValue(rs2.getInt("pos_value"));
								param.setPosMin(rs2.getInt("pos_min"));
								param.setPosMax(rs2.getInt("pos_max"));
								t.setTypeProps(new TriStateMask(rs2.getLong("type_props")));
								t.setName(rs2.getString("type_name"));
								
								int paramId = rs2.getInt("id");
								
								PreparedStatement ps3 = c.prepareStatement("SELECT position FROM delegate_param_id WHERE delegate_param_id = ?");
								
								ps3.setInt(1, paramId);
								
								ResultSet rs3 = ps3.executeQuery();
								
								param.setPosList(new HashSet<Integer>());
								
								while (rs3.next())
								{
									param.getPosList().add(rs3.getInt("position"));
								}
							}							
						}
					}
				}
			}
			
			if (!query.getAllMembers())
			{
				query.setMemberFilter(new ArrayList<MemberFilter>());
				
				ps = c.prepareStatement("SELECT * FROM member_filter WHERE query_id = ?");
				
				ps.setInt(1, queryId);
				rs = ps.executeQuery();
				
				while (rs.next())
				{
					MemberFilter filter = new MemberFilter();
					Type type = new Type();

					query.getMemberFilter().add(filter);
					filter.setType(type);
					
					filter.setCategories(rs.getInt("categories"));
					filter.setModifiers(new TriStateMask(rs.getLong("modifiers")));
					filter.setOperators(rs.getInt("operators"));
					filter.setAnyParams(rs.getShort("any_params") != 0);
					filter.setAnyThrows(rs.getShort("any_throws") != 0);
					filter.setName(rs.getString("name"));
					type.setName(rs.getString("type_name"));
					type.setTypeProps(new TriStateMask(rs.getLong("type_props")));
					
					int filterId = rs.getInt("id");
					
					PreparedStatement ps2 = c.prepareStatement("SELECT * FROM throw WHERE member_id = ?");
					
					ps2.setInt(1, filterId);
					
					ResultSet rs2 = ps2.executeQuery();
					
					filter.setThrowList(new ArrayList<String>());
					
					while (rs2.next())
					{
						filter.getThrowList().add(rs2.getString("name"));
					}
					
					if (!filter.getAnyParams())
					{
						filter.setParamList(new ArrayList<Parameter>());
						
						ps2 = c.prepareStatement("SELECT * FROM member_param WHERE member_id = ?");
						
						ps2.setInt(1, filterId);
						rs2 = ps2.executeQuery();
						
						while (rs2.next())
						{
							Parameter param = new Parameter();
							Type t = new Type();

							filter.getParamList().add(param);
							param.setType(t);

							int paramId = rs2.getInt("id");
							
							param.setModifiers(new TriStateMask(rs2.getLong("modifiers")));
							t.setTypeProps(new TriStateMask(rs2.getLong("type_props")));
							t.setName(rs2.getString("type_name"));
							param.setName(rs2.getString("name"));
							param.setPosType(Parameter.Position.values()[rs2.getInt("pos_type")]);
							param.setPosValue(rs2.getInt("pos_value"));
							param.setPosMin(rs2.getInt("pos_min"));
							param.setPosMax(rs2.getInt("pos_max"));
							
							PreparedStatement ps3 = c.prepareStatement("SELECT position FROM member_param_pos WHERE member_param_id = ?");
							
							ps3.setInt(1, paramId);
							
							ResultSet rs3 = ps3.executeQuery();
							
							param.setPosList(new HashSet<Integer>());
							
							while (rs3.next())
							{
								param.getPosList().add(rs3.getInt("position"));
							}
						}
					}
				}
			}
			
			if (!query.getAllLocalDecls())
			{
				query.setLocalDeclFilter(new ArrayList<LocalDeclFilter>());
				
				ps = c.prepareStatement("SELECT * FROM local_decl_filter WHERE query_id = ?");
				
				ps.setInt(1, queryId);
				
				rs = ps.executeQuery();
				
				while (rs.next())
				{
					LocalDeclFilter filter = new LocalDeclFilter();
					Type type = new Type();
					
					query.getLocalDeclFilter().add(filter);
					filter.setType(type);
					
					filter.setFinal(TriStateBoolean.values()[rs.getShort("final")]);
					filter.setName(rs.getString("name"));
					type.setTypeProps(new TriStateMask(rs.getLong("type_props")));
					type.setName(rs.getString("type_name"));
				}
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return query;
	}
	
	
	public static Integer addSymbolQuery(String projectId, Integer folderId, SymbolQuery query) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{						
			c = getConnection();
						
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");		
			
			ps = c.prepareStatement("SELECT * FROM query WHERE project_id = ? AND " + 
			"(folder_id = ? OR (folder_id IS NULL AND ? IS NULL)) AND name = ?");
		
			ps.setInt(1, prjId);
			
			if (folderId == null)
			{
				ps.setNull(2, Types.INTEGER);
				ps.setNull(3, Types.INTEGER);
			}
			else
			{
				ps.setInt(2, folderId);
				ps.setInt(3, folderId);
			}
			
			ps.setString(4, query.getName());
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();		
			
			ps = c.prepareStatement("INSERT INTO query(project_id, folder_id, name, destination, all_namespaces, " +
				"all_types, all_members, all_local_decls, global_namespace) VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, prjId);
			
			if (folderId == null)
				ps.setNull(2, Types.INTEGER);
			else
				ps.setInt(2, folderId);
			
			ps.setString(3, query.getName());
			ps.setString(4, query.getDestination());
			ps.setShort(5, (short)(query.getAllNamespaces() ? 1 : 0));
			ps.setShort(6, (short)(query.getAllTypes() ? 1 : 0));
			ps.setShort(7, (short)(query.getAllMembers()? 1 : 0));
			ps.setShort(8, (short)(query.getAllLocalDecls() ? 1 : 0));
			ps.setShort(9, (short)(query.getGlobalNamespace() ? 1 : 0));
			
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			rs.next();

			int queryId = rs.getInt(1);
			
			if (!query.getAllNamespaces() && query.getNamespaceFilter() != null)
			{
				for (String ns : query.getNamespaceFilter())
				{
					ps = c.prepareStatement("INSERT INTO namespace_filter(query_id, name) VALUES(?,?)");
					
					ps.setInt(1, queryId);
					ps.setString(2, ns);
					
					ps.executeUpdate();
				}
			}
			
			if (!query.getAllTypes() && query.getTypeFilter() != null)
			{
				for (TypeFilter tf : query.getTypeFilter())
				{
					ps = c.prepareStatement("INSERT INTO type_filter(query_id, categories, modifiers, " +
						"all_types, inner_types, name) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
					
					ps.setInt(1, queryId);
					ps.setInt(2, tf.getCategories());
					ps.setLong(3, tf.getModifiers().getValue());
					ps.setShort(4, (short)(tf.getAllBaseTypes() ? 1 : 0));
					ps.setShort(5, (short)(tf.getInnerTypes() ? 1 : 0));
					ps.setString(6, tf.getName());
					
					ps.executeUpdate();
					
					rs = ps.getGeneratedKeys();
					rs.next();
	
					int filterId = rs.getInt(1);
					
					if (tf.getBaseTypes() != null)
					{
						for (BaseType bt : tf.getBaseTypes())
						{
							ps = c.prepareStatement("INSERT INTO base_type(filter_id, category, name) VALUES(?,?,?)");
						
							ps.setInt(1, filterId);
							ps.setInt(2, bt.getCategory());
							ps.setString(3, bt.getName());
							
							ps.executeUpdate();
						}
					}
					
					Delegate delegate = tf.getDelegate();
					
					if (delegate != null)
					{
						ps = c.prepareStatement("INSERT INTO delegate(filter_id, type_props, type_name, any_params) VALUES(?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
						
						ps.setInt(1, filterId);
						ps.setLong(2, delegate.getType().getTypeProps().getValue());
						ps.setString(3, delegate.getType().getName());
						ps.setShort(4, (short)(delegate.getAnyParams() ? 1 : 0));
						
						ps.executeUpdate();
	
						rs = ps.getGeneratedKeys();
						rs.next();
	
						int delegateId = rs.getInt(1);
	
						if (delegate.getParamList() != null)
						{
							for (Parameter p : delegate.getParamList())
							{
								ps = c.prepareStatement("INSERT INTO delegate_param(delegate_id, modifiers, type_props, " +
									"type_name, name, position, pos_value, pos_min, pos_max) VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
								
								ps.setInt(1, delegateId);
								ps.setLong(2, p.getModifiers().getValue());
								ps.setLong(3, p.getType().getTypeProps().getValue());
								ps.setString(4, p.getType().getName());
								ps.setString(5, p.getName());
								ps.setInt(6, p.getPosType().value());
								ps.setInt(7, p.getPosValue());
								ps.setInt(8, p.getPosMin());
								ps.setInt(9, p.getPosMax());
		
								ps.executeUpdate();
		
								rs = ps.getGeneratedKeys();
								rs.next();
		
								int paramId = rs.getInt(1);
						
								if (p.getPosList() != null)
								{
									for (Integer pos : p.getPosList())
									{
										ps = c.prepareStatement("INSERT INTO delegate_param_pos(delegate_param_id, position) " +
											"VALUES(?,?)");
										
										ps.setInt(1, paramId);
										ps.setInt(2, pos);
										
										ps.executeUpdate();
									}
								}
							}
						}
					}			
				}
			}
	
			if (!query.getAllMembers() && query.getMemberFilter() != null)				
			{
				for (MemberFilter mf : query.getMemberFilter())
				{
					ps = c.prepareStatement("INSERT INTO member_filter(query_id, categories, modifiers, operators, " +
						"any_params, type_props, type_name, any_throws, name) VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
					
					ps.setInt(1, queryId);
					ps.setInt(2, mf.getCategories());
					ps.setLong(3, mf.getModifiers().getValue());
					ps.setInt(4, mf.getOperators());
					ps.setShort(5, (short)(mf.getAnyParams() ? 1 : 0));
					ps.setLong(6, mf.getType().getTypeProps().getValue());
					ps.setString(7, mf.getType().getName());
					ps.setShort(8, (short)(mf.getAnyThrows() ? 1 : 0));
					ps.setString(9, mf.getName());
					
					ps.executeUpdate();
					
					rs = ps.getGeneratedKeys();
					rs.next();
	
					int memberId = rs.getInt(1);
					
					if (mf.getThrowList() != null)
					{
						for (String t : mf.getThrowList())
						{
							ps = c.prepareStatement("INSERT INTO throw(member_id, name) VALUES(?,?)");
							
							ps.setInt(1, memberId);
							ps.setString(2, t);
							
							ps.executeUpdate();
						}
					}
	
					if (mf.getParamList() != null)
					{
						for (Parameter p : mf.getParamList())
						{
							ps = c.prepareStatement("INSERT INTO member_param(member_id, modifiers, type_props, " +
									"type_name, name, pos_type, pos_value, pos_min, pos_max) VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
								
							ps.setInt(1, memberId);
							ps.setLong(2, p.getModifiers().getValue());
							ps.setLong(3, p.getType().getTypeProps().getValue());
							ps.setString(4, p.getType().getName());
							ps.setString(5, p.getName());
							ps.setInt(6, p.getPosType().value());
							ps.setInt(7, p.getPosValue());
							ps.setInt(8, p.getPosMin());
							ps.setInt(9, p.getPosMax());
			
							ps.executeUpdate();
			
							rs = ps.getGeneratedKeys();
							rs.next();
			
							int paramId = rs.getInt(1);
					
							if (p.getPosList() != null)
							{
								for (Integer pos : p.getPosList())
								{
									ps = c.prepareStatement("INSERT INTO member_param_pos(member_param_id, position) " +
										"VALUES(?,?)");
									
									ps.setInt(1, paramId);
									ps.setInt(2, pos);
									
									ps.executeUpdate();
								}
							}
						}
					}
				}	
			}
	
			if (!query.getAllLocalDecls() && query.getLocalDeclFilter() != null)
			{
				for (LocalDeclFilter local : query.getLocalDeclFilter())
				{
					ps = c.prepareStatement("INSERT INTO local_decl_filter(query_id, final, type_props, type_name, name)" +
						"VALUES(?,?,?,?,?)");
					
					ps.setInt(1, queryId);
					
					if (local.getFinal() == null)
						ps.setNull(2, Types.SMALLINT);
					else
						ps.setShort(2, (short)local.getFinal().value());
					
					ps.setLong(3, local.getType().getTypeProps().getValue());
					ps.setString(4, local.getType().getName());
					ps.setString(5, local.getName());
					
					ps.executeUpdate();
				}
			}
			
			c.commit();
			
			return queryId;
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());	
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}

	
	public static void updateQuery(int id, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
			
			PreparedStatement ps = c.prepareStatement("SELECT folder_id FROM query WHERE id = ?");
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			Integer parentId = rs.getInt("folder_id");
			
			if (rs.wasNull())
				parentId = null;
			
			ps = c.prepareStatement("SELECT * FROM query WHERE (folder_id = ? " +
				"OR (folder_id IS NULL AND ? IS NULL)) AND name = ? and id <> ?");
			
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);
			}
				
			ps.setString(3, name);
			ps.setInt(4, id);
			
			rs = ps.executeQuery();
			
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE query SET name = ? WHERE id = ?");
			
			ps.setString(1, name);
			ps.setInt(2, id);
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}

	
	public static void moveQuery(int id, Integer parentId, String name) throws Exception, DuplicateNameException
	{
		Connection c = null;
		
		try
		{
			c = getConnection();		
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM query WHERE (folder_id = ? " +
				"OR (folder_id IS NULL AND ? IS NULL)) AND name = ?");
						
			if (parentId == null)
			{
				ps.setNull(1, Types.INTEGER);
				ps.setNull(2, Types.INTEGER);
			}
			else
			{
				ps.setInt(1, parentId);
				ps.setInt(2, parentId);
			}
			
			ps.setString(3, name);
			
			ResultSet rs = ps.executeQuery();
		
			if (rs.next())
				throw new DuplicateNameException();
			
			ps = c.prepareStatement("UPDATE query SET folder_id = ?, name = ? WHERE id = ?");
			
			if (parentId == null)
				ps.setNull(1, Types.INTEGER);
			else
				ps.setInt(1, parentId);

			ps.setString(2, name);
			ps.setInt(3, id);
			
			ps.executeUpdate();
					
			c.commit();
		}
		catch (DuplicateNameException e)
		{
			rollbackTrans(c);
			throw e;
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}	
		
	
	public static void deleteQuery(int id) throws Exception
	{
		Connection c = null;
		
		try
		{
			c = getConnection();			
	
			PreparedStatement ps = c.prepareStatement("DELETE FROM query WHERE id = ?");
			
			ps.setInt(1, id);
			ps.executeUpdate();
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
	}

	
	public static void deleteAll(String projectId, boolean isQuery) throws Exception
	{
		Connection c = null;
		
		try
		{
			c = getConnection();
			
			PreparedStatement ps = c.prepareStatement("SELECT id FROM project WHERE ext_id = ?");
			
			ps.setString(1, projectId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			int prjId = rs.getInt("id");
			
			ps = c.prepareStatement("DELETE FROM folder WHERE parent_id IS NULL AND project_id = ? AND query_or_folder = ?");
			
			ps.setInt(1, prjId);
			ps.setShort(2, (short)(isQuery ? 1 : 0));
			
			ps.executeUpdate();
			
			if (isQuery)
			{
				ps = c.prepareStatement("DELETE FROM query WHERE project_id = ?");				
				ps.setInt(1, prjId);
				
				ps.executeUpdate();
			}
			else
			{
				ps = c.prepareStatement("DELETE FROM snapshot WHERE project_id = ?");				
				ps.setInt(1, prjId);
				
				ps.executeUpdate();				
			}
			
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);			
		}
	}

	
	public static String getFolderPath(int folderId) throws Exception
	{
		Connection c = null;
		String path = "";
		
		try
		{
			c = getConnection();
			
			Integer curId = folderId;
			
			while (true)
			{
				PreparedStatement ps = c.prepareStatement("SELECT parent_id, name FROM folder WHERE id = ?");
			
				ps.setInt(1, curId);
				
				ResultSet rs = ps.executeQuery();
			
				rs.next();
			
				path = rs.getString("name") + Folder.DIVIDER + path;

				curId = rs.getInt("parent_id");				
				
				if (rs.wasNull())
					break;
			}
								
			c.commit();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
			rollbackTrans(c);
			
			throw e;
		}
		finally
		{
			closeConn(c);
		}
		
		return path;
	}
	
	
	protected static void closeConn(Connection c)
	{
		if (c == null)
			return;
		
		try
		{
			if (!c.isClosed())
				c.close();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}		
	}
	
	
	protected static void rollbackTrans(Connection c)
	{
		if (c == null)
			return;
		
		try
		{
			c.rollback();
		}
		catch (Exception e)
		{
			MessageDialog.openError(null, "Error", "Database layer exception " + e.getMessage());
		}
	}
	
	
	protected static void createDb(Connection c) throws SQLException
	{
		String[][] spec = 
		{
			{
				"PROJECT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"ext_id VARCHAR(36)",
				"name VARCHAR(1024) NOT NULL",
				"language INT NOT NULL",
				"readonly SMALLINT NOT NULL",
				"PRIMARY KEY (id)"
			},
			{
				"DEPENDENT_PROJECT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"parent_id INT",
				"ext_id VARCHAR(36)",
				"name VARCHAR(1024) NOT NULL",
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
				"FOREIGN KEY (parent_id) REFERENCES FOLDER ON DELETE CASCADE",
				"FOREIGN KEY (project_id) REFERENCES PROJECT"
			},
			{
				"SNAPSHOT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"folder_id INT",
				"project_id INT NOT NULL",
				"name VARCHAR(1024) NOT NULL",				
				"PRIMARY KEY (id)",
				"FOREIGN KEY (folder_id) REFERENCES FOLDER ON DELETE CASCADE",
				"FOREIGN KEY (project_id) REFERENCES PROJECT"
			},
			{
				"SNAPSHOT_NODE",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"project_id INT NOT NULL",
				"snapshot_id INT NOT NULL",
				"parent_id INT",
				"label VARCHAR(1024)",
				"type INT NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (parent_id) REFERENCES SNAPSHOT_NODE ON DELETE CASCADE",				
				"FOREIGN KEY (snapshot_id) REFERENCES SNAPSHOT ON DELETE CASCADE",
				"FOREIGN KEY (project_id) REFERENCES PROJECT"
			},
			{
				"REFERENCE",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"snapshot_node_id INT NOT NULL",
				"ext_id VARCHAR(36) NOT NULL",
				"file_id VARCHAR(36) NOT NULL",
				"start_pos INT NOT NULL",
				"end_pos INT NOT NULL"
			},
			{
				"QUERY",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"folder_id INT",
				"project_id INT NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"destination VARCHAR(1024)",
				"global_namespace SMALLINT",
				"all_namespaces SMALLINT",
				"all_types SMALLINT",
				"all_members SMALLINT",
				"all_local_decls SMALLINT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (folder_id) REFERENCES FOLDER ON DELETE CASCADE",
				"FOREIGN KEY (project_id) REFERENCES PROJECT ON DELETE CASCADE"
			},
			{
				"NAMESPACE_FILTER",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"query_id INT NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (query_id) REFERENCES QUERY ON DELETE CASCADE"
			},
			{
				"TYPE_FILTER",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"query_id INT NOT NULL",
				"categories INT",
				"modifiers BIGINT",
				"all_types SMALLINT",
				"inner_types SMALLINT",
				"name VARCHAR(1024) NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (query_id) REFERENCES QUERY ON DELETE CASCADE"
			},
			{
				"BASE_TYPE",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"filter_id INT NOT NULL",
				"category INT",
				"name VARCHAR(1024) NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (filter_id) REFERENCES TYPE_FILTER ON DELETE CASCADE"
			},
			{
				"DELEGATE",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"filter_id INT NOT NULL",
				"type_props BIGINT",
				"type_name VARCHAR(1024) NOT NULL",
				"any_params SMALLINT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (filter_id) REFERENCES TYPE_FILTER ON DELETE CASCADE"
			},
			{
				"DELEGATE_PARAM",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"delegate_id INT NOT NULL",
				"modifiers BIGINT",
				"type_props BIGINT",
				"type_name VARCHAR(1024) NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"pos_type INT",
				"pos_value INT",
				"pos_min INT",
				"pos_max INT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (delegate_id) REFERENCES DELEGATE ON DELETE CASCADE"
			},
			{
				"DELEGATE_PARAM_POS",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"delegate_param_id INT NOT NULL",
				"position INT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (delegate_param_id) REFERENCES DELEGATE_PARAM ON DELETE CASCADE"
			},
			{
				"MEMBER_FILTER",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"query_id INT NOT NULL",
				"categories INT",
				"modifiers BIGINT",
				"operators INT",
				"any_params SMALLINT",
				"type_props BIGINT",
				"type_name VARCHAR(1024) NOT NULL",
				"any_throws SMALLINT",
				"name VARCHAR(1024) NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (query_id) REFERENCES QUERY ON DELETE CASCADE"
			},
			{
				"THROW",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"member_id INT NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (member_id) REFERENCES MEMBER_FILTER ON DELETE CASCADE"
			},
			{
				"MEMBER_PARAM",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"member_id INT NOT NULL",
				"modifiers BIGINT",
				"type_props BIGINT",
				"type_name VARCHAR(1024) NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"pos_type INT",
				"pos_value INT",
				"pos_min INT",
				"pos_max INT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (member_id) REFERENCES MEMBER_FILTER ON DELETE CASCADE"
			},
			{
				"MEMBER_PARAM_POS",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"member_param_id INT NOT NULL",
				"position INT",
				"PRIMARY KEY (id)",
				"FOREIGN KEY (member_param_id) REFERENCES MEMBER_PARAM ON DELETE CASCADE"
			},
			{
				"LOCAL_DECL_FILTER",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"query_id INT NOT NULL",
				"final SMALLINT",
				"type_props BIGINT",
				"type_name VARCHAR(1024) NOT NULL",
				"name VARCHAR(1024) NOT NULL",
				"FOREIGN KEY (query_id) REFERENCES QUERY ON DELETE CASCADE"
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
