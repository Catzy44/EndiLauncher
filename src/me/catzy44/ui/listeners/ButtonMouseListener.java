package me.catzy44.ui.listeners;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class ButtonMouseListener implements MouseListener {
	private JButton button;
	private Color c;

	public ButtonMouseListener(JButton button, Color c) {
		this.button = button;
		this.c = c;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		button.setBackground(c);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!button.isEnabled()) {
			return;
		}
		button.setBackground(new Color(c.getRed() + 20, c.getGreen() + 20, c.getBlue() + 20));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		button.setBackground(c);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!button.isEnabled()) {
			return;
		}
		button.setBackground(new Color(c.getRed() + 40, c.getGreen() + 40, c.getBlue() + 40));
	}

	public Color getColor() {
		return c;
	}

	public void setColor(Color c) {
		this.c = c;
	}
	
	

}
