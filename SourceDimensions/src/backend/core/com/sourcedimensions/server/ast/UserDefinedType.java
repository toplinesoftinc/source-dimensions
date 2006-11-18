package com.sourcedimensions.server.ast;

import java.util.*;

public class UserDefinedType extends Type 
{
	public List<Name> m_name = new AstArrayList<Name>(this, 1);
}
