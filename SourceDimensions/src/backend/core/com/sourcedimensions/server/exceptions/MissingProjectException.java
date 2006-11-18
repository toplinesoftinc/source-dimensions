package com.sourcedimensions.server.exceptions;

public class MissingProjectException extends Exception
{
	private static final long serialVersionUID = 7526472295622776147L;	
	protected static final String message = "Project ID = %s is missing";
	
	public MissingProjectException() 
	{
		super(); 
	}
	
	public MissingProjectException(String s)
	{
		super(String.format(message, s));
	}
	
	public MissingProjectException(String s, Throwable cause)
	{
		super(String.format(message, s), cause); 
	}
}
