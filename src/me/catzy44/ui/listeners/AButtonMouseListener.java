package me.catzy44.ui.listeners;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import me.catzy44.utils.UIUtils;

public class AButtonMouseListener implements MouseListener {
	private JButton button;
	private Color c = null;

	public AButtonMouseListener(JButton button) {
		this.button = button;
		c = button.getForeground();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		button.setForeground(c);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!button.isEnabled()) {
			return;
		}
		//button.setBackground(new Color(c.getRed() + 20, c.getGreen() + 20, c.getBlue() + 20));
		button.setFont(UIUtils.underLine(button.getFont(),true));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//button.setBackground(c);
		button.setFont(UIUtils.underLine(button.getFont(),false));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!button.isEnabled()) {
			return;
		}
		button.setForeground(new Color(c.getRed() + 40, c.getGreen() + 40, c.getBlue() + 40));
	}

	public Color getColor() {
		return c;
	}

	public void setColor(Color c) {
		this.c = c;
	}
	
	

}
