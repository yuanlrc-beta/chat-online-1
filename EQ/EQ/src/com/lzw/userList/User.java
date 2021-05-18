package com.lzw.userList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.lzw.EQ;

public class User {
	private String ip;
	private String host;
	private String tipText;
	private String name;
	private String icon;
	private Icon iconImg = null;
	public User() {
	}
	public User(String host, String ip) {
		this.ip = ip;
		this.host = host;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String toString() {
		String strName = getName() == null ? getHost() : getName();
		return strName;
	}
	public String getIcon() {
		return icon;
	}
	public Icon getIconImg() {// »ñÈ¡Í·ÏñÍ¼Æ¬
		int faceNum = 1;
		if (ip != null && !ip.isEmpty()) {
			String tip = ip.replace(".", ",");
			String[] num = tip.split(",");
			if (num.length == 4) {
				Integer num1 = Integer.parseInt(num[2]) + 1;
				Integer num2 = Integer.parseInt(num[3]);
				faceNum = (num1 * num2) % 11 + 1;
			}
		}
		try {
			iconImg = new ImageIcon(
					EQ.class.getResource("/NEWFACE/" + faceNum + ".png"));
		} catch (RuntimeException e) {
			iconImg = new ImageIcon(
					EQ.class.getResource("/NEWFACE/1.png"));
			e.printStackTrace();
		}
		return iconImg;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTipText() {
		return tipText;
	}
	public void setTipText(String tipText) {
		this.tipText = tipText;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
