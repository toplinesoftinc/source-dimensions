package com.sourcedimensions.client.forms;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import com.sourcedimensions.client.TriStateBoolean;
import com.sourcedimensions.client.forms.DialogBase.TriStateCheckBoxAdapter;
import com.sourcedimensions.client.model.Project.Language;
import com.sourcedimensions.client.views.ProjectView;


public class LocalDeclDialog extends DialogBase
{
	private Shell m_shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Group m_typeGroup;
	private Label m_typePropsLabel;
	private Table m_typePropsList;
	private Label m_typeNameLabel;
	private Text m_typeNameText;
	private Label m_localDeclNameFilterLabel;
	private Text m_localDeclNameText;
	private Button m_okButton;
	private Button m_cancelButton;
	private Table m_finalCheckBox;
	private Type m_type = new Type();
	private String m_name;
	private TriStateBoolean m_final;
	
	private Type.Property[] m_javaTypeProps =
	{
		Type.Property.ARRAY,
		Type.Property.TYPE_PARAM
	};
	
	private Type.Property[] m_csharpTypeProps =
	{
		Type.Property.ARRAY,
		Type.Property.TYPE_PARAM,
		Type.Property.POINTER,
		Type.Property.NULLABLE
	};

	public LocalDeclDialog(Display display, Shell parent, Type type, String name, TriStateBoolean finalFlag)
	{
		m_display = display;
		createShell(parent);
		m_typeNameText.setText(type.m_name);
		m_localDeclNameText.setText(name);
		
		for (int i = 0; i < m_typePropsList.getItemCount(); i++)
		{
			setTriStateBoolValue(m_typePropsList.getItem(i), 
					type.m_typeProps.getMask(Type.Property.values()[i].value()));
		}		

		if (finalFlag != null)
		{
			setTriStateBoolValue(m_finalCheckBox.getItem(0), finalFlag);
		}
	}	
	
	public LocalDeclDialog(Display display, Shell parent)
	{
		m_display = display;
		createShell(parent);		
	}
	
	protected void createShell(Shell parent) 
	{	
		m_shell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Local Declaration");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		createTypeGroup();
		m_shell.setSize(new Point(361, 262));
		m_localDeclNameFilterLabel = new Label(getShell(), SWT.NONE);
		m_localDeclNameFilterLabel.setBounds(new Rectangle(14, 185, 150, 13));
		m_localDeclNameFilterLabel.setText("&Local Declaration Name Filter:");
		m_localDeclNameText = new Text(getShell(), SWT.BORDER);
		m_localDeclNameText.setBounds(new Rectangle(14, 199, 231, 19));
		m_okButton = new Button(getShell(), SWT.NONE);
		m_okButton.setLocation(new Point(256, 18));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String val = m_typeNameText.getText().trim();
				
				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Type Name Filter");
					return;
				}
				
				try
				{
					Pattern.compile(val);
				}
				catch(PatternSyntaxException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input",
						"Pattern for type name \"" + val + "\" has the following error: " + ex.getMessage());
					return;
				}
				m_type.m_name = val;
				
				val = m_localDeclNameText.getText().trim();

				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Local Declaration Name Filter");
					return;
				}				
				
				try
				{
					Pattern.compile(val);
				}
				catch(PatternSyntaxException ex)
				{
					MessageDialog.openError(m_shell, "Incorrect input",
						"Pattern for filter name \"" + val + "\" has the following error: " + ex.getMessage());
					return;
				}
				m_name = val;
				
				m_type.m_typeProps.reset();				
				for (int i = 0; i < m_typePropsList.getItemCount(); i++)
				{
					m_type.m_typeProps.setMask(Type.Property.values()[i].value(), getTriStateBoolValue(m_typePropsList.getItem(i)));
				}
				
				if (m_finalCheckBox != null)
				{
					m_final = getTriStateBoolValue(m_finalCheckBox.getItem(0));
				}
				else
				{
					m_final = null;
				}
				
				m_cancel = false;
				m_shell.close();				
			}
		});
		m_cancelButton = new Button(getShell(), SWT.NONE);
		m_cancelButton.setLocation(new Point(256, 55));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		m_cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		
		Language lang = ProjectView.getProject().getLanguage();
		if (lang == Language.JAVA14 || lang == Language.JAVA15)
		{		
			m_finalCheckBox = new Table(getShell(), SWT.BORDER | SWT.HIDE_SELECTION | SWT.CHECK);
			m_finalCheckBox.setHeaderVisible(false);
			m_finalCheckBox.setLinesVisible(false);
			m_finalCheckBox.setBounds(new Rectangle(256, 198, 87, 20));
			new TableItem(m_finalCheckBox, 0).setText("Final");
			m_finalCheckBox.addSelectionListener(new TriStateCheckBoxAdapter());
		}

		m_shell.setDefaultButton(m_okButton);
		
		super.createShell(parent);
		
		m_localDeclNameText.setFocus();
	}

	private void createTypeGroup() 
	{
		m_typeGroup = new Group(m_shell, SWT.NONE);
		m_typeGroup.setLayout(null);
		m_typeGroup.setText("&Type");
		m_typeGroup.setBounds(new Rectangle(14, 12, 230, 160));
		m_typePropsLabel = new Label(m_typeGroup, SWT.NONE);
		m_typePropsLabel.setText("Type &Properties:");
		m_typePropsLabel.setBounds(new Rectangle(12, 19, 88, 13));
		m_typePropsList = new Table(m_typeGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.HIDE_SELECTION);
		m_typePropsList.setHeaderVisible(false);
		m_typePropsList.setLinesVisible(false);
		m_typePropsList.setBounds(new Rectangle(12, 33, 108, 69));
		m_typePropsList.addSelectionListener(new TriStateCheckBoxAdapter());
		
		Type.Property[] props = getTypePropArray();
		
		for (int i = 0; i < props.length; i++)
		{
			new TableItem(m_typePropsList, 0, i).setText(props[i].toString());
		}							
		
		m_typeNameLabel = new Label(m_typeGroup, SWT.NONE);
		m_typeNameLabel.setBounds(new Rectangle(12, 115, 73, 13));
		m_typeNameLabel.setText("Type &Name:");
		m_typeNameText = new Text(m_typeGroup, SWT.BORDER);
		m_typeNameText.setBounds(new Rectangle(12, 130, 206, 19));
	}

	private Type.Property[] getTypePropArray()
	{
  		switch (ProjectView.getProject().getLanguage())
  		{
  			case JAVA14:
  			case JAVA15:
  				return m_javaTypeProps;
  				
  			case CSHARP11:
  			case CSHARP20:
  				return m_csharpTypeProps;
  				
  			default:
  				return null;
  		}
	}

	public Type getType()
	{
		return m_type;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public TriStateBoolean getFinal()
	{
		return m_final;
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
