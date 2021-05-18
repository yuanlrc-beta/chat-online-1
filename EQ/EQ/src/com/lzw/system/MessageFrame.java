package com.lzw.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class MessageFrame extends JFrame {
	private final ImageIcon successIcon = new ImageIcon(MessageFrame.class
			.getResource("/messSendIcon/Success.gif"));

	private final ImageIcon failIcon = new ImageIcon(MessageFrame.class
			.getResource("/messSendIcon/Fail.gif"));

	private JTextPane textPane;

	private final JLabel stateLabel = new JLabel();

	private final JScrollPane scrollPane = new JScrollPane();

	public MessageFrame() {
		setBounds(100, 100, 307, 383);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setVisible(true);

		getContentPane().add(stateLabel, BorderLayout.SOUTH);
		stateLabel.setText("请等待消息结果。");

		getContentPane().add(scrollPane);
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setFont(new Font("", Font.PLAIN, 14));
		textPane.setDragEnabled(true);
		textPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	public void addMessage(String message, boolean success) {
		textPane.setEditable(true);
		textPane.setCaretPosition(textPane.getDocument().getLength());
		if (success)
			textPane.insertIcon(successIcon);
		else
			textPane.insertIcon(failIcon);
		textPane.setCaretPosition(textPane.getDocument().getLength());
		textPane.replaceSelection(message + "\n");
		if (!isVisible())
			setVisible(true);
		textPane.setEditable(false);
	}

	public void setStateBarInfo(String str) {
		stateLabel.setText(str);
	}
}
