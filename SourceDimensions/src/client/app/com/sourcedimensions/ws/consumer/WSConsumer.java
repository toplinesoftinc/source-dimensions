package com.sourcedimensions.ws.consumer;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.ssl.HttpSecureProtocol;
import org.apache.commons.ssl.TrustMaterial;
import org.codehaus.xfire.*;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.client.*;

import java.lang.reflect.Proxy;
import java.util.Properties;

import org.codehaus.xfire.fault.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.sourcedimensions.ws.provider.IWebService;
import com.sourcedimensions.client.Login;



public class WSConsumer 
{
	protected static final String m_propFileName = "ws-consumer.properties"; 
	protected static final String m_urlPropName = "URL";
	protected static final String m_certPropName = "CERT";
	protected static final String m_sslPortPropName = "SSLPORT";
		
	protected static String m_serviceUrl;

	protected Client m_client;
	protected boolean m_cancelled = false;

	static
	{
		Properties props = new Properties();
		try
		{
			props.load(WSConsumer.class.getResourceAsStream(m_propFileName));
			m_serviceUrl = props.getProperty(m_urlPropName);
			
			String cert = props.getProperty(m_certPropName);
			
			if (cert != null)
			{
				String port = props.getProperty(m_sslPortPropName);
				
				HttpSecureProtocol protocolSocketFactory = new HttpSecureProtocol();
				protocolSocketFactory.addTrustMaterial(new TrustMaterial(WSConsumer.class.getResource(cert)));
				protocolSocketFactory.setDoVerify(true);

				Protocol protocol = new Protocol("https", (ProtocolSocketFactory) protocolSocketFactory, Integer.parseInt(port));
				Protocol.registerProtocol("https", protocol);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception during properties reading: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public Object loginToServer(Display display, Shell parent, String methodName, Object[] params) throws Exception
	{
		return invoke(display, parent, methodName, params);
	}
	
	public Object invokeWebService(Display display, Shell parent, String methodName, Object[] params) throws Exception
	{
		if (Login.getSessionID() == null)
		{
			new Login(display, parent).open();
			
			if (Login.getSessionID() == null)
				return null;
		}
		
		while (true)
		{
			try
			{
				return invoke(display, parent, methodName, params);
			}
			catch (XFireFault fault)
			{
				if (fault.getDetail().equals(IWebService.FaultValues.SESSION_EXPIRED.name()))
				{
					MessageDialog.openWarning(null, "Session expired", "You session is expired. Please re-login");
			
					new Login(display, parent).open();
					
					if (Login.getSessionID() == null)
						return null;
				}
				else
				{
					MessageDialog.openError(null, "Error", fault.getMessage());
					
					return null;
				}
			}
		}
	}
	
	protected Object invoke(Display display, Shell parent, String methodName, Object[] params) throws Exception
	{
		Service serviceModel = new ObjectServiceFactory().create(IWebService.class);

		XFire xfire = XFireFactory.newInstance().getXFire();
		XFireProxyFactory factory = new XFireProxyFactory(xfire);      
		
		IWebService service = (IWebService) factory.create(serviceModel, m_serviceUrl);
	
		m_client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();
		m_client.setProperty("mtom-enabled", "true");

		m_cancelled = false;
		
		WaitDialog waitDialog = new WaitDialog(display, this, parent);

		WorkingThread thread = new WorkingThread(m_client, methodName, params, waitDialog);

		thread.start();
		waitDialog.open();

		m_client = null;
		
		if (thread.getException() != null)
			throw thread.getException();
		else
			return thread.getResult();
	}
	
	public void cancelRequest()
	{	
		if (m_client != null)
		{
			try
			{
				m_client.close();
			}
			catch (Exception e) { }
			
			m_client = null;
			m_cancelled = true;
		}
	}
	
	public boolean wasCancelled()
	{
		return m_cancelled;
	}
	
	
	protected class WorkingThread extends Thread
	{
		protected Client m_client;	
		protected String m_methodName;
		protected Object[] m_params;
		protected WaitDialog m_waitDialog;
		protected Exception m_exception;
		protected Object m_result;
		
		public WorkingThread(Client client, String methodName, Object[] params, WaitDialog waitDialog)
		{
			m_client = client;
			m_methodName = methodName;
			m_params = params;
			m_waitDialog = waitDialog;
		}
		
		public Exception getException()
		{
			return m_exception;
		}
		
		public Object getResult()
		{
			return m_result;
		}
		
		public void run()
		{
			Object[] result = null;
			
			try
			{
				result = m_client.invoke(m_methodName, m_params);
			}
			catch (Exception e)
			{
				m_exception = e;
				return;
			}
			finally
			{
				if (result != null && result.length > 0)
					m_result = result[0];
				
				m_waitDialog.close();
			}
			
		}
	}
}
