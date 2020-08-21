package me.catzy44.ui.creators;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import me.catzy44.Interface;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.struct.accounts.Mojang;
import me.catzy44.ui.PlaceholderPasswordField;
import me.catzy44.ui.PlaceholderTextField;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.UIUtils;
import me.catzy44.utils.Utils;

public class MojangLoginUI {
	private MinecraftProfile ak;
	
	public enum Status {
		SUCCESS,FAILED
	}
	
	private void startLogging() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (login.getText().isEmpty()) {
					DialogUtil.showConfirmDialog("Musisz podać login!\n");
					return;
				}

				if (login.getText().length() > 80) {
					DialogUtil.showConfirmDialog("Login jest za długi.\nMax liczba znaków wynosi 80.");
					return;
				}

				if (password.getText().isEmpty()) {
					DialogUtil.showConfirmDialog("Musisz podać hasło!\n");
					return;
				}
				zaloguj.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						ak = Mojang.authenticate(login.getText(), password.getText());
						zaloguj.setEnabled(true);
						if (ak.getStatus() == MinecraftProfile.Status.INVALID_CREDITIENALS) {
							DialogUtil.showConfirmDialog("Nieprawidłowa nazwa użytkownika lub hasło.");
							return;
						} else if (ak.getStatus() == MinecraftProfile.Status.NETWORK_ERR) {
							DialogUtil.showConfirmDialog("Błąd podczas łaczenia z Mojang.\nUpewnij się że posiadasz połaczenie z internetem.");
							return;
						} else if (ak.getStatus() == MinecraftProfile.Status.UNKNW_ERR) {
							DialogUtil.showConfirmDialog("Nieznany błąd.\nUpewnij się że posiadasz połaczenie z internetem.\nJeżeli problem nie ustąpi skontaktuj się z administracją.");
							return;
						} else if (ak.getStatus() == MinecraftProfile.Status.UNKNW) {
							DialogUtil.showConfirmDialog("Nieznany błąd.\nUpewnij się że posiadasz połaczenie z internetem.\nJeżeli problem nie ustąpi skontaktuj się z administracją.");
							return;
						}

						/*UserProfileManager.getActiveProfile().setAk(ak);
						Interface.setActiveUserProfile(UserProfileManager.getActiveProfile());
						UserProfileManager.getActiveProfile().getSkin().refreshAsync();*/
						pcs.firePropertyChange("status",Status.FAILED,Status.SUCCESS);
						close();
					}
				}).start();
			}
		}).start();
	}
	
	private PlaceholderTextField login;
	private PlaceholderPasswordField password;
	private JButton zaloguj;
	private JFrame frame;
	
	private void close() {
		frame.setVisible(false);
		frame = null;
	}

	public MinecraftProfile getProfile() {
		return ak;
	}
	/**
	 * @wbp.parser.entryPoint
	 */

	public void open() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {

					int w = 240;
					int h = 140;

					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame = new JFrame();
					frame.setUndecorated(true);
					frame.setBackground(new Color(0, 0, 0, 0));
					frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().setLayout(null);
					frame.setUndecorated(true);
					frame.setBackground(new Color(0, 0, 0, 0));
					frame.setAlwaysOnTop(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

					FrameDragListener frameDragListener = new FrameDragListener(frame);
					frame.addMouseListener(frameDragListener);
					frame.addMouseMotionListener(frameDragListener);

					JLabel label_2 = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
					label_2.setBounds(21, 16, 20, 20);
					frame.getContentPane().add(label_2);

					JLabel lblEndimcLauncher = new JLabel(Interface.name + " Launcher");
					lblEndimcLauncher.setBounds(49, 15, 162, 19);
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
							close();
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

					JLabel lblLogowanieDoKonta = new JLabel("Logowanie do konta Mojang", SwingConstants.CENTER);
					lblLogowanieDoKonta.setBounds(10, 11, 240, 25);
					lblLogowanieDoKonta.setForeground(Interface.polaText);
					lblLogowanieDoKonta.setFont(Interface.ralewayBold.deriveFont(12f));
					panel.add(lblLogowanieDoKonta);

					login = new PlaceholderTextField("");
					login.setPlaceholder("Email lub nick...");
					login.setBounds(10, 56, 239, 25);
					panel.add(login);
					login.setColumns(10);
					login.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, Interface.borders));
					login.setBackground(Interface.polaTlo);
					login.setForeground(Interface.polaText);

					password = new PlaceholderPasswordField("");
					password.setPlaceholder("Hasło...");
					password.setBounds(10, 92, 239, 25);
					panel.add(password);
					password.setColumns(10);
					password.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, Interface.borders));
					password.setBackground(Interface.polaTlo);
					password.setForeground(Interface.polaText);

					JButton btnZapomnialem = new JButton("Zapomniałem hasła");
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
					btnZapomnialem.setBounds(109, 162, 140, 13);
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

					JButton btnRegister = new JButton("Zakup grę");
					btnRegister.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							Utils.openUrl("https://www.xbox.com/en-us/games/store/Minecraft-for-Windows/9NBLGGH2JHXJ");
						}
					});
					btnRegister.setBounds(10, 162, 89, 13);
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

					zaloguj = new JButton("Zaloguj");
					zaloguj.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							startLogging();
						}
					});
					zaloguj.setBounds(10, 128, 239, 23);
					panel.add(zaloguj);
					zaloguj.setContentAreaFilled(false);
					zaloguj.setBackground(Interface.buttonsBg);
					zaloguj.setForeground(Interface.buttonsText);
					zaloguj.setOpaque(true);
					zaloguj.setBorder(null);
					zaloguj.setFocusable(false);
					zaloguj.addMouseListener(new ButtonMouseListener(zaloguj, Interface.buttonsBg));
					zaloguj.setFont(Interface.ralewayBold.deriveFont(14f));
					frame.getRootPane().setDefaultButton(zaloguj);

					JLabel lblNewLabel = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(w + 61, h + 114)));
					lblNewLabel.setBounds(0, 0, w + 61, h + 114);
					frame.getContentPane().add(lblNewLabel);

					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "MojangUI";
	};
}
