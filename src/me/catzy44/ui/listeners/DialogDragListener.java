package me.catzy44.ui.listeners;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;

public class DialogDragListener extends MouseAdapter {

    private final JDialog frame;
    private Point mouseDownCompCoords = null;

    public DialogDragListener(JDialog frame) {
        this.frame = frame;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDownCompCoords = null;
    }

    public void mousePressed(MouseEvent e) {
        mouseDownCompCoords = e.getPoint();
    }

    public void mouseDragged(MouseEvent e) {
        Point currCoords = e.getLocationOnScreen();
        if(frame != null && currCoords != null) {
        	frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }
}