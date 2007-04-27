package com.sourcedimensions.client.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;

import com.sourcedimensions.client.IImageKeys;
import com.sourcedimensions.client.Util;
import com.sourcedimensions.client.db.DbAdapter;
import com.sourcedimensions.client.views.SnapshotView;


public class SnapshotListDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Button m_exitButton;
	private Button m_activateButton;
	private Table m_snapshotList;
	private List<SnapshotView> m_viewerList = new ArrayList<SnapshotView>();  //  @jve:decl-index=0:

	public SnapshotListDialog(Shell parent)
	{
		createShell(parent);
	}	
	
	protected void createShell(Shell parent)
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		if (parent != null)
			m_shell.setParent(parent);		
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setText("Switch To Snapshot");
		m_shell.setSize(new Point(609, 346));
		m_shell.setLayout(null);
		m_activateButton = new Button(m_shell, SWT.NONE);
		m_activateButton.setToolTipText("Cancel");
		m_activateButton.setText("&Activate");
		m_activateButton.setLocation(new Point(505, 14));
		m_activateButton.setSize(new Point(88, 25));
		m_activateButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_activateButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int sel = m_snapshotList.getSelectionIndex();
				
				if (sel == -1)
				{
					MessageDialog.openWarning(m_shell, "Selection", "Please select snapshot");
					return;
				}
				else
				{
					activateSnapshot(sel);
				}
			}
		});
		m_exitButton = new Button(m_shell, SWT.NONE);
		m_exitButton.setToolTipText("Cancel");
		m_exitButton.setText("E&xit");
		m_exitButton.setLocation(new Point(505, 63));
		m_exitButton.setSize(new Point(88, 25));
		m_exitButton.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.NORMAL));
		m_snapshotList = new Table(getShell(), SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER);
		m_snapshotList.setHeaderVisible(true);
		m_snapshotList.setLinesVisible(true);
		m_snapshotList.setBounds(new Rectangle(10, 14, 483, 289));
		double width = m_snapshotList.getBounds().width - 2 * m_snapshotList.getBorderWidth(); 
		TableColumn column = new TableColumn(m_snapshotList, SWT.LEFT, 0);
		column.setWidth((int)(0.25 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Title");
		column = new TableColumn(m_snapshotList, SWT.LEFT, 1);
		column.setWidth((int)(0.75 * width));
		column.setResizable(true);
		column.setMoveable(true);
		column.setText("Snapshot Path");
		
		m_snapshotList.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				int sel = m_snapshotList.getSelectionIndex(); 
				
				if (sel != -1)
				{
					activateSnapshot(sel);
				}
			}			
		});

		
		m_exitButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				m_shell.close();
			}
		});

		m_shell.setDefaultButton(m_activateButton);
		super.createShell(parent);

		Map<Integer, List<SnapshotView>> viewerTable = SnapshotView.getViewerTable();
		
		for (Entry<Integer, List<SnapshotView>> entry : viewerTable.entrySet())
		{
			Integer id = entry.getKey();
			String fullName = "";
			
			try
			{
				fullName = DbAdapter.getSnapshot(id).getFullName();
			}
			catch (Exception e)
			{
				return;
			}
			
			for (SnapshotView view : entry.getValue())
			{
				TableItem item = new TableItem(m_snapshotList, SWT.NONE);

				item.setImage(0, Util.getSharedImage(IImageKeys.IMG_SNAPSHOT));
				item.setText(0, view.getTitle());
				item.setText(1, fullName);
				
				m_viewerList.add(view);
			}
		}	
	}

	protected void activateSnapshot(int index)
	{
		m_shell.close();
		
		SnapshotView view = m_viewerList.get(index);
		IWorkbenchPage page = view.getSite().getPage();
		
		page.activate(view);
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}	
}
