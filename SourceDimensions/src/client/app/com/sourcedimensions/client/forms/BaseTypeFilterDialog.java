package com.sourcedimensions.client.forms;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import com.sourcedimensions.client.model.Project.Language;
import com.sourcedimensions.client.views.ProjectView;

public class BaseTypeFilterDialog extends DialogBase 
{
	private Shell m_shell = null;  //  @jve:decl-index=0:visual-constraint="11,6"
	private String m_value;  //  @jve:decl-index=0:
	private Label m_typeCategoryLabel = null;
	private Label m_baseTypeNameFilterLabel = null;
	private Text m_baseTypeFilterText = null;
	private Combo m_integralTypeCombo = null;
	private Button m_okButton = null;
	private Button m_cancelButton = null;
	private Combo m_typeCategoryCombo = null;  //  @jve:decl-index=0:visual-constraint="369,103"
	private TypeCategory m_categoryValue;
	
	protected static String[] m_typeCategoryNames = 
	{
		"CLASS",
		"INTERFACE",
		"CLASS/INTERFACE",
		"INTEGRAL TYPE"
	};

	public enum TypeCategory
	{
		CLASS(0),
		INTERFACE(1),
		CLASS_INTERFACE(2),
		INTEGRAL_TYPE(3);
		
		TypeCategory(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}

	public BaseTypeFilterDialog(Display display, Shell parent)
	{
		m_display = display;
		m_value = null;
		m_categoryValue = TypeCategory.CLASS;
		createShell(parent);
	}		
	
	public BaseTypeFilterDialog(Display display, Shell parent, String value, int category)
	{
		m_display = display;
		m_value = value;
		m_categoryValue = TypeCategory.values()[category];
		createShell(parent);
	}	
	
	private void createShell(Shell parent)
	{
		m_shell = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		if (parent != null)
			m_shell.setParent(parent);
		
		m_shell.setText("Base Type Filter");
		m_shell.setImage(new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/img16.gif")));
		m_shell.setSize(new Point(336, 142));
		m_shell.setToolTipText("");
		m_shell.setLayout(null);
		m_typeCategoryLabel = new Label(m_shell, SWT.NONE);
		m_typeCategoryLabel.setBounds(new Rectangle(17, 20, 82, 13));
		m_typeCategoryLabel.setText("&Type Category:");
		m_baseTypeNameFilterLabel = new Label(m_shell, SWT.NONE);
		m_baseTypeNameFilterLabel.setBounds(new Rectangle(17, 46, 113, 15));
		m_baseTypeNameFilterLabel.setText("&Base Type Name Filter:");
		m_baseTypeFilterText = new Text(m_shell, SWT.BORDER);
		m_baseTypeFilterText.setBounds(new Rectangle(130, 42, 184, 19));
		createIntegralTypeCombo();
		m_okButton = new Button(m_shell, SWT.NONE);
		m_okButton.setLocation(new Point(62, 79));
		m_okButton.setText("O&k");
		m_okButton.setSize(new Point(88, 25));
		m_okButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String val;
				
				if (m_typeCategoryCombo.getSelectionIndex() == TypeCategory.INTEGRAL_TYPE.value())
					val = m_integralTypeCombo.getText().trim();
				else
					val = m_baseTypeFilterText.getText().trim();

				if (val.length() == 0)
				{
					MessageDialog.openError(m_shell, "Incorrect input",	"Please enter value for Base Type Name Filter");					
					return;
				}

				String[] names = val.split("/");
				
				for (String name : names)
				{
					if (name.length() == 0)
					{
						MessageDialog.openError(m_shell, "Incorrect input", "Namespace section cannot be empty (like \"com//abc\")");
						return;
					}
					
					if (name.equals("**"))
					{
						continue;
					}
					
					try
					{
						Pattern.compile(name);
					}
					catch(PatternSyntaxException ex)
					{
						MessageDialog.openError(m_shell, "Incorrect input",
							"Pattern \"" + name + "\" has the following error: " + ex.getMessage());
						return;
					}
				}
				
				m_value = val;
				m_categoryValue = TypeCategory.values()[m_typeCategoryCombo.getSelectionIndex()];
				m_shell.close();
			}
		});
		m_cancelButton = new Button(m_shell, SWT.NONE);
		m_cancelButton.setLocation(new Point(179, 79));
		m_cancelButton.setText("&Cancel");
		m_cancelButton.setSize(new Point(88, 25));
		createTypeCategoryCombo();
		m_cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				cancelClose();
			}
		});
		
		if (m_value != null)
		{
			m_typeCategoryCombo.select(m_categoryValue.value);
			
			if (m_categoryValue == TypeCategory.INTEGRAL_TYPE)
			{
				m_integralTypeCombo.setVisible(true);
				m_baseTypeFilterText.setVisible(false);				
				m_integralTypeCombo.select(m_integralTypeCombo.indexOf(m_value));
			}
			else
				m_baseTypeFilterText.setText(m_value);
		}
		
		m_shell.setDefaultButton(m_okButton);
		postCreate(parent);
	}

	public String getValue()
	{
		return m_value;
	}

	public TypeCategory getTypeCategory()
	{
		return m_categoryValue;
	}
	
	public String getTypeCategoryName()
	{
		return m_typeCategoryNames[m_categoryValue.value];
	}	
	
	public static String getTypeCategoryName(int code)
	{
		return m_typeCategoryNames[code];
	}
	
	protected void cancelClose()
	{
		m_value = null;
		m_shell.close();
	}
	
	private void createIntegralTypeCombo() 
	{
		m_integralTypeCombo = new Combo(m_shell, SWT.READ_ONLY);
		m_integralTypeCombo.setVisible(false);
		m_integralTypeCombo.setText("");
		m_integralTypeCombo.setVisibleItemCount(9);
		m_integralTypeCombo.setBounds(new Rectangle(130, 42, 183, 21));
		m_integralTypeCombo.add("sbyte");
		m_integralTypeCombo.add("byte");
		m_integralTypeCombo.add("short");
		m_integralTypeCombo.add("ushort");
		m_integralTypeCombo.add("int");
		m_integralTypeCombo.add("uint");
		m_integralTypeCombo.add("long");
		m_integralTypeCombo.add("ulong");
		m_integralTypeCombo.add("char");		
	}

	private void createTypeCategoryCombo() 
	{
		m_typeCategoryCombo = new Combo(m_shell, SWT.READ_ONLY);
		m_typeCategoryCombo.setBounds(new Rectangle(130, 12, 154, 21));
		m_typeCategoryCombo.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				boolean integral_type = (m_typeCategoryCombo.getSelectionIndex() == TypeCategory.INTEGRAL_TYPE.value()); 
				m_integralTypeCombo.setVisible(integral_type);
				m_baseTypeFilterText.setVisible(!integral_type);
			}
		});
		
		int namelen = m_typeCategoryNames.length;
		Language lang = ProjectView.getProject().getLanguage();
		
		if (lang != Language.CSHARP11 && lang != Language.CSHARP20)
			namelen--;
				
		for (int i = 0; i < namelen; i++)
		{
			m_typeCategoryCombo.add(m_typeCategoryNames[i]);
		}
		
		m_typeCategoryCombo.select(m_categoryValue.value);		
	}
	
	protected Shell getShell()
	{
		return m_shell;
	}
}
