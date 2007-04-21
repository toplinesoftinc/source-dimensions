package com.sourcedimensions.ws.provider;

import com.sourcedimensions.client.model.Project;
import com.sourcedimensions.client.model.Snapshot;
import com.sourcedimensions.client.model.SymbolQuery;

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
	public Set<Project> getProjectList(String sessionID) throws XFireFault;
	public Snapshot runSymbolQuery(String sessionID, SymbolQuery query) throws XFireFault;
}
