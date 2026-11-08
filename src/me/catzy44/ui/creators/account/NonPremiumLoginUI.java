package me.catzy44.ui.creators.account;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import me.catzy44.Interface;
import me.catzy44.ui.PlaceholderTextField;
import me.catzy44.ui.TransparentPanelFixUI;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.DialogDragListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.UIUtils;

/** Styled nickname prompt used whenever a non-premium account is selected. */
public final class NonPremiumLoginUI {
	private NonPremiumLoginUI() {
	}

	public static void open(Window owner, Consumer<String> onConfirm) {
		int width = 290;
		// The controls end at y=73 inside the panel.  With an 84 px panel,
		// the lower margin is 11 px — the same as the upper margin.
		int height = 28;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		JDialog dialog = new JDialog(owner, "", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);
		dialog.setBackground(new Color(0, 0, 0, 0));
		dialog.setBounds(screen.width / 2 - (width + 61) / 2, screen.height / 2 - (height + 114) / 2, width + 61, height + 114);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(null);
		DialogDragListener drag = new DialogDragListener(dialog);
		dialog.addMouseListener(drag);
		dialog.addMouseMotionListener(drag);

		JLabel icon = new JLabel(new ImageIcon(Interface.class.getResource("/files/images/icon.png")));
		icon.setBounds(21, 16, 20, 20);
		dialog.add(icon);

		JLabel title = new JLabel("Profil non-premium", SwingConstants.CENTER);
		title.setBounds(49, 15, 296, 19);
		title.setFont(Interface.ralewayBold.deriveFont(12f));
		title.setForeground(Interface.polaText);
		dialog.add(title);

		ImageIcon normal = new ImageIcon(Interface.class.getResource("/files/images/x.png"));
		ImageIcon clicked = new ImageIcon(Interface.class.getResource("/files/images/x_clicked.png"));
		ImageIcon rollover = new ImageIcon(Interface.class.getResource("/files/images/x_rollover.png"));
		JButton close = new JButton(normal);
		close.setBounds(width, 16, 38, 13);
		close.setContentAreaFilled(false);
		close.setBorder(null);
		close.addMouseListener(new NavButtonMouseListener(close, normal, rollover, clicked));
		close.addActionListener(e -> dialog.dispose());
		dialog.add(close);

		JPanel panel = new JPanel(null);
		panel.setBounds(21, 38, width + 20, height + 56);
		panel.setBackground(new Color(24, 24, 24));
		panel.setUI(new TransparentPanelFixUI());
		dialog.add(panel);

		JLabel prompt = new JLabel("Wpisz nick gracza", SwingConstants.CENTER);
		prompt.setBounds(10, 11, 276, 25);
		prompt.setForeground(Interface.polaText);
		prompt.setFont(Interface.ralewayBold.deriveFont(12f));
		panel.add(prompt);

		PlaceholderTextField nickname = new PlaceholderTextField("");
		nickname.setPlaceholder("Nick...");
		nickname.setBounds(10, 48, 168, 25);
		nickname.setBorder(UIUtils.createOffsetedLineBorder(0, 5, 0, 0, Interface.borders));
		nickname.setBackground(Interface.polaTlo);
		nickname.setForeground(Interface.polaText);
		panel.add(nickname);

		JButton confirm = new JButton("Dalej");
		confirm.setBounds(188, 48, 112, 25);
		confirm.setContentAreaFilled(false);
		confirm.setBackground(Interface.buttonsBg);
		confirm.setForeground(Interface.buttonsText);
		confirm.setOpaque(true);
		confirm.setBorder(null);
		confirm.setFocusable(false);
		confirm.setFont(Interface.ralewayBold.deriveFont(16f));
		confirm.addMouseListener(new ButtonMouseListener(confirm, Interface.buttonsBg));
		confirm.addActionListener((ActionEvent e) -> {
			String value = nickname.getText().trim();
			if (value.isEmpty()) {
				prompt.setText("Podaj nick gracza");
				return;
			}
			dialog.dispose();
			onConfirm.accept(value);
		});
		panel.add(confirm);
		nickname.addActionListener(e -> confirm.doClick());

		JLabel border = new JLabel(new ImageIcon(DialogUtil.dialogRamkaGenerator(width + 61, height + 114)));
		border.setBounds(0, 0, width + 61, height + 114);
		dialog.add(border);
		dialog.setVisible(true);
	}
}
