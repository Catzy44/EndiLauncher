package me.catzy44.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;

import me.catzy44.Interface;
import me.catzy44.utils.UIUtils;

public class CustomComboBoxRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = -1363514186588801222L;

	public CustomComboBoxRenderer() {
        setOpaque(true);
        setFont(Interface.ralewayMedium.deriveFont(14f));
        setBackground(Interface.panelsBgColor);
        setForeground(Interface.polaText);
    }
     
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	setBorder(new CompoundBorder(BorderFactory.createMatteBorder(index == 0 ? 1 : 0,1,1,1,Interface.borders),UIUtils.createOffsetedLineBorder(0, 10, 0, 0, new Color(0,0,0,0))));
        setText(value.toString());
        return this;
    }
}
