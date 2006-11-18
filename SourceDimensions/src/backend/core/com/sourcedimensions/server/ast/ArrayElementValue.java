package com.sourcedimensions.server.ast;

public class ArrayElementValue extends ElementValue 
{
	protected ElementValueArrayInitializer m_initializer;
	
	public ElementValueArrayInitializer getInitializer()
	{
		return m_initializer;
	}
	
	public void setInitializer(ElementValueArrayInitializer initializer)
	{
		m_initializer = initializer;
		addChild(initializer);
	}
}
