package me.catzy44.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

public class TransparentPanelFixUI extends PanelUI {
	@Override
	public void update(Graphics g, JComponent c) {
		if (c.isOpaque() || (!c.isOpaque() && c.isBackgroundSet())) {
			g.setColor(c.getBackground());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
		paint(g, c);
	}
}
