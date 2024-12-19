package me.catzy44.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class PlaceholderPasswordField extends JPasswordField {

	private String placeholder;

	public PlaceholderPasswordField() {
	}

	public PlaceholderPasswordField(final Document pDoc, final String pText, final int pColumns) {
		super(pDoc, pText, pColumns);
	}

	public PlaceholderPasswordField(final int pColumns) {
		super(pColumns);
	}

	public PlaceholderPasswordField(final String pText) {
		super(pText);
	}

	public PlaceholderPasswordField(final String pText, final int pColumns) {
		super(pText, pColumns);
	}

	public String getPlaceholder() {
		return placeholder;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getText() {
		if(super.getText().equals(placeholder)) {
			return "";
		}
		return super.getText();
	}
	
	private boolean placeholderActive = false;
	public void setPlaceholder(final String s) {
		placeholder = s;
		if(getText().isEmpty()) {
			placeholderActive = true;
			setText(placeholder);
			setEchoChar((char)0);
		}
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(placeholderActive) {
					placeholderActive = false;
					setEchoChar('*');
					setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(!placeholderActive && getText().isEmpty()) {
					placeholderActive = true;
					setEchoChar((char)0);
					setText(placeholder);
				}
			}
		});
	}

}