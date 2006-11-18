package com.sourcedimensions.server.exceptions;

public class DuplicateFileNameException extends Exception
{
	private static final long serialVersionUID = 7526472295622776147L;	
	protected static final String message = "File/folder with name\"%s\" already exists.";
	
	public DuplicateFileNameException() 
	{
		super(); 
	}
	
	public DuplicateFileNameException(String s)
	{
		super(String.format(message, s));
	}
	
	public DuplicateFileNameException(String s, Throwable cause)
	{
		super(String.format(message, s), cause); 
	}
}
