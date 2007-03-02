package com.sourcedimensions.client.model;

import java.util.List;


public class SymbolQuery 
{
	protected boolean m_allNamespaces;
	protected List<String> m_namespacesFilter;
	
	protected boolean m_allTypes;
	protected List<TypeFilter> m_typeFilter;
	
	protected boolean m_allMembers;
	protected List<MemberFilter> m_memberFilter;
	
	private boolean m_allLocalDecls;
	private List<LocalDeclFilter> m_localDeclFilter;
	
	public boolean getAllNamespaces()
	{
		return m_allNamespaces;
	}
	
	public void setAllNamespaces(boolean allNamespaces)
	{
		m_allNamespaces = allNamespaces;
	}
	
	public List<String> getNamespaceFilter()
	{
		return m_namespacesFilter;
	}
	
	public void setNamespaceFilter(List<String> namespaceFilter)
	{
		m_namespacesFilter = namespaceFilter;
	}
	
	public boolean getAllTypes()
	{
		return m_allTypes;
	}

	public void setAllTypes(boolean allTypes)
	{
		m_allTypes = allTypes;
	}
	
	public List<TypeFilter> getTypeFilter()
	{
		return m_typeFilter;
	}
	
	public void setTypeFilter(List<TypeFilter> typeFilter)
	{
		m_typeFilter = typeFilter;
	}

	protected void setAllMembers(boolean allMembers) 
	{
		m_allMembers = allMembers;
	}

	protected boolean getAllMembers() 
	{
		return m_allMembers;
	}

	public void setMemberFilter(List<MemberFilter> memberFilter) 
	{
		m_memberFilter = memberFilter;
	}

	public List<MemberFilter> getMemberFilter() 
	{
		return m_memberFilter;
	}

	public void setAllLocalDecls(boolean allLocalDecls) 
	{
		m_allLocalDecls = allLocalDecls;
	}

	public boolean getAllLocalDecls() 
	{
		return m_allLocalDecls;
	}

	public void setLocalDeclFilter(List<LocalDeclFilter> localDeclFilter) 
	{
		m_localDeclFilter = localDeclFilter;
	}

	public List<LocalDeclFilter> getLocalDeclFilter() 
	{
		return m_localDeclFilter;
	}
}
