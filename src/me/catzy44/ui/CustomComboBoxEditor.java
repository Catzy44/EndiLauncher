package me.catzy44.ui;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import me.catzy44.Interface;
 
public class CustomComboBoxEditor extends BasicComboBoxEditor {
    private JLabel label = new JLabel();
    private JPanel panel = new JPanel();
    private Object selectedItem;
     
    public CustomComboBoxEditor() {
         
        label.setOpaque(false);
        label.setFont(Interface.ralewayMedium.deriveFont(14f));
        label.setForeground(Interface.polaText);
         
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.add(label);
        panel.setBackground(Interface.panelsBgColor);
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Interface.borders));
    }
     
    @Override
    public Component getEditorComponent() {
        return this.panel;
    }
     
    @Override
    public Object getItem() {
        return "" + this.selectedItem.toString() + "";
    }
     
    @Override
    public void setItem(Object item) {
    	if(item == null) {
    		return;
    	}
        this.selectedItem = item;
        label.setText(item.toString());
    }
     
}