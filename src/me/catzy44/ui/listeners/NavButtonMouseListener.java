package me.catzy44.ui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class NavButtonMouseListener implements MouseListener {
	private JButton button;
	private ImageIcon normal;
	private ImageIcon rollover;
	private ImageIcon clicked;

	public NavButtonMouseListener(JButton button, ImageIcon normal, ImageIcon rollover, ImageIcon clicked) {
		this.button = button;
		this.normal = normal;
		this.rollover = rollover;
		this.clicked = clicked;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		button.setIcon(normal);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		button.setIcon(rollover);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		button.setIcon(normal);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		button.setIcon(clicked);
	}

	public ImageIcon getNormal() {
		return normal;
	}

	public void setNormal(ImageIcon normal) {
		this.normal = normal;
	}

	public ImageIcon getRollover() {
		return rollover;
	}

	public void setRollover(ImageIcon rollover) {
		this.rollover = rollover;
	}

	public ImageIcon getClicked() {
		return clicked;
	}

	public void setClicked(ImageIcon clicked) {
		this.clicked = clicked;
	}
	
	

}
