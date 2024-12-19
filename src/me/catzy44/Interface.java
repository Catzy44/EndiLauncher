package me.catzy44;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jnafilechooser.api.JnaFileChooser;
import me.catzy44.game.FabricAPI;
import me.catzy44.game.ForgeVersionManager;
import me.catzy44.game.GameInstallation;
import me.catzy44.game.GameInstallationManager;
import me.catzy44.game.Sodium;
import me.catzy44.skin.SkinPreview;
import me.catzy44.struct.GameVersion;
import me.catzy44.struct.GameVersion.ModApi;
import me.catzy44.struct.accounts.Microsoft;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.struct.accounts.MinecraftProfile.Status;
import me.catzy44.struct.accounts.MinecraftProfile.Type;
import me.catzy44.tools.Download;
import me.catzy44.tools.Logger;
import me.catzy44.tools.Themes;
import me.catzy44.ui.CustomComboBoxEditor;
import me.catzy44.ui.CustomComboBoxEditorBig;
import me.catzy44.ui.CustomComboBoxRenderer;
import me.catzy44.ui.CustomComboBoxRendererBig;
import me.catzy44.ui.CustomComboBoxUI;
import me.catzy44.ui.CustomScrollbarUI;
import me.catzy44.ui.CustomSliderUI;
import me.catzy44.ui.JMultilineLabel;
import me.catzy44.ui.PlaceholderTextField;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.creators.GameVersionCreator;
import me.catzy44.ui.creators.MicrosoftLoginUI;
import me.catzy44.ui.creators.MojangLoginUI;
import me.catzy44.ui.creators.UserAccountCreator;
import me.catzy44.ui.creators.UserProfileNameChange;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.FrameDragListener;
import me.catzy44.ui.listeners.JSChangeListener;
import me.catzy44.ui.listeners.JTChangeListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.user.UserProfile;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.UIUtils;
import me.catzy44.utils.Utils;
import me.catzy44.utils.Version;

public class Interface {

	public static Color buttonsBg = new Color(27, 111, 148);
	public static Color buttonsText = new Color(255, 255, 255);
	public static Color borderColor = new Color(52, 56, 29);
	public static Color textColor = new Color(27, 111, 148);
	public static Color polaTlo = new Color(34, 34, 34);
	public static Color polaText = new Color(27, 111, 148);
	public static Color borders = new Color(40, 56, 70);
	public static Color panelsBgColor = new Color(25, 25, 25, 240);

	private static final Border textFieldsLeftPadding = UIUtils.createOffsetedLineBorder(0, 1, 0, 0, new Color(0, 0, 0, 0));

	public static Font ralewayBold;
	public static Font ralewayMedium;

	private static JFrame frame;
	private static Dimension screenSize = null;
	private static JSChangeListener js = null;
	private static JTChangeListener ct = null;
	private static JButton graj;
	private static JProgressBar progressDown;
	private static JProgressBar overallProgressBar;
	private static JLabel lblWersje;
	private static JPanel konta;
	private static JPanel kontener;
	private static JPanel aktualizacja;
	private static JLabel lblPobieraniePobrano;
	private static JLabel lblPobieranieCzas;
	private static JLabel overallProgressLblY;
	private static JLabel lblPobieraniePreskosc;
	private static JLabel overallProgressLbl;
	private static JLabel overallProgressCounter;
	private static JPanel unzipPanel;
	private static JPanel consolePanel;
	private static JLabel console;
	private static JPanel downloadPanel;

	public static String name = "Endi";
	private static String wersja = "1.3.2";
	public static String title = name + "Launcher";

	public static void preInit() {
		// https://stackoverflow.com/questions/6978017/first-call-to-jframe-constructor-takes-a-long-time-during-swing-application-star
		// -Dsun.java2d.d3d=false
		try {
			ralewayBold = Font.createFont(Font.TRUETYPE_FONT, Interface.class.getResourceAsStream("/files/fonts/Raleway-Bold.ttf"));
			ralewayMedium = Font.createFont(Font.TRUETYPE_FONT, Interface.class.getResourceAsStream("/files/fonts/Raleway-Medium.ttf"));
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void build() throws IOException, FontFormatException {

		// windowbiulder font fix
		// $hide>>$
		if (ralewayBold == null || ralewayMedium == null) {
			// $hide<<$
			preInit();
			// $hide>>$
		} // $hide<<$

		frame = new JFrame();

		frame.setTitle(title);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setBounds(screenSize.width / 2 - 842 / 2, screenSize.height / 2 - 542 / 2, 842, 542);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		BufferedImage icon = ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"));
		List<Image> icons = new ArrayList<>();
		icons.add(icon);
		frame.setIconImages(icons);
		final Taskbar taskbar = Taskbar.getTaskbar();
		try {
			if (taskbar.isSupported(Feature.ICON_IMAGE)) {
				taskbar.setIconImage(icon);
			}
		} catch (final UnsupportedOperationException e) {
			System.out.println("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		}

		FrameDragListener frameDragListener = new FrameDragListener(frame);
		frame.addMouseListener(frameDragListener);
		frame.addMouseMotionListener(frameDragListener);

		JLabel label2 = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/icon.png"))));
		label2.setBounds(21, 16, 20, 20);
		frame.getContentPane().add(label2);

		JLabel lblEndimcLauncher = new JLabel(name + " Launcher");
		lblEndimcLauncher.setBounds(49, 15, 162, 19);
		lblEndimcLauncher.setFont(ralewayBold.deriveFont(12f));
		lblEndimcLauncher.setForeground(polaText);
		frame.getContentPane().add(lblEndimcLauncher);

		JPanel panel = new JPanel();
		panel.setBounds(21, 42, 800, 480);
		panel.setBorder(null);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		rightSide = new JPanel();
		rightSide.setBounds(601, 0, 200, 480);
		rightSide.setBackground(panelsBgColor);
		rightSide.setOpaque(false);
		rightSide.setUI(new TransparentPanelFixUI());
		panel.add(rightSide);
		rightSide.setLayout(null);

		panel_4 = new JPanel();
		panel_4.setBounds(10, 11, 178, 142);
		rightSide.add(panel_4);
		panel_4.setLayout(null);
		panel_4.setBackground(new Color(0, 0, 0, 0));
		panel_4.setBorder(BorderFactory.createLineBorder(borders));
		panel_4.setOpaque(false);
		panel_4.setUI(new TransparentPanelFixUI());

		// it takes so much time!
		lblInfo = new JLabel("<html><b>" + name + " - Launcher<br>Wersja " + wersja + "<br>~PrzemoVi</b></html>");

		lblInfo.setBounds(10, 11, 158, 61);
		panel_4.add(lblInfo);
		ralewayBold.deriveFont(12f);
		lblInfo.setForeground(textColor);
		lblInfo.setFont(ralewayBold.deriveFont(12f));// HOTSPOT
		lblInfo.setBorder(textFieldsLeftPadding);

		lblInfo.setVerticalAlignment(JLabel.TOP);
		lblInfo.setVerticalTextPosition(JLabel.TOP);
		lblInfo.setAlignmentY(JLabel.TOP_ALIGNMENT);

		lblWersje = new JLabel("<html><b>" + "Wersja paczki: N/A<br>" + "Wersja cmdline: N/A<br>" + "Wersja runtime: N/A<br>" + "Algorytm GC: ??<br>" + "</b></html>");
		lblWersje.setHorizontalTextPosition(JLabel.CENTER);
		lblWersje.setVerticalTextPosition(JLabel.BOTTOM);
		lblWersje.setBounds(10, 65, 158, 70);
		panel_4.add(lblWersje);
		lblWersje.setFont(ralewayBold.deriveFont(12f));
		lblWersje.setForeground(textColor);
		lblWersje.setBorder(textFieldsLeftPadding);
		lblWersje.setVerticalAlignment(JLabel.TOP);
		lblWersje.setVerticalTextPosition(JLabel.TOP);
		lblWersje.setAlignmentY(JLabel.TOP_ALIGNMENT);

		panel_3 = new JPanel();
		panel_3.setBounds(10, 336, 178, 133);
		rightSide.add(panel_3);
		panel_3.setLayout(null);
		panel_3.setBackground(new Color(0, 0, 0, 0));
		panel_3.setBorder(BorderFactory.createLineBorder(borders));
		panel_3.setOpaque(false);
		panel_3.setUI(new TransparentPanelFixUI());

		panel_2 = new JPanel();
		panel_2.setBounds(10, 250, 178, 75);
		rightSide.add(panel_2);
		panel_2.setLayout(null);
		panel_2.setOpaque(false);
		panel_2.setUI(new TransparentPanelFixUI());
		panel_2.setBackground(new Color(0, 0, 0, 0));
		panel_2.setBorder(BorderFactory.createLineBorder(borders));

		JLabel lblNewLabel_12 = new JLabel("Instalacja gry");
		lblNewLabel_12.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12.setBounds(10, 11, 158, 15);
		panel_2.add(lblNewLabel_12);
		lblNewLabel_12.setForeground(polaText);
		lblNewLabel_12.setFont(ralewayBold.deriveFont(12f));

		installationsCB = new JComboBox<String>();
		installationsCB.setBounds(10, 36, 158, 28);
		panel_2.add(installationsCB);
		installationsCB.setEditable(true);

		installationsCB.addItemListener(new ItemListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						JComboBox<String> cb = (JComboBox<String>) e.getSource();
						String newSelection = (String) cb.getSelectedItem();
						installationSelected(newSelection);
					}
				}
			}
		});
		panel_12 = new JPanel();
		panel_12.setBounds(10, 164, 180, 75);
		rightSide.add(panel_12);
		panel_12.setLayout(null);
		panel_12.setOpaque(false);
		panel_12.setUI(new TransparentPanelFixUI());
		panel_12.setBackground(new Color(0, 0, 0, 0));
		panel_12.setBorder(BorderFactory.createLineBorder(borders));

		accountsCB = new JComboBox<String>();
		accountsCB.setBounds(10, 36, 158, 28);
		panel_12.add(accountsCB);
		accountsCB.setEditable(true);

		// $hide>>$
		accountsCB.setUI(new CustomComboBoxUI());
		accountsCB.setRenderer(new CustomComboBoxRendererBig());
		accountsCB.setEditor(new CustomComboBoxEditorBig(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accountSettings();
			}
		}));

		installationsCB.setUI(new CustomComboBoxUI());
		installationsCB.setRenderer(new CustomComboBoxRendererBig());
		installationsCB.setEditor(new CustomComboBoxEditorBig(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				installationSettings();
			}
		}));
		// $hide<<$

		graj = new JButton();
		graj.setBounds(10, 89, 158, 28);
		panel_3.add(graj);
		graj.setBorder(null);
		graj.setContentAreaFilled(false);
		graj.setBorder(BorderFactory.createLineBorder(borders));
		graj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				play();
			}
		});
		graj.setBackground(buttonsBg);
		graj.setForeground(buttonsText);
		graj.setOpaque(true);
		graj.setText("Graj");
		// graj.setFont(raleway.deriveFont(18f));
		graj.setFont(ralewayBold.deriveFont(18f));
		graj.setFocusable(false);

		graj.addMouseListener(new ButtonMouseListener(graj, buttonsBg));

		konsolaBtn = new JButton();
		konsolaBtn.setBounds(10, 50, 158, 28);
		panel_3.add(konsolaBtn);
		konsolaBtn.setBorder(null);
		konsolaBtn.setContentAreaFilled(false);
		konsolaBtn.setBorder(BorderFactory.createLineBorder(borders));
		konsolaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchCard("instalacja");
			}
		});

		konsolaBtn.setBackground(buttonsBg);
		konsolaBtn.setForeground(buttonsText);
		konsolaBtn.setOpaque(true);
		konsolaBtn.setText("Konsola");
		konsolaBtn.setFont(ralewayBold.deriveFont(18f));
		konsolaBtn.setFocusable(false);
		konsolaBtn.addMouseListener(new ButtonMouseListener(konsolaBtn, buttonsBg));

		informacjeBtn = new JButton();
		informacjeBtn.setBounds(10, 11, 158, 28);
		panel_3.add(informacjeBtn);
		informacjeBtn.setBorder(null);
		informacjeBtn.setContentAreaFilled(false);
		informacjeBtn.setBorder(BorderFactory.createLineBorder(borders));
		informacjeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchCard("informacje");
			}
		});

		informacjeBtn.setBackground(buttonsBg);
		informacjeBtn.setForeground(buttonsText);
		informacjeBtn.setOpaque(true);
		informacjeBtn.setText("Informacje");
		informacjeBtn.setFont(ralewayBold.deriveFont(18f));
		informacjeBtn.setFocusable(false);
		informacjeBtn.addMouseListener(new ButtonMouseListener(informacjeBtn, buttonsBg));

		lblNewLabel_13 = new JLabel("Profil gracza");
		lblNewLabel_13.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_13.setBounds(10, 11, 158, 15);
		lblNewLabel_13.setForeground(polaText);
		lblNewLabel_13.setFont(ralewayBold.deriveFont(12f));
		panel_12.add(lblNewLabel_13);
		accountsCB.addItemListener(new ItemListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						JComboBox<String> cb = (JComboBox<String>) e.getSource();
						String newSelection = (String) cb.getSelectedItem();
						accountSelected(newSelection);
					}
				}
			}
		});
		graj.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isMiddleMouseButton(e) && graj.isEnabled()) {

				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}
		});

		kontener = new JPanel();
		kontener.setBounds(10, 11, 581, 458);
		panel.add(kontener);
		kontener.setLayout(new CardLayout(0, 0));
		kontener.setOpaque(false);

		konta = new JPanel();
		kontener.add(konta, "konta");
		konta.setLayout(null);
		konta.setBackground(panelsBgColor);
		konta.setOpaque(false);
		konta.setUI(new TransparentPanelFixUI());

		zxprofiles = new JPanel();
		zxprofiles.setLayout(null);
		zxprofiles.setBorder(BorderFactory.createLineBorder(borders));
		zxprofiles.setBackground(new Color(0, 0, 0, 0));
		zxprofiles.setBounds(10, 296, 182, 151);
		konta.add(zxprofiles);

		lblKontoMojang = new JLabel("Typ konta", SwingConstants.CENTER);
		lblKontoMojang.setForeground(new Color(27, 111, 148));
		lblKontoMojang.setBounds(10, 11, 160, 25);
		zxprofiles.add(lblKontoMojang);
		lblKontoMojang.setForeground(polaText);
		lblKontoMojang.setFont(ralewayBold.deriveFont(14f));

		upAccTypeMojang = new JButton("Mojang");
		upAccTypeMojang.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeActiveUserProfileType(MinecraftProfile.Type.MOJANG);
			}
		});
		upAccTypeMojang.setBounds(10, 47, 160, 23);
		upAccTypeMojang.setContentAreaFilled(false);
		upAccTypeMojang.setBackground(buttonsBg);
		upAccTypeMojang.setForeground(buttonsText);
		upAccTypeMojang.setOpaque(true);
		upAccTypeMojang.setBorder(null);
		upAccTypeMojang.setFocusable(false);
		upAccTypeMojang.addMouseListener(new ButtonMouseListener(upAccTypeMojang, buttonsBg));
		upAccTypeMojang.setFont(ralewayBold.deriveFont(14f));
		zxprofiles.add(upAccTypeMojang);

		upAccTypeNP = new JButton("Non-premium/brak");
		upAccTypeNP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeActiveUserProfileType(MinecraftProfile.Type.NONPREMIUM);
			}
		});
		upAccTypeNP.setBounds(10, 115, 160, 23);
		upAccTypeNP.setContentAreaFilled(false);
		upAccTypeNP.setBackground(buttonsBg);
		upAccTypeNP.setForeground(buttonsText);
		upAccTypeNP.setOpaque(true);
		upAccTypeNP.setBorder(null);
		upAccTypeNP.setFocusable(false);
		upAccTypeNP.addMouseListener(new ButtonMouseListener(upAccTypeNP, buttonsBg));
		upAccTypeNP.setFont(ralewayBold.deriveFont(14f));
		zxprofiles.add(upAccTypeNP);

		upAccTypeMicrosoft = new JButton("Microsoft");
		upAccTypeMicrosoft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeActiveUserProfileType(MinecraftProfile.Type.MICROSOFT);
			}
		});
		upAccTypeMicrosoft.setBounds(10, 81, 160, 23);
		upAccTypeMicrosoft.setContentAreaFilled(false);
		upAccTypeMicrosoft.setBackground(buttonsBg);
		upAccTypeMicrosoft.setForeground(buttonsText);
		upAccTypeMicrosoft.setOpaque(true);
		upAccTypeMicrosoft.setBorder(null);
		upAccTypeMicrosoft.setFocusable(false);
		upAccTypeMicrosoft.addMouseListener(new ButtonMouseListener(upAccTypeMicrosoft, buttonsBg));
		upAccTypeMicrosoft.setFont(ralewayBold.deriveFont(14f));
		zxprofiles.add(upAccTypeMicrosoft);

		panel_5 = new JPanel();
		panel_5.setBounds(10, 11, 561, 274);
		konta.add(panel_5);
		panel_5.setLayout(null);
		panel_5.setBackground(new Color(0, 0, 0, 0));
		panel_5.setBorder(BorderFactory.createLineBorder(borders));
		panel_5.setOpaque(false);
		panel_5.setUI(new TransparentPanelFixUI());

		lblTypSkina = new JLabel("Typ skina: Brak");
		lblTypSkina.setBounds(391, 11, 160, 14);
		panel_5.add(lblTypSkina);
		lblTypSkina.setForeground(polaText);
		lblTypSkina.setFont(ralewayBold.deriveFont(12f));

		lblData = new JLabel("Data: --");
		lblData.setBounds(391, 36, 160, 14);
		panel_5.add(lblData);
		lblData.setForeground(polaText);
		lblData.setFont(ralewayBold.deriveFont(12f));

		btnLosowySkin = new JButton("Ustaw losowy skin");
		btnLosowySkin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		btnLosowySkin.setBounds(391, 206, 160, 23);
		panel_5.add(btnLosowySkin);
		btnLosowySkin.setContentAreaFilled(false);
		btnLosowySkin.setBackground(buttonsBg);
		btnLosowySkin.setForeground(buttonsText);
		btnLosowySkin.setOpaque(true);
		btnLosowySkin.setBorder(null);
		btnLosowySkin.setFocusable(false);
		btnLosowySkin.addMouseListener(new ButtonMouseListener(btnLosowySkin, buttonsBg));
		btnLosowySkin.setFont(ralewayBold.deriveFont(14f));

		btnSkinZNicku = new JButton("Skin gracza premium");
		btnSkinZNicku.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {

					}
				}).start();
			}
		});
		btnSkinZNicku.setBounds(391, 240, 160, 23);
		panel_5.add(btnSkinZNicku);
		btnSkinZNicku.setContentAreaFilled(false);
		btnSkinZNicku.setBackground(buttonsBg);
		btnSkinZNicku.setForeground(buttonsText);
		btnSkinZNicku.setOpaque(true);
		btnSkinZNicku.setBorder(null);
		btnSkinZNicku.setFocusable(false);
		btnSkinZNicku.addMouseListener(new ButtonMouseListener(btnSkinZNicku, buttonsBg));
		btnSkinZNicku.setFont(ralewayBold.deriveFont(14f));

		btnSkinZPliku = new JButton("Własny skin z pliku");
		btnSkinZPliku.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JnaFileChooser fc = new JnaFileChooser();
						fc.addFilter("All Files", "*");
						fc.addFilter("Pictures", "jpg", "jpeg", "png", "gif", "bmp");
						if (fc.showSaveDialog(frame)) {
							try {
								UserProfile up = UserProfileManager.getActiveProfile();
								up.getSkin().change(fc.getSelectedFile());
								up.getSkin().refreshAsync();
								DialogUtil.showConfirmDialog("<center>Pomyślnie zmieniono skina.</center>");
							} catch (Exception e) {
								DialogUtil.showConfirmDialog("Wystąpił błąd:\n" + e.getMessage());
							}
						}
					}
				}).start();

			}
		});
		btnSkinZPliku.setBounds(391, 138, 160, 23);
		panel_5.add(btnSkinZPliku);
		btnSkinZPliku.setContentAreaFilled(false);
		btnSkinZPliku.setBackground(buttonsBg);
		btnSkinZPliku.setForeground(buttonsText);
		btnSkinZPliku.setOpaque(true);
		btnSkinZPliku.setBorder(null);
		btnSkinZPliku.setFocusable(false);
		btnSkinZPliku.addMouseListener(new ButtonMouseListener(btnSkinZPliku, buttonsBg));
		btnSkinZPliku.setFont(ralewayBold.deriveFont(14f));

		btnSkinDomyslny = new JButton("Przywróć domyślny");
		btnSkinDomyslny.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		btnSkinDomyslny.setBounds(391, 172, 160, 23);
		panel_5.add(btnSkinDomyslny);
		btnSkinDomyslny.setContentAreaFilled(false);
		btnSkinDomyslny.setBackground(buttonsBg);
		btnSkinDomyslny.setForeground(buttonsText);
		btnSkinDomyslny.setOpaque(true);
		btnSkinDomyslny.setBorder(null);
		btnSkinDomyslny.setFocusable(false);
		btnSkinDomyslny.addMouseListener(new ButtonMouseListener(btnSkinDomyslny, buttonsBg));
		btnSkinDomyslny.setFont(ralewayBold.deriveFont(14f));

		nickSkina = new JLabel("");
		nickSkina.setBounds(391, 86, 160, 14);
		panel_5.add(nickSkina);
		nickSkina.setForeground(polaText);
		nickSkina.setFont(ralewayBold.deriveFont(12f));

		btnPobierzSkina = new JButton("Zapisz do pliku");
		btnPobierzSkina.setHorizontalTextPosition(SwingConstants.LEFT);
		btnPobierzSkina.setHorizontalAlignment(SwingConstants.LEFT);
		btnPobierzSkina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		btnPobierzSkina.setBounds(391, 61, 100, 14);
		panel_5.add(btnPobierzSkina);
		btnPobierzSkina.setContentAreaFilled(false);
		btnPobierzSkina.setForeground(Interface.polaText);
		btnPobierzSkina.setBorder(null);
		btnPobierzSkina.setFocusable(false);
		btnPobierzSkina.addMouseListener(new AButtonMouseListener(btnPobierzSkina));
		btnPobierzSkina.setFont(ralewayBold.deriveFont(12f));
		btnPobierzSkina.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		cbDynamicznyPogladSkina = new JCheckBox("Dynamiczny podgląd");
		cbDynamicznyPogladSkina.setBounds(6, 7, 160, 23);
		panel_5.add(cbDynamicznyPogladSkina);
		// btnPobierzSkina.setVisible(false);
		cbDynamicznyPogladSkina.setOpaque(false);
		cbDynamicznyPogladSkina.setBackground(polaText);
		cbDynamicznyPogladSkina.setForeground(polaText);
		cbDynamicznyPogladSkina.setFocusable(false);
		cbDynamicznyPogladSkina.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		cbDynamicznyPogladSkina.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		cbDynamicznyPogladSkina.setFont(ralewayBold.deriveFont(12f));
		cbDynamicznyPogladSkina.setToolTipText("Włącz dynamiczny podgląd skina. Możesz wyłaczyć, aby oszczędzać wydajność.");

		panelAccSettings = new JPanel();
		panelAccSettings.setBounds(202, 296, 235, 151);
		panelAccSettings.setLayout(new CardLayout(0, 0));
		panelAccSettings.setOpaque(false);

		JPanel panelNonpremium = new JPanel();
		panelNonpremium.setBounds(0, 0, 235, 151);
		panelAccSettings.add(panelNonpremium, "nonpremium");
		panelNonpremium.setLayout(null);
		panelNonpremium.setBackground(new Color(0, 0, 0, 0));
		panelNonpremium.setBorder(BorderFactory.createLineBorder(borders));
		panelNonpremium.setOpaque(false);
		panelNonpremium.setUI(new TransparentPanelFixUI());

		konta.add(panelAccSettings);

		JLabel lblNewLabel = new JLabel("Ustawienia konta Non-premium");
		lblNewLabel.setForeground(polaText);
		lblNewLabel.setFont(ralewayBold.deriveFont(14f));
		lblNewLabel.setHorizontalAlignment(JLabel.CENTER);
		lblNewLabel.setBounds(0, 11, 235, 25);
		panelNonpremium.add(lblNewLabel);

		lblNewLabel_1 = new JLabel("Nick:");
		lblNewLabel_1.setBounds(10, 53, 38, 14);
		lblNewLabel_1.setForeground(polaText);
		lblNewLabel_1.setFont(ralewayBold.deriveFont(12f));
		panelNonpremium.add(lblNewLabel_1);

		textField = new PlaceholderTextField("");
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				userProfileNickChanged(textField.getText());
			}
		});
		textField.setPlaceholder("Wybierz dowolny nick...");
		textField.setBounds(58, 51, 167, 20);
		textField.setColumns(10);
		textField.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, borders));
		textField.setBackground(polaTlo);
		textField.setForeground(polaText);
		panelNonpremium.add(textField);

		lblNewLabel_2 = new JLabel("<html>Pamiętaj że używając konta<br>non-premium nie będziesz mógł<br>wchodzić na serwery premium.</html>");
		lblNewLabel_2.setBounds(10, 78, 215, 62);
		lblNewLabel_2.setForeground(polaText);
		lblNewLabel_2.setFont(ralewayBold.deriveFont(12f));
		panelNonpremium.add(lblNewLabel_2);

		panelMojang = new JPanel();
		panelMojang.setLayout(null);
		panelMojang.setOpaque(false);
		panelMojang.setBorder(BorderFactory.createLineBorder(borders));
		panelMojang.setBackground(new Color(0, 0, 0, 0));
		panelAccSettings.add(panelMojang, "mojang");

		lblUstawieniaKontaMojang = new JLabel("Ustawienia konta Mojang");
		lblUstawieniaKontaMojang.setForeground(new Color(27, 111, 148));
		lblUstawieniaKontaMojang.setFont(null);
		lblUstawieniaKontaMojang.setFont(ralewayBold.deriveFont(14f));
		lblUstawieniaKontaMojang.setBounds(0, 11, 235, 25);
		lblUstawieniaKontaMojang.setHorizontalAlignment(JLabel.CENTER);
		panelMojang.add(lblUstawieniaKontaMojang);

		accMojangNickLbl = new JLabel("");
		accMojangNickLbl.setForeground(new Color(27, 111, 148));
		accMojangNickLbl.setFont(null);
		accMojangNickLbl.setBounds(10, 47, 215, 14);
		accMojangNickLbl.setHorizontalAlignment(JLabel.CENTER);
		panelMojang.add(accMojangNickLbl);

		btnZalogujMojang = new JButton("Zaloguj");
		btnZalogujMojang.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userProfileLoginLogout();
			}
		});
		btnZalogujMojang.setContentAreaFilled(false);
		btnZalogujMojang.setBackground(buttonsBg);
		btnZalogujMojang.setForeground(buttonsText);
		btnZalogujMojang.setOpaque(true);
		btnZalogujMojang.setBorder(null);
		btnZalogujMojang.setFocusable(false);
		btnZalogujMojang.addMouseListener(new ButtonMouseListener(btnZalogujMojang, buttonsBg));
		btnZalogujMojang.setFont(ralewayBold.deriveFont(14f));
		btnZalogujMojang.setBounds(10, 78, 215, 23);
		panelMojang.add(btnZalogujMojang);

		btnNewButton = new JButton("Ustawienia konta");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userProfileSettings();
			}
		});
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBackground(buttonsBg);
		btnNewButton.setForeground(buttonsText);
		btnNewButton.setOpaque(true);
		btnNewButton.setBorder(null);
		btnNewButton.setFocusable(false);
		btnNewButton.addMouseListener(new ButtonMouseListener(btnNewButton, buttonsBg));
		btnNewButton.setFont(ralewayBold.deriveFont(14f));
		btnNewButton.setBounds(10, 112, 215, 23);
		panelMojang.add(btnNewButton);

		panelMicrosoft = new JPanel();
		panelMicrosoft.setLayout(null);
		panelMicrosoft.setOpaque(false);
		panelMicrosoft.setBorder(BorderFactory.createLineBorder(borders));
		panelMicrosoft.setBackground(new Color(0, 0, 0, 0));
		panelAccSettings.add(panelMicrosoft, "microsoft");

		lblUstawieniaKontaMicrosoft = new JLabel("Ustawienia konta Microsoft");
		lblUstawieniaKontaMicrosoft.setForeground(new Color(27, 111, 148));
		lblUstawieniaKontaMicrosoft.setFont(null);
		lblUstawieniaKontaMicrosoft.setBounds(0, 11, 235, 25);
		lblUstawieniaKontaMicrosoft.setFont(ralewayBold.deriveFont(14f));
		lblUstawieniaKontaMicrosoft.setHorizontalAlignment(JLabel.CENTER);
		panelMicrosoft.add(lblUstawieniaKontaMicrosoft);

		accMicrosoftNickLbl = new JLabel("");
		accMicrosoftNickLbl.setForeground(new Color(27, 111, 148));
		accMicrosoftNickLbl.setFont(null);
		accMicrosoftNickLbl.setBounds(10, 47, 215, 14);
		accMicrosoftNickLbl.setHorizontalAlignment(JLabel.CENTER);
		panelMicrosoft.add(accMicrosoftNickLbl);

		btnZalogujMicrosoft = new JButton("Zaloguj");
		btnZalogujMicrosoft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userProfileLoginLogout();
			}
		});
		btnZalogujMicrosoft.setContentAreaFilled(false);
		btnZalogujMicrosoft.setBackground(buttonsBg);
		btnZalogujMicrosoft.setForeground(buttonsText);
		btnZalogujMicrosoft.setOpaque(true);
		btnZalogujMicrosoft.setBorder(null);
		btnZalogujMicrosoft.setFocusable(false);
		btnZalogujMicrosoft.addMouseListener(new ButtonMouseListener(btnZalogujMicrosoft, buttonsBg));
		btnZalogujMicrosoft.setFont(ralewayBold.deriveFont(14f));
		btnZalogujMicrosoft.setBounds(10, 78, 215, 23);
		panelMicrosoft.add(btnZalogujMicrosoft);

		btnNewButton_1 = new JButton("Ustawienia konta");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userProfileSettings();
			}
		});
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setBackground(buttonsBg);
		btnNewButton_1.setForeground(buttonsText);
		btnNewButton_1.setOpaque(true);
		btnNewButton_1.setBorder(null);
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.addMouseListener(new ButtonMouseListener(btnNewButton_1, buttonsBg));
		btnNewButton_1.setFont(ralewayBold.deriveFont(14f));
		btnNewButton_1.setBounds(10, 112, 215, 23);
		panelMicrosoft.add(btnNewButton_1);

		panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setOpaque(false);
		panel_7.setBorder(BorderFactory.createLineBorder(borders));
		panel_7.setBackground(new Color(0, 0, 0, 0));
		panel_7.setBounds(447, 296, 124, 64);
		konta.add(panel_7);

		btnZmieMotyw = new JButton("Zmień motyw");
		btnZmieMotyw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Themes.next();
			}
		});
		btnZmieMotyw.setBounds(10, 11, 101, 23);
		btnZmieMotyw.setContentAreaFilled(false);
		btnZmieMotyw.setBackground(buttonsBg);
		btnZmieMotyw.setForeground(buttonsText);
		btnZmieMotyw.setOpaque(true);
		btnZmieMotyw.setBorder(null);
		btnZmieMotyw.setFocusable(false);
		btnZmieMotyw.addMouseListener(new ButtonMouseListener(btnZmieMotyw, buttonsBg));
		btnZmieMotyw.setFont(ralewayBold.deriveFont(14f));
		panel_7.add(btnZmieMotyw);

		lblThemeName = new JLabel("Futuristic");
		lblThemeName.setHorizontalTextPosition(SwingConstants.CENTER);
		lblThemeName.setHorizontalAlignment(SwingConstants.CENTER);
		lblThemeName.setForeground(new Color(27, 111, 148));
		lblThemeName.setBounds(10, 40, 101, 16);
		panel_7.add(lblThemeName);

		panel_8 = new JPanel();
		panel_8.setBounds(447, 371, 124, 76);
		panel_8.setBorder(BorderFactory.createLineBorder(borders));
		panel_8.setBackground(new Color(0, 0, 0, 0));
		konta.add(panel_8);
		panel_8.setLayout(null);

		btnNewButton_3 = new JButton("Usuń profil");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProfile();
			}
		});
		btnNewButton_3.setBounds(10, 11, 104, 23);
		btnNewButton_3.setContentAreaFilled(false);
		btnNewButton_3.setBackground(buttonsBg);
		btnNewButton_3.setForeground(buttonsText);
		btnNewButton_3.setOpaque(true);
		btnNewButton_3.setBorder(null);
		btnNewButton_3.setFocusable(false);
		btnNewButton_3.addMouseListener(new ButtonMouseListener(btnNewButton_3, buttonsBg));
		btnNewButton_3.setFont(ralewayBold.deriveFont(14f));
		panel_8.add(btnNewButton_3);

		btnNewButton_4 = new JButton("Zmień nazwę");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserProfileNameChange changer = new UserProfileNameChange(UserProfileManager.getActiveProfile());
				changer.showInterface();
			}
		});
		btnNewButton_4.setBounds(10, 42, 104, 23);
		btnNewButton_4.setContentAreaFilled(false);
		btnNewButton_4.setBackground(buttonsBg);
		btnNewButton_4.setForeground(buttonsText);
		btnNewButton_4.setOpaque(true);
		btnNewButton_4.setBorder(null);
		btnNewButton_4.setFocusable(false);
		btnNewButton_4.addMouseListener(new ButtonMouseListener(btnNewButton_4, buttonsBg));
		btnNewButton_4.setFont(ralewayBold.deriveFont(14f));
		panel_8.add(btnNewButton_4);

		ToolTipManager.sharedInstance().setInitialDelay(500);

		JPanel informacje = new JPanel();
		informacje.setLayout(null);
		informacje.setOpaque(false);
		informacje.setBackground(panelsBgColor);
		informacje.setUI(new TransparentPanelFixUI());
		kontener.add(informacje, "informacje");

		JPanel staty = new JPanel();
		staty.setLayout(null);
		staty.setOpaque(false);
		staty.setBackground(panelsBgColor);
		staty.setUI(new TransparentPanelFixUI());
		kontener.add(staty, "statystyki");

		JPanel statyInner = new JPanel();
		statyInner.setBounds(10, 11, 561, 436);
		staty.add(statyInner);
		statyInner.setLayout(null);
		statyInner.setBackground(new Color(0, 0, 0, 0));
		statyInner.setBorder(BorderFactory.createLineBorder(borders));
		statyInner.setOpaque(false);
		statyInner.setUI(new TransparentPanelFixUI());

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 380, 541, 45);
		statyInner.add(panel_1);
		panel_1.setLayout(null);
		panel_1.setBackground(new Color(0, 0, 0, 0));
		panel_1.setBorder(BorderFactory.createLineBorder(borders));
		panel_1.setOpaque(false);
		panel_1.setUI(new TransparentPanelFixUI());

		btnCzasOnline = new JButton("Czas online");
		btnCzasOnline.setBounds(10, 11, 123, 23);
		panel_1.add(btnCzasOnline);
		btnCzasOnline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnCzasOnline.setContentAreaFilled(false);
		btnCzasOnline.setBackground(buttonsBg);
		btnCzasOnline.setForeground(buttonsText);
		btnCzasOnline.setOpaque(true);
		btnCzasOnline.setBorder(null);
		btnCzasOnline.setFocusable(false);
		btnCzasOnline.addMouseListener(new ButtonMouseListener(btnCzasOnline, buttonsBg));
		btnCzasOnline.setFont(ralewayBold.deriveFont(14f));

		btnZabiciGracze = new JButton("Zabici gracze");
		btnZabiciGracze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnZabiciGracze.setBounds(142, 11, 123, 23);
		panel_1.add(btnZabiciGracze);
		btnZabiciGracze.setContentAreaFilled(false);
		btnZabiciGracze.setBackground(buttonsBg);
		btnZabiciGracze.setForeground(buttonsText);
		btnZabiciGracze.setOpaque(true);
		btnZabiciGracze.setBorder(null);
		btnZabiciGracze.setFocusable(false);
		btnZabiciGracze.addMouseListener(new ButtonMouseListener(btnZabiciGracze, buttonsBg));
		btnZabiciGracze.setFont(ralewayBold.deriveFont(14f));

		btnZabiteMoby = new JButton("Zabite moby");
		btnZabiteMoby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnZabiteMoby.setBounds(275, 11, 123, 23);
		panel_1.add(btnZabiteMoby);
		btnZabiteMoby.setContentAreaFilled(false);
		btnZabiteMoby.setBackground(buttonsBg);
		btnZabiteMoby.setForeground(buttonsText);
		btnZabiteMoby.setOpaque(true);
		btnZabiteMoby.setBorder(null);
		btnZabiteMoby.setFocusable(false);
		btnZabiteMoby.addMouseListener(new ButtonMouseListener(btnZabiteMoby, buttonsBg));
		btnZabiteMoby.setFont(ralewayBold.deriveFont(14f));

		btnmierci = new JButton("Śmierci");
		btnmierci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnmierci.setBounds(408, 11, 123, 23);
		panel_1.add(btnmierci);
		btnmierci.setContentAreaFilled(false);
		btnmierci.setBackground(buttonsBg);
		btnmierci.setForeground(buttonsText);
		btnmierci.setOpaque(true);
		btnmierci.setBorder(null);
		btnmierci.setFocusable(false);
		btnmierci.addMouseListener(new ButtonMouseListener(btnmierci, buttonsBg));
		btnmierci.setFont(ralewayBold.deriveFont(14f));

		JPanel statyContainer = new JPanel();
		statyContainer.setBounds(10, 11, 541, 358);
		statyInner.add(statyContainer);
		statyContainer.setLayout(null);
		statyContainer.setBackground(new Color(0, 0, 0, 0));
		statyContainer.setBorder(BorderFactory.createLineBorder(borders));
		statyContainer.setOpaque(false);
		statyContainer.setUI(new TransparentPanelFixUI());

		statyLabel = new JLabel("");
		statyLabel.setBounds(1, 1, 539, 356);
		statyContainer.add(statyLabel);

		JPanel dfgh = new JPanel();
		dfgh.setBounds(10, 11, 561, 436);
		informacje.add(dfgh);
		dfgh.setLayout(null);
		dfgh.setBackground(new Color(0, 0, 0, 0));
		dfgh.setBorder(BorderFactory.createLineBorder(borders));
		dfgh.setOpaque(false);
		dfgh.setUI(new TransparentPanelFixUI());

		tyui = new JPanel();
		tyui.setLayout(null);
		tyui.setBackground(new Color(0, 0, 0, 0));
		tyui.setOpaque(false);
		tyui.setUI(new TransparentPanelFixUI());

		sp = new JScrollPane();
		sp.setBounds(10, 11, 360, 15);
		dfgh.add(sp);
		sp.setBackground(new Color(0, 0, 0, 0));
		sp.setBorder(BorderFactory.createLineBorder(borders));
		sp.setViewportView(tyui);
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUI(new CustomScrollbarUI());
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

		// sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		ghjk = new JPanel();
		ghjk.setBounds(380, 11, 171, 414);
		dfgh.add(ghjk);
		ghjk.setLayout(null);
		ghjk.setBackground(new Color(0, 0, 0, 0));
		ghjk.setBorder(BorderFactory.createLineBorder(borders));

		JMultilineLabel lblT = new JMultilineLabel("Teamspeak:\nendimc.pl\n\nDiscord:\ndiscord.gg/8zwUaF6\n\nWersja gry: 1.12.2\n\nWłaściciel: PrzemoVi\nHeadAdmin: slepysniper\nModerator: Yasei_WiLQ\n\nKontakt: \nendimc.pl/?p=kontakt");
		lblT.setBounds(10, 191, 151, 212);
		ghjk.add(lblT);
		lblT.setFont(ralewayBold.deriveFont(12f));
		lblT.setForeground(textColor);
		lblT.setBorder(textFieldsLeftPadding);
		lblT.setAlignmentY(JLabel.TOP_ALIGNMENT);

		JPanel panel_11 = new JPanel();
		panel_11.setBounds(10, 37, 360, 388);
		dfgh.add(panel_11);
		panel_11.setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 11, 340, 342);
		panel_11.add(textArea);

		textField_3 = new JTextField();
		textField_3.setBounds(10, 357, 281, 20);
		panel_11.add(textField_3);
		textField_3.setColumns(10);

		JButton btnNewButton_9 = new JButton("New button");
		btnNewButton_9.setBounds(301, 356, 49, 23);
		panel_11.add(btnNewButton_9);

		aktualizacja = new JPanel();
		aktualizacja.setLayout(null);
		aktualizacja.setOpaque(false);
		aktualizacja.setBackground(panelsBgColor);
		aktualizacja.setUI(new TransparentPanelFixUI());
		kontener.add(aktualizacja, "instalacja");

		consolePanel = new JPanel();
		consolePanel.setOpaque(false);
		consolePanel.setUI(new TransparentPanelFixUI());
		kontener.setOpaque(false);
		consolePanel.setBounds(10, 11, 561, 208);
		consolePanel.setLayout(null);
		consolePanel.setBackground(new Color(0, 0, 0, 0));
		consolePanel.setBorder(BorderFactory.createLineBorder(borders));
		aktualizacja.add(consolePanel);

		// TODO
		consoleSaveButton = new JButton(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/save.png"))));
		consoleSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						//JnaFileChooser chooser = new JnaFileChooser();
						JFileChooser chooser = new JFileChooser();
						//chooser.addFilter("Plik tekstowy", "txt");
						//if (chooser.showSaveDialog(Window.getWindows()[Window.getWindows().length - 1])) {
						int returnValue = chooser.showSaveDialog(Interface.getFrame());
						if (returnValue == JFileChooser.APPROVE_OPTION) {
							File f = chooser.getSelectedFile();
							if (!f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(".") + 1).equals("txt")) {
								f = new File(f.getAbsolutePath() + ".txt");
							}
							try {
								Logger.save();
								Files.copy(Logger.getFile().toPath(), f.toPath());
								DialogUtil.showConfirmDialog("Pomyślnie zapisano plik z logiem.");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						chooser = null;
						System.gc();
					}
				}).start();
			}
		});
		consoleSaveButton.setOpaque(false);
		consoleSaveButton.setBackground(new Color(0, 0, 0, 0));
		consoleSaveButton.setBorder(null);
		consoleSaveButton.setBounds(537, 183, 14, 14);
		consolePanel.add(consoleSaveButton);

		blinkingConsoleCursor = new JLabel("_");
		blinkingConsoleCursor.setVerticalAlignment(JLabel.BOTTOM);
		blinkingConsoleCursor.setVerticalTextPosition(JLabel.BOTTOM);
		blinkingConsoleCursor.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
		blinkingConsoleCursor.setBounds(10, 183, 14, 14);
		blinkingConsoleCursor.setForeground(polaText);
		consolePanel.add(blinkingConsoleCursor);

		console = new JLabel();
		console.setBounds(11, 10, 540, 187);
		console.setVerticalAlignment(JLabel.BOTTOM);
		console.setVerticalTextPosition(JLabel.BOTTOM);
		console.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
		console.setBorder(textFieldsLeftPadding);
		// console.setText("<html><font color=" + cursorHexColor + "
		// id=\"cursor\">_</font></html>");
		console.setForeground(polaText);
		console.setFont(ralewayMedium.deriveFont(11f));
		/*
		 * console.addPropertyChangeListener("text", new PropertyChangeListener() {
		 * 
		 * @Override public void propertyChange(PropertyChangeEvent arg0) { //
		 * aktualizacja.repaint(); // bg.repaint(); } });
		 */
		console.setFont(ralewayMedium.deriveFont(11f));
		consolePanel.add(console);

		downloadPanel = new JPanel();
		downloadPanel.setOpaque(false);
		downloadPanel.setUI(new TransparentPanelFixUI());
		downloadPanel.setLayout(null);
		downloadPanel.setBackground(new Color(0, 0, 0, 0));
		downloadPanel.setBorder(BorderFactory.createLineBorder(borders));
		downloadPanel.setBounds(10, 382, 561, 65);
		aktualizacja.add(downloadPanel);

		progressDown = new JProgressBar();
		progressDown.setBounds(10, 39, 541, 17);
		downloadPanel.add(progressDown);
		progressDown.setBackground(polaTlo);
		progressDown.setForeground(textColor);
		progressDown.setBorder(BorderFactory.createEmptyBorder());
		progressDown.setBorderPainted(false);

		lblPobieranie = new JLabel("Pobieranie...");
		lblPobieranie.setBounds(10, 12, 80, 17);
		downloadPanel.add(lblPobieranie);
		lblPobieranie.setForeground(polaText);
		lblPobieranie.setFont(ralewayMedium.deriveFont(12f));

		lblPobieranieCzas = new JLabel("Do zakończenia:");
		lblPobieranieCzas.setBounds(411, 11, 140, 17);
		downloadPanel.add(lblPobieranieCzas);
		lblPobieranieCzas.setForeground(polaText);
		lblPobieranieCzas.setFont(ralewayMedium.deriveFont(12f));

		lblPobieraniePobrano = new JLabel("Pobrano:  [%]");
		lblPobieraniePobrano.setBounds(100, 11, 176, 17);
		downloadPanel.add(lblPobieraniePobrano);
		lblPobieraniePobrano.setForeground(polaText);
		lblPobieraniePobrano.setFont(ralewayMedium.deriveFont(12f));

		lblPobieraniePreskosc = new JLabel("Prędkość:");
		lblPobieraniePreskosc.setBounds(286, 12, 115, 17);
		downloadPanel.add(lblPobieraniePreskosc);
		lblPobieraniePreskosc.setForeground(polaText);
		lblPobieraniePreskosc.setFont(ralewayMedium.deriveFont(12f));

		button = new JButton("");
		button.setBounds(538, 12, 17, 17);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setForeground(polaText);
		button.setBorder(null);
		button.setMargin(null);
		button.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/pause.png"))));
		downloadPanel.add(button);

		unzipPanel = new JPanel();
		unzipPanel.setOpaque(false);
		unzipPanel.setUI(new TransparentPanelFixUI());
		unzipPanel.setBounds(10, 230, 561, 65);
		aktualizacja.add(unzipPanel);
		unzipPanel.setLayout(null);
		unzipPanel.setBackground(new Color(0, 0, 0, 0));
		unzipPanel.setBorder(BorderFactory.createLineBorder(borders));

		overallProgressBar = new JProgressBar();
		overallProgressBar.setBounds(10, 39, 541, 17);
		unzipPanel.add(overallProgressBar);
		overallProgressBar.setBackground(polaTlo);
		overallProgressBar.setForeground(textColor);
		overallProgressBar.setBorder(BorderFactory.createEmptyBorder());
		overallProgressBar.setBorderPainted(false);

		overallProgressLblY = new JLabel("Instalacja...");
		overallProgressLblY.setBounds(10, 11, 109, 17);
		unzipPanel.add(overallProgressLblY);
		overallProgressLblY.setForeground(polaText);
		overallProgressLblY.setFont(ralewayMedium.deriveFont(12f));

		overallProgressLbl = new JLabel("");
		overallProgressLbl.setBounds(129, 11, 342, 17);
		unzipPanel.add(overallProgressLbl);
		overallProgressLbl.setForeground(polaText);
		overallProgressLbl.setFont(ralewayMedium.deriveFont(12f));

		overallProgressCounter = new JLabel("0/0");
		overallProgressCounter.setHorizontalAlignment(SwingConstants.RIGHT);
		overallProgressCounter.setBounds(481, 11, 70, 17);
		unzipPanel.add(overallProgressCounter);
		overallProgressCounter.setForeground(polaText);
		overallProgressCounter.setFont(ralewayMedium.deriveFont(12f));

		plikPanel = new JPanel();
		plikPanel.setBounds(10, 306, 561, 65);
		aktualizacja.add(plikPanel);
		plikPanel.setOpaque(false);
		plikPanel.setUI(new TransparentPanelFixUI());
		plikPanel.setLayout(null);
		plikPanel.setBackground(new Color(0, 0, 0, 0));
		plikPanel.setBorder(BorderFactory.createLineBorder(borders));

		progressFile = new JProgressBar();
		progressFile.setBounds(10, 39, 541, 17);
		plikPanel.add(progressFile);
		progressFile.setBackground(polaTlo);
		progressFile.setForeground(textColor);
		progressFile.setBorder(BorderFactory.createEmptyBorder());
		progressFile.setBorderPainted(false);

		przetworzonePlikiLbl = new JLabel("Przetwarzany plik:");
		przetworzonePlikiLbl.setBounds(10, 12, 439, 17);
		plikPanel.add(przetworzonePlikiLbl);
		przetworzonePlikiLbl.setForeground(polaText);
		przetworzonePlikiLbl.setFont(ralewayMedium.deriveFont(12f));

		processingFilesRight = new JLabel("New label");
		processingFilesRight.setHorizontalAlignment(SwingConstants.RIGHT);
		processingFilesRight.setBounds(459, 13, 92, 17);
		plikPanel.add(processingFilesRight);
		processingFilesRight.setForeground(polaText);
		processingFilesRight.setFont(ralewayMedium.deriveFont(12f));

		// TODO overall/files/down bounds
		slot0 = unzipPanel.getBounds();
		slot1 = plikPanel.getBounds();
		slot2 = downloadPanel.getBounds();

		switchCard("instalacja");

		background = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/bg.png"))));
		background.setBounds(0, 0, 800, 480);
		panel.add(background);

		ImageIcon Xnormal = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x.png")));
		ImageIcon Xclicked = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_clicked.png")));
		ImageIcon Xrollover = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_rollover.png")));

		JButton close = new JButton(Xnormal);
		close.setDisabledIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/x_disabled.png"))));
		close.addMouseListener(new NavButtonMouseListener(close, Xnormal, Xrollover, Xclicked));

		close.setBounds(781, 17, 38, 13);
		frame.getContentPane().add(close);
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});
		close.setContentAreaFilled(false);
		// close.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, borderColor));
		// close.setBorder(BorderFactory.createLineBorder(borderColor));
		close.setBorder(null);

		ImageIcon Mnormal = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/mini.png")));
		ImageIcon Mclicked = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/mini_clicked.png")));
		ImageIcon Mrollover = new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/mini_rollover.png")));

		JButton minimalize = new JButton(Mnormal);
		minimalize.setDisabledIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/mini_disabled.png"))));
		minimalize.addMouseListener(new NavButtonMouseListener(minimalize, Mnormal, Mrollover, Mclicked));

		minimalize.setBounds(721, 17, 27, 13);
		frame.getContentPane().add(minimalize);
		minimalize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		minimalize.setContentAreaFilled(false);
		minimalize.setBorder(null);

		// TODO
		JLabel ramka = new JLabel(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/ramka.png"))));
		ramka.setBounds(0, 0, 842, 542);
		frame.getContentPane().add(ramka);

		setFilePanelVisible(false);
		setDownloadPanelVisible(false);
		setOverallPanelVisible(false);

		startBlinkingCursor();

		SkinPreview.prepareSkinPreviewLabels(SkinPreview.SKIN_NONE, panel_5);

		instalacje = new JPanel();
		kontener.add(instalacje, "instalacje");
		instalacje.setLayout(null);
		instalacje.setOpaque(false);
		instalacje.setBackground(panelsBgColor);
		instalacje.setUI(new TransparentPanelFixUI());

		JPanel podstawowe = new JPanel();
		podstawowe.setBounds(10, 11, 561, 260);
		instalacje.add(podstawowe);
		podstawowe.setLayout(null);
		podstawowe.setOpaque(false);
		podstawowe.setUI(new TransparentPanelFixUI());
		podstawowe.setBackground(new Color(0, 0, 0, 0));
		podstawowe.setBorder(BorderFactory.createLineBorder(borders));

		JLabel lblNewLabel_3 = new JLabel("Podstawowe opcje");
		lblNewLabel_3.setBounds(10, 11, 541, 14);
		podstawowe.add(lblNewLabel_3);
		lblNewLabel_3.setForeground(polaText);
		lblNewLabel_3.setFont(ralewayBold.deriveFont(12f));

		JPanel panel_9 = new JPanel();
		panel_9.setBounds(10, 36, 170, 123);
		podstawowe.add(panel_9);
		panel_9.setLayout(null);
		panel_9.setLayout(null);
		panel_9.setOpaque(false);
		panel_9.setUI(new TransparentPanelFixUI());
		panel_9.setBackground(new Color(0, 0, 0, 0));
		panel_9.setBorder(BorderFactory.createLineBorder(borders));

		JLabel lblNewLabel_4 = new JLabel("Wersja gry");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(10, 11, 154, 15);
		panel_9.add(lblNewLabel_4);
		lblNewLabel_4.setForeground(polaText);
		lblNewLabel_4.setFont(ralewayBold.deriveFont(12f));

		showSnapshotsChb = new JCheckBox("Pokaż snapshoty");
		showSnapshotsChb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSnapshotsOldsChanged();
			}
		});
		showSnapshotsChb.setBounds(10, 65, 154, 23);
		panel_9.add(showSnapshotsChb);
		showSnapshotsChb.setOpaque(false);
		showSnapshotsChb.setBackground(polaText);
		showSnapshotsChb.setForeground(polaText);
		showSnapshotsChb.setFocusable(false);
		showSnapshotsChb.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		showSnapshotsChb.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		showSnapshotsChb.setFont(ralewayBold.deriveFont(12f));

		showOldsChb = new JCheckBox("Pokaż stare wersje");
		showOldsChb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSnapshotsOldsChanged();
			}
		});
		showOldsChb.setBounds(10, 91, 154, 23);
		panel_9.add(showOldsChb);
		showOldsChb.setOpaque(false);
		showOldsChb.setBackground(polaText);
		showOldsChb.setForeground(polaText);
		showOldsChb.setFocusable(false);
		showOldsChb.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		showOldsChb.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		showOldsChb.setFont(ralewayBold.deriveFont(12f));

		mcVersionsCB = new JComboBox<String>();// TODO bookmark
		mcVersionsCB.setBounds(10, 36, 150, 22);
		panel_9.add(mcVersionsCB);

		mcVersionsCB.setMaximumRowCount(15);
		mcVersionsCB.setEditable(true);
		// $hide>>$
		mcVersionsCB.setRenderer(new CustomComboBoxRenderer());
		mcVersionsCB.setEditor(new CustomComboBoxEditor());
		// $hide<<$
		mcVersionsCB.setUI(new CustomComboBoxUI());

		mcVersionsCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				gameVesionChangeRequested((String) e.getItem());
			}
		});

		JPanel panel_10 = new JPanel();
		panel_10.setBounds(190, 36, 170, 123);
		podstawowe.add(panel_10);
		panel_10.setLayout(null);
		panel_10.setLayout(null);
		panel_10.setOpaque(false);
		panel_10.setUI(new TransparentPanelFixUI());
		panel_10.setBackground(new Color(0, 0, 0, 0));
		panel_10.setBorder(BorderFactory.createLineBorder(borders));

		JLabel lblNewLabel_5 = new JLabel("Mody");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setBounds(10, 11, 150, 15);
		panel_10.add(lblNewLabel_5);
		lblNewLabel_5.setForeground(polaText);
		lblNewLabel_5.setFont(ralewayBold.deriveFont(12f));

		forgeRadio = new JRadioButton("Zainstaluj Forge");
		forgeRadio.setBounds(10, 40, 158, 23);
		panel_10.add(forgeRadio);
		forgeRadio.setOpaque(false);
		forgeRadio.setBackground(polaText);
		forgeRadio.setForeground(polaText);
		forgeRadio.setFocusable(false);
		forgeRadio.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox.png"))));
		forgeRadio.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox_checked.png"))));
		forgeRadio.setFont(ralewayBold.deriveFont(12f));
		forgeRadio.setActionCommand("forge");

		fabricRadio = new JRadioButton("Zainstaluj Fabric");
		fabricRadio.setBounds(10, 66, 158, 23);
		panel_10.add(fabricRadio);
		fabricRadio.setOpaque(false);
		fabricRadio.setBackground(polaText);
		fabricRadio.setForeground(polaText);
		fabricRadio.setFocusable(false);
		fabricRadio.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox.png"))));
		fabricRadio.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox_checked.png"))));
		fabricRadio.setFont(ralewayBold.deriveFont(12f));
		fabricRadio.setActionCommand("fabric");

		nullRadio = new JRadioButton("Brak");
		nullRadio.setBounds(10, 93, 158, 23);
		panel_10.add(nullRadio);
		nullRadio.setOpaque(false);
		nullRadio.setBackground(polaText);
		nullRadio.setForeground(polaText);
		nullRadio.setFocusable(false);
		nullRadio.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox.png"))));
		nullRadio.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/radiobox_checked.png"))));
		nullRadio.setFont(ralewayBold.deriveFont(12f));
		nullRadio.setActionCommand("null");

		ButtonGroup group = new ButtonGroup();
		group.add(forgeRadio);
		group.add(fabricRadio);
		group.add(nullRadio);

		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setInstallationModded(e.getActionCommand());
			}
		};
		forgeRadio.addActionListener(al);
		fabricRadio.addActionListener(al);
		nullRadio.addActionListener(al);

		moddingApiSettings = new JPanel();
		moddingApiSettings.setBounds(381, 36, 170, 123);
		podstawowe.add(moddingApiSettings);
		moddingApiSettings.setOpaque(false);
		moddingApiSettings.setLayout(new CardLayout(0, 0));

		forgeSettings = new JPanel();
		moddingApiSettings.add(forgeSettings, "forge");
		forgeSettings.setLayout(null);
		forgeSettings.setOpaque(false);
		forgeSettings.setUI(new TransparentPanelFixUI());
		forgeSettings.setBackground(new Color(0, 0, 0, 0));
		forgeSettings.setBorder(BorderFactory.createLineBorder(borders));

		JLabel lblNewLabel_6 = new JLabel("Forge");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setBounds(10, 11, 150, 15);
		lblNewLabel_6.setForeground(polaText);
		lblNewLabel_6.setFont(ralewayBold.deriveFont(12f));
		forgeSettings.add(lblNewLabel_6);

		forgeVersionCombo = new JComboBox<String>();
		forgeVersionCombo.setBounds(74, 36, 86, 22);
		forgeSettings.add(forgeVersionCombo);

		forgeVersionCombo.setMaximumRowCount(15);
		forgeVersionCombo.setEditable(true);
		// $hide>>$
		forgeVersionCombo.setRenderer(new CustomComboBoxRenderer());
		forgeVersionCombo.setEditor(new CustomComboBoxEditor());
		// $hide<<$
		forgeVersionCombo.setUI(new CustomComboBoxUI());

		forgeVersionCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

			}
		});

		JLabel lblNewLabel_9 = new JLabel("Wersja");
		lblNewLabel_9.setBounds(10, 40, 54, 14);
		lblNewLabel_9.setForeground(polaText);
		lblNewLabel_9.setFont(ralewayMedium.deriveFont(12f));
		forgeSettings.add(lblNewLabel_9);

		JButton btnNewButton_8 = new JButton("Otwórz folder modów");
		btnNewButton_8.setBounds(10, 89, 150, 23);
		forgeSettings.add(btnNewButton_8);
		btnNewButton_8.setContentAreaFilled(false);
		btnNewButton_8.setBackground(buttonsBg);
		btnNewButton_8.setForeground(buttonsText);
		btnNewButton_8.setOpaque(true);
		btnNewButton_8.setBorder(null);
		btnNewButton_8.setFocusable(false);
		btnNewButton_8.addMouseListener(new ButtonMouseListener(btnNewButton_8, buttonsBg));
		btnNewButton_8.setFont(ralewayBold.deriveFont(14f));

		fabricSettings = new JPanel();
		moddingApiSettings.add(fabricSettings, "fabric");
		fabricSettings.setLayout(null);
		fabricSettings.setOpaque(false);
		fabricSettings.setUI(new TransparentPanelFixUI());
		fabricSettings.setBackground(new Color(0, 0, 0, 0));
		fabricSettings.setBorder(BorderFactory.createLineBorder(borders));

		lblNewLabel_10 = new JLabel("Fabric");
		lblNewLabel_10.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_10.setBounds(10, 11, 150, 14);
		fabricSettings.add(lblNewLabel_10);
		lblNewLabel_10.setForeground(polaText);
		lblNewLabel_10.setFont(ralewayBold.deriveFont(12f));

		installFabricAPI = new JCheckBox("Zainstaluj FabricAPI");
		installFabricAPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameInstallationManager.getActive().setInstallFabricAPI(installFabricAPI.isSelected());
			}
		});
		installFabricAPI.setBounds(10, 32, 154, 23);
		fabricSettings.add(installFabricAPI);
		installFabricAPI.setOpaque(false);
		installFabricAPI.setBackground(polaText);
		installFabricAPI.setForeground(polaText);
		installFabricAPI.setFocusable(false);
		installFabricAPI.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		installFabricAPI.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		installFabricAPI.setFont(ralewayBold.deriveFont(12f));
		installFabricAPI.setToolTipText("Fabric-API jest wymagany przez niektóe mody i jeżeli nie ma innych przeciwskazań to zaleca się jego instalację.");

		installSodium = new JCheckBox("Zainstaluj Sodium");
		installSodium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameInstallationManager.getActive().setInstallSodium(installSodium.isSelected());
			}
		});
		installSodium.setBounds(10, 59, 154, 23);
		fabricSettings.add(installSodium);
		installSodium.setOpaque(false);
		installSodium.setBackground(polaText);
		installSodium.setForeground(polaText);
		installSodium.setFocusable(false);
		installSodium.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		installSodium.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		installSodium.setFont(ralewayBold.deriveFont(12f));
		installSodium.setToolTipText("Fabric-API jest wymagany przez niektóe mody i jeżeli nie ma innych przeciwskazań to zaleca się jego instalację.");

		JButton btnNewButton_7 = new JButton("Otwórz folder modów");
		btnNewButton_7.setBounds(10, 89, 150, 23);
		fabricSettings.add(btnNewButton_7);
		btnNewButton_7.setContentAreaFilled(false);
		btnNewButton_7.setBackground(buttonsBg);
		btnNewButton_7.setForeground(buttonsText);
		btnNewButton_7.setOpaque(true);
		btnNewButton_7.setBorder(null);
		btnNewButton_7.setFocusable(false);
		btnNewButton_7.addMouseListener(new ButtonMouseListener(btnNewButton_7, buttonsBg));
		btnNewButton_7.setFont(ralewayBold.deriveFont(14f));

		JPanel nullSettings = new JPanel();
		moddingApiSettings.add(nullSettings, "null");
		nullSettings.setLayout(null);
		nullSettings.setOpaque(false);
		nullSettings.setUI(new TransparentPanelFixUI());
		nullSettings.setBackground(new Color(0, 0, 0, 0));
		nullSettings.setBorder(BorderFactory.createLineBorder(borders));

		/**/

		sliderStep = 128;
		long memory = me.catzy44.utils.SystemInfo.getMemorySize() - 512;
		int sliderElements = (int) (memory / sliderStep);

		/**/
		ct = new JTChangeListener() {
			public void update(DocumentEvent e) {
				if (ramField.getText().equals("")) {
					return;
				}
				js.setEnabled(false);
				if (Integer.parseInt(ramField.getText()) > memory) {
					new Thread(new Runnable() {
						public void run() {
							ramField.setText(memory + "");
							DialogUtil.showConfirmDialog("Wpisana wartość musi być mniejsza od " + memory + "!\nNie możesz przydzielić więcej ramu niż posiadasz!\nDodatkowo musi zostać zachowane 512mb dla systemu. (uwzględnione)");
							js.setEnabled(true);
						}
					}).start();
				} else if (Integer.parseInt(ramField.getText()) < 512) {
					new Thread(new Runnable() {
						public void run() {
							ramField.setText(memory + "");
							DialogUtil.showConfirmDialog("Wpisana wartość musi być większa od 512!\nJest to minimalna ilość RAMu wymagana do uruchomienia paczki.");
							js.setEnabled(true);
						}
					}).start();
				} else {
					// slider.setValue(Integer.parseInt(ramField.getText()) / sliderElements);
					slider.setValue(Integer.parseInt(ramField.getText()) / sliderStep);
					js.setEnabled(true);
				}
			}
		};

		js = new JSChangeListener() {
			public void update(ChangeEvent event) {
				ct.setEnabled(false);
				int ram = slider.getValue() * sliderStep;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ramField.setText(ram + "");
						ct.setEnabled(true);
					}
				});
			}
		};
		/**/
		lblNewLabel_7 = new JLabel("New label");
		lblNewLabel_7.setBounds(10, 11, 46, 14);
		podstawowe.add(lblNewLabel_7);

		JPanel asdf = new JPanel();
		asdf.setBounds(10, 170, 274, 78);
		podstawowe.add(asdf);
		asdf.setLayout(null);
		asdf.setBackground(new Color(0, 0, 0, 0));
		asdf.setBorder(BorderFactory.createLineBorder(borders));
		asdf.setOpaque(false);
		asdf.setUI(new TransparentPanelFixUI());

		txtNazwaInst = new JTextField();
		txtNazwaInst.setBounds(120, 13, 140, 20);
		asdf.add(txtNazwaInst);
		txtNazwaInst.setText("nazwa inst");
		txtNazwaInst.setColumns(10);
		txtNazwaInst.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, borders));
		txtNazwaInst.setBackground(polaTlo);
		txtNazwaInst.setForeground(polaText);

		JButton btnNewButton_2 = new JButton("Otwórz folder");
		btnNewButton_2.setBounds(10, 44, 120, 23);
		asdf.add(btnNewButton_2);
		btnNewButton_2.setContentAreaFilled(false);
		btnNewButton_2.setBackground(buttonsBg);
		btnNewButton_2.setForeground(buttonsText);
		btnNewButton_2.setOpaque(true);
		btnNewButton_2.setBorder(null);
		btnNewButton_2.setFocusable(false);
		btnNewButton_2.addMouseListener(new ButtonMouseListener(btnNewButton_2, buttonsBg));
		btnNewButton_2.setFont(ralewayBold.deriveFont(14f));

		JButton btnNewButton_6 = new JButton("Usuń");
		btnNewButton_6.setBounds(140, 44, 120, 23);
		asdf.add(btnNewButton_6);
		btnNewButton_6.setContentAreaFilled(false);
		btnNewButton_6.setBackground(buttonsBg);
		btnNewButton_6.setForeground(buttonsText);
		btnNewButton_6.setOpaque(true);
		btnNewButton_6.setBorder(null);
		btnNewButton_6.setFocusable(false);
		btnNewButton_6.addMouseListener(new ButtonMouseListener(btnNewButton_6, buttonsBg));
		btnNewButton_6.setFont(ralewayBold.deriveFont(14f));

		lblNewLabel_11 = new JLabel("Nazwa instalacji:");
		lblNewLabel_11.setBounds(10, 16, 100, 14);
		asdf.add(lblNewLabel_11);
		lblNewLabel_11.setForeground(polaText);
		lblNewLabel_11.setFont(ralewayMedium.deriveFont(12f));

		JPanel panel_6 = new JPanel();
		panel_6.setBounds(294, 170, 257, 79);
		podstawowe.add(panel_6);
		panel_6.setLayout(null);
		panel_6.setOpaque(false);
		panel_6.setUI(new TransparentPanelFixUI());
		panel_6.setBackground(new Color(0, 0, 0, 0));
		panel_6.setBorder(BorderFactory.createLineBorder(borders));

		JButton btnNewButton_5x = new JButton("Ponowna instalacja");
		btnNewButton_5x.setBounds(10, 11, 237, 23);
		panel_6.add(btnNewButton_5x);
		btnNewButton_5x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reinstall();
			}
		});
		btnNewButton_5x.setContentAreaFilled(false);
		btnNewButton_5x.setBackground(buttonsBg);
		btnNewButton_5x.setForeground(buttonsText);
		btnNewButton_5x.setOpaque(true);
		btnNewButton_5x.setBorder(null);
		btnNewButton_5x.setFocusable(false);
		btnNewButton_5x.addMouseListener(new ButtonMouseListener(btnNewButton_5x, buttonsBg));
		btnNewButton_5x.setFont(ralewayBold.deriveFont(14f));

		JButton btnNewButton_5 = new JButton("Sprawdź spójnośc plików");
		btnNewButton_5.setBounds(10, 45, 237, 23);
		panel_6.add(btnNewButton_5);
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reinstall();
			}
		});
		btnNewButton_5.setContentAreaFilled(false);
		btnNewButton_5.setBackground(buttonsBg);
		btnNewButton_5.setForeground(buttonsText);
		btnNewButton_5.setOpaque(true);
		btnNewButton_5.setBorder(null);
		btnNewButton_5.setFocusable(false);
		btnNewButton_5.addMouseListener(new ButtonMouseListener(btnNewButton_5, buttonsBg));
		btnNewButton_5.setFont(ralewayBold.deriveFont(14f));

		zaawansowane = new JPanel();
		zaawansowane.setBounds(10, 282, 561, 165);
		instalacje.add(zaawansowane);
		zaawansowane.setLayout(null);
		zaawansowane.setOpaque(false);
		zaawansowane.setUI(new TransparentPanelFixUI());
		zaawansowane.setBackground(new Color(0, 0, 0, 0));
		zaawansowane.setBorder(BorderFactory.createLineBorder(borders));

		lblNewLabel_8 = new JLabel("Zaawansowane");
		lblNewLabel_8.setBounds(10, 11, 221, 14);
		zaawansowane.add(lblNewLabel_8);
		lblNewLabel_8.setForeground(polaText);
		lblNewLabel_8.setFont(ralewayMedium.deriveFont(12f));

		JLabel lblPamiRam = new JLabel("Pamięć RAM:", SwingConstants.RIGHT);
		lblPamiRam.setBounds(10, 36, 90, 25);
		zaawansowane.add(lblPamiRam);
		lblPamiRam.setForeground(polaText);
		lblPamiRam.setFont(ralewayBold.deriveFont(12f));

		slider = new JSlider();
		slider.setBounds(110, 36, 366, 25);
		zaawansowane.add(slider);
		slider.setOpaque(false);
		slider.setForeground(buttonsBg);
		// $hide>>$
		slider.setMaximum(sliderElements);
		slider.setMinimum(512 / sliderStep);
		// $hide<<$
		slider.addChangeListener(js);
		// slider.setPaintTicks(true);
		// slider.setPaintLabels(true);
		// slider.setMinorTickSpacing(5);
		// slider.setMajorTickSpacing(25);
		slider.setUI(new CustomSliderUI(slider));
		slider.setFocusable(false);
		ramField = new JTextField("2048");
		ramField.setBounds(486, 36, 40, 25);
		zaawansowane.add(ramField);
		ramField.setColumns(10);
		ramField.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, borders));
		ramField.setBackground(polaTlo);
		ramField.setForeground(polaText);

		JLabel lblMb = new JLabel("MB");
		lblMb.setBounds(528, 36, 23, 25);
		zaawansowane.add(lblMb);
		lblMb.setForeground(polaText);
		lblMb.setFont(ralewayMedium.deriveFont(12f));

		chckbxNewCheckBox_5 = new JCheckBox("Renderowanie programowe na CPU (jeżeli występuje błąd 65543)");
		chckbxNewCheckBox_5.setBounds(10, 120, 541, 23);
		zaawansowane.add(chckbxNewCheckBox_5);
		chckbxNewCheckBox_5.setOpaque(false);
		chckbxNewCheckBox_5.setBackground(polaText);
		chckbxNewCheckBox_5.setForeground(polaText);
		chckbxNewCheckBox_5.setFocusable(false);
		chckbxNewCheckBox_5.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		chckbxNewCheckBox_5.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		chckbxNewCheckBox_5.setFont(ralewayBold.deriveFont(12f));
		chckbxNewCheckBox_5.setToolTipText("Włącz dynamiczny podgląd skina. Możesz wyłaczyć, aby oszczędzać wydajność.");

		chckbxNewCheckBox_6 = new JCheckBox("Własne argumenty JVM");
		chckbxNewCheckBox_6.setBounds(10, 68, 181, 23);
		zaawansowane.add(chckbxNewCheckBox_6);
		chckbxNewCheckBox_6.setOpaque(false);
		chckbxNewCheckBox_6.setBackground(polaText);
		chckbxNewCheckBox_6.setForeground(polaText);
		chckbxNewCheckBox_6.setFocusable(false);
		chckbxNewCheckBox_6.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		chckbxNewCheckBox_6.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		chckbxNewCheckBox_6.setFont(ralewayBold.deriveFont(12f));
		chckbxNewCheckBox_6.setToolTipText("Włącz dynamiczny podgląd skina. Możesz wyłaczyć, aby oszczędzać wydajność.");

		chckbxNewCheckBox_7 = new JCheckBox("Własne argumenty gry");
		chckbxNewCheckBox_7.setBounds(10, 94, 181, 23);
		zaawansowane.add(chckbxNewCheckBox_7);
		chckbxNewCheckBox_7.setOpaque(false);
		chckbxNewCheckBox_7.setBackground(polaText);
		chckbxNewCheckBox_7.setForeground(polaText);
		chckbxNewCheckBox_7.setFocusable(false);
		chckbxNewCheckBox_7.setIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox.png"))));
		chckbxNewCheckBox_7.setSelectedIcon(new ImageIcon(ImageIO.read(Interface.class.getResourceAsStream("/files/images/checkbox_checked.png"))));
		chckbxNewCheckBox_7.setFont(ralewayBold.deriveFont(12f));
		chckbxNewCheckBox_7.setToolTipText("Włącz dynamiczny podgląd skina. Możesz wyłaczyć, aby oszczędzać wydajność.");

		textField_1 = new JTextField();
		textField_1.setBounds(197, 70, 354, 20);
		zaawansowane.add(textField_1);
		textField_1.setColumns(10);
		textField_1.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, borders));
		textField_1.setBackground(polaTlo);
		textField_1.setForeground(polaText);

		textField_2 = new JTextField();
		textField_2.setBounds(197, 96, 354, 20);
		zaawansowane.add(textField_2);
		textField_2.setColumns(10);
		textField_2.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, borders));
		textField_2.setBackground(polaTlo);
		textField_2.setForeground(polaText);

		Themes.loadAsync();

		printToConsole("Witaj w launcherze " + name + " v" + wersja);
		printToConsole("Wszelkie problemy, prośby i sugestie możesz nam zgłaszać używając zakładki kontakt na naszej stronie");
	}

	public static void show(boolean show) {
		frame.setVisible(show);
	}

	static NumberFormat nf = NumberFormat.getInstance();

	public static void updateDownloadInfo(Download downloader) {
		getProgressBar().setValue((int) downloader.getProgressInPercent());
		String string = nf.format(downloader.getProgressInPercent()).replace("-", "");
		getLblPobieraniePobrano().setText("Pobrano: " + downloader.getDownloaded() / 1024 / 1024 + "/" + downloader.getSize() / 1024 / 1024 + "MB [" + string + "%]");
		getLblPobieraniePreskosc().setText("Prędkość: " + downloader.getFormattedSomethingPerSecond());
		getLblPobieranieCzas().setText("Pozostało: " + downloader.getTimeLeft());
	}

	private static void reinstall() {
		GameInstallation gi = GameInstallationManager.getActive();
		gi.setInstalled(false);
		play();
	}

	private static void setVisiblePanels(boolean overall, boolean file, boolean down) {
		setFilePanelVisible(file);
		setDownloadPanelVisible(down);
		setOverallPanelVisible(overall);
	}

	public static void lockButtons(boolean b) {
		/*
		 * folder.setEnabled(!b); statyBtn.setEnabled(!b); statyBtn2.setEnabled(!b);
		 * konsolaBtn.setEnabled(!b); informacjeBtn.setEnabled(!b);
		 * ustawieniaBtn.setEnabled(!b); graj.setEnabled(!b);
		 */
		konsolaBtn.setEnabled(!b);
		informacjeBtn.setEnabled(!b);
		accountsCB.setEnabled(!b);
		installationsCB.setEnabled(!b);
	}

	// static boolean installRunning = false;

	private static void play() {
		switchCard("instalacja");
		GameInstallation gi = GameInstallationManager.getActive();
		try {
			if (gi.isGameRunning()) {
				gi.cleanupGame(false);
				return;
			}

			if (!gi.isInstalled()) {
				if (gi.isInstallerRunning()) {
					// if (installRunning) {
					// user clicked "Cancel"
					lockButtons(true);

					// disable cancel button after click!!!! TODO
					graj.setEnabled(false);

					gi.cancelInstall();
					gi.removeAllObservers();
					lockButtons(false);

					setVisiblePanels(false, false, false);

					printToConsole("  <font color=red>Instalacja została anulowana.</font>");

					return;
				}

				// installRunning = true;

				lockButtons(true);

				printToConsole("");
				printToConsole("-------------------------------------------------------");
				printToConsole("  Przygotowywanie instalacji \"" + gi.getName() + "\" [" + gi.getVersion().getMcVersion() + "]");
				printToConsole("-------------------------------------------------------");

				gi.removeAllObservers();

				gi.addObserver("status", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						GameInstallation.Status s = ((GameInstallation.Status) e.getNewValue());

						if (s == GameInstallation.Status.INSTALLING_CREATING_COMMANDLINE) {
							setVisiblePanels(true, true, false);
						} else if (s == GameInstallation.Status.INSTALLING_DOWNLOADING_ASSETS) {
							setVisiblePanels(true, true, true);
						} else if (s == GameInstallation.Status.INSTALLING_DOWNLOADING_LIBRARIES || s == GameInstallation.Status.INSTALLING_DOWNLOADING_FABRIC_LIBRARIES || s == GameInstallation.Status.INSTALLING_DOWNLOADING_FORGE_LIBRARIES) {
							setVisiblePanels(true, true, true);
						} else if (s == GameInstallation.Status.INSTALLING_DOWNLOADING_CORE || s == GameInstallation.Status.INSTALLING_DOWNLOADING_FORGE || s == GameInstallation.Status.INSTALLING_DOWNLOADING_FABRIC) {
							setVisiblePanels(true, false, true);
						} else if (s == GameInstallation.Status.INSTALLING_FAILED_NETWORK || s == GameInstallation.Status.INSTALLING_FAILED_OTHER || s == GameInstallation.Status.INSTALLING_FINISHED) {
							setVisiblePanels(false, false, false);
							// installRunning = false;
							printToConsole("  <font color=green>Ukończono, teraz możesz uruchomić grę.</font>");
							lockButtons(false);
							reloadPlayBtnText();
							return;
						} else if (s == GameInstallation.Status.INSTALLING_INSTALLING_FABRIC) {
							setVisiblePanels(true, true, false);
						} else if (s == GameInstallation.Status.INSTALLING_INSTALLING_FORGE) {
							setVisiblePanels(true, false, false);
						} else if (s == GameInstallation.Status.INSTALLING_UNZIPPING_LIBRARY) {
							setVisiblePanels(true, true, false);
						}
						printToConsole(s.toString() + "...");
						overallProgressLbl.setText(s.toString());
						overallProgressBar.setValue((int) (((double) s.getStep(gi.getVersion().isModded()) / (double) gi.getStepCount()) * 100d));
						overallProgressCounter.setText(s.getStep(gi.getVersion().isModded()) + "/" + gi.getStepCount());
					}
				});

				gi.addObserver("completedArtifact", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {// max/processed
						int x[] = ((int[]) e.getNewValue());
						int processed = x[0];
						int length = x[1];
						progressFile.setValue((int) ((float) processed / (float) length * 100d));
						processingFilesRight.setText(processed + "/" + length);
					}
				});
				gi.addObserver("processingArtifact", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						String name = (String) e.getNewValue();
						przetworzonePlikiLbl.setText("Przetwarzany plik: " + name);
					}
				});
				gi.addObserver("downloaded", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						/*
						 * int x[] = ((int[]) e.getNewValue()); int processed = x[0]; int length = x[1];
						 * progressDown.setValue((int) ((float) processed / (float) length * 100d));
						 */
						Download d = gi.getDownloader();
						if (d == null) {
							return;
						}
						updateDownloadInfo(d);
					}
				});
				gi.install();
				return;
			}
			gi.prepareToStart();
			gi.startGame();
			graj.setText("Zatrzymaj");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ACCOUNTS CARD */

	private static void accountSelected(String newSel) {
		if (newSel.equals("+ Dodaj nowy profil")) {
			// changing selected item will execute accountSelected function again so no need
			// to re-set everything.
			accountsCB.setSelectedItem(UserProfileManager.getActiveProfile().getProfileName());
			new Thread(() -> {
				UserAccountCreator.showInterface();
			}).start();
			return;
		}
		for (UserProfile p : UserProfileManager.getProfiles()) {
			if (!p.getProfileName().equals(newSel)) {
				continue;
			}
			UserProfileManager.setActiveProfile(p);
			setActiveUserProfile(p);
			return;
		}
	}

	// on btn click
	private static void deleteProfile() {
		new Thread(() -> {
			if (UserProfileManager.getProfiles().size() <= 1) {
				DialogUtil.showConfirmDialog("<font color=red>Nie możesz usunąć ostatniego profilu.\nMusisz wcześniej utworzyć kolejny.</font>");
				return;
			}
			UserProfileManager.getProfiles().remove(UserProfileManager.getActiveProfile());
			UserProfileManager.setActiveProfile(UserProfileManager.getProfiles().get(0));
			setActiveUserProfile(UserProfileManager.getProfiles().get(0));
		}).start();
	}

	public static void setActiveUserProfile(UserProfile up) {
		if (up == null) {
			return;
		}

		// set active game installation - for this user profile.
		GameInstallation ga = GameInstallationManager.getByName(up.getSelectedInstallation());
		if (ga != null) {
			GameInstallationManager.setActive(ga);
			setActiveGameInstallation(ga);
		} else {
			if(!GameInstallationManager.getAll().isEmpty()) {
				ga = GameInstallationManager.getAll().get(0);
				setActiveGameInstallation(ga);
				up.setSelectedInstallation(ga.getName());
			}
		}

		MinecraftProfile ak = up.getAk();
		Type accType = ak.getType();

		upAccTypeMicrosoft.setEnabled(accType == Type.MICROSOFT ? false : true);
		upAccTypeMojang.setEnabled(accType == Type.MOJANG ? false : true);
		upAccTypeNP.setEnabled(accType == Type.NONPREMIUM ? false : true);

		textField.setText(up.getAk().getNickname());

		switchCLCard(panelAccSettings, accType == Type.MOJANG ? "mojang" : (accType == Type.MICROSOFT ? "microsoft" : "nonpremium"));

		accMojangNickLbl.setText((accType == Type.MOJANG && ak.getNickname() != null && ak.isLogged()) ? up.getAk().getNickname() : "");
		accMicrosoftNickLbl.setText((accType == Type.MICROSOFT && ak.getNickname() != null && ak.isLogged()) ? up.getAk().getNickname() : "");

		btnZalogujMojang.setText((accType == Type.MOJANG && ak.getStatus() == Status.LOGGED_IN) ? "Wyloguj" : "Zaloguj się");
		btnZalogujMicrosoft.setText((accType == Type.MICROSOFT && ak.getStatus() == Status.LOGGED_IN) ? "Wyloguj" : "Zaloguj się");

		SkinPreview.prepareSkinPreviewLabels(SkinPreview.SKIN_DYNAMIC, panel_5);
		up.getSkin().showSkin();

		ItemListener al[] = extractItemListeners(accountsCB);
		accountsCB.removeAllItems();
		for (UserProfile gis : UserProfileManager.getProfiles()) {
			accountsCB.addItem(gis.getProfileName());
			if (gis == up) {
				accountsCB.setSelectedItem(gis.getProfileName());
			}
		}
		accountsCB.addItem("+ Dodaj nowy profil");
		insertItemListeners(accountsCB,al);
	}

	public static void changeActiveUserProfileType(Type type) {
		UserProfile up = UserProfileManager.getActiveProfile();
		up.getAk().setType(type);
		up.getAk().logout();
		// up.resetSkin();
		SkinPreview.loadSkinPreview(null);
		setActiveUserProfile(up);
	}

	public static void userProfileLoginLogout() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				UserProfile up = UserProfileManager.getActiveProfile();
				if (up.getAk().getStatus() == Status.LOGGED_IN) {
					// logout
					up.getAk().logout();
					SkinPreview.loadSkinPreview(null);
					setActiveUserProfile(up);
				} else {
					if (up.getAk().getType() == Type.MOJANG) {
						MojangLoginUI ui = new MojangLoginUI();
						ui.addObserver("status", new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if (evt.getNewValue() == MojangLoginUI.Status.SUCCESS) {
									up.setAk(ui.getProfile());
									setActiveUserProfile(up);
									up.getSkin().refreshAsync();
								}
							}
						});
						ui.open();
					} else {
						MicrosoftLoginUI ui = new MicrosoftLoginUI();
						ui.addObserver("status", new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if (evt.getNewValue() == Microsoft.Status.SUCCESS) {
									up.setAk(ui.getProfile());
									setActiveUserProfile(up);
									up.getSkin().refreshAsync();
								}
							}
						});
						ui.open();
					}
				}
			}
		}).start();
	}

	public static void userProfileSettings() {
		Utils.openUrl("https://www.minecraft.net/pl-pl/login");
	}

	public static void userProfileNickChanged(String s) {
		UserProfileManager.getActiveProfile().getAk().setNickname(s);
	}

	public static void accountSettings() {
		switchCard("konta");
	}

	/* INSTALLATIONS CARD */

	public static void installationSettings() {
		switchCard("instalacje");
	}

	private static void radioSetSelected(JRadioButton btn, boolean b) {
		if (btn.getActionListeners().length == 0) {
			btn.setSelected(b);
			return;
		}
		ActionListener al = btn.getActionListeners()[0];
		btn.removeActionListener(al);
		btn.setSelected(b);
		btn.addActionListener(al);
	}

	private static void chkSetSelected(JCheckBox btn, boolean b) {
		if (btn.getActionListeners().length == 0) {
			btn.setSelected(b);
			return;
		}
		ActionListener al = btn.getActionListeners()[0];
		btn.removeActionListener(al);
		btn.setSelected(b);
		btn.addActionListener(al);
	}

	private static void comboSetSelected(JComboBox<?> btn, Object b) {
		if (btn.getItemListeners().length == 0) {
			btn.setSelectedItem(b);
			return;
		}
		ItemListener al = btn.getItemListeners()[0];
		btn.removeItemListener(al);
		btn.setSelectedItem(b);
		btn.addItemListener(al);
	}

	private static ItemListener[] extractItemListeners(JComponent comp) {
		if (comp instanceof JComboBox) {
			JComboBox<?> jc = (JComboBox<?>) comp;
			ItemListener al[] = jc.getItemListeners();
			for (ItemListener a : al) {
				jc.removeItemListener(a);
			}
			return al;
		}
		return null;
	}

	private static void insertItemListeners(JComponent comp, ItemListener[] als) {
		if (comp instanceof JComboBox) {
			JComboBox<?> jc = (JComboBox<?>) comp;
			for (ItemListener al : als) {
				jc.addItemListener(al);
			}
		}
	}

	private static void installationSelected(String newSel) {
		if (newSel.equals("+ Dodaj instalację")) {
			// changing selected item will execute accountSelected function again so no need
			// to re-set everything.
			installationsCB.setSelectedItem(GameInstallationManager.getActive().getName());
			new Thread(() -> {
				// UserAccountCreator.showInterface();
				GameVersionCreator.showInterface();
			}).start();
			return;
		}
		for (GameInstallation i : GameInstallationManager.getAll()) {
			if (!i.getName().equals(newSel)) {
				continue;
			}
			GameInstallationManager.setActive(i);
			setActiveGameInstallation(i);
			UserProfileManager.getActiveProfile().setSelectedInstallation(newSel);
			return;
		}
	}

	public static void setActiveGameInstallation(GameInstallation gi) {
		if (gi == null) {
			return;// TODO
		}

		chkSetSelected(showSnapshotsChb, gi.showSnapshots());
		chkSetSelected(showOldsChb, gi.showOlds());

		loadFabricAddons();
		// TODO setActiveGameInstallation

		GameVersion ver = gi.getVersion();

		// todo
		comboSetSelected(mcVersionsCB, ver.getMcVersion());

		if (!ver.isModded())
			radioSetSelected(nullRadio, true);
		if (ver.isForge())
			radioSetSelected(forgeRadio, ver.isForge());
		if (ver.isFabric())
			radioSetSelected(fabricRadio, ver.isFabric());

		switchCLCard(moddingApiSettings, ver.getModded().getString());

		ItemListener[] al = extractItemListeners(installationsCB);
		installationsCB.removeAllItems();
		for (GameInstallation gis : GameInstallationManager.getAll()) {
			installationsCB.addItem(gis.getName());
			if (gis == gi) {
				installationsCB.setSelectedItem(gis.getName());
			}
		}
		installationsCB.addItem("+ Dodaj instalację");
		insertItemListeners(installationsCB,al);
		// new Exception().printStackTrace();
		reloadVersions();
	}

	private static void loadFabricAddons() {
		GameInstallation gi = GameInstallationManager.getActive();

		try {
			boolean sodiumComp = Sodium.isCompatible(gi.getVersion().getMcVersion());
			chkSetSelected(installSodium, gi.isInstallSodium() && sodiumComp);
			installSodium.setEnabled(sodiumComp);// TODO is incompatible disable in installation
		} catch (Exception e) {
			if (e.getMessage().equals("network")) {
				Interface.printToConsole("<font color=orange>Nie można zaktualizować wersji sodium. Problem z połączeniem.</font>");
			}
		}
		try {
			boolean fabricApiComp = FabricAPI.isCompatible(gi.getVersion().getMcVersion());
			chkSetSelected(installFabricAPI, gi.isInstallFabricAPI() && fabricApiComp);
			installFabricAPI.setEnabled(fabricApiComp);
		} catch (Exception e) {
			if (e.getMessage().equals("network")) {
				Interface.printToConsole("<font color=orange>Nie można zaktualizować wersji FabricAPI. Problem z połączeniem.</font>");
			}
		}
	}

	private static void setInstallationModded(String s) {
		GameInstallation gi = GameInstallationManager.getActive();
		if (gi == null) {
			System.out.println("A");
			return;
		}
		GameVersion gv = gi.getVersion();
		if (s.equals("forge")) {
			gv.setModded(ModApi.FORGE, (String) forgeVersionCombo.getSelectedItem());
		} else if (s.equals("fabric")) {
			gv.setModded(ModApi.FABRIC);
		} else {
			gv.setModded(ModApi.NULL);
		}
		gi.setInstalled(false);
		switchCLCard(moddingApiSettings, s);
	}

	public static void reloadPlayBtnText() {
		GameInstallation gi = GameInstallationManager.getActive();
		if (gi.isInstallerRunning()) {
			graj.setText("Anuluj");
		} else if (gi.isGameRunning()) {
			graj.setText("Zatrzymaj");
		} else if (gi.isInstalled()) {
			graj.setText("Graj");
		} else {
			graj.setText("Zainstaluj");
		}
		graj.setEnabled(true);
	}

	public static void gameVesionChangeRequested(String s) {
		loadForgeComboBox(s);
		GameInstallation gi = GameInstallationManager.getActive();
		gi.getVersion().setVersion(s);
		gi.setInstalled(false);
		loadFabricAddons();
	}

	private static void loadForgeComboBox(String mcVersion) {
		forgeVersionCombo.removeAllItems();
		List<Version> vers = ForgeVersionManager.getForgesListForMc(mcVersion);
		if (vers == null) {
			// System.out.println("A");
			// setInstallationModded("null");
			forgeRadio.setEnabled(false);
			// if(forgeRadio.isSelected()) {
			// nullRadio.setSelected(true);
			// }
			return;// TODO forge versions fix
		}
		forgeRadio.setEnabled(true);

		for (Version s : vers) {
			forgeVersionCombo.addItem(s.get());
		}
		forgeVersionCombo.setSelectedItem(ForgeVersionManager.getPromotedVersion(mcVersion).getRecommended());
	}

	public static void reloadForgeComboBox() {
		loadForgeComboBox((String) mcVersionsCB.getSelectedItem());
	}

	public static void reloadVersions() {
		GameInstallation gi = GameInstallationManager.getActive();
		if (gi == null) {
			return;
		}
		loadVersions(gi.showSnapshots(), gi.showOlds());
	}

	private static void loadVersions(boolean snapshots, boolean olds) {
		if (GameInstallationManager.getVersionsManifest() == null) {
			return;
		}

		ItemListener al = null;
		if (mcVersionsCB.getItemListeners().length > 0) {
			al = mcVersionsCB.getItemListeners()[0];
			mcVersionsCB.removeItemListener(al);
		} // temp disable itemlistener to prevent triggering //gameVesionChangeRequested

		mcVersionsCB.removeAllItems();
		JsonArray versions = GameInstallationManager.getVersionsManifest().get("versions").getAsJsonArray();
		for (int i = 0; i < versions.size(); i++) {
			JsonObject ver = versions.get(i).getAsJsonObject();
			String id = ver.get("id").getAsString();
			String type = ver.get("type").getAsString();

			if (type.equals("old_alpha") && !olds) {
				continue;
			}

			if (type.equals("old_beta") && !olds) {
				continue;
			}

			if (type.equals("snapshot") && !snapshots) {
				continue;
			}

			mcVersionsCB.addItem(id);
		}
		mcVersionsCB.setSelectedItem(GameInstallationManager.getActive().getVersion().getMcVersion());

		if (al != null) {
			mcVersionsCB.addItemListener(al);
		}
	}

	private static void showSnapshotsOldsChanged() {
		GameInstallation gi = GameInstallationManager.getActive();
		gi.showSnapshots(showSnapshotsChb.isSelected());
		gi.showOlds(showOldsChb.isSelected());
		reloadVersions();
	}

	/* CONSOLE */

	static void changeConsoleHeight(int i) {
		if (i == 0) {
			consolePanel.setBounds(consolePanel.getBounds().x, consolePanel.getBounds().y, consolePanel.getBounds().width, 208);
			console.setBounds(console.getBounds().x, console.getBounds().y, console.getBounds().width, 187);
		} else if (i == 1) {
			consolePanel.setBounds(consolePanel.getBounds().x, consolePanel.getBounds().y, consolePanel.getBounds().width, 284);
			console.setBounds(console.getBounds().x, console.getBounds().y, console.getBounds().width, 262);
		} else if (i == 2) {
			consolePanel.setBounds(consolePanel.getBounds().x, consolePanel.getBounds().y, consolePanel.getBounds().width, 360);
			console.setBounds(console.getBounds().x, console.getBounds().y, console.getBounds().width, 338);
		} else if (i == 3) {
			consolePanel.setBounds(consolePanel.getBounds().x, consolePanel.getBounds().y, consolePanel.getBounds().width, 436);
			console.setBounds(console.getBounds().x, console.getBounds().y, console.getBounds().width, 414);
		}
		recalculateConsoleAddonsPositions();
	}

	private static Rectangle slot0;
	private static Rectangle slot1;
	private static Rectangle slot2;

	public static void setFilePanelVisible(boolean visible) {
		plikPanel.setVisible(visible);
		recalculateProgressPanelsPositions();
	}

	public static void setDownloadPanelVisible(boolean visible) {
		downloadPanel.setVisible(visible);
		recalculateProgressPanelsPositions();
	}

	public static void setOverallPanelVisible(boolean visible) {
		unzipPanel.setVisible(visible);
		recalculateProgressPanelsPositions();
	}

	public static void recalculateProgressPanelsPositions() {
		if (downloadPanel.isVisible() && unzipPanel.isVisible() && plikPanel.isVisible()) {
			unzipPanel.setBounds(slot0);
			plikPanel.setBounds(slot1);
			downloadPanel.setBounds(slot2);

			changeConsoleHeight(0);
		} else if (!downloadPanel.isVisible() && unzipPanel.isVisible() && plikPanel.isVisible()) {
			unzipPanel.setBounds(slot1);
			plikPanel.setBounds(slot2);

			changeConsoleHeight(1);
		} else if (downloadPanel.isVisible() && !unzipPanel.isVisible() && plikPanel.isVisible()) {
			plikPanel.setBounds(slot1);
			downloadPanel.setBounds(slot2);

			changeConsoleHeight(1);
		} else if (downloadPanel.isVisible() && unzipPanel.isVisible() && !plikPanel.isVisible()) {
			unzipPanel.setBounds(slot1);
			downloadPanel.setBounds(slot2);

			changeConsoleHeight(1);
		} else if (!downloadPanel.isVisible() && !unzipPanel.isVisible() && plikPanel.isVisible()) {
			plikPanel.setBounds(slot2);

			changeConsoleHeight(2);
		} else if (downloadPanel.isVisible() && !unzipPanel.isVisible() && !plikPanel.isVisible()) {
			downloadPanel.setBounds(slot2);

			changeConsoleHeight(2);
		} else if (!downloadPanel.isVisible() && unzipPanel.isVisible() && !plikPanel.isVisible()) {
			unzipPanel.setBounds(slot2);

			changeConsoleHeight(2);
		} else if (!downloadPanel.isVisible() && !unzipPanel.isVisible() && !plikPanel.isVisible()) {
			changeConsoleHeight(3);
		}
	}

	private static String activeCard = "instalacja";

	public static void switchCard(String card) {
		activeCard = card;
		CardLayout cl = (CardLayout) kontener.getLayout();
		cl.show(kontener, card);
	}

	public static String getCard(String card) {
		return activeCard;
	}

	public static void switchCLCard(JPanel p, String card) {
		CardLayout cl = (CardLayout) p.getLayout();
		cl.show(p, card);
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static JPanel getSkinPanel() {
		return panel_5;
	}

	public static JLabel getThemeNameLbl() {
		return lblThemeName;
	}

	private static List<String> lines = new ArrayList<String>();

	public static synchronized void printToConsole(String s) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				synchronized (console) {
					StringBuilder str = new StringBuilder();
					synchronized (lines) {
						lines.add(s);
						if (lines.size() > 30) {
							lines.remove(0);
						}
						str.append("<html>");
						for (String l : lines) {
							str.append(l + "<br>");
						}
					}
					str.append("<br></html>");
					console.setText(str.toString());
					Logger.log(s);

					str = null;
				}
			}
		});

	}

	private static boolean blinkingCursorState = true;
	private static JLabel lblPobieranie;
	private static JButton button;
	private static JLabel background;
	private static JLabel lblInfo;
	private static Thread blinkingCursor;

	private static void startBlinkingCursor() {
		blinkingCursor = new Thread(new Runnable() {
			public void run() {
				try {
					while (!Thread.interrupted()) {
						if (activeCard == "instalacja") {
							if (blinkingCursorState) {
								blinkingConsoleCursor.setText("");
								blinkingCursorState = false;
							} else {
								blinkingConsoleCursor.setText("_");
								blinkingCursorState = true;
							}
						}
						Thread.sleep(500);
					}
				} catch (Exception e) {

				}
			}
		});
		blinkingCursor.start();
	}

	@SuppressWarnings("unused")
	private static void stopBlinkingCursor() {
		if (blinkingCursor != null && !blinkingCursor.isInterrupted()) {
			blinkingCursor.interrupt();
			blinkingCursor = null;
			blinkingConsoleCursor.setText("");

		}
	}

	private static void recalculateConsoleAddonsPositions() {
		blinkingConsoleCursor.setBounds(console.getBounds().x + 2, console.getBounds().height, blinkingConsoleCursor.getBounds().width, blinkingConsoleCursor.getBounds().height);
		consoleSaveButton.setBounds(console.getBounds().width, console.getBounds().height, consoleSaveButton.getBounds().width, consoleSaveButton.getBounds().height);
	}

	/*
	 * public static JLabel getStatusLbl() { return statusLabel; }
	 */

	public static JLabel getBackgroudnLbl() {
		return background;
	}

	public static JLabel getStatyLabel() {
		return statyLabel;
	}

	public static JProgressBar getProgressBar() {
		return progressDown;
	}

	public static JProgressBar getProgressBar_1() {
		return overallProgressBar;
	}

	public static JProgressBar getProgressBar_2() {
		return progressFile;
	}

	public static JLabel getLblPrzetworzonePliki() {
		return przetworzonePlikiLbl;
	}

	public static JLabel getLblPobieraniePobrano() {
		return lblPobieraniePobrano;
	}

	public static JLabel getLblPobieranieCzas() {
		return lblPobieranieCzas;
	}

	public static JLabel getLblPobieraniePreskosc() {
		return lblPobieraniePreskosc;
	}

	public static JLabel getWypakowywaniePlik() {
		return overallProgressLbl;
	}

	public static JLabel getWypakowywanieIlosc() {
		return overallProgressCounter;
	}

	public static JLabel getPobieranieLbl() {
		return lblPobieranie;
	}

	public static JLabel getWypakowywanieLbl() {
		return overallProgressLblY;
	}

	public static JComboBox<String> getMcVersionsCB() {
		return mcVersionsCB;
	}

	static Rectangle kontenerOrigBounds;
	static Rectangle consolePanelOrigBounds;
	static Rectangle consoleOrigBounds;

	public static void consoleMode(boolean mode) {
		if (mode) {
			rightSide.setVisible(false);

			kontenerOrigBounds = kontener.getBounds();
			consolePanelOrigBounds = consolePanel.getBounds();
			consoleOrigBounds = console.getBounds();

			kontener.setBounds(kontener.getBounds().x, kontener.getBounds().y, 780, kontener.getBounds().height);
			consolePanel.setBounds(consolePanel.getBounds().x, consolePanel.getBounds().y, 760, consolePanel.getBounds().height);
			console.setBounds(console.getBounds().x, console.getBounds().y, 740, console.getBounds().height);
			recalculateConsoleAddonsPositions();
			stopBlinkingCursor();

			// frame.setBounds(screenSize.width / 2 - 842 / 2 - 100, screenSize.height / 2 -
			// 542 / 2 - 100, 842, 542);

			switchCard("aktualizacja");
			setDownloadPanelVisible(false);
			setOverallPanelVisible(false);
			setFilePanelVisible(false);
		} else {
			rightSide.setVisible(true);

			kontener.setBounds(kontenerOrigBounds);
			consolePanel.setBounds(consolePanelOrigBounds);
			console.setBounds(consoleOrigBounds);

			recalculateConsoleAddonsPositions();
			stopBlinkingCursor();
		}
	}

	public static JButton getGraj() {
		return graj;
	}

	private static JLabel blinkingConsoleCursor;
	private static JButton consoleSaveButton;
	private static int sliderStep;
	private static JPanel plikPanel;
	private static JProgressBar progressFile;
	private static JLabel przetworzonePlikiLbl;
	private static JPanel tyui;
	private static JScrollPane sp;
	private static JButton informacjeBtn;
	private static JButton konsolaBtn;
	private static JButton btnCzasOnline;
	private static JButton btnZabiciGracze;
	private static JButton btnZabiteMoby;
	private static JButton btnmierci;
	private static JLabel statyLabel;
	private static JPanel panel_3;
	private static JPanel panel_4;
	private static JPanel panel_5;
	private static JLabel lblTypSkina;
	private static JLabel lblData;
	private static JButton btnSkinZNicku;
	private static JButton btnSkinZPliku;
	private static JButton btnSkinDomyslny;
	private static JLabel nickSkina;
	private static JButton btnPobierzSkina;
	private static JButton btnLosowySkin;
	private static JCheckBox cbDynamicznyPogladSkina;
	private static JPanel ghjk;
	private static JPanel zxprofiles;
	private static JLabel lblKontoMojang;
	private static JButton upAccTypeMicrosoft;
	private static JButton upAccTypeMojang;
	private static JButton upAccTypeNP;
	private static JPanel panelAccSettings;
	private static JLabel lblNewLabel_1;
	private static PlaceholderTextField textField;
	private static JPanel panel_7;
	private static JButton btnZmieMotyw;
	private static JLabel lblThemeName;
	private static JPanel panel_8;
	private static JButton btnNewButton_3;
	private static JButton btnNewButton_4;
	private static JLabel lblNewLabel_2;
	private static JPanel panelMojang;
	private static JLabel lblUstawieniaKontaMojang;
	private static JLabel accMojangNickLbl;
	private static JButton btnNewButton;
	private static JPanel panelMicrosoft;
	private static JLabel lblUstawieniaKontaMicrosoft;
	private static JLabel accMicrosoftNickLbl;
	private static JButton btnZalogujMicrosoft;
	private static JButton btnNewButton_1;
	private static JButton btnZalogujMojang;
	private static JPanel instalacje;
	private static JTextField ramField;
	private static JSlider slider;
	private static JPanel zaawansowane;
	private static JLabel lblNewLabel_7;
	private static JLabel lblNewLabel_8;
	private static JCheckBox chckbxNewCheckBox_5;
	private static JCheckBox chckbxNewCheckBox_6;
	private static JCheckBox chckbxNewCheckBox_7;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private static JTextField txtNazwaInst;
	private static JComboBox<String> mcVersionsCB;
	private static JCheckBox showOldsChb;
	private static JCheckBox showSnapshotsChb;
	private static JPanel forgeSettings;
	private static JPanel fabricSettings;
	private static JComboBox<String> forgeVersionCombo;
	private static JRadioButton nullRadio;
	private static JRadioButton fabricRadio;
	private static JRadioButton forgeRadio;
	private static JPanel moddingApiSettings;
	private static JLabel lblNewLabel_10;
	private static JLabel lblNewLabel_11;
	private static JComboBox<String> installationsCB;
	private static JComboBox<String> accountsCB;
	private static JLabel processingFilesRight;
	private static JCheckBox installSodium;
	private static JCheckBox installFabricAPI;
	private static JPanel rightSide;
	private static JTextField textField_3;
	private static JPanel panel_2;
	private static JPanel panel_12;
	private static JLabel lblNewLabel_13;
}
