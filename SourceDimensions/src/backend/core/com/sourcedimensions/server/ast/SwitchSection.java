package com.sourcedimensions.server.ast;

import java.util.*;

public class SwitchSection extends AstNode 
{
	public Set<SwitchLabel> m_labels = new AstHashSet<SwitchLabel>(this, 0);
	public List<AbstractStatement> m_statements = new AstArrayList<AbstractStatement>(this, 1);
}
