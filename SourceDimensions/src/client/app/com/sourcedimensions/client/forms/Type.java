package com.sourcedimensions.client.forms;

import com.sourcedimensions.client.Util.TriStateBoolean;


public class Type 
{
	public TriStateBoolean m_isArray = TriStateBoolean.FALSE;
	public TriStateBoolean m_isPointer = TriStateBoolean.FALSE;
	public TriStateBoolean m_isNullable = TriStateBoolean.FALSE;
	public TriStateBoolean m_isTypeParam = TriStateBoolean.FALSE;
	public String m_name;
}
