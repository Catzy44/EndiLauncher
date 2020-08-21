package me.catzy44.skin;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.catzy44.Interface;
import me.catzy44.skin.skin2d.MinecraftSkin;
import me.catzy44.skin.skin3d.ModelPreview;

public class SkinPreview {
	private static ModelPreview modelPreview = null;
	private static ModelPreview modelPreview2 = null;
	private static JLabel lblMoeszUyMyszki;
	
	private static JLabel skinLbl = null;
	private static JLabel skinLbl2 = null;
	
	public static final int SKIN_STATIC = 0;
	public static final int SKIN_DYNAMIC = 1;
	public static final int SKIN_NONE = 2;
	
	private static int loadedLabels = SKIN_NONE;
	
	public static void prepareSkinPreviewLabels(int type, JPanel panel) {
		if(loadedLabels == type) {return;}
		if(loadedLabels == SKIN_STATIC) {
			panel.remove(skinLbl);
			panel.remove(skinLbl2);
			skinLbl = null;
			skinLbl2 = null;
		} else if(loadedLabels == SKIN_DYNAMIC) {
			modelPreview.stop();
			modelPreview2.stop();
			panel.remove(modelPreview);
			panel.remove(modelPreview2);
			panel.remove(lblMoeszUyMyszki);
			modelPreview = null;
			modelPreview2 = null;
			lblMoeszUyMyszki = null;
		}
		panel.repaint();
		if(type == SKIN_STATIC) {
			skinLbl = new JLabel("");
			//skinLbl.setBounds(10, 28, 110, 196);
			skinLbl.setBounds(10, 28, 110, 196);
			panel.add(skinLbl);
			
			skinLbl2 = new JLabel("");
			//skinLbl2.setBounds(130, 28, 110, 196);
			skinLbl2.setBounds(130, 28, 110, 196);
			panel.add(skinLbl2);
		} else if(type == SKIN_DYNAMIC) {
			modelPreview = new ModelPreview(1.2f,180,196);
			panel.add(modelPreview);
			modelPreview.setBounds(10, 28, 180, 196);
			modelPreview.setOffset(2, 5, 30);
			modelPreview.setTargetFps(60);
			
			modelPreview2 = new ModelPreview(1.4f,180,196);
			modelPreview2.zombie.anim = 1;
			panel.add(modelPreview2);
			modelPreview2.setBounds(200, 28, 180, 196);
			modelPreview2.setOffset(2, 5, 30);
			modelPreview2.xRot = 0.08f;
			modelPreview2.yRot = 0.5f;
			modelPreview2.setTargetFps(15);
			
			//lblMoeszUyMyszki = new JLabel("Możesz użyć myszki do obracania i skalowania modeli!");
			//lblMoeszUyMyszki.setBounds(10, 11, 371, 14);
			lblMoeszUyMyszki = new JLabel("Użyj myszy do obracania modeli");
			lblMoeszUyMyszki.setBounds(180, 11, 371, 14);
			panel.add(lblMoeszUyMyszki);
			lblMoeszUyMyszki.setForeground(Interface.polaText);
			lblMoeszUyMyszki.setFont(Interface.ralewayBold.deriveFont(12f));
		}
		loadedLabels = type;
	}
	
	public static void loadSkinPreview(BufferedImage image) {
		if(image == null) {
			try {
				image = ImageIO.read(Interface.class.getResourceAsStream("/files/images/skins/steve.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(loadedLabels == SKIN_STATIC) {
			MinecraftSkin ms = new MinecraftSkin(image);
			skinLbl.setIcon(new ImageIcon(ms.getPreview(6, MinecraftSkin.SKIN_FRONT)));
			skinLbl2.setIcon(new ImageIcon(ms.getPreview(6, MinecraftSkin.SKIN_BACK)));
		} else if(loadedLabels == SKIN_DYNAMIC) {
			modelPreview.stop();
			modelPreview.zombie.loadTexture(image);
			modelPreview.start();
			
			modelPreview2.stop();
			modelPreview2.zombie.loadTexture(image);
			modelPreview2.zombie.anim = 1;
			modelPreview2.start();
		}
	}
	
	
}
