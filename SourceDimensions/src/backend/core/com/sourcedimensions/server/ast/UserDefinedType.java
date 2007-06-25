package com.sourcedimensions.server.ast;

import java.util.*;

public class UserDefinedType extends Type 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 1);
	
	public String getName()
	{
		if (m_name.size() > 0)
			return m_name.get(m_name.size() - 1).m_name;
		else 
			return "";
	}
}
