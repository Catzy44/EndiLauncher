package me.catzy44.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.catzy44.Interface;
import me.catzy44.tools.Themes;
import me.catzy44.ui.JMultilineLabel;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DialogUtil {
	
	//sorry for poor implementation, only one dialog at a time...

	static int exit = -2;
	
	public static int showNotClosableDialog(String text) {
		return showMultipleOptionsDialog(text,(Map)InlineMap.of(),false,false,false,true,0,0);
	}
	
	public static int showYesNoDialog(String text) {
		return showYesNoDialog(text,"Tak","Nie");
	}
	
	public static int showYesNoDialog(String text, String btnYesText, String btnNoText) {
		return showMultipleOptionsDialog(text,(Map)InlineMap.of(btnYesText,0,btnNoText,1),false,false,true,true,10,60);
	}
	
	public static int showConfirmDialog(String text) {
		return showConfirmDialog(text,"Ok");
	}
	
	public static int showConfirmDialog(String text, String btnText) {
		return showMultipleOptionsDialog(text,(Map)InlineMap.of(btnText,0),false,false,true,true,10,60);
	}
	
	public static int showMultipleOptionsDialog(String text, Map<String, Integer> buttons, 
			boolean allButtonsSameWidth, boolean verticalAlignButtons, boolean closable, boolean alwaysOnTop,
			int marginBetweenButtons, int textRightLeftPaddings) {
		exit = -2;
		try {
			Font font = Interface.ralewayBold.deriveFont(12f);
			
			int buttonsHeight = 23;
			if (verticalAlignButtons) {
				buttonsHeight = (buttons.size()*23)+((buttons.size()-1)*marginBetweenButtons);
			}
			if(buttons == null || buttons.size() == 0) {
				buttonsHeight = 0;
			}
			
			JMultilineLabel label = new JMultilineLabel(text);
			label.setForeground(Interface.polaText);
			label.setFont(font);
			Dimension d = getPrefferedJLabelSize(font,text,label);
			
			int ls = 0;
			if(verticalAlignButtons) {
				for(String s : buttons.keySet()) {
					ls = Math.max(ls, (int) getPrefferedJLabelSize(font,s,label).getWidth());
				}
			}
			
			int w = (int)d.getWidth();
			w = Math.max(w, ls);
			
			int h = (int)d.getHeight();
			if(w < 210) {w = 160;}
			if(h < 17) {h = 17;}
			label.setBounds(10,11,w,h);
			
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			JFrame frame = new JFrame();
			frame.setTitle("EndiMc Dialog");
			List<Image> icons = new ArrayList<Image>();
			icons.add(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png")));
			frame.setIconImages(icons);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width/2-(w+61)/2, screenSize.height/2-(h+114)/2, w+61, h+91+buttonsHeight);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setAlwaysOnTop(alwaysOnTop);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			FrameDragListener frameDragListener = new FrameDragListener(frame);
			frame.addMouseListener(frameDragListener);
			frame.addMouseMotionListener(frameDragListener);

			JLabel label_2 = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
			label_2.setBounds(21, 16, 20, 20);
			frame.getContentPane().add(label_2);

			JLabel lblInterface = new JLabel(Interface.name+" Launcher");
			lblInterface.setBounds(49, 15, 162, 19);
			lblInterface.setFont(Interface.ralewayBold.deriveFont(12f));
			lblInterface.setForeground(Interface.polaText);
			frame.getContentPane().add(lblInterface);

			ImageIcon Xnormal = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x.png")));
			ImageIcon Xclicked = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_clicked.png")));
			ImageIcon Xrollover = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_rollover.png")));
			JButton close = new JButton(Xnormal);
			close.setDisabledIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_disabled.png"))));
			close.addMouseListener(new NavButtonMouseListener(close, Xnormal, Xrollover, Xclicked));
			close.setBounds(w, 16, 38, 13);
			frame.getContentPane().add(close);
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					exit = -1;
				}
			});
			close.setContentAreaFilled(false);
			close.setBorder(null);
			close.setEnabled(closable);

			JPanel panel = new JPanel();
			panel.setBounds(21, 38, w+20, h+33+buttonsHeight);
			panel.setBackground(new Color(24, 24, 24));

			frame.getContentPane().add(panel);
			panel.setLayout(null);
			
			panel.add(label);
			/*panel.setBorder(BorderFactory.createLineBorder(Color.RED));
			label.setBorder(BorderFactory.createLineBorder(Color.RED));*/
			
			@SuppressWarnings("deprecation")
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(Interface.ralewayBold.deriveFont(14f));
			
			if (verticalAlignButtons) {
				int biggestButtonSize = 0;
				if (allButtonsSameWidth) {
					for (String btext : buttons.keySet()) {
						biggestButtonSize = Math.max(biggestButtonSize, metrics.stringWidth(btext) + textRightLeftPaddings);
					}
				}
				
				int yoffset = panel.getHeight() - buttonsHeight - 30;
				
				for (String btext : buttons.keySet()) {
					JButton btnOk = new JButton(btext);
					btnOk.addActionListener(new ActionListener() {
						int exval = buttons.get(btext);;
						public void actionPerformed(ActionEvent arg0) {
							exit = exval;
						}
					});

					if (allButtonsSameWidth) {
						int y = yoffset + 23;
						btnOk.setBounds((panel.getWidth() - biggestButtonSize) / 2, y, biggestButtonSize, 23);
						yoffset = y + marginBetweenButtons;
					} else {
						int bw = metrics.stringWidth(btext) + textRightLeftPaddings;
						int y = yoffset + 23;
						btnOk.setBounds((panel.getWidth() - bw) / 2, y, bw, 23);
						yoffset = y + marginBetweenButtons;
					}
					
					btnOk.setContentAreaFilled(false);
					btnOk.setBackground(Interface.buttonsBg);
					btnOk.setForeground(Interface.buttonsText);
					btnOk.setOpaque(true);
					btnOk.setBorder(null);
					btnOk.setFocusable(false);
					btnOk.addMouseListener(new ButtonMouseListener(btnOk, Interface.buttonsBg));
					btnOk.setFont(Interface.ralewayBold.deriveFont(14f));
					panel.add(btnOk);
				}
			} else {
				// obliczanie sumy długości wszystkich przycisków, och paddingów i marginesów.
				int buttonsWidth = 0;
				int biggestButtonSize = 0; // ONLY FOR "allButtonsSameWidth=true"
				if (allButtonsSameWidth) {
					for (String btext : buttons.keySet()) {
						biggestButtonSize = Math.max(biggestButtonSize, metrics.stringWidth(btext) + textRightLeftPaddings);
					}
					buttonsWidth = (biggestButtonSize * buttons.size()) + (marginBetweenButtons * (buttons.size() - 1));
				} else {
					for (String btext : buttons.keySet()) {
						buttonsWidth += metrics.stringWidth(btext) + marginBetweenButtons + textRightLeftPaddings;
					}
				}
				buttonsWidth -= marginBetweenButtons;

				int boffset = 0;
				for (String btext : buttons.keySet()) {
					JButton btnOk = new JButton(btext);
					btnOk.addActionListener(new ActionListener() {
						int exval = buttons.get(btext);;
						public void actionPerformed(ActionEvent arg0) {
							exit = exval;
						}
					});

					if (allButtonsSameWidth) {
						int bw = biggestButtonSize;
						btnOk.setBounds((panel.getWidth() - buttonsWidth) / 2 + boffset, panel.getHeight() - 23 - 10, bw, 23);
						boffset += bw + marginBetweenButtons;
					} else {
						int bw = metrics.stringWidth(btext) + textRightLeftPaddings;
						btnOk.setBounds((panel.getWidth() - buttonsWidth) / 2 + boffset, panel.getHeight() - 23 - 10, bw, 23);
						boffset += bw + marginBetweenButtons;
					}
					btnOk.setContentAreaFilled(false);
					btnOk.setBackground(Interface.buttonsBg);
					btnOk.setForeground(Interface.buttonsText);
					btnOk.setOpaque(true);
					btnOk.setBorder(null);
					btnOk.setFocusable(false);
					btnOk.addMouseListener(new ButtonMouseListener(btnOk, Interface.buttonsBg));
					btnOk.setFont(Interface.ralewayBold.deriveFont(14f));
					panel.add(btnOk);
				}
			}

			JLabel lblNewLabel = new JLabel(new ImageIcon(dialogRamkaGenerator(w+61, h+91+buttonsHeight)));
			lblNewLabel.setBounds(0, 0, w+61, h+91+buttonsHeight);
			frame.getContentPane().add(lblNewLabel);

			Themes.recheckElement(label_2);
			Themes.recheckElement(close);
			
			frame.setVisible(true);

			while (!Thread.interrupted() && exit == -2) {
				try {Thread.sleep(100);} catch (InterruptedException e) {}
			}

			frame.setVisible(false);
			frame.dispose();
			frame = null;
			System.gc();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return exit;
	}
	
	public static void exit(int ex) {
		exit = ex;
	}
	
	public static int countMatches(String text, String str) {
		return text.split(str, -1).length - 1;
	}
	
	public static String getLongestString(String[] array) {
		int maxLength = 0;
		String longestString = null;
		for (String s : array) {
			if (s.length() > maxLength) {
				maxLength = s.length();
				longestString = s;
			}
		}
		return longestString;
	}
	
	public static Dimension getPrefferedJLabelSize(Font font, String text, Component j) {
		FontMetrics metrics = j.getFontMetrics(font);
		int textW = (int) j.getPreferredSize().getWidth();
		int textH = (countMatches(text,"\n")+1)*metrics.getHeight();
		return new Dimension(textW,textH);
	}
	
	public static Dimension getPrefferedJLabelSize(Font font, String text, JMultilineLabel j) {
		FontMetrics metrics = j.getFontMetrics(font);
		//int textW = metrics.stringWidth(Utils.removeHTMLTagsFromString(getLongestString(text.split("\n"))));
		int textW = j.getPreferredScrollableViewportSize().width+10;
		int textH = j.getPreferredScrollableViewportSize().height+10;
		return new Dimension(textW,textH);
	}

	public static BufferedImage dialogRamkaGenerator(int w, int h) {
		try {
			BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) out.getGraphics();

			Class<?> cl = Interface.class;
			BufferedImage tl = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/tl.png"));
			BufferedImage tr = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/tr.png"));
			BufferedImage bl = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/bl.png"));
			BufferedImage br = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/br.png"));
			BufferedImage top = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/top.png"));
			BufferedImage left = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/left.png"));
			BufferedImage bottom = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/bottom.png"));
			BufferedImage right = ImageIO.read(cl.getResourceAsStream("/files/images/dialogRamka/right.png"));

			g2d.drawImage(tl, 0, 0, null);
			g2d.drawImage(tr, out.getWidth()-tr.getWidth(), 0, null);
			g2d.drawImage(bl, 0, out.getHeight()-tr.getHeight(), null);
			g2d.drawImage(br, out.getWidth()-br.getWidth(), out.getHeight()-br.getHeight(), null);
			
			int topSpace = out.getWidth()-(tl.getWidth()+tr.getWidth());
			for(int i = 0; i < topSpace; i++) {
				g2d.drawImage(top, tl.getWidth()+i, 0, null);
			}
			
			int bottomSpace = out.getWidth()-(bl.getWidth()+br.getWidth());
			for(int i = 0; i < bottomSpace; i++) {
				g2d.drawImage(bottom, bl.getWidth()+i, out.getHeight()-bottom.getHeight()-3, null);
			}
			
			int leftSpace = out.getHeight()-(tl.getHeight()+bl.getHeight());
			for(int i = 0; i < leftSpace; i++) {
				g2d.drawImage(left, 6, tl.getHeight()+i, null);
			}
			
			int rightSpace = out.getHeight()-(tr.getHeight()+br.getHeight());
			for(int i = 0; i < rightSpace; i++) {
				g2d.drawImage(right, out.getWidth()-right.getWidth()-6, tr.getHeight()+i, null);
			}
			
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
