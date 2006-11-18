package com.sourcedimensions.server.sys.astimport;

import com.sourcedimensions.server.sys.Project;
import com.sourcedimensions.server.sys.Project.Language;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.sys.Parser;
import com.sourcedimensions.server.exceptions.*;
import com.sourcedimensions.server.utils.SystemProps;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import org.hibernate.Session;
import org.xml.sax.SAXException;


public class ImportManager
{	
	protected static final String m_javaSchema = "schema/java.xsd";
	protected static final String m_csSchema = "schema/csharp.xsd";
	
	protected static final String m_delFileXmlBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<SourceFile file=\"%s\" delete=\"true\"/>";

	protected static final String[] m_srcExt = {".java", ".java", ".cs", ".cs"};
	protected static final String m_delExt = ".delete";
	protected static final String m_xmlExt = ".xml";

	
	public static void batchImport(Database db, String projectId, String zipFileName, 
			String tempFolder, ProcessCallback callback) throws Exception 
	{
		int total = 0, current = 0, outlen[] = new int[1], initLen = 0;
		ZipFile srcZipIn = new ZipFile(zipFileName);
		ZipEntry entry;
		byte[] inBuffer = null, outBuffer = null;
		ZipFile tmpZipIn = null;
		ZipOutputStream tmpZipOut = null;
		String tempZipName = "";
		Language lang = null;
		Session session = null;

		try
		{
			session = db.getDbSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Project project = (Project)session.get(Project.class, projectId);
			
			if (project == null)
			{
				session.getTransaction().commit();
				throw new MissingProjectException(projectId);			
			}	
			
			lang = project.getLanguage();
			
			session.getTransaction().commit();
						
			if (callback != null)
				callback.reportStart();

			for (Enumeration<? extends ZipEntry> e = srcZipIn.entries(); e.hasMoreElements();)
			{
				entry =  e.nextElement();
				String name = entry.getName().toLowerCase();
				
				if ((name.endsWith(m_srcExt[lang.value()]) || name.endsWith(m_srcExt[lang.value()] + m_delExt)) && 
						!entry.isDirectory())
				{
					total++;
					initLen = (int)Math.max(initLen, entry.getSize());
				}
			}
		
			tempZipName = tempFolder + (tempFolder.endsWith("/") ? "" : "/") + UUID.randomUUID().toString() + ".zip";
			
			File temp = new File(tempZipName);
			
			if (temp.getParent() != null)
				(new File(temp.getParent())).mkdirs();
		}
		catch(Exception e)
		{
			if (callback != null)
				callback.reportError(e);
			
			throw e;
		}

		if (total == 0)
			return;
			
		try
		{
			tmpZipOut = new ZipOutputStream(new FileOutputStream(tempZipName));
			tmpZipOut.setLevel(ZipOutputStream.STORED);
			
			if (initLen > 0)
				inBuffer = new byte[initLen];
			
			outBuffer = new byte[Math.min(10*initLen, 4096*1024)];
						
			for (Enumeration<? extends ZipEntry> e = srcZipIn.entries(); e.hasMoreElements();)
			{
				entry = e.nextElement();
				String name = entry.getName().toLowerCase();
	
				if ((name.endsWith(m_srcExt[lang.value()]) || name.endsWith(m_srcExt[lang.value()] + m_delExt)) &&
						!entry.isDirectory())
				{
					
					name = entry.getName();

					if (callback != null)
					{
						callback.reportProgress(true, total, ++current, name);
						
						if (callback.isCancel())
							return;
					}									
					
					if (name.endsWith(m_srcExt[lang.value()]))
					{
						InputStream is = srcZipIn.getInputStream(entry);
						int len = is.available();
						
						if (inBuffer == null || inBuffer.length < len)
							inBuffer = new byte[len];
					
						for (int r = 0; r < len; r += is.read(inBuffer, r, len - r));
						
						outBuffer = Parser.parse(lang.value(), name, inBuffer, len, outBuffer, outlen);
						
						ZipEntry ze = new ZipEntry(name + m_xmlExt);
						tmpZipOut.putNextEntry(ze);
						tmpZipOut.write(outBuffer, 0, outlen[0]);
						tmpZipOut.closeEntry();
					}
					else
					{
						name = name.substring(0, name.length() - m_delExt.length());
						String xmlBody = String.format(m_delFileXmlBody, name);
						
						ZipEntry ze = new ZipEntry(entry.getName() + m_xmlExt);
						tmpZipOut.putNextEntry(ze);
						tmpZipOut.write(xmlBody.getBytes(), 0, xmlBody.length());
						tmpZipOut.closeEntry();
					}
				}
			}

			tmpZipOut.close();
			tmpZipOut = null;
			inBuffer = null;
			outBuffer = null;
			
			tmpZipIn = new ZipFile(tempZipName);
			current = 0;

			ImportThread.initClass();
			Thread[] ths = new Thread[SystemProps.getImportThreadNum()];

			for (int i = 0; i < ths.length; i++)
			{
				ths[i] = new ImportThread(db, projectId, tmpZipIn, srcZipIn, callback, total, lang);
				ths[i].start();
			}
					
			for (int i = 0; i < ths.length; i++)
			{
				ths[i].join();
			}
			
			tmpZipIn.close();
			srcZipIn.close();
			
			if (ImportThread.getException() != null)
				throw ImportThread.getException();
			
			if (callback != null)
				callback.reportSuccess();
		}
		catch (Exception e)
		{
			if (callback != null)
				callback.reportError(e);
			
			throw e;
		}			
		finally
		{
			if (tmpZipOut != null)
				tmpZipOut.close();
			
			if (tmpZipIn != null)
				tmpZipIn.close();
			
			(new File(tempZipName)).delete();
		}
	}

	
	protected static class ImportThread extends Thread
	{
		protected Database m_db;
		protected String m_projectId;
		protected Schema m_schema;
		protected ZipFile m_tmpZip, m_srcZip;
		protected ProcessCallback m_callback;
		protected int m_total;
		protected AstImport m_astImport;
		protected static boolean m_cancel = false;
		protected static Enumeration<? extends ZipEntry> m_entry;
		protected static int m_current = 0;
		protected static Exception m_exception;
		
		public ImportThread(Database db, String projectId, ZipFile tmpZip, 
				ZipFile srcZip, ProcessCallback callback, int total, Language language) throws SAXException
		{
			m_db = db;
			m_projectId = projectId;
			m_tmpZip = tmpZip;
			m_srcZip = srcZip;
			m_callback = callback;
			m_total = total;

			String schemaName = "";
			
			switch (language)
			{
				case JAVA_14:
				case JAVA_15:
					m_astImport = new JavaImport();
					schemaName = m_javaSchema;
					break;
					
				case CSHARP_11:
				case CSHARP_20:
					m_astImport = new CsImport();
					schemaName = m_csSchema;
			}			

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			m_schema = schemaFactory.newSchema(new StreamSource(ImportManager.class.getClassLoader().getResourceAsStream(schemaName)));
			
			if (m_entry == null)
				m_entry = m_tmpZip.entries();			
		}

		protected ZipEntry getNext()
		{
			synchronized (m_entry)
			{
				if (m_entry.hasMoreElements() && !m_cancel && m_exception == null)
				{
					ZipEntry e = m_entry.nextElement();

					if (m_callback != null)
					{
						m_callback.reportProgress(false, m_total, ++m_current, getSrcName(e));
						
						m_cancel = m_callback.isCancel();
						
						if (m_cancel)
							return null;
					}
					
					return e;
				}
				else
					return null;
			}
		}
		
		protected String getSrcName(ZipEntry e)
		{
			return e.getName().substring(0, e.getName().length() - m_xmlExt.length());
		}
		
		public void run()
		{
			try
			{
				for (ZipEntry e = getNext(); e != null; e = getNext())
				{
					InputStream xml = m_tmpZip.getInputStream(e);
					InputStream src = m_srcZip.getInputStream(m_srcZip.getEntry(getSrcName(e)));
	
					m_astImport.importFile(m_db, m_projectId, m_schema, xml, src);
				}
			}
			catch (Exception e)
			{
				m_exception = e;
			}
		}
		
		public static Exception getException()
		{
			return m_exception;
		}
		
		public static void initClass()
		{
			m_cancel = false;
			m_entry = null;
			m_current = 0;
			m_exception = null;
		}
	}
	
	
	public static abstract class ProcessCallback
	{
		public abstract void reportStart();
		public abstract void reportProgress(boolean parsing, int total, int current, String fileName);
		public abstract void reportError(Exception e);
		public abstract void reportSuccess();
		public abstract boolean isCancel();
	}
	
	
	public static class SimpleProcessCallback extends ProcessCallback
	{
		public void reportStart()
		{
			System.out.println(String.format("**** Process started at %tT ****", Calendar.getInstance()));
		}
		
		public void reportProgress(boolean parsing, int total, int current, String fileName)
		{
			System.out.println(String.format("%s file \"%s\" (%d of %d files).", parsing ? "Parsing" : "Saving", fileName, current, total));
		}
		
		public void reportError(Exception e)
		{
			System.out.println("*** ERROR *** " + e.getMessage());
		}
		
		public void reportSuccess()
		{
			System.out.println(String.format("**** Process successfully finished at %tT ****", Calendar.getInstance()));
		}
		
		public boolean isCancel()
		{
			return false;
		}
	}
}
