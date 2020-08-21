package me.catzy44.ui.creators;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import me.catzy44.Interface;
import me.catzy44.struct.accounts.Microsoft;
import me.catzy44.struct.accounts.Microsoft.Status;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.DialogDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.Utils;

public class MicrosoftLoginUI {
	private Microsoft m;
	private JLabel statusLbl;
	private JDialog frame;
	private JLabel lblMess;
	private JButton zaloguj;
	
	public void open() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				show();
			}
		});
		m = new Microsoft();
		m.addObserver("status", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				Status s = (Status) e.getNewValue();
				Status last = (Status) e.getOldValue();
				if(last == Status.WAITING_FOR_USER_TO_LOGIN) {
					lblMess.setText("Przetwarzanie danych...");
					zaloguj.setEnabled(false);
					
					Interface.getFrame().toFront();
					Interface.getFrame().requestFocus();
					
					frame.toFront();
					frame.requestFocus();
				}
				if(s == Status.SUCCESS || s == Status.FAILED) {
					if(s == Status.SUCCESS) {
						lblMess.setText("Ukończono");
						zaloguj.setText("Gotowe");
						statusLbl.setText("");
						zaloguj.removeActionListener(zaloguj.getActionListeners()[0]);
						zaloguj.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								closeFrame();
								pcs.firePropertyChange("status", Status.FAILED, Status.SUCCESS);
							}
						});
						zaloguj.setEnabled(true);
						
						/*UserProfileManager.getActiveProfile().setAk(m.getAk());
						Interface.setActiveUserProfile(UserProfileManager.getActiveProfile());
						UserProfileManager.getActiveProfile().getSkin().refreshAsync();*/
						
						return;
					} else if(s == Status.FAILED) {
						lblMess.setText("Błąd!");
						zaloguj.setText("Spróbuj ponownie");
						zaloguj.removeActionListener(zaloguj.getActionListeners()[0]);
						zaloguj.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								m.login();
							}
						});
					}
					zaloguj.setEnabled(true);
					
					lblMess.setText("Strona logowania została otwarta");
				}
				statusLbl.setText(s.getMessage());
			}
		});
		m.login();
	}
	
	public MinecraftProfile getProfile() {
		return m.getAk();
	}

	public void closeFrame() {
		frame.setVisible(false);
		frame = null;
		pcs.firePropertyChange("status", Status.FAILED, Status.FAILED);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void show() {
		try {

			int w = 240;
			int h = 140;

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			frame = new JDialog(SwingUtilities.windowForComponent(Interface.getFrame()),"",Dialog.ModalityType.APPLICATION_MODAL);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setBounds(screenSize.width / 2 - (w + 61) / 2, screenSize.height / 2 - (h + 114) / 2, w + 61, h + 114);
			frame.getContentPane().setLayout(null);
			frame.setUndecorated(true);
			frame.setBackground(new Color(0, 0, 0, 0));
			frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			DialogDragListener frameDragListener = new DialogDragListener(frame);
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
					closeFrame();
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

			JLabel lblLogowanieDoKonta = new JLabel("Logowanie do konta Microsoft", SwingConstants.CENTER);
			lblLogowanieDoKonta.setBounds(10, 11, 240, 25);
			lblLogowanieDoKonta.setForeground(Interface.polaText);
			lblLogowanieDoKonta.setFont(Interface.ralewayBold.deriveFont(12f));
			panel.add(lblLogowanieDoKonta);

			JButton btnZapomnialem = new JButton("Zapomniałem hasła");
			btnZapomnialem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// DialogUtil.showConfirmDialog("Ta funkcja jeszcze nie działa.\nNapisz do
							// administracji.\n");
							Utils.openUrl("https://www.minecraft.net/pl-pl/password/forgot");
							closeFrame();
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

			JButton btnRegister = new JButton("Rejestracja");
			btnRegister.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					closeFrame();
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

			zaloguj = new JButton("Otwórz ponownie");
			zaloguj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Utils.openUrl(m.getStage1Str());
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
			
			lblMess = new JLabel("Strona logowania została otwarta");
			lblMess.setBounds(10, 47, 240, 25);
			lblMess.setForeground(Interface.polaText);
			lblMess.setFont(Interface.ralewayBold.deriveFont(12f));
			lblMess.setHorizontalAlignment(JLabel.CENTER);
			panel.add(lblMess);
			
			statusLbl = new JLabel("");
			statusLbl.setBounds(10, 83, 240, 14);
			statusLbl.setForeground(Interface.polaText);
			statusLbl.setFont(Interface.ralewayBold.deriveFont(12f));
			statusLbl.setHorizontalAlignment(JLabel.CENTER);
			panel.add(statusLbl);

			JLabel lblNewLabel = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(w + 61, h + 114)));
			lblNewLabel.setBounds(0, 0, w + 61, h + 114);
			frame.getContentPane().add(lblNewLabel);

			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "MicrosoftUI";
	};
}
