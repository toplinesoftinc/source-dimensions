package com.sourcedimensions.client.db;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

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

	
	public static void saveProjectList(List<Project> list) throws SQLException, IOException
	{
		try
		{
			Connection c = getConnection();
	
			c.setAutoCommit(false);
			
			for (Project p : list)
			{
				PreparedStatement ps = c.prepareStatement("INSERT INTO PROJECT(ext_id, name) VALUES(?,?)");
				
				ps.setString(1, p.m_id);
				ps.setString(2, p.m_name);
				ps.executeUpdate();
				ps.close();
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
				"NAME varchar(256)"
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
