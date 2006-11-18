package com.sourcedimensions.server.sys;

import java.io.*;

public class Parser
{
	static
	{
		String path = "";
		
		try
		{
			path = new File(".").getCanonicalPath();
		}
		catch (IOException e)
		{
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
		
		System.load(path + File.separatorChar + System.mapLibraryName("icudt34"));	
		System.load(path + File.separatorChar + System.mapLibraryName("icuuc34"));
		System.load(path + File.separatorChar + System.mapLibraryName("parserlib"));
	}
	
	public static native byte[] parse(int language, String filename, 
					byte[] text, int len, byte[] outbuf, int[] outlen);
}
