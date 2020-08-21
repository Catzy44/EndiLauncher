package me.catzy44.ui.creators;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import me.catzy44.Interface;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.Utils;

public class LauncherUpdateUI {
	private static JFrame frame;
	private static JProgressBar progressBar_1;
	
	public static void showInterface() {
		build();
		show(true);
	}
	
	private static void show(boolean b) {
		frame.setVisible(b);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private static void build() {
		try {
			int w = 290;
			int h = 40;
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame = new JFrame();
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			//frame.setAlwaysOnTop(true);

			FrameDragListener frameDragListener = new FrameDragListener(frame);
			frame.addMouseListener(frameDragListener);
			frame.addMouseMotionListener(frameDragListener);

			JLabel label_2 = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
			label_2.setBounds(21, 16, 20, 20);
			frame.getContentPane().add(label_2);

			JLabel lblEndimcLauncher = new JLabel("Endi Launcher - Aktualizacja");
			lblEndimcLauncher.setBounds(49, 15, 334, 19);
			lblEndimcLauncher.setFont(Interface.ralewayBold.deriveFont(12f));
			lblEndimcLauncher.setForeground(Interface.polaText);
			frame.getContentPane().add(lblEndimcLauncher);

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
					show(false);
				}
			});
			close.setContentAreaFilled(false);
			close.setBorder(null);

			JPanel panel = new JPanel();
			panel.setBounds(21, 38, w + 20, h + 56);
			panel.setBackground(new Color(24, 24, 24));
			panel.setUI(new TransparentPanelFixUI());

			frame.getContentPane().add(panel);
			panel.setLayout(null);

			JLabel lblLogowanieDoKonta = new JLabel("Aktualizowanie...", SwingConstants.CENTER);
			lblLogowanieDoKonta.setBounds(10, 11, 276, 25);
			lblLogowanieDoKonta.setForeground(Interface.polaText);
			lblLogowanieDoKonta.setFont(Interface.ralewayBold.deriveFont(12f));
			panel.add(lblLogowanieDoKonta);

			JButton btnZapomnialem = new JButton("Nie wiesz który wybrać?");
			btnZapomnialem.setHorizontalAlignment(SwingConstants.RIGHT);
			btnZapomnialem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// DialogUtil.showConfirmDialog("Ta funkcja jeszcze nie działa.\nNapisz do
							// administracji.\n");
							Utils.openUrl("https://www.minecraft.net/pl-pl/password/forgot");
						}
					}).start();
				}
			});
			btnZapomnialem.setBounds(277, 252, 141, 13);
			panel.add(btnZapomnialem);
			btnZapomnialem.setContentAreaFilled(false);
			// btnZapomnialem.setBackground(new Color(0,0,0,0));
			btnZapomnialem.setForeground(Interface.polaText);
			// btnZapomnialem.setOpaque(true);
			btnZapomnialem.setBorder(null);
			btnZapomnialem.setFocusable(false);
			btnZapomnialem.addMouseListener(new AButtonMouseListener(btnZapomnialem));
			btnZapomnialem.setFont(Interface.ralewayBold.deriveFont(11f));
			btnZapomnialem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			progressBar_1 = new JProgressBar();
			progressBar_1.setBounds(10, 68, 290, 17);
			panel.add(progressBar_1);
			progressBar_1.setBackground(Interface.polaTlo);
			progressBar_1.setForeground(Interface.textColor);
			progressBar_1.setBorder(BorderFactory.createEmptyBorder());
			progressBar_1.setBorderPainted(false);

			JLabel lblNewLabel = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(w + 61, h + 114)));
			lblNewLabel.setBounds(0, 0, w + 61, h + 114);
			frame.getContentPane().add(lblNewLabel);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static JProgressBar getProgressBar() {
		return progressBar_1;
	}
	
	
}
