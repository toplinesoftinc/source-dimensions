package com.sourcedimensions.client;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class Util 
{
	public static void centerWindow(Shell window, Shell parent)
	{
		Dimension parentSize;
		Point parentLoc = new Point(0,0);
		
		if (parent == null)
			parentSize = Toolkit.getDefaultToolkit().getScreenSize();
		else
		{
			Point size = parent.getSize();
			parentSize = new Dimension(size.x, size.y);
			parentLoc = parent.getLocation();
		}
		
		Point size = window.getSize();

		int x = parentSize.width / 2 - size.x / 2 + parentLoc.x;
		int y = parentSize.height / 2 - size.y / 2 + parentLoc.y;

		window.setLocation(x, y);
	}
	
	public static ImageDescriptor createImageDescriptor(String image)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID,  image);
	}
}
