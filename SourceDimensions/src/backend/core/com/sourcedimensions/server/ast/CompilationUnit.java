package com.sourcedimensions.server.ast;

import java.util.*;
import com.sourcedimensions.server.sys.*;

public class CompilationUnit extends AstNode 
{	
	public void setSourceFile(SourceFile file)
	{
		m_file = file;
	}

	public Set<AttributeBlock> m_attributes = new AstHashSet<AttributeBlock>(this, 0);
	public Set<Directive> m_directives = new AstHashSet<Directive>(this, 1);
	public Set<Declaration> m_declarations = new AstHashSet<Declaration>(this, 2);
}
