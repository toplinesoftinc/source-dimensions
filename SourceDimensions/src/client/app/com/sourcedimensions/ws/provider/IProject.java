package com.sourcedimensions.ws.provider;

import java.util.Set;

public interface IProject
{
	public String getID();
	public String getName();
	public int getLanguage();
	public boolean getReadOnly();
	public Set<IProject> getParents();
}
