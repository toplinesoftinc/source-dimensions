package com.sourcedimensions.server.sys.astimport;

import java.util.*;
import java.io.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.hibernate.Query;
import org.hibernate.Transaction;

import com.sourcedimensions.server.sys.*;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.ast.*;
import com.sourcedimensions.server.exceptions.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.hibernate.*;


public abstract class AstImport
{
	protected Session m_session;
	protected SAXParser m_parser = null;
	protected Project m_project;
	protected SourceFile m_file;
	
	public abstract void runProcess(XmlNode rootXml, CompilationUnit unit) throws Exception;
	
	public void importFile(Database db, String projectId, Schema schema, InputStream xml, InputStream src) throws Exception
	{
		try
		{
			XmlNode root = parseDocument(schema, xml);		
									
			m_session = db.getDbSessionFactory().openSession();
			m_session.beginTransaction();
			
			m_project = (Project)m_session.get(Project.class, projectId);
			
			if (m_project == null)
				throw new MissingProjectException(projectId);			
			
			String del = root.getAttribute("delete");
			
			if (del.equals("true") || del.equals("1"))
			{
				deleteSourceFile(m_project, m_session, root.getAttribute("file"));
				m_session.getTransaction().commit();
				
				deleteFile(root.getAttribute("file"));
			}				
			else
			{
				m_file = getSourceFile(m_project, m_session, root.getAttribute("file"), true);
				
				String bin = root.getAttribute("binary");
				
				m_file.m_binary = (bin.equals("true") || bin.equals("1"));
				
				deleteSyntaxTree(m_session, m_file);
				
				m_file.setCompilationUnit(createAstNode(CompilationUnit.class, null));		
	
				runProcess(root.getNode("CompUnit"), m_file.getCompilationUnit());
			
				m_session.saveOrUpdate(m_file);
				m_session.getTransaction().commit();
				
				saveFile(root.getAttribute("file"), src);
			}

			m_parser.reset();
			m_parser = null;
		}
		catch (Exception e)
		{
			if (m_session != null && m_session.isOpen())
			{
				Transaction tx = m_session.getTransaction();
				if (tx != null)
					tx.rollback();
			}
				
			throw e;
		}	
	}

	
	protected <T extends AstNode> T createAstNode(Class<T> clazz) throws Exception
	{
		return createAstNode(clazz, null);
	}
	

	protected <T extends AstNode> T createAstNode(Class<T> clazz, Integer kind) throws Exception
	{
		T node = (T)clazz.newInstance();		
		node.setProject(m_project);
		node.setSourceFile(m_file);
		
		if (kind != null)
			node.setKind(kind);
		
		return node;
	}
	
	
	protected XmlNode parseDocument(Schema schema, InputStream src) throws Exception
	{
		if (m_parser == null)
		{
			System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setSchema(schema);
			m_parser = factory.newSAXParser();
		}
		
		XmlHandler handler = new XmlHandler();
		m_parser.parse(src, handler);
		return handler.getTopNode();		
	}

	
	protected static synchronized SourceFile getSourceFile(Project project, Session session, String fullFileName, boolean createFile) throws Exception
	{
		String[] path = fullFileName.split(com.sourcedimensions.client.model.Folder.DIVIDER);
		
		if (path.length == 0)
			throw new InvalidFileNameException(fullFileName);
		
		Session ss = session.getSessionFactory().openSession();
		ss.beginTransaction();
		
		Folder parent = (Folder)ss.get(Folder.class, project.getRoot().getID());
		String fileName = path[path.length - 1];
		
		for (int i = path[0].trim().equals("") ? 1 : 0; i < path.length - 1; i++)
		{
			Query q = ss.createQuery("FROM Folder WHERE m_parentFolder = :parent AND m_name = :name").
				setEntity("parent", parent).setString("name", path[i]);

			Folder cur = (Folder)q.uniqueResult();
			
			if (cur == null)
			{			
				if (createFile)
				{
					for (int j = i; j < path.length - 1; j++)
						parent = parent.addFolder(path[j]);
					
					SourceFile srcfile = parent.addFile(fileName, fullFileName);
					
					ss.saveOrUpdate(parent);
					ss.getTransaction().commit();
					
					return (SourceFile)session.get(SourceFile.class, srcfile.getID());
				}
				else
				{
					return null;
				}
			}
			else
				parent = cur;
		}
		
		Query q = ss.createQuery("FROM SourceFile WHERE m_parentFolder = :parent AND m_name = :name").
			setEntity("parent", parent).setString("name", fileName);
		
		SourceFile srcfile = (SourceFile)q.uniqueResult();
		
		if (srcfile == null && createFile)
			srcfile = parent.addFile(fileName, fullFileName);
			
		ss.saveOrUpdate(parent);
		ss.getTransaction().commit();
		
		return (SourceFile)session.get(SourceFile.class, srcfile.getID());
	}

	
	protected static synchronized void deleteSourceFile(Project project, Session session, String fullFileName) throws Exception
	{
		
		Session ss = session.getSessionFactory().openSession();
		ss.beginTransaction();
		
		SourceFile srcfile = getSourceFile(project, ss, fullFileName, false);

		if (srcfile != null)
		{
			Folder folder = srcfile.getParent();
		
			deleteSyntaxTree(ss, srcfile);
			srcfile.delete();
			
			while (folder.m_children.size() == 0 && folder.getParent() != null)
			{
				Folder parent = folder.getParent();
				folder.delete();
				ss.delete(folder);
				folder = parent;
			}

			ss.delete(srcfile);
		}
		
		ss.getTransaction().commit();
	}

	
	protected static void deleteSyntaxTree(Session session, SourceFile file)
	{
		if (file.getCompilationUnit() != null)
		{
			while (true)
			{
				Iterator iter = session.createQuery("FROM AstNode WHERE m_file = :file").setEntity("file", file).iterate();
				
				if (!iter.hasNext())
					break;
		
				String className = Hibernate.getClass(iter.next()).getName();
				session.createQuery("DELETE FROM " + className + " WHERE m_file = :file").setEntity("file", file).executeUpdate();
			}

			file.setCompilationUnit(null);
			
			session.flush();
		}		
	}
	
	protected void saveFile(String fileName, InputStream src) throws IOException
	{
		String targetFileName = "";
		String rootPath = m_project.m_rootPath; 
		
		if (rootPath != null)
		{
			String slash = "/";
			
			if (rootPath.endsWith(slash) || fileName.startsWith(slash))
				slash = "";
			
			targetFileName = rootPath + slash + fileName;
		}
		else
			targetFileName = fileName;		
		
		File file = new File(targetFileName);
		file.delete();
		if (file.getParent() != null)
			(new File(file.getParent())).mkdirs();
		
		FileOutputStream os = new FileOutputStream(targetFileName);
		byte[] buf = new byte[16*1024];
		int len;
		
		while ((len = src.read(buf)) != -1)
		{
			os.write(buf, 0, len);
		}
		
		os.close();
	}


	protected void deleteFile(String fileName) throws IOException
	{
		String targetFileName = "";
		String rootPath = m_project.m_rootPath; 
		
		if (rootPath != null)
		{
			String slash = "/";
			
			if (rootPath.endsWith(slash) || fileName.startsWith(slash))
				slash = "";
			
			targetFileName = rootPath + slash + fileName;
		}
		else
			targetFileName = fileName;		
		
		File file = new File(targetFileName);
		File dir = file.getParentFile();
		File root = new File(rootPath);
		file.delete();
		
		if (dir.list() != null)
		{
			while (!dir.equals(root) && dir.list().length == 0)
			{
				File parent = dir.getParentFile();
				dir.delete();
				dir = parent;
			}
		}
	}
	
	protected void parseTextPos(XmlNode node, AstNode ast)
	{
		ast.m_left = Integer.parseInt(node.getAttribute("l"));
		ast.m_right = Integer.parseInt(node.getAttribute("r"));
	}
	
	
	protected String getTermValue(XmlNode node)
	{
		String val = node.getAttribute("val");
		
		if (val == null)
			return "";
		else
			return val;
	}
		
	
	protected class TolerantList<T> extends ArrayList<T>
	{
		private static final long serialVersionUID = 8683452581122892189L;
		
		public T get(int index)
		{
			if (index < 0 || index >= size())
				return null;
			else
				return super.get(index);
		}
	}
	
	protected static class XmlNode
	{
		protected Map<String, String> m_attrMap = new HashMap<String, String>();
		protected Map<String, List<XmlNode>> m_nodeMap = new HashMap<String, List<XmlNode>>();
		protected String m_name;
		protected XmlNode m_firstChild, m_lastChild, m_prevSibling, m_nextSibling;
		int m_childCount = 0;
		protected static final List<XmlNode> m_emptyList = new ArrayList<XmlNode>();
		
		public XmlNode()
		{
			
		}
		
		public XmlNode(String name)
		{
			setName(name);
		}
		
		public String getName()
		{
			return m_name;
		}
		
		public void setName(String name)
		{
			m_name = name.intern();
		}
		
		public String getAttribute(String name)
		{
			String val = m_attrMap.get(name);
			
			if (val == null)
				return "";
			else
				return val;
		}
		
		public void setAttribute(String name, String value)
		{
			m_attrMap.put(name.intern(), value.intern());
		}
		
		public XmlNode addNode(String name)
		{
			XmlNode node = new XmlNode();
			node.setName(name);
			
			if (m_nodeMap.size() == 0)
				m_firstChild = node;
			
			List<XmlNode> list = m_nodeMap.get(name);
			if (list == null)
			{
				list = new ArrayList<XmlNode>();
				m_nodeMap.put(name, list);
			}

			list.add(node);
			
			if (m_lastChild != null)
				m_lastChild.m_nextSibling = node;
			
			node.m_prevSibling = m_lastChild;			
			m_lastChild = node;
			
			m_childCount++;
			
			return node;
		}
		
		public XmlNode getNode(String name)
		{
			List<XmlNode> list = m_nodeMap.get(name);
			
			if (list == null)
				return null;
			else
				return list.get(0);
		}
		
		public List<XmlNode> getNodeList(String name)
		{
			List<XmlNode> list = m_nodeMap.get(name);
			
			if (list == null)
				return m_emptyList;
			else
				return list;
		}
		
		public List<XmlNode> getAllChildren()
		{
			List<XmlNode> list = new ArrayList<XmlNode>();
			
			for (List<XmlNode> l : m_nodeMap.values())
			{
				list.addAll(l);
			}
			
			return list;
		}
		
		public XmlNode getFirstChild()
		{
			return m_firstChild;
		}
		
		public XmlNode getLastChild()
		{
			return m_lastChild;
		}
		
		public XmlNode getPrevSibling()
		{
			return m_prevSibling;
		}
		
		public XmlNode getNextSibling()
		{
			return m_nextSibling;
		}
		
		public int getChildCount()
		{
			return m_childCount;
		}
	}
	
	
	class XmlHandler extends DefaultHandler
	{
		protected Stack<XmlNode> m_nodeStack = new Stack<XmlNode>();
		protected XmlNode m_topNode = null;
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			XmlNode node = null;
			
			if (m_nodeStack.empty())
				node = new XmlNode(qName);
			else
				node = m_nodeStack.peek().addNode(qName);
			
			for (int i = 0; i < attributes.getLength(); i++)
			{
				node.setAttribute(attributes.getQName(i), attributes.getValue(i));
			}
			
			m_nodeStack.push(node);
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			XmlNode node = m_nodeStack.pop();
			
			if (m_nodeStack.empty())
				m_topNode = node;
		}		
		
		public XmlNode getTopNode()
		{
			return m_topNode;
		}
		
		public void error(SAXParseException e) throws SAXException
		{
			throw e;
		}
	}	
	
	
	protected abstract interface ITypeDeclWrapperFactory
	{
		public AstNode wrapTypeDecl(Declaration decl) throws Exception;
	}
	
	
	protected class MemberTypeDeclWrapperFactory implements ITypeDeclWrapperFactory 
	{
		public AstNode wrapTypeDecl(Declaration decl) throws Exception
		{
			TypeDeclarationMember member = createAstNode(TypeDeclarationMember.class, null);
			member.setDeclaration(decl);
			member.m_left = decl.m_left;
			member.m_right = decl.m_right;
			
			return member;
		}
	}

	
	protected class StmtTypeDeclWrapperFactory implements ITypeDeclWrapperFactory 
	{
		public AstNode wrapTypeDecl(Declaration decl) throws Exception
		{
			TypeDeclarationStatement stmt = createAstNode(TypeDeclarationStatement.class, null);
			stmt.setDeclaration(decl);
			stmt.m_left = decl.m_left;
			stmt.m_right = decl.m_right;
			
			return stmt;
		}
	}	
	
}
