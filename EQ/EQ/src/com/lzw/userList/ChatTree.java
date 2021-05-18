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
	private synchronized void sortUsers() {//排序用户列表
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
					root.removeAllChildren();
					String ip = InetAddress.getLocalHost().getHostAddress();
					User localUser = dao.getUser(ip);
					if (localUser != null) {// 把自己显示在首位
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(
								localUser);
						root.add(node);
					}
					userMap = dao.getUsers();
					Iterator<User> iterator = userMap.iterator();
					while (iterator.hasNext()) { // 从集合中装载用户信息
						User user = iterator.next();
						if(user.getIp().equals(localUser.getIp()))
							continue;
						root.add(new DefaultMutableTreeNode(user));
					}
					treeModel.reload();
					ChatTree.this.setSelectionRow(0);
					if (eq != null)
						eq.setStatic("　　总人数：" + getRowCount());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void delUser() { // 删除用户
		TreePath path = getSelectionPath();
		if (path == null)
			return;
		User user = (User) ((DefaultMutableTreeNode) path
				.getLastPathComponent()).getUserObject();
		int operation = JOptionPane.showConfirmDialog(this, "确定要删除用户：" + user
				+ "?", "删除用户", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (operation == JOptionPane.YES_OPTION) {
			dao.delUser(user);
			root.remove((DefaultMutableTreeNode)path.getLastPathComponent());
			treeModel.reload();
		}
	}
	public boolean addUser(String ip, String opration) {// 添加用户
		try {
			if (ip == null)
				return false;
			User oldUser = dao.getUser(ip);
			if (oldUser == null) {// 如果数据库中不存在该用户
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
						JOptionPane.showMessageDialog(EQ.frame, "用户" + host
								+ "添加成功", "添加用户",
								JOptionPane.INFORMATION_MESSAGE);
					return true;
					
				} else {
					if (!opration.equals("search"))
						JOptionPane.showMessageDialog(EQ.frame, "检测不到用户IP："
								+ ip, "错误添加用户", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else {
				if (!opration.equals("search"))
					JOptionPane.showMessageDialog(EQ.frame, "已经存在用户IP" + ip,
							"不能添加用户", JOptionPane.WARNING_MESSAGE);
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
	private class ThisMouseListener extends MouseAdapter {//鼠标事件监听器
		public void mousePressed(final MouseEvent e) {
			if (e.getButton() == 3) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (!isPathSelected(path))
					setSelectionPath(path);
			}
		}
	}
}
