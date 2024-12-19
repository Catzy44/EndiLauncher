package me.catzy44.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import me.catzy44.Interface;

public class UIUtils {
	// TOP, LEFT, BOTTOM, RIGHT
	public static Border createOffsetedLineBorder(int o1, int o2, int o3, int o4, Color color) {
		Border lineBorder = BorderFactory.createLineBorder(color);
		Border emptyBorder = new EmptyBorder(o1, o2, o3, o4);
		Border border = new CompoundBorder(lineBorder, emptyBorder);
		return border;
	}

	// TOP, LEFT, BOTTOM, RIGHT
	public static Border createOffsetedLineBorder(int o1, int o2, int o3, int o4, int m1, int m2, int m3, int m4, Color color) {
		Border lineBorder = BorderFactory.createMatteBorder(m1, m2, m3, m4, Interface.borderColor);
		Border emptyBorder = new EmptyBorder(o1, o2, o3, o4);
		Border border = new CompoundBorder(lineBorder, emptyBorder);
		return border;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Font underLine(Font font, boolean underline) {
		Map attributes = font.getAttributes();
		if (underline) {
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		} else {
			attributes.put(TextAttribute.UNDERLINE, null);
		}
		return font.deriveFont(attributes);
	}

	public static String createHTMLFontTag(Font f) {
		return "<font face=\"" + f.getFamily() + "\">";
	}

	public static JButton getButtonSubComponent(Container container) {
		if (container instanceof JButton) {
			return (JButton) container;
		} else {
			Component[] components = container.getComponents();
			for (Component component : components) {
				if (component instanceof Container) {
					return getButtonSubComponent((Container) component);
				}
			}
		}
		return null;
	}
}
