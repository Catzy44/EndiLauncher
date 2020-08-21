package me.catzy44.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollbarUI extends BasicScrollBarUI {

	private final Dimension d = new Dimension();
	
	public Color dark = new Color(34, 34, 34);
	public Color blue = new Color(27, 111, 148);
	public Color darkerBlue = new Color(40, 56, 70);

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new JButton() {
			private static final long serialVersionUID = -3592643796245558676L;
			@Override
			public Dimension getPreferredSize() {
				return d;
			}
		};
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new JButton() {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				return d;
			}
		};
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
		Graphics2D g2 = (Graphics2D) g.create();
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(dark);
		g2.fillRect(r.x, r.y, r.width, r.height);
		g2.setPaint(darkerBlue);
		g2.drawLine(0, 0, 0, r.height);
		g2.dispose();
	}
	
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
		Graphics2D graphics2D = (Graphics2D) g.create();
		//graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setColor(blue);
		graphics2D.fillRect(r.x+1, r.y, r.width-1, r.height);
		graphics2D.dispose();
	}

	public Color getDark() {
		return dark;
	}

	public void setDark(Color dark) {
		this.dark = dark;
	}

	public Color getBlue() {
		return blue;
	}

	public void setBlue(Color blue) {
		this.blue = blue;
	}

	public Color getDarkerBlue() {
		return darkerBlue;
	}

	public void setDarkerBlue(Color darkerBlue) {
		this.darkerBlue = darkerBlue;
	}

	

	/*@Override
	protected void setThumbBounds(int x, int y, int width, int height) {
		super.setThumbBounds(x, y, width, height);
		scrollbar.repaint();
	}*/
	
	
	
}