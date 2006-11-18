package com.sourcedimensions.server.ast;

import java.util.*;

public class DelegateMember extends Member 
{
	public String m_name;	
	public List<Parameter> m_parameters = new AstArrayList<Parameter>(this, 2);
	public List<TypeParameter> m_typeParams = new AstArrayList<TypeParameter>(this, 3);
	public List<Constraint> m_constraints = new AstArrayList<Constraint>(this, 4);
	
	public String toString()
	{
		return toString(m_name);
	}
}
