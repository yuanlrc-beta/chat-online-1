package com.lzw.userList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.lzw.EQ;
import com.lzw.dao.Dao;
public class ChatTree extends JTree {
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private List<User> userMap;
	private Dao dao;
	private EQ eq;
	public ChatTree(EQ eq) {
		super();
		root = new DefaultMutableTreeNode("root");
		treeModel = new DefaultTreeModel(root);
		userMap = new ArrayList<User>();
		dao = Dao.getDao();
		addMouseListener(new ThisMouseListener());
		setRowHeight(50);
		setToggleClickCount(2);
		setRootVisible(false);
		DefaultTreeCellRenderer defaultRanderer = new DefaultTreeCellRenderer();
		UserTreeRanderer treeRanderer = new UserTreeRanderer(defaultRanderer
				.getOpenIcon(), defaultRanderer.getClosedIcon(),
				defaultRanderer.getLeafIcon());
		setCellRenderer(treeRanderer);
		setModel(treeModel);
		sortUsers();
		this.eq = eq;
	}
	private synchronized void sortUsers() {//�����û��б�
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
					root.removeAllChildren();
					String ip = InetAddress.getLocalHost().getHostAddress();
					User localUser = dao.getUser(ip);
					if (localUser != null) {// ���Լ���ʾ����λ
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(
								localUser);
						root.add(node);
					}
					userMap = dao.getUsers();
					Iterator<User> iterator = userMap.iterator();
					while (iterator.hasNext()) { // �Ӽ�����װ���û���Ϣ
						User user = iterator.next();
						if(user.getIp().equals(localUser.getIp()))
							continue;
						root.add(new DefaultMutableTreeNode(user));
					}
					treeModel.reload();
					ChatTree.this.setSelectionRow(0);
					if (eq != null)
						eq.setStatic("������������" + getRowCount());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void delUser() { // ɾ���û�
		TreePath path = getSelectionPath();
		if (path == null)
			return;
		User user = (User) ((DefaultMutableTreeNode) path
				.getLastPathComponent()).getUserObject();
		int operation = JOptionPane.showConfirmDialog(this, "ȷ��Ҫɾ���û���" + user
				+ "?", "ɾ���û�", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (operation == JOptionPane.YES_OPTION) {
			dao.delUser(user);
			root.remove((DefaultMutableTreeNode)path.getLastPathComponent());
			treeModel.reload();
		}
	}
	public boolean addUser(String ip, String opration) {// ����û�
		try {
			if (ip == null)
				return false;
			User oldUser = dao.getUser(ip);
			if (oldUser == null) {// ������ݿ��в����ڸ��û�
				InetAddress addr = InetAddress.getByName(ip);
				if (addr.isReachable(1500)) {
					String host = addr.getHostName();
					root.add(new DefaultMutableTreeNode(new User(host, ip)));
					User newUser = new User();
					newUser.setIp(ip);
					newUser.setHost(host);
					newUser.setName(host);
					newUser.setIcon("1.gif");
					dao.addUser(newUser);
					sortUsers();
					if (!opration.equals("search"))
						JOptionPane.showMessageDialog(EQ.frame, "�û�" + host
								+ "��ӳɹ�", "����û�",
								JOptionPane.INFORMATION_MESSAGE);
					return true;
					
				} else {
					if (!opration.equals("search"))
						JOptionPane.showMessageDialog(EQ.frame, "��ⲻ���û�IP��"
								+ ip, "��������û�", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else {
				if (!opration.equals("search"))
					JOptionPane.showMessageDialog(EQ.frame, "�Ѿ������û�IP" + ip,
							"��������û�", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	private class ThisMouseListener extends MouseAdapter {//����¼�������
		public void mousePressed(final MouseEvent e) {
			if (e.getButton() == 3) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (!isPathSelected(path))
					setSelectionPath(path);
			}
		}
	}
}
