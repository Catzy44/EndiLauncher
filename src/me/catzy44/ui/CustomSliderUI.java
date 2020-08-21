package me.catzy44.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import me.catzy44.utils.Utils;

public class CustomSliderUI extends BasicSliderUI {
	public CustomSliderUI(JSlider b) {
		super(b);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g, c);
	}

	@Override
	protected Dimension getThumbSize() {
		return new Dimension(12, 16);
	}

	@Override
	public void paintTrack(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(slider.getForeground());
		g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2, trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2);
		g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2 - 4, trackRect.x, trackRect.y + trackRect.height / 2 + 4);
		g2d.drawLine(trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2 - 4, trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2 + 4);
	}

	@Override
	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int x1 = thumbRect.x + 2;
		int x2 = thumbRect.x + thumbRect.width - 2;
		int width = thumbRect.width - 4;
		int topY = thumbRect.y + thumbRect.height / 2 - thumbRect.width / 3;
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		shape.moveTo(x1, topY);
		shape.lineTo(x2, topY);
		shape.lineTo((x1 + x2) / 2, topY + width);
		shape.closePath();
		// g2d.setPaint(new Color(81, 83, 186));
		g2d.setPaint(new Color(slider.getForeground().getRed() - 20, slider.getForeground().getGreen() - 20, slider.getForeground().getBlue() - 20));
		g2d.fill(shape);
		Stroke old = g2d.getStroke();
		g2d.setStroke(new BasicStroke(2f));
		// g2d.setPaint(new Color(131, 127, 211));
		g2d.setPaint(slider.getForeground());
		g2d.draw(shape);
		g2d.setStroke(old);
		g2d.dispose();
	}
	@Override
	protected TrackListener createTrackListener(JSlider slider) {
	    return new RangeTrackListener();
	}
	public class RangeTrackListener extends TrackListener {

	    @Override
	    public void mouseClicked(MouseEvent e) {
	    	int x = e.getX();
	    	int width = slider.getWidth();
	    	
	    	int ef = (int) Utils.map(x, 0, width, slider.getMinimum(), slider.getMaximum());
	    	
	    	slider.setValue(ef);
	    }

	    public void mousePressed(MouseEvent e) {
	    	if (!slider.isEnabled()) {
	            return;
	        }

	        currentMouseX = e.getX();
	        currentMouseY = e.getY();
	        
	        if (thumbRect.contains(currentMouseX, currentMouseY)) {
                super.mousePressed(e);
            }
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    	super.mouseReleased(e);
	    }

	    @Override
	    public void mouseDragged(MouseEvent e) {
	    	super.mouseDragged(e);
	    }
	}
}