package com.sourcedimensions.server.ast;

import java.util.*;

public class UsingDirective extends Directive 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 0);
	public boolean m_isStatic;
	public boolean m_isOnDemand;
	
	public UsingDirective()
	{
		m_isStatic = false;
		m_isOnDemand = false;
	}
	
	public UsingDirective(boolean _static, boolean ondemand)
	{
		m_isStatic = _static;
		m_isOnDemand = ondemand;
	}	
}
