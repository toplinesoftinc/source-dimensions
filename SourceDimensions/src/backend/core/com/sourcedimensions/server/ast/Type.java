package com.sourcedimensions.server.ast;

import java.util.*;

public class Type extends AstNode
{
	public int m_rank = 0;
	public int m_ptrIndirection = 0;
	public boolean m_nullable = false;
	public List<TypeArgument> m_arguments = new AstArrayList<TypeArgument>(this, 0);
}
