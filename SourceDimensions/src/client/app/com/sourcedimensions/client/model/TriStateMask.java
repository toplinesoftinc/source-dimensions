package com.sourcedimensions.client.model;

import com.sourcedimensions.client.model.TriStateBoolean;

public class TriStateMask 
{
	protected int m_included, m_excluded;

	public void setMask(int mask, TriStateBoolean value)
	{

		switch (value)
		{
			case TRUE:
				m_included |= mask;
				m_excluded &= ~mask;
				break;
				
			case FALSE:
				m_included &= ~mask;
				m_excluded |= mask;
				break;
				
			case EITHER:
				m_included |= mask;
				m_excluded |= mask;
		}
	}
	
	public TriStateBoolean getMask(int mask)
	{
		if ((mask & m_included) == 0)
		{
			return TriStateBoolean.FALSE;
		}
		else
		{
			if ((mask & m_excluded) == 0)
				return TriStateBoolean.TRUE;
			else
				return TriStateBoolean.EITHER;
		}
	}
	
	public void reset()
	{
		m_included = 0;
		m_excluded = 0;
	}
	
	public long getValue()
	{
		return (long)(m_included | (m_excluded >> Integer.SIZE));
	}

	public void setValue(long value)
	{
		m_included = (int)value;
		m_excluded = (int)(value << Integer.SIZE);
	}	
}
