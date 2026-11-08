package me.catzy44.ui.creators.account;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import me.catzy44.ui.JMultilineLabel;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.user.UserProfile;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.Locker;
import me.catzy44.utils.UIUtils;
import me.catzy44.utils.Utils;

public class UserAccountCreator {
	private static JFrame frame;

	public static void showInterface() {
		// This wizard is required on first start. Reset the lock so a later
		// invocation cannot accidentally pass through because of an earlier login.
		locker.lock();
		build();
		show(true);

		locker.waitForUnlock();

	}

	private static void finished(UserProfile up) {
		if (up == null) {
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

		Interface.switchCard("konta");

		locker.unlock();
	}

	private static Locker locker = new Locker();

	/**
	 * Re-authenticates an existing launcher profile. Its account type is changed
	 * only after the selected authentication flow reports success.
	 */
	public static void changeExistingProfile(UserProfile profile, Type type) {
		if (profile == null || profile.getAk().getType() == type) {
			return;
		}
		if (type == Type.NONPREMIUM) {
			NonPremiumLoginUI.open(Interface.getFrame(), nickname -> applyAccount(profile,
					new MinecraftProfile(nickname, "uuid", "token", Type.NONPREMIUM)));
			return;
		}

		if (type == Type.MICROSOFT) {
			MicrosoftLoginUI login = new MicrosoftLoginUI();
			login.addObserver("status", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue() == Microsoft.Status.SUCCESS) {
						applyAccount(profile, login.getProfile());
					}
				}
			});
			login.open();
			return;
		}

		MojangLoginUI login = new MojangLoginUI();
		login.addObserver("status", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == MojangLoginUI.Status.SUCCESS) {
					applyAccount(profile, login.getProfile());
				}
			}
		});
		login.open();
	}

	private static void applyAccount(UserProfile profile, MinecraftProfile account) {
		if (account == null) {
			return;
		}
		profile.setAk(account);
		profile.resetSkin();
		Interface.setActiveUserProfile(profile);
		profile.getSkin().refreshAsync();
	}

	private static void startLoginProcess(Type p, String nonPremiumNickname) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (p == Type.MICROSOFT) {
					show(false);
					MicrosoftLoginUI login = new MicrosoftLoginUI();
					login.addObserver("status", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (evt.getNewValue() == Microsoft.Status.SUCCESS) {
								finished(new UserProfile(login.getProfile().getNickname(), login.getProfile(), null));
							} else {
								show(true);
							}
						}
					});
					login.open();
				} else if (p == Type.MOJANG) {
					show(false);
					MojangLoginUI login = new MojangLoginUI();
					login.addObserver("status", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (evt.getNewValue() == MojangLoginUI.Status.SUCCESS) {
								finished(new UserProfile(login.getProfile().getNickname(), login.getProfile(), null));
							} else {
								show(true);
							}
						}
					});
					login.open();
				} else {
					finished(new UserProfile(nonPremiumNickname,
							new MinecraftProfile(nonPremiumNickname, "uuid", "token", Type.NONPREMIUM), null));
				}

			}
		}).start();
	}

	private static void show(boolean b) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			if (frame == null || !frame.isDisplayable() && !b)
				return;
			frame.setVisible(b);
			if (b) {
				frame.toFront();
				frame.requestFocus();
			}
		});
	}

	public static void Xmain(String args[]) {
		Interface.preInit();
		showInterface();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private static void build() {
		try {
			int w = 350;
			int h = 180;
			String title = "Kreator dodawania Profilu";
			Font buttons = Interface.ralewayBold.deriveFont(15f);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame = new JFrame();
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
			// Closing the account selector exits the launcher, including via Alt+F4.
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			// frame.setAlwaysOnTop(true);

			FrameDragListener frameDragListener = new FrameDragListener(frame);
			frame.addMouseListener(frameDragListener);
			frame.addMouseMotionListener(frameDragListener);

			JLabel label_2 = new JLabel(
					new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
			label_2.setBounds(21, 16, 20, 20);
			frame.getContentPane().add(label_2);

			JLabel lblEndimcLauncher = new JLabel(title);
			lblEndimcLauncher.setHorizontalAlignment(SwingConstants.CENTER);
			lblEndimcLauncher.setBounds(67, 15, 278, 19);
			lblEndimcLauncher.setFont(Interface.ralewayBold.deriveFont(12f));
			lblEndimcLauncher.setForeground(Interface.polaText);
			frame.getContentPane().add(lblEndimcLauncher);

			ImageIcon Xnormal = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x.png")));
			ImageIcon Xclicked = new ImageIcon(
					ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_clicked.png")));
			ImageIcon Xrollover = new ImageIcon(
					ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_rollover.png")));
			JButton close = new JButton(Xnormal);
			close.setDisabledIcon(
					new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_disabled.png"))));
			close.setBounds(w, 16, 38, 13);
			close.addMouseListener(new NavButtonMouseListener(close, Xnormal, Xrollover, Xclicked));
			close.setToolTipText("Zamknij launcher");
			close.addActionListener(e -> frame
					.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING)));
			frame.getContentPane().add(close);
			close.setContentAreaFilled(false);
			close.setBorder(null);

			JPanel panel = new JPanel();
			panel.setBounds(21, 38, w + 20, h + 56);
			panel.setBackground(new Color(24, 24, 24));
			panel.setUI(new TransparentPanelFixUI());

			frame.getContentPane().add(panel);
			panel.setLayout(null);

			JMultilineLabel lblLogowanieDoKonta = new JMultilineLabel(
					"Wybierz sposób logowania.\nNazwa profilu zostanie pobrana z konta Minecraft.");
			lblLogowanieDoKonta.setText(
					"<html align=\"center\" style=\"white-space: nowrap\">\r\n  <head>\r\n    \r\n  </head>\r\n  <body>\r\n    Wybierz rodzaj konta<br>Jeżeli nie masz kupionej gry wybierz brak konta.\r\n  </body>\r\n</html>\r\n");
			lblLogowanieDoKonta.setBounds(10, 11, 350, 48);
			lblLogowanieDoKonta.setForeground(Interface.polaText);
			lblLogowanieDoKonta.setFont(Interface.ralewayBold.deriveFont(12f));
			lblLogowanieDoKonta.setAlignmentY(JLabel.TOP_ALIGNMENT);
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
			btnZapomnialem.setBounds(219, 212, 141, 13);
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

			JButton btnRegister = new JButton("Kupowanie gry");
			btnRegister.setHorizontalAlignment(SwingConstants.LEFT);
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Utils.openUrl("https://www.xbox.com/en-us/games/store/Minecraft-for-Windows/9NBLGGH2JHXJ");
				}
			});
			btnRegister.setBounds(10, 212, 167, 13);
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

			JButton nonpresmium = new JButton("KONTO MICROSOFT");
			nonpresmium.setBounds(20, 70, 330, 32);
			panel.add(nonpresmium);
			nonpresmium.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MICROSOFT, null);
				}
			});
			nonpresmium.setContentAreaFilled(false);
			nonpresmium.setBackground(Interface.buttonsBg);
			nonpresmium.setForeground(Interface.buttonsText);
			nonpresmium.setOpaque(true);
			nonpresmium.setBorder(null);
			nonpresmium.setFocusable(false);
			nonpresmium.addMouseListener(new ButtonMouseListener(nonpresmium, Interface.buttonsBg));
			nonpresmium.setFont(buttons);

			JButton nonpresmium_1 = new JButton("KONTO MOJANG");
			nonpresmium_1.setBounds(20, 113, 330, 32);
			panel.add(nonpresmium_1);
			nonpresmium_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MOJANG, null);
				}
			});
			nonpresmium_1.setContentAreaFilled(false);
			nonpresmium_1.setBackground(Interface.buttonsBg);
			nonpresmium_1.setForeground(Interface.buttonsText);
			nonpresmium_1.setOpaque(true);
			nonpresmium_1.setBorder(null);
			nonpresmium_1.setFocusable(false);
			nonpresmium_1.addMouseListener(new ButtonMouseListener(nonpresmium_1, Interface.buttonsBg));
			nonpresmium_1.setFont(buttons);

			JButton nonpresmium_2 = new JButton("BRAK KONTA");
			nonpresmium_2.setBounds(20, 156, 330, 32);
			panel.add(nonpresmium_2);
			nonpresmium_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Hide synchronously on the EDT before opening the modal prompt.
					frame.setVisible(false);
					boolean[] confirmed = { false };
					try {
						NonPremiumLoginUI.open(frame, nickname -> {
							confirmed[0] = true;
							startLoginProcess(Type.NONPREMIUM, nickname);
						});
					} finally {
						if (!confirmed[0])
							show(true);
					}
				}
			});
			nonpresmium_2.setContentAreaFilled(false);
			nonpresmium_2.setBackground(Interface.buttonsBg);
			nonpresmium_2.setForeground(Interface.buttonsText);
			nonpresmium_2.setOpaque(true);
			nonpresmium_2.setBorder(null);
			nonpresmium_2.setFocusable(false);
			nonpresmium_2.addMouseListener(new ButtonMouseListener(nonpresmium_2, Interface.buttonsBg));
			nonpresmium_2.setFont(buttons);

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
	}
}
