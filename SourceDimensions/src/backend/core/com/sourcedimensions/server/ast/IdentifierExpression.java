package com.sourcedimensions.server.ast;

import java.util.*;

public class IdentifierExpression extends Expression 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 0);
}
