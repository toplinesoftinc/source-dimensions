package com.sourcedimensions.server.ast;

import java.util.*;

public class ElementValueArrayInitializer extends AstNode 
{	
	public List<ElementValue> m_elementValues = new AstArrayList<ElementValue>(this, 0);
}
