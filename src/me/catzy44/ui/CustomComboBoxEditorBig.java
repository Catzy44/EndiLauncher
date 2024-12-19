package me.catzy44.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import me.catzy44.Interface;
import me.catzy44.ui.listeners.ButtonMouseListener;
 
public class CustomComboBoxEditorBig extends BasicComboBoxEditor {
    private JButton button = new JButton();
    JPanel panel= new JPanel();
    private Object selectedItem;
     
    public CustomComboBoxEditorBig(ActionListener al) {
         
    	button = new JButton();
    	button.addActionListener(al);
    	
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createLineBorder(Interface.borders));

		button.setBackground(Interface.buttonsBg);
		button.setForeground(Interface.buttonsText);
		
		button.setOpaque(true);
		
		button.setFont(Interface.ralewayBold.deriveFont(18f));
		button.setFocusable(false);
		button.addMouseListener(new ButtonMouseListener(button, Interface.buttonsBg));
		button.setFocusPainted(false);
		
    	panel.setLayout(new BorderLayout());
    	panel.setBackground(Interface.panelsBgColor);
    	panel.add(button);
    	
    	pcl = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				boolean val = (boolean) evt.getNewValue();
				button.setEnabled(val);
			}
		};
    }
    
    PropertyChangeListener pcl;
     
    @Override
    public Component getEditorComponent() {
    	 new Thread(()->{//hook into parent, give swing some time to inject this panel into other component.
         	try {
				Thread.sleep(200);
				
				if(panel.getParent() == null || pcl == null) {
					return;
				}
				Component c = panel.getParent();
				for(PropertyChangeListener pc : c.getPropertyChangeListeners("enabled")) {
					if(pc == pcl) {
						return;//already hooked.
					}
				}
				c.addPropertyChangeListener("enabled",pcl);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
         }).start();
        return panel;
    }
     
    @Override
    public Object getItem() {
    	if(this.selectedItem == null) {
    		return "";
    	}
        return "" + this.selectedItem.toString() + "";
    }
     
    @Override
    public void setItem(Object item) {
    	if(item == null) {
    		return;
    	}
        this.selectedItem = item;
        button.setText(item.toString());
        button.setActionCommand(item.toString());
    }
     
}