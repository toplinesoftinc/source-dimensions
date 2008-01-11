package com.sourcedimensions.server.ast;

import java.util.*;

public class FixedSizeBufMember extends Member 
{	
	public Set<FixedSizeBufDeclarator> m_bufDeclarators = new AstHashSet<FixedSizeBufDeclarator>(this, 2);
}
