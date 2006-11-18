package com.sourcedimensions.server.ast;

import java.util.*;

public class UsingAliasDirective extends Directive 
{
	public String m_alias;
	public List<Name> m_name = new AstArrayList<Name>(this, 0);
	
	public String toString()
	{
		return toString(m_alias);
	}
}
