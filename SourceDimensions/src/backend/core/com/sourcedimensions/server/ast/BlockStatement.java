package com.sourcedimensions.server.ast;

import java.util.*;

public class BlockStatement extends EmbeddedStatement  
{
	public List<AbstractStatement> m_statements = new AstArrayList<AbstractStatement>(this, 0);
}
