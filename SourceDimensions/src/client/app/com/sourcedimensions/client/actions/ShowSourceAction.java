package com.sourcedimensions.client.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.model.SourceFilePackage;
import com.sourcedimensions.client.views.ProjectView;
import com.sourcedimensions.client.views.SnapshotView;
import com.sourcedimensions.client.views.SnapshotView.SnapshotNodeTreeItem;
import com.sourcedimensions.ws.consumer.WSConsumer;


public class ShowSourceAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate 
{
	protected IStructuredSelection m_selection;	
	protected IWorkbenchWindow m_window;

	public void init(IWorkbenchWindow window) 
	{
		m_window = window;
	}

	public void run(IAction action) 
	{
		try
		{
			showSource(m_window.getShell(), (SnapshotNodeTreeItem)m_selection.getFirstElement());
		}
		catch (Exception e)
		{
			MessageDialog.openError(m_window.getShell(), "Show Source Error", e.getMessage());
		}
	}
	
	public static void showSource(Shell shell, SnapshotNodeTreeItem item) throws Exception
	{
		Map<String,String> fileNames;
		Set<String> absentFiles = new HashSet<String>();
		
		try
		{
			fileNames = DbAdapter.getFileNames(item.getID());
		}
		catch (Exception e)
		{
			return;
		}
		
		Set<Map.Entry<String,String>> entrySet = fileNames.entrySet();
		
		for (Map.Entry<String,String> entry : entrySet)
		{
			if (entry.getValue() == null)
				absentFiles.add(entry.getKey());
		}
		
		WSConsumer consumer = new WSConsumer();
		String projectId = ProjectView.getProject().getId();
		SourceFilePackage pack;

		if (absentFiles.size() > 0)
		{
			try
			{
				pack = (SourceFilePackage)consumer.invokeWebService(shell, "getSourceFiles", new Object[] { projectId, absentFiles });			
			}
			catch (Exception ex)
			{
				MessageDialog.openError(shell, "Web Service Error", ex.getMessage());
				return;
			}
			
			if (consumer.wasCancelled())
				return;
			
			ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(pack.getData()));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			for (ZipEntry entry = zipInput.getNextEntry(); entry != null; entry = zipInput.getNextEntry())
			{
				byte[] buffer = new byte[4096];
				output.reset();
				
				for (int read = zipInput.read(buffer); read != -1; read = zipInput.read(buffer))
				{
					output.write(buffer, 0, read);
				}
			
				String fileId = entry.getName();
				String fileName = pack.getFileMap().get(fileId);
				
				try
				{
					DbAdapter.writeSourceFile(projectId, fileId, fileName, output.toByteArray());
				}
				catch (Exception e)
				{
					zipInput.close();
					return;
				}
				
				fileNames.put(fileId, fileName);
				
				zipInput.closeEntry();
			}
	
			zipInput.close();
		}
		
		item.getSnapshotView().setSelectedItem(item, fileNames);		
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		m_window = targetPart.getSite().getWorkbenchWindow();	
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		if (selection instanceof IStructuredSelection)
			m_selection = (IStructuredSelection)selection;		
	}	
	
	public void dispose() 
	{
	}
}
