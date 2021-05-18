package com.lzw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.lzw.dao.Dao;
import com.lzw.frame.TelFrame;
import com.lzw.system.Resource;
import com.lzw.userList.ChatTree;
import com.lzw.userList.User;

public class EQ extends Dialog {
	private JTextField ipEndTField;
	private JTextField ipStartTField;
	private JTextField userNameTField;
	private JPasswordField passwordTField;
	private JTextField placardPathTField;
	private JTextField updatePathTField;
	private JTextField pubPathTField;
	public static EQ frame = null;
	private ChatTree chatTree;
	private JPopupMenu popupMenu;
	private JTabbedPane tabbedPane;
	private JToggleButton searchUserButton;
	private JProgressBar progressBar;
	private JList faceList;
	private JButton selectInterfaceOKButton;
	private DatagramSocket ss;
	private final JLabel stateLabel;
	private static String user_dir;
	private static File localFile;
	private static File netFile;
	private String netFilePath;
	private JButton messageAlertButton;
	private Stack<String> messageStack;
	private ImageIcon messageAlertIcon;
	private ImageIcon messageAlertNullIcon;
	private Rectangle location;
	public static TrayIcon trayicon;
	private Dao dao;
	public final static Preferences preferences = Preferences.systemRoot();;
	private JButton userInfoButton;
	public static void main(String args[]) {
		try {
			String laf = preferences.get("lookAndFeel", "javaĬ��");
			if (laf.indexOf("��ǰϵͳ")>-1)
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			EQ frame = new EQ();
			frame.setVisible(true);
			frame.SystemTrayInitial();// ��ʼ��ϵͳ��
			frame.server();
			frame.checkPlacard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public EQ() {
		super(new Frame());
		frame = this;
		dao = Dao.getDao();
		location = dao.getLocation();
		setTitle("EQͨѶ");
		setBounds(location);
		progressBar = new JProgressBar();
		progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		tabbedPane = new JTabbedPane();
		popupMenu = new JPopupMenu();
		chatTree = new ChatTree(this);
		user_dir = System.getProperty("user.dir"); // ����ִ��·������ϵͳ����
		localFile = new File(user_dir + File.separator + "EQ.jar");// ����EQ�ļ�
		stateLabel = new JLabel(); // ״̬����ǩ
		addWindowListener(new FrameWindowListener());// ��Ӵ��������
		addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				saveLocation();
			}
			public void componentMoved(final ComponentEvent e) {
				saveLocation();
			}
		});
		try {// ����ͨѶ����˿�
			ss = new DatagramSocket(1111);
		} catch (SocketException e2) {
			if (e2.getMessage().startsWith("Address already in use"))
				showMessageDialog("����˿ڱ�ռ��,���߱�����Ѿ����С�");
			System.exit(0);
		}
		{ // ��ʼ��������Ϣ��ť
			messageAlertIcon = new ImageIcon(EQ.class
					.getResource("/image/messageAlert.gif"));
			messageAlertNullIcon = new ImageIcon(EQ.class
					.getResource("/image/messageAlertNull20.gif"));
			messageStack = new Stack<String>();
			messageAlertButton = new JButton();
			messageAlertButton.setHorizontalAlignment(SwingConstants.RIGHT);
			messageAlertButton.setContentAreaFilled(false);
			final JPanel BannerPanel = new JPanel();
			BannerPanel.setLayout(new BorderLayout());
			add(BannerPanel, BorderLayout.NORTH);
			userInfoButton = new JButton();
			BannerPanel.add(userInfoButton, BorderLayout.WEST);
			userInfoButton.setMargin(new Insets(0, 0, 0, 10));
			initUserInfoButton();// ��ʼ�������û�ͷ��ť
			BannerPanel.add(messageAlertButton, BorderLayout.CENTER);
			messageAlertButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (!messageStack.empty()) {
						showMessageDialog(messageStack.pop());
					}
				}
			});
			messageAlertButton.setIcon(messageAlertIcon);
			showMessageBar();
		}
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setTabPlacement(SwingConstants.LEFT);
		ImageIcon userTicon = new ImageIcon(EQ.class
				.getResource("/image/tabIcon/tabLeft.PNG"));
		tabbedPane.addTab(null, userTicon, createUserList(), "�û��б�");
		ImageIcon sysOTicon = new ImageIcon(EQ.class
				.getResource("/image/tabIcon/tabLeft2.PNG"));
		tabbedPane.addTab(null, sysOTicon, createSysToolPanel(), "ϵͳ����");
		ImageIcon sysSTicon = new ImageIcon(EQ.class
				.getResource("/image/tabIcon/tabLeft3.png"));
		tabbedPane.addTab(null, sysSTicon, createSysSetPanel(), "ϵͳ����");
		setAlwaysOnTop(true);
	}

	private JScrollPane createSysSetPanel() {
		final JPanel sysSetPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(sysSetPanel);
		sysSetPanel.setLayout(new BoxLayout(sysSetPanel, BoxLayout.Y_AXIS));
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		final JPanel sysPathPanel = new JPanel();
		sysPathPanel.setMaximumSize(new Dimension(600, 200));
		sysPathPanel.setBorder(new TitledBorder("ϵͳ·��"));
		sysPathPanel.setLayout(new GridLayout(0, 1));
		sysSetPanel.add(sysPathPanel);
		sysPathPanel.add(new JLabel("��������·����"));
		updatePathTField = new JTextField(preferences
				.get("updatePath", "������·��"));
		sysPathPanel.add(updatePathTField);
		sysPathPanel.add(new JLabel("ϵͳ����·����"));
		placardPathTField = new JTextField(preferences.get("placardPath",
				"������·��"));
		sysPathPanel.add(placardPathTField);
		sysPathPanel.add(new JLabel("��������·����"));
		pubPathTField = new JTextField(preferences.get("pubPath", "������·��"));
		sysPathPanel.add(pubPathTField);
		final JButton pathOKButton = new JButton("ȷ��");
		pathOKButton.setActionCommand("sysOK");
		pathOKButton.addActionListener(new SysSetPanelOKListener());
		sysSetPanel.add(pathOKButton);
		final JPanel loginPanel = new JPanel();
		loginPanel.setMaximumSize(new Dimension(600, 90));
		loginPanel.setBorder(new TitledBorder("��¼����������"));
		final GridLayout gridLayout_1 = new GridLayout(0, 1);
		gridLayout_1.setVgap(5);
		loginPanel.setLayout(gridLayout_1);
		sysSetPanel.add(loginPanel);
		final JPanel panel_7 = new JPanel();
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.X_AXIS));
		loginPanel.add(panel_7);
		panel_7.add(new JLabel("�û�����"));
		userNameTField = new JTextField(preferences.get("username", "�������û���"));
		panel_7.add(userNameTField);
		final JPanel panel_8 = new JPanel();
		panel_8.setLayout(new BoxLayout(panel_8, BoxLayout.X_AXIS));
		loginPanel.add(panel_8);
		panel_8.add(new JLabel("�ܡ��룺"));
		passwordTField = new JPasswordField("*****");
		panel_8.add(passwordTField);
		final JButton loginOKButton = new JButton("ȷ��");
		sysSetPanel.add(loginOKButton);
		loginOKButton.setActionCommand("loginOK");
		loginOKButton.addActionListener(new SysSetPanelOKListener());
		final JPanel ipPanel = new JPanel();
		final GridLayout gridLayout_2 = new GridLayout(0, 1);
		gridLayout_2.setVgap(5);
		ipPanel.setLayout(gridLayout_2);
		ipPanel.setMaximumSize(new Dimension(600, 90));
		ipPanel.setBorder(new TitledBorder("IP������Χ"));
		sysSetPanel.add(ipPanel);
		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		ipPanel.add(panel_5);
		panel_5.add(new JLabel("��ʼIP��"));
		ipStartTField = new JTextField(preferences
				.get("ipStart", "192.168.0.1"));
		panel_5.add(ipStartTField);
		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		ipPanel.add(panel_6);
		panel_6.add(new JLabel("��ֹIP��"));
		ipEndTField = new JTextField(preferences.get("ipEnd", "192.168.1.255"));
		panel_6.add(ipEndTField);
		final JButton ipOKButton = new JButton("ȷ��");
		ipOKButton.setActionCommand("ipOK");
		ipOKButton.addActionListener(new SysSetPanelOKListener());
		sysSetPanel.add(ipOKButton);
		return scrollPane;
	}

	private JScrollPane createUserList() {// �û��б����
		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		addUserPopup(chatTree, getPopupMenu());// Ϊ�û���ӵ����˵�
		scrollPane.setViewportView(chatTree);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		chatTree.addMouseListener(new ChatTreeMouseListener());
		return scrollPane;
	}

	private JScrollPane createSysToolPanel() {// ϵͳ�������
		JPanel sysToolPanel = new JPanel(); // ϵͳ�������
		sysToolPanel.setLayout(new BorderLayout());
		JScrollPane sysToolScrollPanel = new JScrollPane();
		sysToolScrollPanel
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sysToolScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		sysToolScrollPanel.setViewportView(sysToolPanel);
		sysToolPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		JPanel interfacePanel = new JPanel();
		sysToolPanel.add(interfacePanel, BorderLayout.NORTH);
		interfacePanel.setLayout(new BorderLayout());
		interfacePanel.setBorder(new TitledBorder("����ѡ��-�ٴ�������Ч"));
		faceList = new JList(new String[]{"��ǰϵͳ", "javaĬ��"});
		interfacePanel.add(faceList);
		faceList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		final JPanel interfaceSubPanel = new JPanel();
		interfaceSubPanel.setLayout(new FlowLayout());
		interfacePanel.add(interfaceSubPanel, BorderLayout.SOUTH);
		selectInterfaceOKButton = new JButton("ȷ��");
		selectInterfaceOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preferences.put("lookAndFeel", faceList.getSelectedValue()
						.toString());
				JOptionPane.showMessageDialog(EQ.this, "�������б��������Ч");
			}
		});
		interfaceSubPanel.add(selectInterfaceOKButton);

		JPanel searchUserPanel = new JPanel(); // �û��������
		sysToolPanel.add(searchUserPanel);
		searchUserPanel.setLayout(new BorderLayout());
		final JPanel searchControlPanel = new JPanel();
		searchControlPanel.setLayout(new GridLayout(0, 1));
		searchUserPanel.add(searchControlPanel, BorderLayout.SOUTH);
		final JList searchUserList = new JList(new String[]{"����û��б�"});// ������û��б�
		final JScrollPane scrollPane_2 = new JScrollPane(searchUserList);
		scrollPane_2.setDoubleBuffered(true);
		searchUserPanel.add(scrollPane_2);
		searchUserList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		searchUserButton = new JToggleButton();
		searchUserButton.setText("�������û�");
		searchUserButton.addActionListener(new SearchUserActionListener(searchUserList));
		searchControlPanel.add(progressBar);
		searchControlPanel.add(searchUserButton);
		searchUserPanel.setBorder(new TitledBorder("�����û�"));

		final JPanel sysUpdatePanel = new JPanel();
		sysUpdatePanel.setOpaque(false);
		sysUpdatePanel.setLayout(new GridBagLayout());
		sysUpdatePanel.setBorder(new TitledBorder("ϵͳ����"));
		sysToolPanel.add(sysUpdatePanel, BorderLayout.SOUTH);
		final JButton sysUpdateButton = new JButton("ϵͳ����");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		sysUpdatePanel.add(sysUpdateButton, gridBagConstraints_1);
		sysUpdateButton.addActionListener(new SysUpdateListener());// ���ϵͳ�����¼�
		final JLabel updateLabel = new JLabel("������£�");
		final GridBagConstraints updateLabelLayout = new GridBagConstraints();
		updateLabelLayout.gridy = 1;
		updateLabelLayout.gridx = 0;
		sysUpdatePanel.add(updateLabel, updateLabelLayout);
		final JLabel updateDateLabel = new JLabel();// ����������ڱ�ǩ
		Date date = new Date(localFile.lastModified());
		String dateStr = String.format("%tF %<tr", date);
		updateDateLabel.setText(dateStr);
		final GridBagConstraints updateDateLayout = new GridBagConstraints();
		updateDateLayout.gridy = 2;
		updateDateLayout.gridx = 0;
		sysUpdatePanel.add(updateDateLabel, updateDateLayout);
		final JLabel updateStaticLabel = new JLabel("����״̬��");
		final GridBagConstraints updateStaticLayout = new GridBagConstraints();
		updateStaticLayout.gridy = 3;
		updateStaticLayout.gridx = 0;
		sysUpdatePanel.add(updateStaticLabel, updateStaticLayout);
		final JLabel updateInfoLabel = new JLabel();// �汾��Ϣ��ǩ
		checkSysInfo(updateInfoLabel);// ���ü��汾���µķ���
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 4;
		gridBagConstraints_5.gridx = 0;
		sysUpdatePanel.add(updateInfoLabel, gridBagConstraints_5);
		JPanel statePanel = new JPanel();
		add(statePanel, BorderLayout.SOUTH);
		statePanel.setLayout(new BorderLayout());
		statePanel.add(stateLabel);
		stateLabel.setText("��������" + chatTree.getRowCount());
		return sysToolScrollPanel;
	}

	private void initUserInfoButton() {// ��ʼ���û���Ϣ��ť
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			User user = dao.getUser(ip);
			userInfoButton.setIcon(user.getIconImg());
			userInfoButton.setText(user.getName());
			userInfoButton.setIconTextGap(JLabel.RIGHT);
			userInfoButton.setToolTipText(user.getTipText());
			userInfoButton.getParent().doLayout();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}

	private void showMessageBar() { // ��ʾ������Ϣ��ť���߳�
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (!messageStack.empty()) {
						try {
							messageAlertButton.setIcon(messageAlertNullIcon);
							messageAlertButton.setPreferredSize(new Dimension(
									20, 20));
							Thread.sleep(500);
							messageAlertButton.setIcon(messageAlertIcon);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	private void checkSysInfo(final JLabel updateInfo) {// ���汾����
		new Thread(new Runnable() {
			public void run() {
				String info = "";
				while (true) {
					try {
						netFilePath = preferences.get("updatePath", "EQ.jar");
						if (netFilePath.equals("EQ.jar")) {
							info = "<html><center><font color=red><b>�޷���¼</b><br>δ��������·��</font></center></html>";
							updateInfo.setText(info);
							continue;
						}
						netFile = new File(netFilePath);
						if (netFile.exists()) {
							Date netDate = new Date(netFile.lastModified());
							if (!localFile.exists())
								info = "<html><font color=blue>���س���λ�ó���</font></html>";
							else {
								Date localDate = new Date(localFile
										.lastModified());
								if (netDate.after(localDate)) {
									info = "<html><font color=blue>�����������³���</font></html>";
									pushMessage(info);
								} else
									info = "<html><font color=green>���������³���</font></html>";
							}
						} else {
							info = "<html><center><font color=red><b>�޷�����</b><br>����·��</font></center></html>";
						}
						updateInfo.setText(info);
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	class SearchUserActionListener implements ActionListener {
		private final JList list;
		SearchUserActionListener(JList list) {
			this.list = list;
		}
		public void actionPerformed(ActionEvent e) {
			if (searchUserButton.isSelected()) {
				searchUserButton.setText("ֹͣ����");
				new Thread(new Runnable() {
					public void run() {
						Resource.searchUsers(chatTree, progressBar,
								list, searchUserButton);
					}
				}).start();
			} else
				searchUserButton.setText("�������û�");
		}
	}

	class SysSetPanelOKListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("sysOK")) {
				String updatePath = updatePathTField.getText();
				String placardPath = placardPathTField.getText();
				String pubPath = pubPathTField.getText();
				preferences.put("updatePath", updatePath); // ����ϵͳ����·��
				preferences.put("placardPath", placardPath);// ����ϵͳ����·��
				preferences.put("pubPath", pubPath); // ���ù�������·��
				JOptionPane.showMessageDialog(EQ.this, "ϵͳ���ñ������");
			}
			if (command.equals("loginOK")) {
				String username = userNameTField.getText();
				String password = new String(passwordTField.getPassword());
				preferences.put("username", username); // ����ϵͳ����·��
				preferences.put("password", password);// ����ϵͳ����·��
				JOptionPane.showMessageDialog(EQ.this, "��¼���ñ������");
			}
			if (command.equals("ipOK")) {
				String ipStart = ipStartTField.getText();
				String ipEnd = ipEndTField.getText();
				try {
					InetAddress.getByName(ipStart);
					InetAddress.getByName(ipEnd);
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(EQ.this, "IP��ַ��ʽ����");
					return;
				}
				preferences.put("ipStart", ipStart); // ����ϵͳ����·��
				preferences.put("ipEnd", ipEnd);// ����ϵͳ����·��
				JOptionPane.showMessageDialog(EQ.this, "IP���ñ������");
			}
		}
	}

	private final class SysUpdateListener implements ActionListener {// ϵͳ�����¼�
		public void actionPerformed(final ActionEvent e) {
			String username = preferences.get("username", null);
			String password = preferences.get("password", null);
			if (username == null || password == null) {
				pushMessage("δ���õ�¼�������������û���������");
				return;
			}
			Resource.loginPublic(username, password);
			updateProject();
		}
	}

	private class ChatTreeMouseListener extends MouseAdapter { // �û��б�ļ�����
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				TreePath path = chatTree.getSelectionPath();
				if (path == null)
					return;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				User user = (User) node.getUserObject();
				try {
					TelFrame.getInstance(ss, new DatagramPacket(new byte[0], 0,
							InetAddress.getByName(user.getIp()), 1111),
							chatTree);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void server() {// ��������������
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (ss != null) {
						byte[] buf = new byte[4096];
						DatagramPacket dp = new DatagramPacket(buf, buf.length);
						try {
							ss.receive(dp);
						} catch (IOException e) {
							e.printStackTrace();
						}
						TelFrame.getInstance(ss, dp, chatTree);
					}
				}
			}
		}).start();
	}

	private void addUserPopup(Component component, final JPopupMenu popup) {// ����û������˵�
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				if (chatTree.getSelectionPaths() == null) {
					popupMenu.getComponent(0).setEnabled(false);
					popupMenu.getComponent(2).setEnabled(false);
					popupMenu.getComponent(3).setEnabled(false);
					popupMenu.getComponent(4).setEnabled(false);
					popupMenu.getComponent(5).setEnabled(false);
				} else {
					if (chatTree.getSelectionPaths().length < 2) {
						popupMenu.getComponent(3).setEnabled(false);
					} else {
						popupMenu.getComponent(3).setEnabled(true);
					}
					popupMenu.getComponent(0).setEnabled(true);
					popupMenu.getComponent(2).setEnabled(true);
					popupMenu.getComponent(4).setEnabled(true);
					popupMenu.getComponent(5).setEnabled(true);
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	private void saveLocation() { // ����������λ�õķ���
		location = getBounds();
		dao.updateLocation(location);
	}
	protected JPopupMenu getPopupMenu() {// �����û������˵�
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.setOpaque(false);
		}
		final JMenuItem rename = new JMenuItem();
		popupMenu.add(rename);
		rename.addActionListener(new RenameActionListener());
		rename.setText("����");
		final JMenuItem addUser = new JMenuItem();
		addUser.addActionListener(new AddUserActionListener());
		popupMenu.add(addUser);
		addUser.setText("����û�");
		final JMenuItem delUser = new JMenuItem();
		delUser.addActionListener(new delUserActionListener());
		popupMenu.add(delUser);
		delUser.setText("ɾ���û�");
		final JMenuItem messagerGroupSend = new JMenuItem();
		messagerGroupSend
				.addActionListener(new messagerGroupSendActionListener());
		messagerGroupSend.setText("��ʹȺ��");
		popupMenu.add(messagerGroupSend);
		final JMenuItem accessComputerFolder = new JMenuItem("����������Դ");
		accessComputerFolder.setActionCommand("computer");
		popupMenu.add(accessComputerFolder);
		accessComputerFolder
				.addActionListener(new accessFolderActionListener());
		final JMenuItem accessPublicFolder = new JMenuItem();
		popupMenu.add(accessPublicFolder);
		accessPublicFolder.setOpaque(false);
		accessPublicFolder.setText("���ʹ�������");
		accessPublicFolder.setActionCommand("public");
		accessPublicFolder.addActionListener(new accessFolderActionListener());
		return popupMenu;
	}
	private void updateProject() { // ������·���
		netFilePath = preferences.get("updatePath", "EQ.jar");
		if (netFilePath.equals("EQ.jar")) {
			pushMessage("δ��������·��");
			return;
		}
		netFile = new File(netFilePath);
		localFile = new File(user_dir + File.separator + "EQ.jar");
		if (localFile != null && netFile != null && netFile.exists()
				&& localFile.exists()) {
			Date netDate = new Date(netFile.lastModified());
			Date localDate = new Date(localFile.lastModified());
			if (netDate.after(localDate)) {
				new Thread(new Runnable() {
					public void run() {
						try {
							Dialog frameUpdate = new UpdateFrame();
							frameUpdate.setVisible(true);
							Thread.sleep(2000);
							FileInputStream fis = new FileInputStream(netFile);
							FileOutputStream fout = new FileOutputStream(
									localFile);
							int len = fis.available();
							if (len > 0) {
								byte[] data = new byte[len];
								if (fis.read(data) > 0) {
									fout.write(data);
								}
							}
							fis.close();
							fout.close();
							frameUpdate.setVisible(false);
							frameUpdate = null;
							showMessageDialog("������ϣ���������������");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			} else {
				showMessageDialog("�Ѿ������µĳ����ˡ�");
			}
		}
	}

	private void checkPlacard() { // ��⹫����Ϣ����
		String placardDir = preferences.get("placardPath", null);
		if (placardDir == null) {
			pushMessage("δ���ù���·��");
			return;
		}
		File placard = new File(placardDir);
		try {
			if (placard.exists() && placard.isFile()) {
				StringBuilder placardStr = new StringBuilder();
				Scanner sc = new Scanner(new FileInputStream(placard));
				while (sc.hasNextLine()) {
					placardStr.append(sc.nextLine());
				}
				pushMessage(placardStr.toString());
			}
		} catch (FileNotFoundException e) {
			pushMessage("����·�����󣬻򹫸��ļ�������");
		}
	}

	public void setStatic(String str) {// ����״̬����Ϣ
		if (stateLabel != null)
			stateLabel.setText(str);
	}

	private void pushMessage(String info) {// ��ѹ��Ϣ
		if (!messageStack.contains(info))
			messageStack.push(info);
	}

	private void showMessageDialog(String mess) {
		JOptionPane.showMessageDialog(this, mess);
	}

	private String showInputDialog(String str) { // ��ʾ����Ի���
		String newName = JOptionPane.showInputDialog(this,
				"<html>����<font color=red>" + str + "</font>��������</html>");
		return newName;
	}

	private class accessFolderActionListener implements ActionListener {// ������Դ
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();
			if (path == null)
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			User user = (User) node.getUserObject();
			String ip = "\\\\"+user.getIp();
			String command = e.getActionCommand();
			if (command.equals("computer")) {
				Resource.startFolder(ip);
			}
			if (command.equals("public")) {
				String serverPaeh = preferences.get("pubPath", null);
				if (serverPaeh == null) {
					pushMessage("δ���ù�������·��");
					return;
				}
				Resource.startFolder(serverPaeh);
			}
		}
	}

	private class RenameActionListener implements ActionListener {// ����
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();
			if (path == null)
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			User user = (User) node.getUserObject();
			String newName = showInputDialog(user.getName());
			if (newName != null && !newName.isEmpty()) {
				user.setName(newName);
				dao.updateUser(user);
				DefaultTreeModel model = (DefaultTreeModel) chatTree.getModel();
				model.reload();
				chatTree.setSelectionPath(path);
				initUserInfoButton();
			}
		}
	}
	private class FrameWindowListener extends WindowAdapter {
		public void windowClosing(final WindowEvent e) {// ϵͳ�ر��¼�
			setVisible(false);
		}
	}
	private class AddUserActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {// ����û�
			String ip = JOptionPane.showInputDialog(EQ.this, "�������û�IP��ַ");
			if (ip != null)
				chatTree.addUser(ip, "add");
		}
	}
	private class delUserActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {// ɾ���û�
			chatTree.delUser();
		}
	}
	private class messagerGroupSendActionListener implements ActionListener {// ��ʹȺ��
		public void actionPerformed(final ActionEvent e) {
			String message = JOptionPane.showInputDialog(EQ.this, "������Ⱥ����Ϣ",
					"��ʹȺ��", JOptionPane.INFORMATION_MESSAGE);
			if (message != null && !message.equals("")) {
				TreePath[] selectionPaths = chatTree.getSelectionPaths();
				Resource.sendGroupMessenger(selectionPaths, message);
			} else if (message != null && message.isEmpty()) {
				JOptionPane.showMessageDialog(EQ.this, "���ܷ��Ϳ���Ϣ��");
			}
		}
	}
	private void SystemTrayInitial() { // ϵͳ����ʼ��
		if (!SystemTray.isSupported()) // �жϵ�ǰϵͳ�Ƿ�֧��ϵͳ��
			return;
		try {
			String title = "EQͨѶ���";
			String company = "����ʡXXX�Ƽ����޹�˾";
			SystemTray sysTray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(
					EQ.class.getResource("/icons/sysTray.png"));// ϵͳ��ͼ��
			trayicon = new TrayIcon(image, title + "\n" + company, createMenu());
			trayicon.setImageAutoSize(true);
			trayicon.addActionListener(new SysTrayActionListener());
			sysTray.add(trayicon);
			trayicon.displayMessage(title, company, MessageType.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private PopupMenu createMenu() { // ����ϵͳ���˵��ķ���
		PopupMenu menu = new PopupMenu();
		MenuItem exitItem = new MenuItem("�˳�");
		exitItem.addActionListener(new ActionListener() { // ϵͳ���˳��¼�
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
		MenuItem openItem = new MenuItem("��");
		openItem.addActionListener(new ActionListener() {// ϵͳ���򿪲˵����¼�
					public void actionPerformed(ActionEvent e) {
						if (!isVisible()) {
							setVisible(true);
							toFront();
						} else
							toFront();
					}
				});

		// ϵͳ���ķ��ʷ������˵����¼�
		MenuItem publicItem = new MenuItem("���ʷ�����");
		publicItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String serverPaeh = preferences.get("pubPath", null);
				if (serverPaeh == null) {
					pushMessage("δ���ù�������·��");
					return;
				}
				Resource.startFolder(serverPaeh);
			}
		});
		menu.add(publicItem);
		menu.add(openItem);
		menu.addSeparator();
		menu.add(exitItem);
		return menu;
	}
	class SysTrayActionListener implements ActionListener {// ϵͳ��˫���¼�
		public void actionPerformed(ActionEvent e) {
			setVisible(true);
			toFront();
		}
	}
}