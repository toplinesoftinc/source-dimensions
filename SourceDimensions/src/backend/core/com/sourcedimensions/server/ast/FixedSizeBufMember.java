package com.sourcedimensions.server.ast;

import java.util.*;

public class FixedSizeBufMember extends Member 
{	
	public Set<FixedSizeBufDeclarator> m_declarators = new AstHashSet<FixedSizeBufDeclarator>(this, 2);
}
