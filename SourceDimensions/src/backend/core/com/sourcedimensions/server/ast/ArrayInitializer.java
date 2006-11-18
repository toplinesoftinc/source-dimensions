package com.sourcedimensions.server.ast;

import java.util.*;

public class ArrayInitializer extends Initializer 
{
	public List<Initializer> m_arrayInit = new AstArrayList<Initializer>(this, 0);
}
