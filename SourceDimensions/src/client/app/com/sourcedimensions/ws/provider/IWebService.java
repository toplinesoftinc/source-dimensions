package com.sourcedimensions.ws.provider;

import java.util.Set;

import org.codehaus.xfire.fault.XFireFault;


public interface IWebService
{	
	enum FaultValues
	{
		LOGIN_FAILED,
		SESSION_EXPIRED;
	}
		
	public String login(String userName, String password) throws XFireFault;
	public Set<IProject> getProjectList(String sessionID) throws XFireFault; 
}
