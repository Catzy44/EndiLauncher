package me.catzy44.ui.listeners;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class JSChangeListener implements ChangeListener{
	private boolean enabled = true;
	public abstract void update(ChangeEvent e);
	@Override
	public void stateChanged(ChangeEvent e) {
		if(!enabled) {
			return;
		}
		update(e);	
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
