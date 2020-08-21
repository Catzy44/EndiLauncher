package me.catzy44.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class PlaceholderTextField extends JTextField {

	private String placeholder;

	public PlaceholderTextField() {
	}

	public PlaceholderTextField(final Document pDoc, final String pText, final int pColumns) {
		super(pDoc, pText, pColumns);
	}

	public PlaceholderTextField(final int pColumns) {
		super(pColumns);
	}

	public PlaceholderTextField(final String pText) {
		super(pText);
	}

	public PlaceholderTextField(final String pText, final int pColumns) {
		super(pText, pColumns);
	}

	public String getPlaceholder() {
		return placeholder;
	}
	
	@Override
	public String getText() {
		if(super.getText().equals(placeholder)) {
			return "";
		}
		return super.getText();
	}
	
	@Override
	public void setText(String s) {
		placeholderActive = false;
		super.setText(s);
	}
	
	private boolean placeholderActive = false;
	public void setPlaceholder(final String s) {
		placeholder = s;
		if(getText().isEmpty()) {
			setText(placeholder);
			placeholderActive = true;
		}
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(placeholderActive) {
					placeholderActive = false;
					setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(!placeholderActive && getText().isEmpty()) {
					setText(placeholder);
					placeholderActive = true;
				}
			}
		});
	}
}