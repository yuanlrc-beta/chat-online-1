package com.lzw;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

public class UpdateFrame extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5140620475440199136L;

	public UpdateFrame() {
		super(new Frame());
		final int w = 302, h = 52;
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(w - w / 10, 16));
		progressBar.setIndeterminate(true);
		final int sw = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		final int sh = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		setBounds((sw - w) / 2, (sh - h) / 2, w, h);
		add(progressBar);
		progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		setVisible(true);
		setTitle("网络上有最新程序，正在更新……");
		setResizable(false);
		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		setLayout(borderLayout);
	}
}