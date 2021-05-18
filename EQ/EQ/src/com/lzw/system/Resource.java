package com.lzw.system;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import com.lzw.EQ;
import com.lzw.frame.TelFrame;
import com.lzw.userList.ChatTree;
import com.lzw.userList.User;
public class Resource {
	public static void searchUsers(ChatTree tree, JProgressBar progressBar,
			JList list, JToggleButton button) {
		String ipStart = EQ.preferences.get("ipStart", "192.168.0.1");
		String ipEnd = EQ.preferences.get("ipEnd", "192.168.1.255");
		String[] is = ipStart.split("\\.");
		String[] ie = ipEnd.split("\\.");
		int[] ipsInt = new int[4];
		int[] ipeInt = new int[4];
		for (int i = 0; i < 4; i++) {
			ipsInt[i] = Integer.parseInt(is[i]);
			ipeInt[i] = Integer.parseInt(ie[i]);
		}
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		DefaultListModel model = new DefaultListModel();
		model.addElement("搜索结果：");
		list.setModel(model);
		try {
			for (int l = ipsInt[0]; l <= ipeInt[0]; l++) {
				boolean b0 = l < ipeInt[0]; // 记录第一层循环的条件
				int k = l != ipsInt[0] ? 0 : ipsInt[1]; // 从第二次循环以后k赋值0
				for (; b0 ? k < 256 : k <= ipeInt[1]; k++) {
					boolean b1 = b0 || k < ipeInt[1]; // 记录第二层循环的条件
					int j = k != ipsInt[1] ? 0 : ipsInt[2]; // 从第二次循环以后j赋值0
					for (; b1 ? j < 256 : j <= ipeInt[2]; j++) {
						boolean b2 = b1 || b1 ? j < 256 : j < ipeInt[2];
						int i = j != ipsInt[2] ? 0 : ipsInt[3];
						for (; b2 ? i < 256 : i <= ipeInt[3]; i++) {
							if(!button.isSelected()){
								progressBar.setIndeterminate(false);
								return;
							}
							Thread.sleep(100);
							String ip = l + "." + k + "." + j + "." + i;
							progressBar.setString("正在搜索：" + ip);
							if (tree.addUser(ip, "search"))
								model.addElement("<html><b><font color=green>添加"
												+ ip + "</font></b></html>");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			progressBar.setIndeterminate(false);
			progressBar.setString("搜索完毕");
			button.setText("搜索新用户");
			button.setSelected(false);
		}
	}
	public static boolean loginPublic(String user, String pass) {
		try {
			String userName = user;
			String updatePath = EQ.preferences.get("updatePath", null);
			if (updatePath == null)
				return false;
			File file = new File(updatePath);
			if (!file.exists())
				return false;
			if (file.isFile())
				updatePath = file.getParent();
			if (userName != null && !userName.equals("")) {
				userName = " /user:" + userName;
			}
			Process process = Runtime.getRuntime().exec(
					"cmd /c %windir%" + File.separator + "System32"
							+ File.separator + "net use " + updatePath + " "
							+ pass + userName);
			System.out.println("cmd /c %windir%" + File.separator + "System32"
					+ File.separator + "net use " + updatePath + " " + pass
					+ userName);
			Scanner sce = new Scanner(process.getErrorStream());
			StringBuilder stre = new StringBuilder();
			while (sce.hasNextLine()) {
				stre.append(sce.nextLine());
			}
			process.destroy();
			String resulte = stre.toString();
			if (resulte.equals(""))
				return true;
			else
				JOptionPane.showMessageDialog(EQ.frame, resulte, "错误信息",
						JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static void sendGroupMessenger(final TreePath[] selectionPaths,
			final String message) {// 群发信使信息
		new Thread(new Runnable() {
			int bufferSize = 512;
			public void run() {
				MessageFrame messageFrame = new MessageFrame();
				try {
					for (TreePath path : selectionPaths) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						User user = (User) node.getUserObject();
						messageFrame
								.setStateBarInfo("<html>正在给<font color=blue>"
										+ user.getName()
										+ "</font>发送消息……</html>");
						Thread.sleep(20);
						InetAddress addr = InetAddress.getByName(user.getIp());
						if (!addr.getHostAddress().equals(addr.getHostName())) {
							Process process = Runtime.getRuntime().exec(
									"net send " + user.getIp() + " " + message);
							InputStream is = process.getInputStream();
							int i;
							String sb = null;
							byte[] data = new byte[bufferSize];
							if ((i = is.read(data)) != -1) {
								sb = new String(data, 0, i);
							}
							String runIs = sb;
							runIs = runIs.replace(user.getIp(), user.getName())
									.trim();
							process.destroy();
							if (runIs.indexOf("出错") < 0)
								messageFrame.addMessage(runIs, true);
							else
								messageFrame.addMessage(runIs, false);
						} else {
							messageFrame.addMessage("错误：" + user.getName()
									+ "可能没有开机或启动了防火墙", false);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				messageFrame.setStateBarInfo("消息发送完毕,可以关闭窗口。");
				messageFrame
						.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		}).start();
	}
	public static void sendMessenger(User user, String message, TelFrame frame) {// 发送信使信息
		class TheThread implements Runnable {
			private User user;
			private String message;
			private TelFrame frame;
			private JButton sendButton;
			public TheThread(User user, String message, TelFrame frame) {
				this.user = user;
				this.message = message;
				this.frame = frame;
				this.sendButton = frame.getSendButton();
			}

			public void run() {
				try {
					sendButton.setEnabled(false);
					Process process = Runtime.getRuntime().exec(
							"net send " + user.getIp() + " " + message);
					InputStream is = process.getInputStream();
					int i, j;
					StringBuilder sb = new StringBuilder();
					while ((i = is.read()) != -1) {
						sb.append((char) i);
					}
					String runIs = new String(sb.toString().getBytes(
							"iso-8859-1")).trim().replace(user.getIp(),
							user.getName());
					InputStream eis = process.getErrorStream();
					StringBuilder esb = new StringBuilder();
					while ((j = eis.read()) != -1) {
						esb.append((char) j);
					}
					String runEis = new String(esb.toString().getBytes(
							"iso-8859-1")).trim().replace(user.getIp(),
							user.getName());
					frame.appendReceiveText(runIs, new Color(187, 30, 193));
					if (runEis.length() > 0)
						frame.appendReceiveText(runIs, Color.RED);
					sendButton.setEnabled(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		new Thread(new TheThread(user, message, frame)).start();
	}

	public static void startFolder(String str) {
		try {
			Runtime.getRuntime().exec("cmd /c start " + str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
