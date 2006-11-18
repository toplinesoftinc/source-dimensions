package com.sourcedimensions.ws.provider;

import org.codehaus.xfire.fault.XFireFault;


public interface IWebService
{	
	enum FaultValues
	{
		LOGIN_FAILED,
		SESSION_EXPIRED;
	}
		
	public String login(String userName, String password) throws XFireFault;
	public String getProjects(String sessionID) throws XFireFault; 
}
