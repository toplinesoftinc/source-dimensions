package com.sourcedimensions.server.ast;

import java.util.*;

public class Constraint extends AstNode 
{
	public String m_paramName;
	public List<ConstraintBound> m_bounds = new AstArrayList<ConstraintBound>(this, 0);
	
	public String toString()
	{
		return toString(m_paramName);
	}
}
