package com.sourcedimensions.server.ast;

import java.util.List;
import java.util.Set;

public class EnumConstMember extends AbstractMember
{
	public String m_name;
	public List<Expression> m_arguments = new AstArrayList<Expression>(this, 2);
	public Set<AbstractMember> m_members = new AstHashSet<AbstractMember>(this, 3);
	
	public String toString()
	{
		return toString(m_name);
	}
}
