package me.catzy44.ui.creators;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import me.catzy44.Interface;
import me.catzy44.struct.accounts.Microsoft;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.struct.accounts.MinecraftProfile.Type;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.user.UserProfile;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.Locker;
import me.catzy44.utils.Utils;

public class UserAccountCreator_OLD {
	private static JFrame frame;
	
	public static void showInterface() {
		build();
		show(true);

		locker.waitForUnlock();

	}

	private static void finished(UserProfile up) {
		if(up == null) {
			System.out.println("finished up=null");
			show(true);
			return;
		}
		show(false);
		frame.dispose();
		
		UserProfileManager.getProfiles().add(up);
		UserProfileManager.setActiveProfile(up);
		
		Interface.setActiveUserProfile(UserProfileManager.getActiveProfile());
		UserProfileManager.getActiveProfile().getSkin().refreshAsync();
		
		locker.unlock();
	}
	
	private static Locker locker = new Locker();
	
	private static void startLoginProcess(Type p) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(p == Type.MICROSOFT) {
					show(false);
					MicrosoftLoginUI login = new MicrosoftLoginUI();
					login.addObserver("status", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if(evt.getNewValue() == Microsoft.Status.SUCCESS) {
								finished(new UserProfile(null,login.getProfile(),null));
							} else {
								show(true);
							}
						}
					});
					login.open();
				} else if(p == Type.MOJANG) {
					show(false);
					MojangLoginUI login = new MojangLoginUI();
					login.addObserver("status", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if(evt.getNewValue() == MojangLoginUI.Status.SUCCESS) {
								finished(new UserProfile(null,login.getProfile(),null));
							} else {
								show(true);
							}
						}
					});
					login.open();
				} else {
					finished(new UserProfile(null,new MinecraftProfile("Nickname","uuid","token",Type.NONPREMIUM),null));
				}
				
			}
		}).start();
	}
	
	private static void show(boolean b) {
		frame.setVisible(b);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private static void build() {
		try {
			int w = 420;
			int h = 220;
			String title = "- Kreator dodawania nowego Profilu";

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame = new JFrame();
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

			JLabel lblEndimcLauncher = new JLabel(Interface.name + " Launcher" + title);
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

			JLabel lblLogowanieDoKonta = new JLabel("Wybierz typ profilu", SwingConstants.CENTER);
			lblLogowanieDoKonta.setBounds(10, 11, 420, 25);
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

			JButton btnRegister = new JButton("Nie masz konta?");
			btnRegister.setHorizontalAlignment(SwingConstants.LEFT);
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Utils.openUrl("https://www.xbox.com/en-us/games/store/Minecraft-for-Windows/9NBLGGH2JHXJ");
				}
			});
			btnRegister.setBounds(21, 252, 167, 13);
			panel.add(btnRegister);
			btnRegister.setContentAreaFilled(false);
			// btnRegister.setBackground(new Color(0,0,0,0));
			btnRegister.setForeground(Interface.polaText);
			// btnRegister.setOpaque(true);
			btnRegister.setBorder(null);
			btnRegister.setFocusable(false);
			btnRegister.addMouseListener(new AButtonMouseListener(btnRegister));
			btnRegister.setFont(Interface.ralewayBold.deriveFont(11f));
			btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			JPanel panel_1 = new JPanel();
			panel_1.setBounds(10, 47, 133, 178);
			panel.add(panel_1);
			panel_1.setLayout(null);
			panel_1.setOpaque(false);
			panel_1.setUI(new TransparentPanelFixUI());
			panel_1.setBackground(new Color(0, 0, 0, 0));
			panel_1.setBorder(BorderFactory.createLineBorder(Interface.borders));

			JButton btnNewButton = new JButton("");
			btnNewButton.setBounds(10, 11, 112, 112);
			panel_1.add(btnNewButton);
			btnNewButton.setIcon(new ImageIcon(UserAccountCreator_OLD.class.getResource("/files/images/logos/mojang.png")));
			btnNewButton.setContentAreaFilled(false);
			btnNewButton.setOpaque(true);
			btnNewButton.setBorder(null);
			btnNewButton.setBackground(new Color(0, 0, 0, 0));
			btnNewButton.setFocusable(false);
			btnNewButton.addMouseListener(new ButtonMouseListener(btnNewButton, Interface.buttonsBg));
			btnNewButton.setFont(Interface.ralewayBold.deriveFont(14f));

			JButton zaloguj = new JButton("MOJANG");
			zaloguj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MOJANG);
				}
			});
			zaloguj.setBounds(10, 134, 112, 32);
			panel_1.add(zaloguj);
			zaloguj.setContentAreaFilled(false);
			zaloguj.setBackground(Interface.buttonsBg);
			zaloguj.setForeground(Interface.buttonsText);
			zaloguj.setOpaque(true);
			zaloguj.setBorder(null);
			zaloguj.setFocusable(false);
			zaloguj.addMouseListener(new ButtonMouseListener(zaloguj, Interface.buttonsBg));
			zaloguj.setFont(Interface.ralewayBold.deriveFont(18f));
			frame.getRootPane().setDefaultButton(zaloguj);
			
			JPanel panel_1_1 = new JPanel();
			panel_1_1.setLayout(null);
			panel_1_1.setOpaque(false);
			panel_1_1.setBorder(BorderFactory.createLineBorder(Interface.borders));
			panel_1_1.setBackground(new Color(0, 0, 0, 0));
			panel_1_1.setBounds(153, 47, 133, 178);
			panel.add(panel_1_1);
			
			JButton btnNewButton_1 = new JButton("");
			btnNewButton_1.setIcon(new ImageIcon(UserAccountCreator_OLD.class.getResource("/files/images/logos/microsoft.png")));
			btnNewButton_1.setOpaque(true);
			btnNewButton_1.setFont(null);
			btnNewButton_1.setFocusable(false);
			btnNewButton_1.setContentAreaFilled(false);
			btnNewButton_1.setBorder(null);
			btnNewButton_1.setBackground(new Color(0, 0, 0, 0));
			btnNewButton_1.setBounds(10, 11, 112, 112);
			panel_1_1.add(btnNewButton_1);
			
			JButton microsoft = new JButton("MICROSOFT");
			microsoft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MICROSOFT);
				}
			});
			microsoft.setContentAreaFilled(false);
			microsoft.setBackground(Interface.buttonsBg);
			microsoft.setForeground(Interface.buttonsText);
			microsoft.setOpaque(true);
			microsoft.setBorder(null);
			microsoft.setFocusable(false);
			microsoft.addMouseListener(new ButtonMouseListener(microsoft, Interface.buttonsBg));
			microsoft.setFont(Interface.ralewayBold.deriveFont(18f));
			microsoft.setBounds(10, 134, 112, 32);
			panel_1_1.add(microsoft);
			
			JPanel panel_1_1_1 = new JPanel();
			panel_1_1_1.setLayout(null);
			panel_1_1_1.setOpaque(false);
			panel_1_1_1.setBorder(BorderFactory.createLineBorder(Interface.borders));
			panel_1_1_1.setBackground(new Color(0, 0, 0, 0));
			panel_1_1_1.setBounds(296, 47, 133, 178);
			panel.add(panel_1_1_1);
			
			JButton btnNewButton_1_1 = new JButton("");
			btnNewButton_1_1.setIcon(new ImageIcon(UserAccountCreator_OLD.class.getResource("/files/images/logos/nonpremium.png")));
			btnNewButton_1_1.setOpaque(true);
			btnNewButton_1_1.setFont(null);
			btnNewButton_1_1.setFocusable(false);
			btnNewButton_1_1.setContentAreaFilled(false);
			btnNewButton_1_1.setBorder(null);
			btnNewButton_1_1.setBackground(new Color(0, 0, 0, 0));
			btnNewButton_1_1.setBounds(10, 11, 112, 112);
			panel_1_1_1.add(btnNewButton_1_1);
			
			JButton nonpresmium = new JButton("NONPREMIUM");
			nonpresmium.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.NONPREMIUM);
				}
			});
			nonpresmium.setContentAreaFilled(false);
			nonpresmium.setBackground(Interface.buttonsBg);
			nonpresmium.setForeground(Interface.buttonsText);
			nonpresmium.setOpaque(true);
			nonpresmium.setBorder(null);
			nonpresmium.setFocusable(false);
			nonpresmium.addMouseListener(new ButtonMouseListener(nonpresmium, Interface.buttonsBg));
			nonpresmium.setFont(Interface.ralewayBold.deriveFont(18f));
			nonpresmium.setBounds(10, 134, 112, 32);
			panel_1_1_1.add(nonpresmium);

			JLabel lblNewLabel = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(w + 61, h + 114)));
			lblNewLabel.setBounds(0, 0, w + 61, h + 114);
			frame.getContentPane().add(lblNewLabel);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "UAC";
	};
}
