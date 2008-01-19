package com.sourcedimensions.server.sys.profile.adminutil;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import java.awt.Dimension;
import javax.swing.JTabbedPane;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sourcedimensions.server.sys.astimport.ImportManager;
import com.sourcedimensions.server.sys.profile.Account;
import com.sourcedimensions.server.sys.profile.Database;
import com.sourcedimensions.server.sys.profile.Limit;
import com.sourcedimensions.server.sys.profile.User;
import com.sourcedimensions.server.sys.Project;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.text.DateFormatter;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Point;
import javax.swing.WindowConstants;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;



public class AdminUtil extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel m_topLvlPanel = null;
	private JTabbedPane m_mainTabPane = null;
	private JPanel m_dbPanel = null;
	private JPanel m_accPanel = null;
	private JScrollPane m_dbScrollPane = null;
	private JTable m_dbTable = null;
	private JButton m_dbNewButton = null;
	private JButton m_dbEditButton = null;
	private JButton m_dbDeleteButton = null;
	private JDialog m_databaseDialog = null;  //  @jve:decl-index=0:visual-constraint="628,77"
	private JPanel m_databasePane = null;
	private JLabel m_dbUrlLabel = null;
	private JLabel m_dbNameLabel = null;
	private JTextField m_dbUrlField = null;
	private JTextField m_dbNameField = null;
	private JLabel m_dbUsrNameLabel = null;
	private JLabel m_dbPwdLabel = null;
	private JTextField m_dbUsrNameField = null;
	private JPasswordField m_dbPwdField = null;
	private JButton m_dbSaveButton = null;
	private JButton m_dbCancelButton = null;
	private JButton m_dbTestButton = null;
	private List<String> m_dbIdList = null;  //  @jve:decl-index=0:
	private List<String> m_accIdList = null;
	private List<String> m_prjIdList = null;  //  @jve:decl-index=0:
	private List<String> m_limitIdList = null;  //  @jve:decl-index=0:
	private String m_curDbId = null;  //  @jve:decl-index=0:
	private String m_curAccId = null;
	private String m_curPrjId = null;
	private String m_curLimitId = null;
	private JComboBox m_dbAccComboBox = null;
	private JLabel m_dbSelLabel = null;
	private JScrollPane m_accScrlPane = null;
	private JTable m_accTable = null;
	private JPanel m_prjPanel = null;
	private JButton m_accNewButton = null;
	private JButton m_accEditButton = null;
	private JButton m_accDeleteButton = null;
	private JDialog m_accountDialog = null;  //  @jve:decl-index=0:visual-constraint="7,394"
	private JPanel m_accountPane = null;
	private JFormattedTextField m_accExpDateField = null;
	private JCheckBox m_accExpCheckBox = null;
	private JLabel m_accExpiredLabel = null;
	private JTextField m_accOwnerNameField = null;
	private JLabel m_accOwnerNameLabel = null;
	private JLabel m_accCreatDateLabel = null;
	private JTextField m_accCreatDateField = null;
	private JLabel m_accStatusLabel = null;
	private JComboBox m_accStatusComboBox = null;
	private JLabel m_accExpDateLabel = null;
	private JCheckBox m_accWriteCheckBox = null;
	private JLabel m_accWriteLabel = null;
	private JLabel m_accLangAccessLabel = null;
	private JComboBox m_accLangAccessComboBox = null;
	private JLabel m_accSuperuserNameLabel = null;
	private JTextField m_accUserNameField = null;
	private JLabel m_accSuperuserPwdLabel = null;
	private JPasswordField m_accPasswordField = null;
	private JButton m_accSaveButton = null;
	private JButton m_accCancelButton = null;
	private JLabel m_prjDbSelLabel = null;
	private JComboBox m_dbPrjComboBox = null;
	private JScrollPane m_prjScrlPane = null;
	private JTable m_prjTable = null;
	private JDialog m_projectDialog = null;  //  @jve:decl-index=0:visual-constraint="469,395"
	private JPanel m_projectPane = null;
	private JLabel m_prjNameLabel = null;
	private JTextField m_prjNameField = null;
	private JLabel m_prjRootLabel = null;
	private JTextField m_prjRootField = null;
	private JCheckBox m_prjReadOnlyCheckBox = null;
	private JLabel m_prjReadOnlyLabel = null;
	private JLabel m_prjLangLabel = null;
	private JComboBox m_prjLangComboBox = null;
	private JButton m_prjSaveButton = null;
	private JButton m_prjCancelButton = null;
	private JButton m_prjAddButton = null;
	private JButton m_prjEditButton = null;
	private JButton m_prjDeleteButton = null;
	private SessionFactory m_curPrjDbFactory = null;
	private SessionFactory m_curAccDbFactory = null;
	private SessionFactory m_curDbFactory = null;
	private JButton m_prjParentButton = null;
	private JDialog m_prjAssocDialog = null;  //  @jve:decl-index=0:visual-constraint="468,636"
	private JPanel m_prjAssocPane = null;
	private JList m_prjProjectsList = null;
	private JList m_prjSelectedList = null;
	private JButton m_prjAddAssocButton = null;
	private JButton m_prjRemAssocButton = null;
	private JButton m_prjAssocCloseButton = null;
	private JLabel m_prjSelectedLabel = null;
	private JLabel m_prjNotSelLabel = null;
	private List<String> m_selectedPrjList = new ArrayList<String>();  //  @jve:decl-index=0:
	private List<String> m_notSelPrjList = new ArrayList<String>();  //  @jve:decl-index=0:
	private JScrollPane m_prjSelScrollPane = null;
	private JScrollPane m_prjNotSelScrollPane = null;
	private JButton m_accProjectButton = null;
	private JButton m_prjImportButton = null;
	private JDialog m_progressDialog = null;  //  @jve:decl-index=0:visual-constraint="59,974"
	private JPanel m_progressContentPane = null;
	private JProgressBar m_topProgBar = null;
	private JProgressBar m_bottomProgBar = null;
	private JButton m_progCancelButton = null;
	private JLabel m_progOverallLabel = null;
	private JLabel m_progDetailLabel = null;
	private JLabel m_procFileLabel = null;
	private boolean m_cancelProcess = false;
	private JLabel m_progElapsedLabel = null;
	private boolean m_dbValid = true;
	private boolean m_prjEditMode = false;
	
	private final static String m_tempPath = File.separator + "tmp";  //  @jve:decl-index=0:
	private final static String[] m_limitNames = 
	{
		"Max. number of namespaces",
		"Max. number of types",
		"Max. number of members"
	}; 
	
	
	private JLabel m_procStatLabel = null;
	private JPanel m_limitPanel = null;
	private JScrollPane m_limitScrlPane = null;
	private JTable m_limitTable = null;
	private JButton m_limitAddButton = null;
	private JButton m_limitEditButton = null;
	private JButton m_limitDeleteButton = null;
	private JDialog m_limitDialog = null;  //  @jve:decl-index=0:visual-constraint="668,977"
	private JPanel m_limitDialogPane = null;
	private JButton m_limitSaveButton = null;
	private JButton m_limitCancelButton = null;
	private JLabel m_limitTypeLabel = null;
	private JComboBox m_limitTypeComboBox = null;
	private JLabel m_limitValueLabel = null;
	private JFormattedTextField m_limitValueField = null;
	/**
	 * This method initializes jTopLvlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDbTopLvlPanel()
	{
		if (m_topLvlPanel == null)
		{
			m_topLvlPanel = new JPanel();
			m_topLvlPanel.setLayout(null);
			m_topLvlPanel.add(getDbMainTabPane(), null);
		}
		return m_topLvlPanel;
	}

	/**
	 * This method initializes jMainTabPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getDbMainTabPane()
	{
		if (m_mainTabPane == null)
		{
			m_mainTabPane = new JTabbedPane();
			m_mainTabPane.setBounds(new Rectangle(14, 13, 580, 338));
			m_mainTabPane.addTab("Databases", null, getDbPanel(), "Databases");
			m_mainTabPane.addTab("Accounts", null, getAccPanel(), "Accounts");
			m_mainTabPane.addTab("Projects", null, getPrjPanel(), "Projects");
			m_mainTabPane.addTab("Limits", null, getLimitPanel(), "Limits");
		}
		
		return m_mainTabPane;
	}

	/**
	 * This method initializes jDbPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDbPanel()
	{
		if (m_dbPanel == null)
		{
			m_dbPanel = new JPanel();
			m_dbPanel.setLayout(null);
			m_dbPanel.setToolTipText("Databases");
			m_dbPanel.add(getDbScrollPane(), null);
			m_dbPanel.add(getDbNewButton(), null);
			m_dbPanel.add(getDbEditButton(), null);
			m_dbPanel.add(getDbDeleteButton(), null);
		}
		return m_dbPanel;
	}

	/**
	 * This method initializes jAccountPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAccPanel()
	{
		if (m_accPanel == null)
		{
			m_dbSelLabel = new JLabel();
			m_dbSelLabel.setBounds(new Rectangle(13, 15, 66, 16));
			m_dbSelLabel.setText("Database:");
			m_accPanel = new JPanel();
			m_accPanel.setLayout(null);
			m_accPanel.setToolTipText("Accounts");
			m_accPanel.add(getDbComboBox(), null);
			m_accPanel.add(m_dbSelLabel, null);
			m_accPanel.add(getAccountScrlPane(), null);
			m_accPanel.add(getAccNewButton(), null);
			m_accPanel.add(getAccEditButton(), null);
			m_accPanel.add(getAccDeleteButton(), null);
		}
		return m_accPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getDbScrollPane()
	{
		if (m_dbScrollPane == null)
		{
			m_dbScrollPane = new JScrollPane();
			m_dbScrollPane.setBounds(new Rectangle(14, 13, 437, 283));
			m_dbScrollPane.setViewportView(getDbTable());
		}
		return m_dbScrollPane;
	}

	/**
	 * This method initializes jDbTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getDbTable()
	{
		if (m_dbTable == null)
		{
			Vector<String> headers = new Vector<String>();
			headers.add("Server URL");
			headers.add("Database name");
			
			DefaultTableModel defaultTableModel = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;
				
		        public boolean isCellEditable(int rowIndex, int vColIndex) 
		        {
		            return false;
		        }
		    };			

		    defaultTableModel.setColumnCount(2);
			defaultTableModel.setColumnIdentifiers(headers);
			defaultTableModel.setNumRows(0);
			m_dbTable = new JTable();
			m_dbTable.setAutoCreateColumnsFromModel(true);
			m_dbTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			m_dbTable.setCellSelectionEnabled(false);
			m_dbTable.setRowSelectionAllowed(true);
			m_dbTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			m_dbTable.setModel(defaultTableModel);
		}
		return m_dbTable;
	}

	/**
	 * This method initializes jNewButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbNewButton()
	{
		if (m_dbNewButton == null)
		{
			m_dbNewButton = new JButton();
			m_dbNewButton.setBounds(new Rectangle(464, 15, 97, 26));
			m_dbNewButton.setText("Add...");
			m_dbNewButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					JDialog dialog = getDatabaseDialog();
					
					m_curDbId = null;
					m_dbUrlField.setText("");
					m_dbNameField.setText("");
					m_dbUsrNameField.setText("");
					m_dbPwdField.setText("");
					
					dialog.setVisible(true);					
				}
			});
		}
		return m_dbNewButton;
	}

	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbEditButton()
	{
		if (m_dbEditButton == null)
		{
			m_dbEditButton = new JButton();
			m_dbEditButton.setBounds(new Rectangle(464, 71, 97, 26));
			m_dbEditButton.setActionCommand("");
			m_dbEditButton.setText("Edit...");
			m_dbEditButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_dbTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No database record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						m_curDbId = m_dbIdList.get(sel);
						
						Session session = Database.getProfileSessionFactory().getCurrentSession();
						
						session.beginTransaction();

						Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + m_curDbId + "'").uniqueResult();
					
						JDialog dialog = getDatabaseDialog();
						
						m_dbUrlField.setText(db.getServerUrl());
						m_dbNameField.setText(db.getDatabaseName());
						m_dbUsrNameField.setText(db.getUserName());
						m_dbPwdField.setText(db.getPassword());
						
						session.getTransaction().commit();
						
						dialog.setVisible(true);
					}
				}
			});
		}
		return m_dbEditButton;
	}

	/**
	 * This method initializes jDeleteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbDeleteButton()
	{
		if (m_dbDeleteButton == null)
		{
			m_dbDeleteButton = new JButton();
			m_dbDeleteButton.setBounds(new Rectangle(464, 127, 97, 26));
			m_dbDeleteButton.setActionCommand("");
			m_dbDeleteButton.setText("Delete");
			m_dbDeleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_dbTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No database record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if (JOptionPane.showConfirmDialog(null, "Do you want to delete selected database?", 
								"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
						{
							String id = m_dbIdList.get(sel);
							
							Session session = Database.getProfileSessionFactory().getCurrentSession();
							session.beginTransaction();
							List list = session.createQuery("FROM Account a JOIN a.m_database AS db WHERE db.m_id = '" + id + "'").list();
							session.getTransaction().commit();
							
							if (list.size() > 0)
							{
								JOptionPane.showMessageDialog(null, "Cannot delete database. It has associated accounts.", 
										"Error", JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								session = Database.getProfileSessionFactory().getCurrentSession();
								session.beginTransaction();
								Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + id + "'").uniqueResult();
								session.delete(db);
								session.getTransaction().commit();
								refreshDbList();
							}
						}
					}
				}
			});
		}
		return m_dbDeleteButton;
	}

	/**
	 * This method initializes jDatabaseDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getDatabaseDialog()
	{
		if (m_databaseDialog == null)
		{
			m_databaseDialog = new JDialog(this);
			m_databaseDialog.setSize(new Dimension(371, 257));
			centerWindow(m_databaseDialog);
			m_databaseDialog.setTitle("Database");
			m_databaseDialog.setName("Database");
			m_databaseDialog.setModal(true);
			m_databaseDialog.setResizable(false);
			m_databaseDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			m_databaseDialog.setSize(new Dimension(366, 257));
			m_databaseDialog.setContentPane(getDatabasePane());
		}
		return m_databaseDialog;
	}

	/**
	 * This method initializes jDatabasePane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDatabasePane()
	{
		if (m_databasePane == null)
		{
			m_dbPwdLabel = new JLabel();
			m_dbPwdLabel.setBounds(new Rectangle(13, 135, 80, 16));
			m_dbPwdLabel.setText("Password:");
			m_dbUsrNameLabel = new JLabel();
			m_dbUsrNameLabel.setBounds(new Rectangle(13, 105, 93, 16));
			m_dbUsrNameLabel.setText("User name:");
			m_dbNameLabel = new JLabel();
			m_dbNameLabel.setBounds(new Rectangle(13, 55, 104, 15));
			m_dbNameLabel.setText("Database name:");
			m_dbUrlLabel = new JLabel();
			m_dbUrlLabel.setText("Database server URL:");
			m_dbUrlLabel.setBounds(new Rectangle(13, 25, 129, 17));
			m_databasePane = new JPanel();
			m_databasePane.setLayout(null);
			m_databasePane.add(m_dbUrlLabel, null);
			m_databasePane.add(getDbUrlField(), null);
			m_databasePane.add(m_dbNameLabel, null);
			m_databasePane.add(getDbNameField(), null);
			m_databasePane.add(m_dbUsrNameLabel, null);
			m_databasePane.add(m_dbPwdLabel, null);
			m_databasePane.add(getDbUserNameField(), null);
			m_databasePane.add(getDbPasswordField(), null);
			m_databasePane.add(getDbSaveButton(), null);
			m_databasePane.add(getDbCancelButton(), null);
			m_databasePane.add(getDbTestButton(), null);
		}
		return m_databasePane;
	}

	/**
	 * This method initializes jUrlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbUrlField()
	{
		if (m_dbUrlField == null)
		{
			m_dbUrlField = new JTextField();
			m_dbUrlField.setBounds(new Rectangle(153, 22, 195, 20));
		}
		return m_dbUrlField;
	}

	/**
	 * This method initializes jDbNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbNameField()
	{
		if (m_dbNameField == null)
		{
			m_dbNameField = new JTextField();
			m_dbNameField.setBounds(new Rectangle(153, 50, 195, 20));
		}
		return m_dbNameField;
	}

	/**
	 * This method initializes jUserNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbUserNameField()
	{
		if (m_dbUsrNameField == null)
		{
			m_dbUsrNameField = new JTextField();
			m_dbUsrNameField.setBounds(new Rectangle(153, 101, 167, 20));
		}
		return m_dbUsrNameField;
	}

	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getDbPasswordField()
	{
		if (m_dbPwdField == null)
		{
			m_dbPwdField = new JPasswordField();
			m_dbPwdField.setBounds(new Rectangle(153, 131, 167, 20));
		}
		return m_dbPwdField;
	}

	/**
	 * This method initializes jSaveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbSaveButton()
	{
		if (m_dbSaveButton == null)
		{
			m_dbSaveButton = new JButton();
			m_dbSaveButton.setText("Save");
			m_dbSaveButton.setSize(new Dimension(87, 23));
			m_dbSaveButton.setLocation(new Point(10, 190));
			m_dbSaveButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_dbUrlField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter database URL.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (m_dbNameField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter database name.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (m_dbUsrNameField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter user name.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					Session session = Database.getProfileSessionFactory().getCurrentSession();
					
					session.beginTransaction();

					Database db = new Database();
					
					db.m_id = m_curDbId;
					db.setServerUrl(m_dbUrlField.getText());
					db.setDatabaseName(m_dbNameField.getText());
					db.setUserName(m_dbUsrNameField.getText());
					
					db.setPassword("");
					char[] pwd = m_dbPwdField.getPassword();
					for (int i = 0; i < pwd.length; i++)
						db.setPassword(db.getPassword() + pwd[i]);
					
					session.saveOrUpdate(db);
					
					session.getTransaction().commit();
					
					m_databaseDialog.setVisible(false);
					m_databaseDialog = null;
					
					refreshDbList();
				}
			});
		}
		return m_dbSaveButton;
	}

	/**
	 * This method initializes jCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbCancelButton()
	{
		if (m_dbCancelButton == null)
		{
			m_dbCancelButton = new JButton();
			m_dbCancelButton.setBounds(new Rectangle(115, 190, 87, 23));
			m_dbCancelButton.setText("Cancel");
			m_dbCancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_databaseDialog.setVisible(false);
					m_databaseDialog = null;
				}
			});
		}
		return m_dbCancelButton;
	}

	/**
	 * This method initializes jTestButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbTestButton()
	{
		if (m_dbTestButton == null)
		{
			m_dbTestButton = new JButton();
			m_dbTestButton.setBounds(new Rectangle(261, 190, 87, 23));
			m_dbTestButton.setText("Test");
			m_dbTestButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					try
					{
						String pwd = "";
						
						for (int i = 0; i < m_dbPwdField.getPassword().length; i++)
							pwd += m_dbPwdField.getPassword()[i];
						
						Database db = new Database(m_dbUrlField.getText(), m_dbNameField.getText(), m_dbUsrNameField.getText(), pwd);
						Session session = db.getDbSessionFactory().getCurrentSession();
						session.beginTransaction();
						session.getTransaction().commit();
					}
					catch(Exception ex)
					{
						Throwable cause = ex.getCause();
						String message;
						
						if (cause == null)
							message = ex.getMessage();
						else
							message = cause.getMessage();
						
						JOptionPane.showMessageDialog(null, "Test failed. " + message, "Test failure", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					JOptionPane.showMessageDialog(null, "Test is successful.", "Test success", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
		return m_dbTestButton;
	}

	/**
	 * This method initializes m_dbComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getDbComboBox()
	{
		if (m_dbAccComboBox == null)
		{
			m_dbAccComboBox = new JComboBox();
			m_dbAccComboBox.setBounds(new Rectangle(76, 11, 272, 20));
			m_dbAccComboBox.addItemListener(new java.awt.event.ItemListener()
			{   
				public void itemStateChanged(java.awt.event.ItemEvent e) 
				{    
					int sel = m_dbAccComboBox.getSelectedIndex();
					
					if (sel == -1)
					{
						if (m_dbAccComboBox.getItemCount() > 0)
							m_dbAccComboBox.setSelectedIndex(0);
						
						return;
					}		
					
					Session session = Database.getProfileSessionFactory().getCurrentSession();
					String dbId = m_dbIdList.get(sel);
					
					session.beginTransaction();
					Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + dbId + "'").uniqueResult();
					session.getTransaction().commit();

					m_curAccDbFactory = db.getDbSessionFactory();					
					
					refreshAccList();
				}
			});
		}
		
		return m_dbAccComboBox;
	}

	/**
	 * This method initializes m_accScrlPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAccountScrlPane()
	{
		if (m_accScrlPane == null)
		{
			m_accScrlPane = new JScrollPane();
			m_accScrlPane.setBounds(new Rectangle(14, 38, 436, 259));
			m_accScrlPane.setViewportView(getAccountTable());
		}
		return m_accScrlPane;
	}

	/**
	 * This method initializes m_AccountTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getAccountTable()
	{
		if (m_accTable == null)
		{
			Vector<String> headers = new Vector<String>();
			headers.add("Owner's name");
			headers.add("Account status");
			
			DefaultTableModel defaultTableModel = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;
				
		        public boolean isCellEditable(int rowIndex, int vColIndex) 
		        {
		            return false;
		        }
		    };			

		    defaultTableModel.setColumnCount(2);
			defaultTableModel.setColumnIdentifiers(headers);
			defaultTableModel.setNumRows(0);
			
			m_accTable = new JTable();
			m_accTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			m_accTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			m_accTable.setModel(defaultTableModel);
		}
		
		return m_accTable;
	}

	/**
	 * This method initializes m_projectPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPrjPanel()
	{
		if (m_prjPanel == null)
		{
			m_prjDbSelLabel = new JLabel();
			m_prjDbSelLabel.setBounds(new Rectangle(13, 15, 69, 16));
			m_prjDbSelLabel.setText("Database:");
			m_prjPanel = new JPanel();
			m_prjPanel.setLayout(null);
			m_prjPanel.setToolTipText("Projects");
			m_prjPanel.add(m_prjDbSelLabel, null);
			m_prjPanel.add(getDbPrjComboBox(), null);
			m_prjPanel.add(getPrjPane(), null);
			m_prjPanel.add(getPrjAddButton(), null);
			m_prjPanel.add(getPrjEditButton(), null);
			m_prjPanel.add(getPrjDeleteButton(), null);
			m_prjPanel.add(getPrjImportButton(), null);
		}
		return m_prjPanel;
	}

	/**
	 * This method initializes m_accNewButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccNewButton()
	{
		if (m_accNewButton == null)
		{
			m_accNewButton = new JButton();
			m_accNewButton.setBounds(new Rectangle(459, 16, 97, 26));
			m_accNewButton.setPreferredSize(new Dimension(65, 26));
			m_accNewButton.setText("Add...");
			m_accNewButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_dbAccComboBox.getSelectedIndex() == -1)
						return;
					
					JDialog dialog = getAccountDialog();
									
					m_curAccId = null;
					m_accOwnerNameField.setText("");
					m_accStatusComboBox.setSelectedIndex(0);
					m_accCreatDateField.setText(new SimpleDateFormat("d/MM/yy").format(new Date()));
					m_accExpDateField.setText(m_accCreatDateField.getText());
					m_accExpCheckBox.setSelected(true);
					m_accWriteCheckBox.setSelected(false);
					m_accLangAccessComboBox.setSelectedIndex(0);
					m_accUserNameField.setText("");
					m_accPasswordField.setText("");
					
					m_notSelPrjList.clear();
					m_selectedPrjList.clear();
					
					dialog.setVisible(true);
				}
			});			
		}
		return m_accNewButton;
	}

	/**
	 * This method initializes m_accEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccEditButton()
	{
		if (m_accEditButton == null)
		{
			m_accEditButton = new JButton();
			m_accEditButton.setBounds(new Rectangle(459, 71, 97, 26));
			m_accEditButton.setText("Edit...");
			m_accEditButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_accTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No account record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						m_curAccId = m_accIdList.get(sel);
						
						JDialog dialog = getAccountDialog();
						
						Session session = Database.getProfileSessionFactory().getCurrentSession();
						
						session.beginTransaction();

						Account acc = (Account)session.createQuery("FROM Account WHERE m_id = '" + m_curAccId + "'").uniqueResult();
						User usr = (User)session.createQuery("FROM User WHERE m_account = :acc AND m_superUser = true").setEntity("acc", acc).uniqueResult();
					
						m_selectedPrjList.clear();
						m_notSelPrjList.clear();
						
						for (String id : acc.m_projectIds)
						{
							m_selectedPrjList.add(id);
						}
												
						session.getTransaction().commit();
						
						m_accOwnerNameField.setText(acc.m_ownerName);
						m_accStatusComboBox.setSelectedIndex(acc.getStatusValue());
						m_accCreatDateField.setText(new SimpleDateFormat("d/MM/yy").format(acc.m_creationDate));
						
						if (acc.m_expDate != null)
						{
							m_accExpDateField.setText(new SimpleDateFormat("d/MM/yy").format(acc.m_expDate));
						}

						m_accExpCheckBox.setSelected(acc.m_expDate == null);
						m_accWriteCheckBox.setSelected(acc.m_writeAccess);
						m_accLangAccessComboBox.setSelectedIndex(acc.getLangAccessValue());
						m_accUserNameField.setText(usr.m_name);
						m_accPasswordField.setText(usr.m_password);
						
						dialog.setVisible(true);
					}
				}
			});
		}
		return m_accEditButton;
	}

	/**
	 * This method initializes m_accDeleteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccDeleteButton()
	{
		if (m_accDeleteButton == null)
		{
			m_accDeleteButton = new JButton();
			m_accDeleteButton.setBounds(new Rectangle(459, 126, 97, 26));
			m_accDeleteButton.setText("Delete");
			m_accDeleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_accTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No account record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if (JOptionPane.showConfirmDialog(null, "Do you want to delete selected account?", 
								"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
						{
							String id = m_accIdList.get(sel);
							
							Session session = Database.getProfileSessionFactory().getCurrentSession();
							session.beginTransaction();
							Account acc = (Account)session.createQuery("FROM Account WHERE m_id = '" + id + "'").uniqueResult();
							session.delete(acc);
							session.getTransaction().commit();
							refreshAccList();
						}
					}
				}
			});
		}
		return m_accDeleteButton;
	}

	/**
	 * This method initializes m_accoutDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getAccountDialog()
	{
		if (m_accountDialog == null)
		{
			m_accountDialog = new JDialog(this);
			m_accountDialog.setSize(new Dimension(431, 408));
			centerWindow(m_accountDialog);
			m_accountDialog.setTitle("Account");
			m_accountDialog.setResizable(false);
			m_accountDialog.setModal(true);
			m_accountDialog.setContentPane(getAccContentPane());
		}
		return m_accountDialog;
	}

	/**
	 * This method initializes m_accContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAccContentPane()
	{
		if (m_accountPane == null)
		{
			m_accSuperuserPwdLabel = new JLabel();
			m_accSuperuserPwdLabel.setBounds(new Rectangle(17, 293, 131, 16));
			m_accSuperuserPwdLabel.setText("Superuser password:");
			m_accSuperuserNameLabel = new JLabel();
			m_accSuperuserNameLabel.setBounds(new Rectangle(17, 261, 103, 16));
			m_accSuperuserNameLabel.setText("Superuser name:");
			m_accLangAccessLabel = new JLabel();
			m_accLangAccessLabel.setBounds(new Rectangle(17, 211, 110, 16));
			m_accLangAccessLabel.setText("Language access:");
			m_accWriteLabel = new JLabel();
			m_accWriteLabel.setBounds(new Rectangle(176, 180, 88, 16));
			m_accWriteLabel.setText("Write access");
			m_accExpDateLabel = new JLabel();
			m_accExpDateLabel.setBounds(new Rectangle(17, 124, 134, 16));
			m_accExpDateLabel.setText("Expiration date (d/m/y):");
			m_accStatusLabel = new JLabel();
			m_accStatusLabel.setBounds(new Rectangle(17, 62, 93, 16));
			m_accStatusLabel.setText("Account status:");
			m_accCreatDateLabel = new JLabel();
			m_accCreatDateLabel.setBounds(new Rectangle(17, 94, 132, 16));
			m_accCreatDateLabel.setText("Creation date (d/m/y):");
			m_accOwnerNameLabel = new JLabel();
			m_accOwnerNameLabel.setBounds(new Rectangle(17, 27, 93, 16));
			m_accOwnerNameLabel.setText("Owner's name:");
			m_accExpiredLabel = new JLabel();
			m_accExpiredLabel.setBounds(new Rectangle(176, 154, 152, 16));
			m_accExpiredLabel.setText("Account does not expire");
			m_accountPane = new JPanel();
			m_accountPane.setLayout(null);
			m_accountPane.add(m_accOwnerNameLabel, null);
			m_accountPane.add(getAccOwnerNameField(), null);
			m_accountPane.add(m_accStatusLabel, null);
			m_accountPane.add(getAccStatusComboBox(), null);
			m_accountPane.add(m_accCreatDateLabel, null);
			m_accountPane.add(getAccOpenDateField(), null);
			m_accountPane.add(m_accExpiredLabel, null);
			m_accountPane.add(getAccExpField(), null);
			m_accountPane.add(m_accExpDateLabel, null);
			m_accountPane.add(getAccExpireBox(), null);
			m_accountPane.add(getAccWriteCheckBox(), null);
			m_accountPane.add(m_accWriteLabel, null);
			m_accountPane.add(m_accLangAccessLabel, null);
			m_accountPane.add(getAccLangAccessComboBox(), null);
			m_accountPane.add(m_accSuperuserNameLabel, null);
			m_accountPane.add(getAccSuperuserNameField(), null);
			m_accountPane.add(m_accSuperuserPwdLabel, null);
			m_accountPane.add(getAccPasswordField(), null);
			m_accountPane.add(getAccSaveButton(), null);
			m_accountPane.add(getAccCancelButton(), null);
			m_accountPane.add(getAccProjectButton(), null);
		}
		return m_accountPane;
	}

	/**
	 * This method initializes m_AccExpField	
	 * 	
	 * @return javax.swing.JFormattedTextField	
	 */
	private JFormattedTextField getAccExpField()
	{
		if (m_accExpDateField == null)
		{
			m_accExpDateField = new JFormattedTextField(new SimpleDateFormat("dd/MM/yy"));
			m_accExpDateField.setBounds(new Rectangle(153, 122, 116, 20));
			DateFormatter formatter = (DateFormatter)m_accExpDateField.getFormatter(); 
			formatter.setAllowsInvalid(false);
			formatter.setOverwriteMode(true);
			m_accExpDateField.setValue(new Date());
		}

		return m_accExpDateField;
	}

	/**
	 * This method initializes m_AccExpireBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAccExpireBox()
	{
		if (m_accExpCheckBox == null)
		{
			m_accExpCheckBox = new JCheckBox();
			m_accExpCheckBox.setBounds(new Rectangle(153, 151, 21, 21));
			m_accExpCheckBox.addItemListener(new java.awt.event.ItemListener()
			{
				public void itemStateChanged(java.awt.event.ItemEvent e)
				{
					m_accExpDateField.setEnabled(!m_accExpCheckBox.isSelected());
				}
			});
		}
		return m_accExpCheckBox;
	}

	/**
	 * This method initializes m_AccOwnerNameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAccOwnerNameField()
	{
		if (m_accOwnerNameField == null)
		{
			m_accOwnerNameField = new JTextField();
			m_accOwnerNameField.setBounds(new Rectangle(153, 23, 252, 20));
		}
		return m_accOwnerNameField;
	}

	/**
	 * This method initializes m_accOpenDateField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAccOpenDateField()
	{
		if (m_accCreatDateField == null)
		{
			m_accCreatDateField = new JTextField();
			m_accCreatDateField.setBounds(new Rectangle(153, 90, 117, 20));
			m_accCreatDateField.setEnabled(false);
			m_accCreatDateField.setEditable(false);
		}
		return m_accCreatDateField;
	}

	/**
	 * This method initializes m_accStatusComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getAccStatusComboBox()
	{
		if (m_accStatusComboBox == null)
		{
			m_accStatusComboBox = new JComboBox();
			m_accStatusComboBox.setBounds(new Rectangle(153, 58, 151, 20));
			m_accStatusComboBox.setEnabled(true);
			m_accStatusComboBox.setEditable(false);
			for (Enum e : Account.AccountStatus.values())
			{
				m_accStatusComboBox.addItem(e.name());
			}
		}
		return m_accStatusComboBox;
	}

	/**
	 * This method initializes m_accWriteCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAccWriteCheckBox()
	{
		if (m_accWriteCheckBox == null)
		{
			m_accWriteCheckBox = new JCheckBox();
			m_accWriteCheckBox.setBounds(new Rectangle(153, 177, 21, 21));
		}
		return m_accWriteCheckBox;
	}

	/**
	 * This method initializes m_accLangAccessComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getAccLangAccessComboBox()
	{
		if (m_accLangAccessComboBox == null)
		{
			m_accLangAccessComboBox = new JComboBox();
			m_accLangAccessComboBox.setBounds(new Rectangle(153, 208, 151, 20));
			m_accLangAccessComboBox.setEditable(false);
			for (Enum e : Account.LangAccess.values())
			{
				m_accLangAccessComboBox.addItem(e.name());
			}
			
		}
		return m_accLangAccessComboBox;
	}

	/**
	 * This method initializes m_accSuperuserNameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAccSuperuserNameField()
	{
		if (m_accUserNameField == null)
		{
			m_accUserNameField = new JTextField();
			m_accUserNameField.setBounds(new Rectangle(153, 258, 195, 20));
			m_accUserNameField.setText("");
		}
		return m_accUserNameField;
	}

	/**
	 * This method initializes m_accPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getAccPasswordField()
	{
		if (m_accPasswordField == null)
		{
			m_accPasswordField = new JPasswordField();
			m_accPasswordField.setBounds(new Rectangle(153, 290, 155, 20));
			m_accPasswordField.setText("");
		}
		return m_accPasswordField;
	}

	/**
	 * This method initializes m_accSaveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccSaveButton()
	{
		if (m_accSaveButton == null)
		{
			m_accSaveButton = new JButton();
			m_accSaveButton.setBounds(new Rectangle(16, 342, 87, 23));
			m_accSaveButton.setText("Save");
			m_accSaveButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_accOwnerNameField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter owner name.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (m_accUserNameField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter superuser name.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Session session = Database.getProfileSessionFactory().getCurrentSession();
					
					session.beginTransaction();

					String query = "FROM User WHERE m_name = '" + m_accUserNameField.getText() + "'";
					
					if (m_curAccId != null)
					{
						User u = (User)session.createQuery("SELECT u FROM Account a INNER JOIN a.m_users u WHERE a.m_id = '" + 
								m_curAccId + "' AND u.m_superUser = true").uniqueResult();
						
						query += " AND m_id != '" + u.getID() + "'";
					}
					
					if (session.createQuery(query).list().size() > 0)
					{
						session.getTransaction().commit();
						
						JOptionPane.showMessageDialog(null, "User name '" + m_accUserNameField.getText() + "' already exists.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String dbId = m_dbIdList.get(m_dbAccComboBox.getSelectedIndex());
					Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + dbId + "'").uniqueResult();

					Account acc = new Account(db);
					
					acc.m_id = m_curAccId;
					acc.m_ownerName = m_accOwnerNameField.getText();
					acc.setStatusValue(m_accStatusComboBox.getSelectedIndex());
					
					DateFormat fmt = new SimpleDateFormat("d/MM/yy");
					
					try
					{
						acc.m_creationDate = fmt.parse(m_accCreatDateField.getText());
						
						if (!m_accExpCheckBox.isSelected())
						{
							acc.m_expDate = fmt.parse(m_accExpDateField.getText());
						}
					}
					catch (Exception ex)
					{
					}
					
					acc.m_writeAccess = m_accWriteCheckBox.isSelected();
					acc.setLangAccessValue(m_accLangAccessComboBox.getSelectedIndex());
					
					acc.m_projectIds.clear();
					for (String id : m_selectedPrjList)
					{
						acc.m_projectIds.add(id);
					}
					
					session.saveOrUpdate(acc);
					
					User usr = null;
					
					if (m_curAccId == null)
					{
						usr = new User();
					}
					else
					{
						usr = (User)session.createQuery("FROM User WHERE m_account = :acc AND m_superUser = true").setEntity("acc", acc).uniqueResult();
					}
					
					usr.m_account = acc;
					usr.m_superUser = true;
					usr.m_fullName = m_accOwnerNameField.getText();;
					usr.m_name = m_accUserNameField.getText();

					usr.m_password = "";
					char[] pwd = m_accPasswordField.getPassword();
					for (int i = 0; i < pwd.length; i++)
						usr.m_password += pwd[i];
		
					session.saveOrUpdate(usr);
					
					session.getTransaction().commit();
					
					m_accountDialog.setVisible(false);
					m_accountDialog = null;
					
					refreshAccList();					
				}
			});
		}
		return m_accSaveButton;
	}

	/**
	 * This method initializes m_accCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccCancelButton()
	{
		if (m_accCancelButton == null)
		{
			m_accCancelButton = new JButton();
			m_accCancelButton.setBounds(new Rectangle(124, 342, 87, 23));
			m_accCancelButton.setText("Cancel");
			m_accCancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_accountDialog.setVisible(false);
					m_accountDialog = null;
				}
			});
		}
		return m_accCancelButton;
	}

	/**
	 * This method initializes m_dbPrjComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getDbPrjComboBox()
	{
		if (m_dbPrjComboBox == null)
		{
			m_dbPrjComboBox = new JComboBox();
			m_dbPrjComboBox.setBounds(new Rectangle(77, 11, 272, 20));
			m_dbPrjComboBox.addItemListener(new java.awt.event.ItemListener()
			{
				public void itemStateChanged(java.awt.event.ItemEvent e)
				{
					int sel = m_dbPrjComboBox.getSelectedIndex();
					
					if (sel == -1)
					{
						if (m_dbPrjComboBox.getItemCount() > 0)
							m_dbPrjComboBox.setSelectedIndex(0);
						
						return;
					}		
					
					Session session = Database.getProfileSessionFactory().getCurrentSession();
					String dbId = m_dbIdList.get(sel);
					
					session.beginTransaction();
					Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + dbId + "'").uniqueResult();
					session.getTransaction().commit();

					m_curPrjDbFactory = db.getDbSessionFactory();
					
					refreshPrjList();
				}
			});
		}
		return m_dbPrjComboBox;
	}

	/**
	 * This method initializes m_prjPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPrjPane()
	{
		if (m_prjScrlPane == null)
		{
			m_prjScrlPane = new JScrollPane();
			m_prjScrlPane.setBounds(new Rectangle(15, 39, 435, 258));
			m_prjScrlPane.setViewportView(getPrjTable());
		}
		return m_prjScrlPane;
	}

	/**
	 * This method initializes m_prjTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getPrjTable()
	{
		if (m_prjTable == null)
		{
			Vector<String> headers = new Vector<String>();
			headers.add("Project name");
			headers.add("Language");
			
			DefaultTableModel defaultTableModel = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;
				
		        public boolean isCellEditable(int rowIndex, int vColIndex) 
		        {
		            return false;
		        }
		    };			

		    defaultTableModel.setColumnCount(2);
			defaultTableModel.setColumnIdentifiers(headers);
			defaultTableModel.setNumRows(0);
						
			m_prjTable = new JTable();
			m_prjTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			m_prjTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			m_prjTable.setModel(defaultTableModel);
		}
		return m_prjTable;
	}

	/**
	 * This method initializes m_projectDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getProjectDialog()
	{
		if (m_projectDialog == null)
		{
			m_projectDialog = new JDialog(this);
			m_projectDialog.setSize(new Dimension(319, 227));
			centerWindow(m_projectDialog);
			m_projectDialog.setResizable(false);
			m_projectDialog.setTitle("Project");
			m_projectDialog.setModal(true);
			m_projectDialog.setContentPane(getProjectPane());
		}
		return m_projectDialog;
	}

	/**
	 * This method initializes m_projectPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getProjectPane()
	{
		if (m_projectPane == null)
		{
			m_prjLangLabel = new JLabel();
			m_prjLangLabel.setBounds(new Rectangle(16, 119, 62, 16));
			m_prjLangLabel.setText("Language:");
			m_prjReadOnlyLabel = new JLabel();
			m_prjReadOnlyLabel.setBounds(new Rectangle(106, 86, 75, 16));
			m_prjReadOnlyLabel.setText("Read-only");
			m_prjRootLabel = new JLabel();
			m_prjRootLabel.setBounds(new Rectangle(16, 56, 65, 16));
			m_prjRootLabel.setText("Root path:");
			m_prjNameLabel = new JLabel();
			m_prjNameLabel.setBounds(new Rectangle(16, 24, 38, 16));
			m_prjNameLabel.setText("Name:");
			m_projectPane = new JPanel();
			m_projectPane.setLayout(null);
			m_projectPane.add(m_prjNameLabel, null);
			m_projectPane.add(getPrjNameField(), null);
			m_projectPane.add(m_prjRootLabel, null);
			m_projectPane.add(getPrjRootField(), null);
			m_projectPane.add(getPrjReadOnlyCheckBox(), null);
			m_projectPane.add(m_prjReadOnlyLabel, null);
			m_projectPane.add(m_prjLangLabel, null);
			m_projectPane.add(getPrjLangComboBox(), null);
			m_projectPane.add(getPrjSaveButton(), null);
			m_projectPane.add(getPrjCancelButton(), null);
			m_projectPane.add(getPrjParentButton(), null);
		}
		return m_projectPane;
	}

	/**
	 * This method initializes m_prjNameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPrjNameField()
	{
		if (m_prjNameField == null)
		{
			m_prjNameField = new JTextField();
			m_prjNameField.setBounds(new Rectangle(87, 20, 207, 20));
		}
		return m_prjNameField;
	}

	/**
	 * This method initializes m_prjRootField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPrjRootField()
	{
		if (m_prjRootField == null)
		{
			m_prjRootField = new JTextField();
			m_prjRootField.setBounds(new Rectangle(87, 52, 207, 20));
		}
		return m_prjRootField;
	}

	/**
	 * This method initializes m_prjReadOnlyCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getPrjReadOnlyCheckBox()
	{
		if (m_prjReadOnlyCheckBox == null)
		{
			m_prjReadOnlyCheckBox = new JCheckBox();
			m_prjReadOnlyCheckBox.setBounds(new Rectangle(83, 83, 21, 21));
		}
		return m_prjReadOnlyCheckBox;
	}

	/**
	 * This method initializes m_prjLangComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getPrjLangComboBox()
	{
		if (m_prjLangComboBox == null)
		{
			m_prjLangComboBox = new JComboBox();
			m_prjLangComboBox.setBounds(new Rectangle(87, 115, 151, 20));

			for (Enum e : Project.Language.values())
			{
				String lang = e.name().toString();
				lang = lang.replace("_", " ");
				lang = (new StringBuffer(lang).insert(lang.length() - 1, ".")).toString();
				m_prjLangComboBox.addItem(lang);
			}

		}
		return m_prjLangComboBox;
	}

	/**
	 * This method initializes m_prjSaveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjSaveButton()
	{
		if (m_prjSaveButton == null)
		{
			m_prjSaveButton = new JButton();
			m_prjSaveButton.setBounds(new Rectangle(12, 160, 87, 23));
			m_prjSaveButton.setText("Save");
			m_prjSaveButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_prjNameField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter project name.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (m_prjRootField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter root path.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					File rootDir = new File(m_prjRootField.getText());
					
					if (rootDir.exists() && !m_prjEditMode)
					{
						JOptionPane.showMessageDialog(null, "Please enter non-existing root path.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					rootDir.mkdirs();

					try
					{
						Session session = m_curPrjDbFactory.getCurrentSession();
						session.beginTransaction();					
	
						Project prj = null;
						
						if (m_curPrjId == null)
							prj = new Project();
						else
							prj = (Project)session.createQuery("FROM Project WHERE m_id = '" + m_curPrjId + "'").uniqueResult();
						
						prj.m_name = m_prjNameField.getText();
						prj.m_rootPath = m_prjRootField.getText();
						prj.m_readOnly = m_prjReadOnlyCheckBox.isSelected();
						prj.setLangValue(m_prjLangComboBox.getSelectedIndex());

						prj.m_parents.clear();
						for (String id : m_selectedPrjList)
						{
							Project p = (Project)session.createQuery("FROM Project WHERE m_id = '" + id + "'").uniqueResult();
							prj.m_parents.add(p);
						}
											
						if (session.createQuery("FROM Project WHERE m_name = '" + m_prjNameField.getText() + 
													"' AND m_id != '" + m_curPrjId + "'").list().size() > 0)
						{
							JOptionPane.showMessageDialog(null, "Project with name '" + m_prjNameField.getText() +
									"' already exists in the database.", "InputError", JOptionPane.ERROR_MESSAGE);
							session.getTransaction().commit();
							return;
						}
	
						session.saveOrUpdate(prj);
						session.getTransaction().commit();
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(null, "Cannot save current project.", 
							"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
	
					m_projectDialog.setVisible(false);
					m_projectDialog = null;
						
					refreshPrjList();
				}
			});
		}
		return m_prjSaveButton;
	}

	/**
	 * This method initializes m_prjCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjCancelButton()
	{
		if (m_prjCancelButton == null)
		{
			m_prjCancelButton = new JButton();
			m_prjCancelButton.setBounds(new Rectangle(111, 160, 87, 23));
			m_prjCancelButton.setText("Cancel");
			m_prjCancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_projectDialog.setVisible(false);
					m_projectDialog = null;
				}
			});
		}
		return m_prjCancelButton;
	}

	/**
	 * This method initializes m_prjAddButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjAddButton()
	{
		if (m_prjAddButton == null)
		{
			m_prjAddButton = new JButton();
			m_prjAddButton.setBounds(new Rectangle(460, 17, 97, 26));
			m_prjAddButton.setText("Add...");
			m_prjAddButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_dbPrjComboBox.getSelectedIndex() == -1 || !m_dbValid)
						return;
					
					JDialog dialog = getProjectDialog();
					
					m_prjEditMode = false;
					m_curPrjId = null;
					m_prjNameField.setText("");
					m_prjRootField.setText("");
					m_prjReadOnlyCheckBox.setSelected(false);
					m_prjLangComboBox.setSelectedIndex(0);
					
					m_selectedPrjList.clear();
					
					dialog.setVisible(true);
				}
			});
		}
		return m_prjAddButton;
	}

	/**
	 * This method initializes m_prjEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjEditButton()
	{
		if (m_prjEditButton == null)
		{
			m_prjEditButton = new JButton();
			m_prjEditButton.setBounds(new Rectangle(460, 72, 97, 26));
			m_prjEditButton.setText("Edit...");
			m_prjEditButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_prjTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No project record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						m_prjEditMode = true;
						m_curPrjId = m_prjIdList.get(sel);
						Project prj = null;

						try
						{
							Session session = m_curPrjDbFactory.getCurrentSession();
							session.beginTransaction();
							prj = (Project)session.createQuery("FROM Project WHERE m_id = '" + m_curPrjId + "'").uniqueResult();					
							
							m_selectedPrjList.clear();
							
							for (Project p : prj.m_parents)
							{
								m_selectedPrjList.add(p.m_id);
							}

							session.getTransaction().commit();
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(null, "Cannot edit the project.", 
									"Error", JOptionPane.ERROR_MESSAGE);				
							return;
						}

						JDialog dialog = getProjectDialog();
						
						m_prjNameField.setText(prj.m_name);
						m_prjRootField.setText(prj.m_rootPath);
						m_prjReadOnlyCheckBox.setSelected(prj.m_readOnly);
						m_prjLangComboBox.setSelectedIndex(prj.getLangValue());
						
						dialog.setVisible(true);
					}
				}
			});
		}
		return m_prjEditButton;
	}

	/**
	 * This method initializes m_prjDeleteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjDeleteButton()
	{
		if (m_prjDeleteButton == null)
		{
			m_prjDeleteButton = new JButton();
			m_prjDeleteButton.setBounds(new Rectangle(460, 127, 97, 26));
			m_prjDeleteButton.setText("Delete");
			m_prjDeleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_prjTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No project record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (JOptionPane.showConfirmDialog(null, "Do you want to delete selected project?", 
								"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
						{						
							String id = m_prjIdList.get(sel);
							Session session = null;
							Project prj = null;
							
							try
							{
								session = m_curPrjDbFactory.getCurrentSession();
								session.beginTransaction();								
								prj = (Project)session.get(Project.class, id);
								int exists = session.createQuery("FROM Project WHERE m_id = :id AND EXISTS(SELECT 1 FROM AstNode WHERE m_project = :project)")
									.setString("id", id).setEntity("project", prj).list().size();
								if (exists > 0)
								{
									if (JOptionPane.showConfirmDialog(null, "The project contains imported files. They will be deleted too.\nDo you still want to delete selected project?", 
											"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.NO_OPTION)
									{
										session.getTransaction().commit();
										return;
									}
									
								}
							}
							catch (Exception ex)
							{
								if (session.isOpen())
									session.getTransaction().commit();
								
								JOptionPane.showMessageDialog(null, "Cannot delete the project.", 
										"Error", JOptionPane.ERROR_MESSAGE);								
							}

							while (true)
							{
								Iterator iter = session.createQuery("FROM AstNode WHERE m_project = :project").setEntity("project", prj).iterate();
								
								if (!iter.hasNext())
									break;
						
								String className = Hibernate.getClass(iter.next()).getName();								
								session.createQuery("DELETE FROM " + className + " WHERE m_project = :project").setEntity("project", prj).executeUpdate();
							}

							session.flush();

							if (!deleteDir(new File(prj.m_rootPath)))
							{
								JOptionPane.showMessageDialog(null, "Cannot delete root folder \"" + prj.m_rootPath + "\". Please remove it manually.", "Error", JOptionPane.ERROR_MESSAGE);								
							}
							
							List list = session.createQuery("SELECT p FROM Project p INNER JOIN p.m_parents par WHERE par = :prj").setEntity("prj", prj).list();
							
							for (int i = 0; i < list.size(); i++)
							{
								Project p = (Project)list.get(i);
								p.m_parents.remove(prj);
								session.update(p);
							}
																					
							if (prj.getRoot() != null)
							{
								session.delete(prj.getRoot());
							}
							
							session.delete(prj);
							session.getTransaction().commit();
							
							session = Database.getProfileSessionFactory().getCurrentSession();
							session.beginTransaction();

							list = session.createQuery("SELECT a FROM Account a INNER JOIN a.m_projectIds pid WHERE pid = '" + id + "'").list();

							for (int i = 0; i < list.size(); i++)
							{
								Account a = (Account)list.get(i);
								a.m_projectIds.remove(id);
								session.update(a);
							}
							
							session.getTransaction().commit();
							
							refreshPrjList();
						}
					}
				}
			});
		}
		
		return m_prjDeleteButton;
	}

	/**
	 * This method initializes m_prjParentButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjParentButton()
	{
		if (m_prjParentButton == null)
		{
			m_prjParentButton = new JButton();
			m_prjParentButton.setBounds(new Rectangle(214, 160, 88, 23));
			m_prjParentButton.setFont(new Font("Dialog", Font.BOLD, 12));
			m_prjParentButton.setText("Parents...");
			m_prjParentButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_curDbFactory = m_curPrjDbFactory;
					fillNotSelectList();					
					refreshAssocPrjList();					
					getPrjAssocDialog("Parent projects").setVisible(true);
				}
			});
		}
		
		return m_prjParentButton;
	}

	
	protected void fillNotSelectList()
	{
		m_notSelPrjList.clear();
		
		String where = "";
		
		for (int i = 0; i < m_selectedPrjList.size(); i++)
		{
			if (i > 0)
				where += ",";
			
			where += "'" + m_selectedPrjList.get(i) + "'";
		}

		if (m_curPrjId != null)
		{
			if (where.length() > 0)
				where += ",";
			
			where += "'" + m_curPrjId + "'";
		}
		
		if (where.length() > 0)
			where = "WHERE m_id NOT IN (" + where + ")";
		
		Session session = m_curDbFactory.getCurrentSession();
		session.beginTransaction();					
		List list = session.createQuery("FROM Project " + where).list();
		session.getTransaction().commit();
		
		for (int i = 0; i < list.size(); i++)
		{
			Project p = (Project)list.get(i);
			m_notSelPrjList.add(p.m_id);
		}		
	}
	
	protected void refreshAssocPrjList()
	{
		fillAssocPrjList((DefaultListModel)getPrjSelectedList().getModel(), m_selectedPrjList);
		fillAssocPrjList((DefaultListModel)getPrjProjectsList().getModel(), m_notSelPrjList);
	}
	
	
	private void fillAssocPrjList(DefaultListModel model, List<String> prjList)
	{
		model.clear();
	
		if (prjList.size() == 0)
			return;
		
		String ids = "";
		
		for (int i = 0; i < prjList.size(); i++)
		{
			if (i > 0)
				ids += ",";
			
			ids += "'" + prjList.get(i) + "'";
		}
		
		Session session = m_curDbFactory.getCurrentSession();
		session.beginTransaction();
		
		List list = session.createQuery("FROM Project WHERE m_id IN (" + ids + ") ORDER BY m_name").list();
		
		prjList.clear();
		
		for (int i = 0; i < list.size(); i++)
		{
			Project p = (Project)list.get(i);
			model.addElement(p.m_name);
			prjList.add(p.m_id);
		}
		
		session.getTransaction().commit();
	}
	
	/**
	 * This method initializes m_prjParentsDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getPrjAssocDialog(String title)
	{
		if (m_prjAssocDialog == null)
		{
			m_prjAssocDialog = new JDialog(this);
			m_prjAssocDialog.setSize(new Dimension(500, 330));
			centerWindow(m_prjAssocDialog);
			m_prjAssocDialog.setResizable(false);
			m_prjAssocDialog.setTitle(title);
			m_prjAssocDialog.setModal(true);
			m_prjAssocDialog.setContentPane(getPrjAssocPane());
		}
		return m_prjAssocDialog;
	}

	/**
	 * This method initializes m_prjParentsPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPrjAssocPane()
	{
		if (m_prjAssocPane == null)
		{
			m_prjNotSelLabel = new JLabel();
			m_prjNotSelLabel.setBounds(new Rectangle(283, 4, 133, 16));
			m_prjNotSelLabel.setText("Not selected projects:");
			m_prjSelectedLabel = new JLabel();
			m_prjSelectedLabel.setBounds(new Rectangle(16, 4, 115, 16));
			m_prjSelectedLabel.setText("Selected projects:");
			m_prjAssocPane = new JPanel();
			m_prjAssocPane.setLayout(null);
			m_prjAssocPane.add(getPrjSelScrollPane(), null);
			m_prjAssocPane.add(getPrjAddAssocButton(), null);
			m_prjAssocPane.add(getPrjRemAssocButton(), null);
			m_prjAssocPane.add(getPrjNotSelScrollPane(), null);
			m_prjAssocPane.add(getPrjAssocCloseButton(), null);
			m_prjAssocPane.add(m_prjSelectedLabel, null);
			m_prjAssocPane.add(m_prjNotSelLabel, null);
		}
		return m_prjAssocPane;
	}

	/**
	 * This method initializes m_prjProjectsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getPrjProjectsList()
	{
		if (m_prjProjectsList == null)
		{
			m_prjProjectsList = new JList();
			m_prjProjectsList.setModel(new DefaultListModel());
			m_prjProjectsList.setLayoutOrientation(JList.VERTICAL);
			m_prjProjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return m_prjProjectsList;
	}

	/**
	 * This method initializes m_prjSelectedList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getPrjSelectedList()
	{
		if (m_prjSelectedList == null)
		{
			m_prjSelectedList = new JList();
			m_prjSelectedList.setModel(new DefaultListModel());
			m_prjSelectedList.setLayoutOrientation(JList.VERTICAL);
			m_prjSelectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return m_prjSelectedList;
	}

	/**
	 * This method initializes m_prjAddParentButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjAddAssocButton()
	{
		if (m_prjAddAssocButton == null)
		{
			m_prjAddAssocButton = new JButton();
			m_prjAddAssocButton.setBounds(new Rectangle(224, 84, 44, 23));
			m_prjAddAssocButton.setText("<");
			m_prjAddAssocButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int[] sel = m_prjProjectsList.getSelectedIndices();
					
					if (sel.length == 0)
					{
						JOptionPane.showMessageDialog(null, "No project record is selected from the right list.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						List<String> rem = new ArrayList<String>();
						
						for (int s : sel)
						{
							String id = m_notSelPrjList.get(s);
							m_selectedPrjList.add(id);
							rem.add(id);
						}
						
						m_notSelPrjList.removeAll(rem);
						
						refreshAssocPrjList();
					}
				}
			});
		}
		return m_prjAddAssocButton;
	}

	/**
	 * This method initializes m_prjRemParentButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjRemAssocButton()
	{
		if (m_prjRemAssocButton == null)
		{
			m_prjRemAssocButton = new JButton();
			m_prjRemAssocButton.setBounds(new Rectangle(224, 136, 44, 23));
			m_prjRemAssocButton.setText(">");
			m_prjRemAssocButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int[] sel = m_prjSelectedList.getSelectedIndices();
					
					if (sel.length == 0)
					{
						JOptionPane.showMessageDialog(null, "No project record is selected from the left list.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						List<String> rem = new ArrayList<String>();
						
						for (int s : sel)
						{
							String id = m_selectedPrjList.get(s);
							m_notSelPrjList.add(id);
							rem.add(id);
						}
						
						m_selectedPrjList.removeAll(rem);
						
						refreshAssocPrjList();
					}
				}
			});
		}
		return m_prjRemAssocButton;
	}

	/**
	 * This method initializes m_prjParentCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjAssocCloseButton()
	{
		if (m_prjAssocCloseButton == null)
		{
			m_prjAssocCloseButton = new JButton();
			m_prjAssocCloseButton.setBounds(new Rectangle(203, 264, 87, 23));
			m_prjAssocCloseButton.setText("Close");
			m_prjAssocCloseButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_prjAssocDialog.setVisible(false);
					m_prjAssocDialog = null;
				}
			});
		}
		return m_prjAssocCloseButton;
	}

	/**
	 * This method initializes m_prjSelScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPrjSelScrollPane()
	{
		if (m_prjSelScrollPane == null)
		{
			m_prjSelScrollPane = new JScrollPane();
			m_prjSelScrollPane.setBounds(new Rectangle(15, 21, 198, 230));
			m_prjSelScrollPane.setViewportView(getPrjSelectedList());
		}
		return m_prjSelScrollPane;
	}

	/**
	 * This method initializes m_prjNotSelScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPrjNotSelScrollPane()
	{
		if (m_prjNotSelScrollPane == null)
		{
			m_prjNotSelScrollPane = new JScrollPane();
			m_prjNotSelScrollPane.setBounds(new Rectangle(282, 21, 198, 230));
			m_prjNotSelScrollPane.setViewportView(getPrjProjectsList());
		}
		return m_prjNotSelScrollPane;
	}

	/**
	 * This method initializes m_accProjectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAccProjectButton()
	{
		if (m_accProjectButton == null)
		{
			m_accProjectButton = new JButton();
			m_accProjectButton.setBounds(new Rectangle(268, 342, 93, 23));
			m_accProjectButton.setText("Projects...");
			m_accProjectButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_curDbFactory = m_curAccDbFactory;
					m_curPrjId = null;
					fillNotSelectList();					
					refreshAssocPrjList();
					
					getPrjAssocDialog("Assigned projects").setVisible(true);
				}
			});
		}
		return m_accProjectButton;
	}

	/**
	 * This method initializes m_prjImportButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrjImportButton()
	{
		if (m_prjImportButton == null)
		{
			m_prjImportButton = new JButton();
			m_prjImportButton.setBounds(new Rectangle(460, 270, 97, 26));
			m_prjImportButton.setText("Import...");
			m_prjImportButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_prjTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No project record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						JFileChooser fc = new JFileChooser();
						fc.addChoosableFileFilter(new FileFilter()
						{
							private final static String zipExt = ".zip";							
							
							public boolean accept(File f)
							{
								if ((f.isFile() && f.getName().toLowerCase().endsWith(zipExt)) || f.isDirectory())
									return true;
								else
									return false;
							}
							
							public String getDescription()
							{
								return "Zip archive";
							}
						});
						
						int ret = fc.showOpenDialog(AdminUtil.this);
						
					    if (ret == JFileChooser.APPROVE_OPTION)
					    {
							String prjId = m_prjIdList.get(sel);
							String dbId = m_dbIdList.get(m_dbPrjComboBox.getSelectedIndex());
							
							Session session = Database.getProfileSessionFactory().getCurrentSession();							
							session.beginTransaction();
							Database db = (Database)session.createQuery("FROM Database WHERE m_id = '" + dbId + "'").uniqueResult();
							session.getTransaction().commit();
				
							JDialog progress = getProgressDialog();
							
							m_topProgBar.setValue(0);
							m_bottomProgBar.setValue(0);
							m_progDetailLabel.setText("");
							m_progElapsedLabel.setText("");
							m_procFileLabel.setText("");
							m_procStatLabel.setText("");
							
							Timer timer = new Timer(1000, new ActionListener()
							{
								protected long m_startTime = new Date().getTime();
								
								public void actionPerformed(ActionEvent e)
								{
									long diff = (new Date().getTime() - m_startTime) / 1000;
									long hrs = diff / 3600;
									long min = (diff / 60) % 60;
									long sec = diff % 60;
									
									m_progElapsedLabel.setText(String.format("Time elapsed: %1$02d:%2$02d:%3$02d", hrs, min, sec));									
								}
							});
							
							new ImportThread(db, prjId, fc.getSelectedFile().getAbsolutePath(), m_tempPath, timer).start();							
							
							progress.setVisible(true);
					    }
					}
				}
			});
		}
		return m_prjImportButton;
	}

	
	protected class ImportThread extends Thread
	{
		protected Database m_db;
		protected String m_prjID;
		protected String m_fileName;
		protected String m_tempPath;
		protected Timer m_timer;
		protected boolean m_cancelled;
			
		public ImportThread(Database db, String prjID, String fileName, String tempPath, Timer timer)
		{
			m_db = db;
			m_prjID = prjID;
			m_fileName = fileName;
			m_tempPath = tempPath;
			m_timer = timer;
			m_cancelled = false;
		}
		
		public void run()
		{
			try
			{			
				ImportManager.batchImport(m_db, m_prjID, m_fileName, m_tempPath, 
					new ImportManager.ProcessCallback()
					{
						public void reportStart() 
						{
							m_timer.start();
						}
						
						public void reportProgress(boolean parsing, int total, int current, String fileName)
						{
							if (parsing)
								m_progDetailLabel.setText("Parsing files...");
							else
								m_progDetailLabel.setText("Importing files...");
							
							m_procFileLabel.setText("Processing file:  " + fileName);
							m_procStatLabel.setText(String.format("Processed %1$d of %2$d files", current, total));
							
							m_topProgBar.setMaximum(total);
							m_topProgBar.setValue(current);
							
							m_bottomProgBar.setMaximum(2 * total);
							m_bottomProgBar.setValue(parsing ? current : (total + current));
						}
												
						public void reportError(Exception e)
						{
							m_timer.stop();
							
							JOptionPane.showMessageDialog(null, "Error during process: " + e.getMessage(), 
								"Error", JOptionPane.ERROR_MESSAGE);
						}
						
						public void reportSuccess()
						{
							m_timer.stop();
							
							if (m_cancelled)
								JOptionPane.showMessageDialog(null, "Import process was interrupted.", 
									"Process interrupted", JOptionPane.INFORMATION_MESSAGE);
							else
								JOptionPane.showMessageDialog(null, "Import process completed successfully.", 
									"Completion", JOptionPane.INFORMATION_MESSAGE);
						}
						
						public boolean isCancel()
						{
							if (m_cancelProcess)
							{
								m_cancelProcess = false;
								
								m_cancelled = (JOptionPane.showConfirmDialog(null, "Do you want to cancel import process?", 
									"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION);
								
								if (m_cancelled)
									m_timer.stop();
								
								return m_cancelled;
							}
							else
								return false;
						}						
					}
				);
			}
			catch (Exception e)
			{
			}
			finally
			{
				m_progressDialog.setVisible(false);
				m_progressDialog = null;
			}
		}
	}

	
	/**
	 * This method initializes m_progressDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getProgressDialog()
	{
		if (m_progressDialog == null)
		{
			m_progressDialog = new JDialog(this);
			m_progressDialog.setSize(new Dimension(561, 228));
			centerWindow(m_progressDialog);
			m_progressDialog.setModal(true);
			m_progressDialog.setResizable(false);
			m_progressDialog.setTitle("Import progress");
			m_progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			m_progressDialog.setContentPane(getProgressContentPane());
		}
		return m_progressDialog;
	}

	/**
	 * This method initializes m_progressContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getProgressContentPane()
	{
		if (m_progressContentPane == null)
		{
			m_procStatLabel = new JLabel();
			m_procStatLabel.setBounds(new Rectangle(20, 76, 240, 19));
			m_procStatLabel.setText("");
			m_progElapsedLabel = new JLabel();
			m_progElapsedLabel.setBounds(new Rectangle(348, 8, 187, 19));
			m_progElapsedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			m_progElapsedLabel.setText("");
			m_procFileLabel = new JLabel();
			m_procFileLabel.setBounds(new Rectangle(20, 56, 515, 19));
			m_procFileLabel.setHorizontalAlignment(SwingConstants.LEFT);
			m_procFileLabel.setText("");
			m_progDetailLabel = new JLabel();
			m_progDetailLabel.setBounds(new Rectangle(20, 8, 227, 19));
			m_progDetailLabel.setHorizontalAlignment(SwingConstants.LEFT);
			m_progDetailLabel.setText("");
			m_progOverallLabel = new JLabel();
			m_progOverallLabel.setBounds(new Rectangle(20, 108, 107, 19));
			m_progOverallLabel.setText("Overall progress:");
			m_progressContentPane = new JPanel();
			m_progressContentPane.setLayout(null);
			m_progressContentPane.add(getTopProgBar(), null);
			m_progressContentPane.add(getBottomProgBar(), null);
			m_progressContentPane.add(getProgCancelButton(), null);
			m_progressContentPane.add(m_progOverallLabel, null);
			m_progressContentPane.add(m_progDetailLabel, null);
			m_progressContentPane.add(m_procFileLabel, null);
			m_progressContentPane.add(m_progElapsedLabel, null);
			m_progressContentPane.add(m_procStatLabel, null);
		}
		return m_progressContentPane;
	}

	/**
	 * This method initializes m_topProgBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getTopProgBar()
	{
		if (m_topProgBar == null)
		{
			m_topProgBar = new JProgressBar();
			m_topProgBar.setBounds(new Rectangle(20, 28, 515, 25));
			m_topProgBar.setStringPainted(true);
		}
		return m_topProgBar;
	}

	/**
	 * This method initializes m_bottomProgBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getBottomProgBar()
	{
		if (m_bottomProgBar == null)
		{
			m_bottomProgBar = new JProgressBar();
			m_bottomProgBar.setBounds(new Rectangle(20, 127, 515, 25));
			m_bottomProgBar.setStringPainted(true);
		}
		return m_bottomProgBar;
	}

	/**
	 * This method initializes m_progCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getProgCancelButton()
	{
		if (m_progCancelButton == null)
		{
			m_progCancelButton = new JButton();
			m_progCancelButton.setBounds(new Rectangle(230, 165, 87, 23));
			m_progCancelButton.setText("Cancel");
			m_progCancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_cancelProcess = true;
				}
			});
		}
		return m_progCancelButton;
	}

	/**
	 * This method initializes m_limitPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLimitPanel() 
	{
		if (m_limitPanel == null) 
		{
			m_limitPanel = new JPanel();
			m_limitPanel.setLayout(null);
			m_limitPanel.add(getLimitPane(), null);
			m_limitPanel.add(getLimitAddButton(), null);
			m_limitPanel.add(getLimitEditButton(), null);
			m_limitPanel.add(getLimitDeleteButton(), null);
		}
		
		return m_limitPanel;
	}

	/**
	 * This method initializes m_limitTable	
	 * 	
	 * @return javax.swing.JTable	
	 */

	/**
	 * This method initializes m_limitScrlPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getLimitPane() 
	{
		if (m_limitScrlPane == null) 
		{
			m_limitScrlPane = new JScrollPane();
			m_limitScrlPane.setSize(new Dimension(400, 139));
			m_limitScrlPane.setLocation(new Point(15, 17));
			m_limitScrlPane.setViewportView(getLimitTable());
		}
		
		return m_limitScrlPane;
	}

	/**
	 * This method initializes m_limitTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getLimitTable() 
	{
		if (m_limitTable == null) 
		{
			Vector<String> headers = new Vector<String>();
			headers.add("Limit name");
			headers.add("Value");
			
			DefaultTableModel defaultTableModel = new DefaultTableModel()
			{
			  private static final long serialVersionUID = 1L;
			
			  public boolean isCellEditable(int rowIndex, int vColIndex)
			  {
			    return false;
			  }
			};
			
			defaultTableModel.setColumnCount(2);
			defaultTableModel.setNumRows(0);
			defaultTableModel.setColumnIdentifiers(headers);
			
			m_limitTable = new JTable();
			m_limitTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			m_limitTable.setRowSelectionAllowed(true);
			m_limitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			m_limitTable.setBounds(new Rectangle(0, 0, 432, 80));
			m_limitTable.setModel(defaultTableModel);
		}
		
		return m_limitTable;
	}

	/**
	 * This method initializes m_limitAddButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLimitAddButton() 
	{
		if (m_limitAddButton == null) 
		{
			m_limitAddButton = new JButton();
			m_limitAddButton.setText("Add...");
			m_limitAddButton.setLocation(new Point(459, 17));
			m_limitAddButton.setSize(new Dimension(97, 26));
			
			m_limitAddButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					JDialog dialog = getLimitDialog();
					
					m_curLimitId = null;
					
					m_limitTypeComboBox.removeAllItems();
					
					for (String name : m_limitNames)
					{
						m_limitTypeComboBox.addItem(name);
					}
					
					m_limitValueField.setValue(0);
					m_limitTypeComboBox.setSelectedIndex(0);
					
					dialog.setVisible(true);
				}
			});			
			
		}
		return m_limitAddButton;
	}

	/**
	 * This method initializes m_limitEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLimitEditButton() 
	{
		if (m_limitEditButton == null) 
		{
			m_limitEditButton = new JButton();
			m_limitEditButton.setText("Edit...");
			m_limitEditButton.setLocation(new Point(459, 72));
			m_limitEditButton.setSize(new Dimension(97, 26));
			
			m_limitEditButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_limitTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No limit record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						m_curLimitId = m_limitIdList.get(sel);
						Limit limit = null;

						try
						{
							Session session = Database.getProfileSessionFactory().getCurrentSession();
							session.beginTransaction();
							
							limit = (Limit)session.createQuery("FROM Limit WHERE m_id = '" + m_curLimitId + "'").uniqueResult();					
							
							session.getTransaction().commit();
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(null, "Cannot edit the limit.", "Error", JOptionPane.ERROR_MESSAGE);				
							return;
						}

						JDialog dialog = getLimitDialog();
												
						m_limitValueField.setValue(limit.getValue());
						
						m_limitTypeComboBox.removeAllItems();
						
						for (String name : m_limitNames)
						{
							m_limitTypeComboBox.addItem(name);
						}
						
						m_limitTypeComboBox.setSelectedIndex(limit.getType().value());
						
						dialog.setVisible(true);
					}
				}
			});
			
		}
		return m_limitEditButton;
	}

	/**
	 * This method initializes m_limitDeleteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLimitDeleteButton() 
	{
		if (m_limitDeleteButton == null) 
		{
			m_limitDeleteButton = new JButton();
			m_limitDeleteButton.setText("Delete");
			m_limitDeleteButton.setLocation(new Point(459, 127));
			m_limitDeleteButton.setSize(new Dimension(97, 26));
			
			m_limitDeleteButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					int sel = m_limitTable.getSelectedRow();
					
					if (sel == -1)
					{
						JOptionPane.showMessageDialog(null, "No limit record is selected.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if (JOptionPane.showConfirmDialog(null, "Do you want to delete selected limit?", 
								"Confirmation",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
						{
							String id = m_limitIdList.get(sel);
							
							Session session = Database.getProfileSessionFactory().getCurrentSession();
							
							session = Database.getProfileSessionFactory().getCurrentSession();
							session.beginTransaction();
							Limit limit = (Limit)session.createQuery("FROM Limit WHERE m_id = '" + id + "'").uniqueResult();
							session.delete(limit);
							session.getTransaction().commit();
							refreshLimitList();
						}
					}
				}
			});
			
		}
		return m_limitDeleteButton;
	}

	/**
	 * This method initializes m_limitDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getLimitDialog() 
	{
		if (m_limitDialog == null) 
		{
			m_limitDialog = new JDialog(this);
			m_limitDialog.setSize(new Dimension(268, 206));
			centerWindow(m_limitDialog);
			m_limitDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			m_limitDialog.setResizable(false);
			m_limitDialog.setTitle("Limit");
			m_limitDialog.setModal(true);
			m_limitDialog.setContentPane(getLimitDialogPane());
		}
		return m_limitDialog;
	}

	/**
	 * This method initializes m_limitPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLimitDialogPane() 
	{
		if (m_limitDialogPane == null) 
		{
			m_limitValueLabel = new JLabel();
			m_limitValueLabel.setBounds(new Rectangle(33, 84, 73, 16));
			m_limitValueLabel.setText("Limit value:");
			m_limitTypeLabel = new JLabel();
			m_limitTypeLabel.setBounds(new Rectangle(32, 13, 75, 16));
			m_limitTypeLabel.setText("Type of limit:");
			m_limitDialogPane = new JPanel();
			m_limitDialogPane.setLayout(null);
			m_limitDialogPane.add(m_limitTypeLabel, null);
			m_limitDialogPane.add(getLimitTypeComboBox(), null);
			m_limitDialogPane.add(m_limitValueLabel, null);
			m_limitDialogPane.add(getLimitValueField(), null);
			m_limitDialogPane.add(getLimitCancelButton(), null);
			m_limitDialogPane.add(getLimitSaveButton(), null);
		}
		return m_limitDialogPane;
	}

	/**
	 * This method initializes m_limitSaveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLimitSaveButton() 
	{
		if (m_limitSaveButton == null) 
		{
			m_limitSaveButton = new JButton();
			m_limitSaveButton.setText("Save");
			m_limitSaveButton.setBounds(new Rectangle(30, 137, 87, 23));
			m_limitSaveButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (m_limitValueField.getText().trim().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please enter limit value.", 
								"InputError", JOptionPane.ERROR_MESSAGE);
						return;
					}
										
					Session session = Database.getProfileSessionFactory().getCurrentSession();
					
					session.beginTransaction();

					Limit limit = new Limit();			
					
					if (m_curLimitId == null)
					{
						Limit l = (Limit)session.createQuery("FROM Limit WHERE m_type = " + m_limitTypeComboBox.getSelectedIndex()).uniqueResult();
						
						if (l != null)
						{
							if (JOptionPane.showConfirmDialog(null, "Limit value for the specified type already exists. " +
									"Do you want to overwrite it?", "Overwrite confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							{
								l.setValue(((Number)m_limitValueField.getValue()).longValue());
								session.saveOrUpdate(l);
								session.getTransaction().commit();
								
								refreshLimitList();

								m_limitDialog.setVisible(false);
								m_limitDialog = null;							
							}
							else
							{
								session.getTransaction().commit();
								
								return;								
							}
						}
					}
					
					limit.m_id = m_curLimitId;
					limit.setType(Limit.Type.values()[m_limitTypeComboBox.getSelectedIndex()]);
					limit.setValue(((Number)m_limitValueField.getValue()).longValue());
					
					session.saveOrUpdate(limit);
					
					session.getTransaction().commit();
					
					m_limitDialog.setVisible(false);
					m_limitDialog = null;
					
					refreshLimitList();
				}
			});					
		}
		return m_limitSaveButton;
	}

	/**
	 * This method initializes m_limitCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLimitCancelButton() 
	{
		if (m_limitCancelButton == null) 
		{
			m_limitCancelButton = new JButton();
			m_limitCancelButton.setBounds(new Rectangle(144, 137, 87, 23));
			m_limitCancelButton.setText("Cancel");
			m_limitCancelButton.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					m_limitDialog.setVisible(false);
					m_limitDialog = null;
				}
			});			
		}
		return m_limitCancelButton;
	}

	/**
	 * This method initializes m_limitTypeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getLimitTypeComboBox() 
	{
		if (m_limitTypeComboBox == null) 
		{
			m_limitTypeComboBox = new JComboBox();
			m_limitTypeComboBox.setBounds(new Rectangle(32, 31, 197, 22));
		}
		return m_limitTypeComboBox;
	}

	/**
	 * This method initializes m_limitValueField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getLimitValueField() 
	{
		if (m_limitValueField == null) 
		{
			NumberFormat format = NumberFormat.getIntegerInstance();
			format.setMaximumIntegerDigits(Long.toString(Long.MAX_VALUE).length() - 1);
			m_limitValueField = new JFormattedTextField(format);
			m_limitValueField.setBounds(new Rectangle(111, 79, 118, 21));
		}
		return m_limitValueField;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				AdminUtil thisClass = new AdminUtil();
				// This call is to diminish bug causing artifacts of incorrect screen area redrawing
				// under moving mouse cursor.
				javax.swing.RepaintManager.currentManager(thisClass).setDoubleBufferingEnabled(false);
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public AdminUtil()
	{
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(614, 398);
		this.setName("mainFrame");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getDbTopLvlPanel());
		this.setResizable(false);
		this.setTitle("Profile administration");

		centerWindow(this);
		refreshDbList();
		refreshLimitList();
	}
	
	protected void refreshDbList()
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		List list = session.createQuery("FROM Database ORDER BY m_serverUrl, m_databaseName").list();		
		session.getTransaction().commit();
		
		((DefaultTableModel) m_dbTable.getModel()).setRowCount(list.size());
		m_dbAccComboBox.removeAllItems();
		m_dbPrjComboBox.removeAllItems();
		m_dbIdList = new ArrayList<String>(list.size());
		
		for (int i = 0; i < list.size(); i++)
		{
			Database db = (Database)list.get(i);

			m_dbIdList.add(i, db.m_id);
			m_dbTable.setValueAt(db.getServerUrl(), i, 0);
			m_dbTable.setValueAt(db.getDatabaseName(), i, 1);
			m_dbAccComboBox.addItem(db.getServerUrl() + "/" + db.getDatabaseName());
			m_dbPrjComboBox.addItem(db.getServerUrl() + "/" + db.getDatabaseName());
		}
	}

	protected void refreshLimitList()
	{
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		List list = session.createQuery("FROM Limit ORDER BY m_type").list();		
		session.getTransaction().commit();
		
		((DefaultTableModel) m_limitTable.getModel()).setRowCount(list.size());
		m_limitIdList = new ArrayList<String>(list.size());
		
		for (int i = 0; i < list.size(); i++)
		{
			Limit limit = (Limit)list.get(i);

			m_limitIdList.add(i, limit.m_id);
			m_limitTable.setValueAt(m_limitNames[limit.getType().value()], i, 0);
			m_limitTable.setValueAt(NumberFormat.getIntegerInstance().format(limit.getValue()), i, 1);
		}
	}

	
	
	protected void refreshAccList()
	{
		int sel = m_dbAccComboBox.getSelectedIndex();
		
		if (sel == -1)
		{
			if (m_dbAccComboBox.getItemCount() > 0)
				m_dbAccComboBox.setSelectedIndex(0);
			
			return;
		}		
		
		Session session = Database.getProfileSessionFactory().getCurrentSession();
		String dbId = m_dbIdList.get(sel);
		
		session.beginTransaction();
		List list = session.createQuery("SELECT a FROM Account a JOIN a.m_database AS db WHERE db.m_id = '" + 
				dbId + "' ORDER BY a.m_ownerName, a.m_status").list();
		session.getTransaction().commit();
		
		((DefaultTableModel) m_accTable.getModel()).setRowCount(list.size());
		m_accIdList = new ArrayList<String>(list.size());
		
		for (int i = 0; i < list.size(); i++)
		{
			Account acc = (Account)list.get(i);

			m_accIdList.add(i, acc.m_id);
			m_accTable.setValueAt(acc.m_ownerName, i, 0);
			m_accTable.setValueAt(acc.getStatus().toString(), i, 1);
		}		
	}

	
	protected void refreshPrjList()
	{
		List list = null;
		
		try
		{
			Session session = m_curPrjDbFactory.getCurrentSession();
			session.beginTransaction();
			list = session.createQuery("FROM Project ORDER BY m_name").list();

			session.getTransaction().commit();
			m_dbValid = true;
		}
		catch(Exception ex)
		{
			m_dbValid = false;
			((DefaultTableModel)m_prjTable.getModel()).setRowCount(0);
			return;
		}
		
		m_prjAddButton.setEnabled(true);

		((DefaultTableModel) m_prjTable.getModel()).setRowCount(list.size());
		m_prjIdList = new ArrayList<String>(list.size());
		
		for (int i = 0; i < list.size(); i++)
		{
			Project prj = (Project)list.get(i);
			
			m_prjIdList.add(i, prj.m_id);
			m_prjTable.setValueAt(prj.m_name, i, 0);
			
			String lang = prj.getLanguage().toString();
			lang = lang.replace("_", " ");
			lang = (new StringBuffer(lang).insert(lang.length() - 1, ".")).toString();
			
			m_prjTable.setValueAt(lang, i, 1);
		}				
	}

	protected boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();

			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				
				if (!success)
				{
					return false;
				}
			}
		}
	    
		return dir.delete();
	}
		
	protected void centerWindow(Window window)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = window.getSize();
		int x;
		int y;

		x = screenSize.width / 2 - size.width / 2;
		y = screenSize.height / 2 - size.height / 2;

		window.setLocation(x, y);
	}

}  //  @jve:decl-index=0:visual-constraint="3,-15"
