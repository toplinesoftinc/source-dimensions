package com.sourcedimensions.client.forms;

import com.sourcedimensions.client.Util.TriStateBoolean;


public class Type 
{
	public TriStateBoolean m_isArray = TriStateBoolean.EITHER;
	public TriStateBoolean m_isPointer = TriStateBoolean.EITHER;
	public TriStateBoolean m_isNullable = TriStateBoolean.EITHER;
	public TriStateBoolean m_isTypeParam = TriStateBoolean.EITHER;

	public String m_name;
}
