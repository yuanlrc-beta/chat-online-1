package com.lzw.userList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.lzw.EQ;
import com.lzw.dao.Dao;
public class UserTreeRanderer extends JPanel implements TreeCellRenderer {
	private Icon openIcon, closedIcon, leafIcon;
	private String tipText = "";
	private final JCheckBox label = new JCheckBox();
	private final JLabel headImg = new JLabel();
	private static User user;
	public UserTreeRanderer() {
		super();
		user = null;
	}
	public UserTreeRanderer(Icon open, Icon closed, Icon leaf) {
		openIcon = open;
		closedIcon = closed;
		leafIcon = leaf;
		setBackground(new Color(0xF5B9BF));
		label.setFont(new Font("ËÎÌו", Font.BOLD, 14));
		URL trueUrl = EQ.class
				.getResource("/image/chexkBoxImg/CheckBoxTrue.png");
		label.setSelectedIcon(new ImageIcon(trueUrl));
		URL falseUrl = EQ.class
				.getResource("/image/chexkBoxImg/CheckBoxFalse.png");
		label.setIcon(new ImageIcon(falseUrl));
		label.setForeground(new Color(0, 64, 128));
		final BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		user = null;
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object uo = node.getUserObject();
			if (uo instanceof User) 
				user = (User) uo;
		} else if (value instanceof User) 
			user = (User) value;
		if (user != null && user.getIcon() != null) {
			int width = EQ.frame.getWidth();
			if (width > 0)
				setPreferredSize(new Dimension(width, user.getIconImg()
						.getIconHeight()));
			headImg.setIcon(user.getIconImg());
			tipText = user.getName();
		} else {
			if (expanded)
				headImg.setIcon(openIcon);
			else if (leaf)
				headImg.setIcon(leafIcon);
			else
				headImg.setIcon(closedIcon);

		}
		add(headImg, BorderLayout.WEST);
		label.setText(value.toString());
		label.setOpaque(false);
		add(label, BorderLayout.CENTER);
		if (selected) {
			label.setSelected(true);
			setBorder(new LineBorder(new Color(0xD46D73), 2, false));
			setOpaque(true);
		} else {
			setOpaque(false);
			label.setSelected(false);
			setBorder(new LineBorder(new Color(0xD46D73), 0, false));
		}
		return this;
	}
	public String getToolTipText() {
		return tipText;
	}
}
