package com.sourcedimensions.ws.provider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codehaus.xfire.fault.XFireFault;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sourcedimensions.client.model.Folder;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SnapshotNode;
import com.sourcedimensions.client.model.SourceFilePackage;
import com.sourcedimensions.client.model.SymbolQuery;
import com.sourcedimensions.server.query.SymbolQueryEngine;
import com.sourcedimensions.server.sys.Project.Language;
import com.sourcedimensions.server.sys.profile.*;
import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.SourceFile;
import com.sourcedimensions.server.utils.DatabaseHelper;


public class WebService implements IWebService
{
	public String login(String userName, String password) throws XFireFault
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		User user = (User)session.createQuery("FROM User WHERE m_name = :name AND m_password = :password").
			setString("name", userName).setString("password", password).uniqueResult();

		session.getTransaction().commit();		
		
		if (user == null)
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.LOGIN_FAILED.name());
			throw fault;
		}
		else
		{
			UserSession userSession = UserSession.createSession(user);
			return userSession.getID();
		}
	}
	
	private void verifySession(String sessionID) throws XFireFault
	{
		if (!UserSession.validateSession(sessionID))
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.SESSION_EXPIRED.name());
			throw fault;
		}
	}
	
	private void verifyLanguage(String sessionID, String projectId) throws XFireFault
	{
		boolean valid = true;
		
		Database db = DatabaseHelper.getDbBySessionID(sessionID);
		
		Session session = db.getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		Project prj = (Project)session.get(Project.class, projectId);	
		session.getTransaction().commit();
		
		if (prj == null)
		{
			XFireFault fault = new XFireFault(new Exception());
			
			fault.setRole(FaultValues.PROJECT_NOT_FOUND.name());
			throw fault;
		}
		
		session = Database.getProfileSessionFactory().getCurrentSession();
		session.beginTransaction();
	
		Account account = (Account)session.createQuery("SELECT a FROM UserSession s " +
			"INNER JOIN s.m_user.m_account a WHERE s.m_id = :id").setString("id", sessionID).uniqueResult();	
		
		if (account != null)
		{
			switch (account.getLangAccess())
			{
				case ALL:
					break;
					
				case JAVA:
					if (prj.getLanguage() == Language.CSHARP_11 || 
						prj.getLanguage() == Language.CSHARP_20)
					{
						valid = false;
					}
					break;
					
				case CSHARP:
					if (prj.getLanguage() == Language.JAVA_14 || 
						prj.getLanguage() == Language.JAVA_15)
					{
						valid = false;
					}
			}
		}
		
		session.getTransaction().commit();
		
		if (!valid)
		{
			XFireFault fault = new XFireFault(new Exception());			
			fault.setRole(FaultValues.LANG_ACCESS_DENIED.name());
			throw fault;			
		}
	}
	
	public Set<com.sourcedimensions.client.model.Project> getProjectList(String sessionID) throws XFireFault
	{
		Set<com.sourcedimensions.client.model.Project> prjSet = new HashSet<com.sourcedimensions.client.model.Project>();
		verifySession(sessionID);
			
		Session session = Database.getProfileSessionFactory().openSession();
				
		session.beginTransaction();

		Account account = (Account)session.createQuery("SELECT s.m_user.m_account FROM UserSession s WHERE s.m_id = :id").
			setString("id", sessionID).uniqueResult();
		
		Set<String> prjIDs = new HashSet<String>();
		prjIDs.addAll(account.m_projectIds);
		
		Database db = account.getDatabase();
		
		SessionFactory factory = db.getDbSessionFactory(); 
		
		session.getTransaction().commit();
		session.close();
		
		session = factory.openSession();
		
		session.beginTransaction();
		
		for (String id : prjIDs)
		{
			Project p = (Project)session.get(Project.class, id);
			
			com.sourcedimensions.client.model.Project prj = 
					new com.sourcedimensions.client.model.Project();

			prj.setId(p.getID());
			prj.setName(p.m_name);
			prj.setLanguage(p.getLangValue());
			
			for (Project pp : p.m_parents)
			{
				com.sourcedimensions.client.model.Project wpp = 
						new com.sourcedimensions.client.model.Project();

				wpp.setId(pp.getID());
				wpp.setName(pp.m_name);
				wpp.setLanguage(pp.getLangValue());

				prj.getParents().add(wpp);
			}

			prjSet.add(prj);
		}
		
		session.getTransaction().commit();
		session.close();
		
		return prjSet;
	}
	
	public Snapshot runSymbolQuery(String sessionID, String projectId, SymbolQuery query) throws XFireFault
	{
		verifySession(sessionID);
		verifyLanguage(sessionID, projectId);
		SymbolQueryEngine engine = new SymbolQueryEngine(sessionID);
		Snapshot snapshot = new Snapshot();
		snapshot.setRoot(engine.execute(projectId, query));
		
		return snapshot;
	}
	
	public Set<SnapshotNode> runSymbolSubquery(String sessionID, String projectId, Set<SnapshotNode> rootSet, SymbolQuery query) throws XFireFault
	{
		verifySession(sessionID);
		verifyLanguage(sessionID, projectId);
		SymbolQueryEngine engine = new SymbolQueryEngine(sessionID);
		return engine.execute(projectId, rootSet, query);
	}
	
	public SourceFilePackage getSourceFiles(String sessionID, String projectId, Set<String> fileIdSet) throws XFireFault, IOException
	{
		verifySession(sessionID);
		verifyLanguage(sessionID, projectId);
		
		SourceFilePackage pack = new SourceFilePackage();
		
		pack.setFileMap(new HashMap<String,String>());
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(output);

		byte[] buffer = new byte[4096];
		
		Session session = DatabaseHelper.getDbBySessionID(sessionID).getDbSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		for (String fileId : fileIdSet)
		{
			Query query = session.createQuery("FROM SourceFile s INNER JOIN FETCH s.m_project WHERE s.m_id = :id");			
			query.setString("id", fileId);
			
			SourceFile file = (SourceFile)query.list().get(0);			
			String filePath = file.getFullName().replace(Folder.DIVIDER_CHAR, File.separatorChar);
			String rootPath = file.getProject().m_rootPath;
			String fullPath = "";
			
			if (rootPath.endsWith(File.separator))
				fullPath = rootPath.substring(0, rootPath.length() - 1);
			else
				fullPath = rootPath;
			
			if (!rootPath.startsWith(File.separator))
				fullPath += File.separator;
			
			fullPath += filePath;
			
			ZipEntry entry = new ZipEntry(file.getID());
			
			zip.putNextEntry(entry);			
			pack.getFileMap().put(file.getID(), file.getFullName());
			
			FileInputStream srcFile = new FileInputStream(fullPath);
			
			for (int read = srcFile.read(buffer); read != -1; read = srcFile.read(buffer))
				zip.write(buffer, 0, read);
			
			zip.closeEntry();
		}
				
		zip.flush();
		
		if (fileIdSet.size() > 0)
			zip.close();
		
		session.getTransaction().commit();
		
		pack.setData(output.toByteArray());
		
		return pack;
	}
}