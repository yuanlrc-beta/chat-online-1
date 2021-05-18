package com.lzw.frame;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;
import com.lzw.EQ;
import com.lzw.dao.Dao;
import com.lzw.system.Resource;
import com.lzw.userList.ChatTree;
import com.lzw.userList.User;
public class TelFrame extends JFrame {
	private Dao dao;
	private User user;
	private JTextPane receiveText = new JTextPane();
	private JScrollPane scrollPane = new JScrollPane();
	private JTextPane sendText = new JTextPane();
	private JScrollPane scrollPane_1 = new JScrollPane();
	private JSplitPane splitPane = new JSplitPane();
	private JButton sendButton = new JButton();
	private final JButton messageButton = new JButton();
	private JPanel panel = new JPanel();
	private final static Map<String, TelFrame> instance = new HashMap<String, TelFrame>();
	private final JCheckBox messageMode = new JCheckBox();
	private JToolBar toolBar = new JToolBar();
	private JToggleButton toolFontButton = new JToggleButton();
	private JButton toolFaceButton = new JButton();
	private JButton button = new JButton();
	private JButton button_3 = new JButton();
	private final JButton button_1 = new JButton();
	private final JPanel panel_5 = new JPanel();
	private JPanel panel_2 = new JPanel();
	private JPanel panel_1 = new JPanel();
	private JLabel label = new JLabel();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	private final JLabel label_1 = new JLabel();
	private JPanel panel_3 = new JPanel();
	private byte[] buf;
	private DatagramSocket ss;
	private String ip;
	private DatagramPacket dp;
	private TelFrame frame;
	private ChatTree tree;
	private int rightPanelWidth = 148;
	public static synchronized TelFrame getInstance(DatagramSocket ssArg,
			DatagramPacket dp, ChatTree treeArg) {
		String tmpIp = dp.getAddress().getHostAddress();
		if (!instance.containsKey(tmpIp)) {
			TelFrame frame = new TelFrame(ssArg, dp, treeArg);
			instance.put(tmpIp, frame);
			frame.receiveInfo(treeArg);
			if (!frame.isVisible()) {
				frame.setVisible(true);
			}
			frame.setState(JFrame.NORMAL);
			frame.toFront();
			return frame;
		} else {
			TelFrame frame = instance.get(tmpIp);
			frame.setBufs(dp.getData());
			frame.receiveInfo(treeArg);
			if (!frame.isVisible()) {
				frame.setVisible(true);
			}
			frame.setState(JFrame.NORMAL);
			frame.toFront();
			return frame;
		}
	}

	public TelFrame(DatagramSocket ssArg, DatagramPacket dpArg,
			final ChatTree treeArg) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.tree = treeArg;
		ip = dpArg.getAddress().getHostAddress();
		dao = Dao.getDao();
		user = dao.getUser(ip);
		frame = this;
		ss = ssArg;
		dp = dpArg;
		buf = dp.getData();
		try {
			setBounds(200, 100, 521, 424);
			getContentPane().add(splitPane);
			splitPane.setDividerSize(2);
			splitPane.setResizeWeight(0.8);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(scrollPane);
			scrollPane.setViewportView(getReceiveText());
			receiveText.setFont(new Font("宋体", Font.PLAIN, 12));
			receiveText.setInheritsPopupMenu(true);
			receiveText.setVerifyInputWhenFocusTarget(false);
			receiveText.setDragEnabled(true);
			receiveText.setMargin(new Insets(0, 0, 0, 0));
			receiveText.setEditable(false);
			getReceiveText().addComponentListener(new ComponentAdapter() {
				public void componentResized(final ComponentEvent e) {
					scrollPane.getVerticalScrollBar().setValue(
							getReceiveText().getHeight());
				}
			});
			getReceiveText().setDoubleBuffered(true);

			splitPane.setRightComponent(panel_2);
			panel_2.setLayout(new BorderLayout());

			final FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(4);
			flowLayout.setAlignment(FlowLayout.LEFT);
			flowLayout.setVgap(0);
			panel_2.add(panel, BorderLayout.SOUTH);
			final FlowLayout flowLayout_1 = new FlowLayout();
			flowLayout_1.setVgap(3);
			flowLayout_1.setHgap(20);
			panel.setLayout(flowLayout_1);

			panel.add(sendButton);
			sendButton.setMargin(new Insets(0, 14, 0, 14));
			sendButton.addActionListener(new sendActionListener());
			sendButton.setText("发送");

			panel.add(messageButton);
			messageButton.setMargin(new Insets(0, 14, 0, 14));
			messageButton.addActionListener(new MessageButtonActionListener());
			messageButton.setText("信史");

			panel_2.add(panel_5, BorderLayout.NORTH);
			panel_5.setLayout(new BorderLayout());
			
			toolbarActionListener toolListener = new toolbarActionListener();
			panel_5.add(toolBar);
			toolBar.setBorder(new BevelBorder(BevelBorder.RAISED));
			toolBar.setFloatable(false);
			toolBar.add(toolFontButton);
			toolFontButton.addActionListener(toolListener);
			toolFontButton.setFocusPainted(false);
			toolFontButton.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarFontIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarFont.png"));
			toolFontButton.setIcon(toolbarFontIcon);
			toolFontButton.setToolTipText("设置字体颜色和格式");
			toolBar.add(toolFaceButton);
			toolFaceButton.addActionListener(toolListener);
			toolFaceButton.setToolTipText("选择表情");
			toolFaceButton.setFocusPainted(false);
			toolFaceButton.setMargin(new Insets(0, 0, 0, 0));

			ImageIcon toolbarFaceIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarFace.png"));
			toolFaceButton.setIcon(toolbarFaceIcon);
			toolBar.add(button);
			
			button.addActionListener(toolListener);
			button.setToolTipText("发送文件");
			button.setFocusPainted(false);
			button.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarPictureIcon = new ImageIcon(
					EQ.class
							.getResource("/image/telFrameImage/toolbarImage/ToolbarPicture.png"));
			button.setIcon(toolbarPictureIcon);
			toolBar.add(button_3);
			button_3.addActionListener(toolListener);
			button_3.setToolTipText("选择聊天场景");
			button_3.setFocusPainted(false);
			button_3.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarSceneIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarScene.png"));
			button_3.setIcon(toolbarSceneIcon);
			System.currentTimeMillis();
			toolBar.add(messageMode);
			messageMode.setText("消息模式");
			panel_5.add(button_1, BorderLayout.EAST);
			button_1.addActionListener(new Button_1ActionListener());
			button_1.setMargin(new Insets(0, 0, 0, 0));
			button_1.setText("<");
			panel_2.add(panel_1);
			panel_1.setLayout(new BorderLayout());
			panel_1.add(scrollPane_1);
			scrollPane_1
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			sendText.setInheritsPopupMenu(true);
			sendText.addKeyListener(new SendTextKeyListener());
			sendText.setVerifyInputWhenFocusTarget(false);
			sendText.setFont(new Font("宋体", Font.PLAIN, 12));
			sendText.setMargin(new Insets(0, 0, 0, 0));
			sendText.setDragEnabled(true);
			sendText.requestFocus();
			scrollPane_1.setViewportView(getSendText());

			addWindowListener(new TelFrameClosing(tree));
			add(panel_3, BorderLayout.EAST);
			panel_3.setLayout(new BorderLayout());
			panel_3.add(scrollPane_2);
			scrollPane_2
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane_2.setViewportView(label);
			label.setIconTextGap(-1);
			String imgPath = EQ.class
					.getResource("/image/telFrameImage/telUserInfo.png")
					+ "";
			label.setText("<html><body background='" + imgPath
					+ "'><table width='" + rightPanelWidth
					+ "'><tr><td>用户名：<br>&nbsp;&nbsp;" + user.getName()
					+ "</td></tr><tr><td>主机名：<br>&nbsp;&nbsp;" + user.getHost()
					+ "</td></tr>" + "<tr><td>IP地址：<br>&nbsp;&nbsp;" + user.getIp()
					+ "</td></tr><tr><td colspan='2' height="
					+ this.getHeight() * 2
					+ "></td></tr></table></body></html>");

			panel_3.add(label_1, BorderLayout.NORTH);
			label_1.setIcon(new ImageIcon(EQ.class
					.getResource("/image/telFrameImage/telUserImage.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(true);
		setTitle("与『" + user + "』通讯中");
	}

	private void receiveInfo(final ChatTree tree) {// 接收信息
		if (buf.length > 0) {
			String rText = new String(buf).replace("" + (char) 0, "");
			String hostAddress = dp.getAddress().getHostAddress();
			String info = dao.getUser(hostAddress).getName();
			info = info + "  (" + new Date().toLocaleString() + ")";
			appendReceiveText(info, Color.BLUE);
			appendReceiveText(rText + "\n", null);
		}
	}

	class sendActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			String sendInfo = getSendInfo();
			if (sendInfo == null)
				return;
			insertUserInfoToReceiveText(tree);
			appendReceiveText(sendInfo + "\n", null);
			byte[] tmpBuf = sendInfo.getBytes();
			DatagramPacket tdp = null;
			try {
				tdp = new DatagramPacket(tmpBuf, tmpBuf.length,
						new InetSocketAddress(ip, 1111));
				ss.send(tdp);
			} catch (SocketException e2) {
				e2.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(TelFrame.this, e1
						.getMessage());
			}
			sendText.setText(null);
			sendText.requestFocus();
			if (messageMode.isSelected())
				setState(ICONIFIED);
		}
	}

	class toolbarActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			JOptionPane.showMessageDialog(TelFrame.this, "此功能尚在建设中。");
		}
	}

	private final class TelFrameClosing extends WindowAdapter {
		private final JTree tree;

		private TelFrameClosing(JTree tree) {
			this.tree = tree;
		}

		public void windowClosing(final WindowEvent e) {
			tree.setSelectionPath(null);
			TelFrame.this.setState(ICONIFIED);
			TelFrame.this.dispose();
		}
	}

	private class MessageButtonActionListener implements ActionListener {// 信史按钮
		public void actionPerformed(final ActionEvent e) {
			try {
				Document doc = sendText.getDocument();
				String sendInfo = doc.getText(0, doc.getLength());
				if (sendInfo.equals("") || sendInfo == null) {
					JOptionPane.showMessageDialog(TelFrame.this, "不能发送空信息。");
					return;
				}
				insertUserInfoToReceiveText(tree);
				appendReceiveText(sendInfo, null);
				Resource.sendMessenger(user, sendInfo, frame);
				sendText.setText(null);
				sendText.requestFocus();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}
	private class SendTextKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == 10)
				sendButton.doClick();
			else if (e.isShiftDown() && e.getKeyCode() == 10)
				messageButton.doClick();
		}
	}
	private class Button_1ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel_3.setVisible(!panel_3.isVisible());
			TelFrame.this.setVisible(true);
		}
	}

	public JButton getSendButton() {
		return sendButton;
	}

	public JTextPane getReceiveText() {
		return receiveText;
	}

	public void setBufs(byte[] bufs) {
		this.buf = bufs;
	}

	public String getSendInfo() {
		String sendInfo = "";
		Document doc = sendText.getDocument();
		try {
			sendInfo = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (sendInfo.equals("")) {
			JOptionPane.showMessageDialog(TelFrame.this, "不能发送空信息。");
			return null;
		}
		return sendInfo;
	}

	private void insertUserInfoToReceiveText(final ChatTree tree) {
		String info = null;
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			info = dao.getUser(hostAddress).getName();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		info = info + "  (" + new Date().toLocaleString() + ")";
		appendReceiveText(info, new Color(68, 184, 29));
	}

	public JTextPane getSendText() {
		return sendText;
	}

	public void appendReceiveText(String sendInfo, Color color) {
		Style style = receiveText.addStyle("title", null);
		if (color != null) {
			StyleConstants.setForeground(style, color);
		} else {
			StyleConstants.setForeground(style, Color.BLACK);
		}
		receiveText.setEditable(true);
		receiveText.setCaretPosition(receiveText.getDocument().getLength());
		receiveText.setCharacterAttributes(style, false);
		receiveText.replaceSelection(sendInfo + "\n");
		receiveText.setEditable(false);
	}
}
