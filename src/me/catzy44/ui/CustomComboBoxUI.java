package me.catzy44.ui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import me.catzy44.Interface;

public class CustomComboBoxUI extends BasicComboBoxUI {
	public static ComponentUI createUI(JComponent c) {
		return new CustomComboBoxUI();
	}
	
	@Override
    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
			private static final long serialVersionUID = -309674138369024826L;

			@Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroller.getVerticalScrollBar().setUI(new CustomScrollbarUI());
               /* scroller.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
                    protected JButton createDecreaseButton(int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    public Dimension getPreferredSize(JComponent c) {
                        return new Dimension(10, super.getPreferredSize(c).height);
                    }

                    private JButton createZeroButton() {
                        return new JButton() {
							private static final long serialVersionUID = -4474484317412367306L;

							@Override
                            public Dimension getMinimumSize() {
                                return new Dimension(new Dimension(0, 0));
                            }

                            @Override
                            public Dimension getPreferredSize() {
                                return new Dimension(new Dimension(0, 0));
                            }

                            @Override
                            public Dimension getMaximumSize() {
                                return new Dimension(new Dimension(0, 0));
                            }
                        };
                    }
                });*/
                return scroller;
            }
        };
    }

	@Override
	protected JButton createArrowButton() {
		//JButton button = new BasicArrowButton(BasicArrowButton.EAST);
		JButton button = new JButton();
		try {
			button.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/dropdown.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		button.setContentAreaFilled(false);
		button.setFocusable(false);
		button.setBackground(Interface.panelsBgColor);
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createMatteBorder(1,0,1,1,Interface.borders));
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(Interface.panelsBgColor);
		    	
		    }
			@Override
		    public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(Interface.polaTlo);
		    }
		});
		return button;
	}
}