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
			
			ResultSet rs = s.executeQuery("select EXT_ID, NAME, LANGUAGE, READONLY from PROJECT");

			while (rs.next())
			{
				Project p = new Project();
				p.m_id = rs.getString("EXT_ID");
				p.m_name = rs.getString("NAME");
				p.m_language = rs.getInt("LANGUAGE");
				p.m_readOnly = rs.getShort("READONLY") > 0;
				
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
			
			PreparedStatement ps = c.prepareStatement("select EXT_ID, NAME, LANGUAGE, READONLY from PROJECT where EXT_ID = ?",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, prj.m_id);
			
			ResultSet rs = ps.executeQuery();
			boolean update = false;
			
			if (rs.next())
				update = true;
			else
				rs.moveToInsertRow();			
				
			rs.updateString("EXT_ID", prj.m_id);
			rs.updateString("NAME", prj.m_name);
			rs.updateInt("LANGUAGE", prj.m_language);
			rs.updateShort("READONLY", (short)(prj.m_readOnly ? 1 : 0));
				
			if (update)
				rs.updateRow();
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
				"ID int generated always as identity",
				"EXT_ID varchar(36)",
				"NAME varchar(256)",
				"LANGUAGE integer",
				"READONLY smallint"
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
