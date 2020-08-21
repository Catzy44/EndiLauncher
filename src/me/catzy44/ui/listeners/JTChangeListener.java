package me.catzy44.ui.listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*@FunctionalInterface*/
public abstract class JTChangeListener implements DocumentListener {
	public abstract void update(DocumentEvent e);

	private boolean enabled = true;

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (enabled) {
			update(e);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (enabled) {
			update(e);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (enabled) {
			update(e);
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}