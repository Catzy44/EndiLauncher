package me.catzy44.ui.creators;

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
import me.catzy44.ui.PlaceholderTextField;
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

	private static void startLoginProcess(Type p) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String profileName = login.getText();
				if(profileName == null || profileName.length() == 0) {
					new Thread(()->{
						DialogUtil.showConfirmDialog("<font color=red>Musisz wpisać nazwę!</font>");
					}).start();
					return;
				}
				if (p == Type.MICROSOFT) {
					show(false);
					MicrosoftLoginUI login = new MicrosoftLoginUI();
					login.addObserver("status", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (evt.getNewValue() == Microsoft.Status.SUCCESS) {
								finished(new UserProfile(profileName,login.getProfile(), null));
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
								finished(new UserProfile(profileName,login.getProfile(), null));
							} else {
								show(true);
							}
						}
					});
					login.open();
				} else {
					finished(new UserProfile(profileName,new MinecraftProfile(profileName, "uuid", "token", Type.NONPREMIUM), null));
				}

			}
		}).start();
	}

	private static void show(boolean b) {
		frame.setVisible(b);
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
			int h = 330;
			String title = "Kreator dodawania Profilu";
			Font buttons = Interface.ralewayBold.deriveFont(15f);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame = new JFrame();
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			// frame.setAlwaysOnTop(true);

			FrameDragListener frameDragListener = new FrameDragListener(frame);
			frame.addMouseListener(frameDragListener);
			frame.addMouseMotionListener(frameDragListener);

			JLabel label_2 = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
			label_2.setBounds(21, 16, 20, 20);
			frame.getContentPane().add(label_2);

			JLabel lblEndimcLauncher = new JLabel(title);
			lblEndimcLauncher.setHorizontalAlignment(SwingConstants.CENTER);
			lblEndimcLauncher.setBounds(67, 15, 278, 19);
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

			JMultilineLabel lblLogowanieDoKonta = new JMultilineLabel("Wybierz nazwę i typ nowego profilu.\nNazwa nie ma znaczenia, po niej rozpoznasz profil na liście.");
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
			btnZapomnialem.setBounds(219, 362, 141, 13);
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
			btnRegister.setBounds(10, 362, 167, 13);
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
			panel_1.setBounds(10, 207, 350, 56);
			panel.add(panel_1);
			panel_1.setLayout(null);
			panel_1.setOpaque(false);
			panel_1.setUI(new TransparentPanelFixUI());
			panel_1.setBackground(new Color(0, 0, 0, 0));
			panel_1.setBorder(BorderFactory.createLineBorder(Interface.borders));

			JButton nonpresmium = new JButton("MICROSOFT");
			nonpresmium.setBounds(10, 11, 140, 32);
			panel_1.add(nonpresmium);
			nonpresmium.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MICROSOFT);
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

			JLabel lblNewLabel_1 = new JLabel("Mam konto Microsoft");
			lblNewLabel_1.setBounds(160, 11, 180, 32);
			panel_1.add(lblNewLabel_1);
			lblNewLabel_1.setForeground(Interface.polaText);
			lblNewLabel_1.setFont(Interface.ralewayMedium.deriveFont(12f));

			JPanel panel_1_1 = new JPanel();
			panel_1_1.setLayout(null);
			panel_1_1.setOpaque(false);
			panel_1_1.setBorder(BorderFactory.createLineBorder(Interface.borders));
			panel_1_1.setBackground(new Color(0, 0, 0, 0));
			panel_1_1.setBounds(10, 140, 350, 56);
			panel.add(panel_1_1);

			JButton nonpresmium_1 = new JButton("MOJANG");
			nonpresmium_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.MOJANG);
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
			nonpresmium_1.setBounds(10, 11, 140, 32);
			panel_1_1.add(nonpresmium_1);

			JLabel lblNewLabel_1_1 = new JLabel("Mam mail/login i hasło");
			lblNewLabel_1_1.setForeground(new Color(27, 111, 148));
			lblNewLabel_1_1.setFont(null);
			lblNewLabel_1_1.setBounds(160, 11, 180, 32);
			lblNewLabel_1_1.setFont(Interface.ralewayMedium.deriveFont(12f));
			panel_1_1.add(lblNewLabel_1_1);

			JPanel panel_1_2 = new JPanel();
			panel_1_2.setLayout(null);
			panel_1_2.setOpaque(false);
			panel_1_2.setBorder(BorderFactory.createLineBorder(Interface.borders));
			panel_1_2.setBackground(new Color(0, 0, 0, 0));
			panel_1_2.setBounds(10, 274, 350, 56);
			panel.add(panel_1_2);

			JButton nonpresmium_2 = new JButton("NONPREMIUM");
			nonpresmium_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startLoginProcess(Type.NONPREMIUM);
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
			nonpresmium_2.setBounds(10, 11, 140, 32);
			panel_1_2.add(nonpresmium_2);

			JLabel lblNewLabel_1_2 = new JLabel("Nie mam konta, wybieram Nick");
			lblNewLabel_1_2.setForeground(new Color(27, 111, 148));
			lblNewLabel_1_2.setBounds(160, 11, 180, 32);
			lblNewLabel_1_2.setFont(Interface.ralewayMedium.deriveFont(12f));
			panel_1_2.add(lblNewLabel_1_2);

			JPanel panel_1_1_1 = new JPanel();
			panel_1_1_1.setLayout(null);
			panel_1_1_1.setOpaque(false);
			panel_1_1_1.setBorder(BorderFactory.createLineBorder(Interface.borders));
			panel_1_1_1.setBackground(new Color(0, 0, 0, 0));
			panel_1_1_1.setBounds(10, 81, 350, 48);
			panel.add(panel_1_1_1);

			login = new PlaceholderTextField("");
			login.setText((String) null);
			login.setPlaceholder("Nazwa...");
			login.setForeground(new Color(27, 111, 148));
			login.setColumns(10);
			login.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, Interface.borders));
			login.setBackground(new Color(34, 34, 34));
			login.setBounds(10, 11, 168, 25);
			panel_1_1_1.add(login);

			JLabel lblNewLabel_2 = new JLabel("Max 12 znaków");
			lblNewLabel_2.setBounds(188, 11, 152, 25);
			lblNewLabel_2.setFont(Interface.ralewayMedium.deriveFont(12f));
			lblNewLabel_2.setForeground(Interface.polaText);
			panel_1_1_1.add(lblNewLabel_2);

			JLabel lblNewLabel = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(w + 61, h + 114)));
			lblNewLabel.setBounds(0, 0, w + 61, h + 114);
			frame.getContentPane().add(lblNewLabel);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private static PlaceholderTextField login;

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "UAC";
	}
}
