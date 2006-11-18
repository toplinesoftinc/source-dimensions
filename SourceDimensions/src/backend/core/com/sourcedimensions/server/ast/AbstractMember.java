package com.sourcedimensions.server.ast;

import java.util.*;

public class AbstractMember extends AstNode
{
	public Set<Modifier> m_modifiers = new AstHashSet<Modifier>(this, 0);
	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 1);
}
