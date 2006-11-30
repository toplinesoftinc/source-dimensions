package com.sourcedimensions.client.db;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;

import com.sourcedimensions.client.model.Project;

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

	
	public static List<Project> getProjectList()
	{
		List<Project> list = new ArrayList<Project>();
		
		try
		{
			Connection c = getConnection();
			Statement s = c.createStatement();
			
			ResultSet rs = s.executeQuery("SELECT ext_id, name, language, readonly FROM project");

			while (rs.next())
			{
				Project p = new Project();
				p.m_id = rs.getString("ext_id");
				p.m_name = rs.getString("name");
				p.m_language = rs.getInt("language");
				p.m_readOnly = rs.getShort("readonly") > 0;
				
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
	
	
	public static void saveProject(Project prj)
	{
		try
		{
			Connection c = getConnection();
			int id = 0;
			
			PreparedStatement ps = c.prepareStatement("SELECT * FROM project WHERE ext_id = ?",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, prj.m_id);
			
			ResultSet rs = ps.executeQuery();
			boolean update = false;
			
			if (rs.next())
			{
				id = rs.getInt("id");
				update = true;
			}
			else
				rs.moveToInsertRow();			
				
			rs.updateString("ext_id", prj.m_id);
			rs.updateString("name", prj.m_name);
			rs.updateInt("language", prj.m_language);
			rs.updateShort("readonly", (short)(prj.m_readOnly ? 1 : 0));
				
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
				
				for (Project p : prj.m_parents)
				{
					rs.moveToInsertRow();
					
					rs.updateInt("parent_id", id);
					rs.updateString("ext_id", p.m_id);
					rs.updateString("name", p.m_name);
					rs.updateInt("language", p.m_language);
					
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
				"name VARCHAR(256)",
				"language INTEGER",
				"readonly SMALLINT",
				"PRIMARY KEY (id)"
			},
			{
				"DEPENDENT_PROJECT",
				"id INT GENERATED ALWAYS AS IDENTITY",
				"parent_id INT",
				"ext_id VARCHAR(36)",
				"name VARCHAR(256)",
				"language INTEGER",				
				"PRIMARY KEY (id)",
				"FOREIGN KEY (parent_id) REFERENCES PROJECT"
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
