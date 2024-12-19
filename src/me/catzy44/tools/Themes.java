package me.catzy44.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import com.google.gson.JsonObject;

import me.catzy44.Interface;
import me.catzy44.ui.CustomScrollbarUI;
import me.catzy44.ui.JMultilineLabel;
import me.catzy44.ui.listeners.AButtonMouseListener;
import me.catzy44.ui.listeners.ButtonMouseListener;
import me.catzy44.ui.listeners.NavButtonMouseListener;
import me.catzy44.utils.ColorUtils;
import me.catzy44.utils.Locker;
import me.catzy44.utils.Utils;

public class Themes {
	public static void next() {
		if(enabled) {
			replaceAllElementsColors(true);
		}
		cur = cur.next();
		enabled = true;
		replaceAllElementsColors(false);
		Interface.getThemeNameLbl().setText(cur.getPl());
		/*Config.setInt("colorScheme", mode);*/
		
		Config.getConfig().addProperty("theme", cur.toString());//TODO per acc
	}
	
	public static Locker readyLock = new Locker();
	public static void loadAsync() {
		new Thread(()->{
			load();
			readyLock.unlock();
		}).start();
	}
	public static void load() {
		
		JsonObject conf = Config.getConfig();
		if(conf.get("theme") != null) {
			cur = Theme.valueOf(conf.get("theme").getAsString());
		} else {
			conf.addProperty("theme", cur.toString());
		}
		
		if(enabled) {
			replaceAllElementsColors(true);
		}
		enabled = true;
		replaceAllElementsColors(false);
		Interface.getThemeNameLbl().setText(cur.getPl());
	}
	public static void replaceAllElementsColors(boolean revert) {
		Interface.buttonsBg = ColorUtils.swapColorChannels(Interface.buttonsBg,cur,revert);
		Interface.buttonsText = ColorUtils.swapColorChannels(Interface.buttonsText,cur,revert);
		Interface.borderColor = ColorUtils.swapColorChannels(Interface.borderColor,cur,revert);
		Interface.textColor = ColorUtils.swapColorChannels(Interface.textColor,cur,revert);
		Interface.polaTlo = ColorUtils.swapColorChannels(Interface.polaTlo,cur,revert);
		Interface.polaText = ColorUtils.swapColorChannels(Interface.polaText,cur,revert);
		Interface.borders = ColorUtils.swapColorChannels(Interface.borders,cur,revert);
		Interface.panelsBgColor = ColorUtils.swapColorChannels(Interface.panelsBgColor,cur,revert);
		
		JFrame frame = Interface.getFrame();
		List<Component> elements = Utils.getAllComponents(frame);
		for(Component c : elements) {
			replaceElementColors(c,revert);
		}
			
	}
	private static boolean enabled = false;
	private static Theme cur = Theme.FUTURISTIC;
	
	public static enum Theme {
		FUTURISTIC("Futuristic","Futurystyczny"),
		HOLO("Holo","Hologram"),
		ENDER("Ender","Ender"),
		LIME("Lime","Limonka"),
		SWEET("Sweet","Oczojebny róż"),
		PHOTON("Photon","Foton");

		
		private String eng;
		private String pl;

		Theme(String eng, String pl) {
			this.eng = eng;
			this.pl = pl;
		}

		public String getEng() {
			return eng;
		}

		public String getPl() {
			return pl;
		}
		
		private static Theme[] values = values();
		
		public Theme prev() {
	        return values[(ordinal() - 1  + values.length) % values.length];
	    }

	    public Theme next() {
	        return values[(ordinal() + 1) % values.length];
	    }
	}

	public static void recheckElement(Component c) {
		replaceElementColors(c,false);
	}
	
	public static void asdf(Component c)  {
		JButton jb = (JButton) c;
		jb.setBackground(new Color(255,255,127));
	}
	
	public static void replaceElementColors(Component c, boolean revert) {
		if(!enabled) {
			return;
		}
		
		if(c instanceof JLabel) {
			JLabel jl = (JLabel) c;
			if(jl.getWidth() == 20 && jl.getHeight() == 20) {
				return;
			}
			if(jl.getIcon() != null) {
				if(jl == Interface.getBackgroudnLbl()) {
					return;
				}
				jl.setIcon(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(jl.getIcon()),cur,revert)));
			}
			jl.setForeground(ColorUtils.swapColorChannels(jl.getForeground(),cur,revert));
			jl.setBackground(ColorUtils.swapColorChannels(jl.getBackground(),cur,revert));
		} else if(c instanceof JMultilineLabel) {
			JMultilineLabel jl = (JMultilineLabel) c;
			jl.setForeground(ColorUtils.swapColorChannels(jl.getForeground(),cur,revert));
			jl.setBackground(ColorUtils.swapColorChannels(jl.getBackground(),cur,revert));
		} else if(c instanceof JButton) {
			JButton jb = (JButton) c;
			if(jb.getIcon() != null) {
				jb.setIcon(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(jb.getIcon()),cur,revert)));
			}
			if(jb.getMouseListeners().length > 0) {
				for(MouseListener ml : jb.getMouseListeners()) {
					if(ml instanceof ButtonMouseListener) {
						ButtonMouseListener bml = (ButtonMouseListener) ml;
						bml.setColor(ColorUtils.swapColorChannels(bml.getColor(),cur,revert));
					} else if(ml instanceof AButtonMouseListener) {
						AButtonMouseListener bml = (AButtonMouseListener) ml;
						bml.setColor(ColorUtils.swapColorChannels(bml.getColor(),cur,revert));
					} else if(ml instanceof NavButtonMouseListener) {
						NavButtonMouseListener nbml = (NavButtonMouseListener) ml;
						if(nbml.getNormal() != null) {
							nbml.setNormal(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(nbml.getNormal()),cur,revert)));
						}
						if(nbml.getRollover() != null) {
							nbml.setRollover(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(nbml.getRollover()),cur,revert)));
						}
						if(nbml.getClicked() != null) {
							nbml.setClicked(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(nbml.getClicked()),cur,revert)));
						}
					}
				}
			}
			jb.setForeground(ColorUtils.swapColorChannels(jb.getForeground(),cur,revert));
			jb.setBackground(ColorUtils.swapColorChannels(jb.getBackground(),cur,revert));
			if(jb.getBorder() instanceof LineBorder) {
				LineBorder lb = (LineBorder) jb.getBorder();
				jb.setBorder(BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness()));
			}
		} else if(c instanceof JCheckBox) {
			JCheckBox jb = (JCheckBox) c;
			if(jb.getIcon() != null) {
				jb.setIcon(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(jb.getIcon()),cur,revert)));
			}
			if(jb.getSelectedIcon() != null) {
				jb.setSelectedIcon(new ImageIcon(ColorUtils.swapImageChannels(Utils.iconToBufferedImage(jb.getSelectedIcon()),cur,revert)));
			}
			jb.setForeground(ColorUtils.swapColorChannels(jb.getForeground(),cur,revert));
			jb.setBackground(ColorUtils.swapColorChannels(jb.getBackground(),cur,revert));
		} else if(c instanceof JTextField) {
			JTextField jp = (JTextField) c;
			jp.setForeground(ColorUtils.swapColorChannels(jp.getForeground(),cur,revert));
			jp.setBackground(ColorUtils.swapColorChannels(jp.getBackground(),cur,revert));
			if(jp.getBorder() instanceof LineBorder) {
				LineBorder lb = (LineBorder) jp.getBorder();
				jp.setBorder(BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness()));
			} else if (jp.getBorder() instanceof CompoundBorder) {
				CompoundBorder cb = (CompoundBorder) jp.getBorder();
				if(cb.getInsideBorder() instanceof LineBorder) {
					LineBorder lb = (LineBorder) cb.getInsideBorder();
					jp.setBorder(new CompoundBorder(cb.getOutsideBorder(),BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness())));
				} else if(cb.getOutsideBorder() instanceof LineBorder) {
					LineBorder lb = (LineBorder) cb.getOutsideBorder();
					jp.setBorder(new CompoundBorder(BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness()),cb.getInsideBorder()));
				}
			}
		} else if(c instanceof JPanel) {
			JPanel jp = (JPanel) c;
			jp.setForeground(ColorUtils.swapColorChannels(jp.getForeground(),cur,revert));
			jp.setBackground(ColorUtils.swapColorChannels(jp.getBackground(),cur,revert));
			if(jp.getBorder() instanceof LineBorder) {
				LineBorder lb = (LineBorder) jp.getBorder();
				jp.setBorder(BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness()));
			}
		} else if(c instanceof JScrollPane) {
			JScrollPane jp = (JScrollPane) c;
			jp.setForeground(ColorUtils.swapColorChannels(jp.getForeground(),cur,revert));
			jp.setBackground(ColorUtils.swapColorChannels(jp.getBackground(),cur,revert));
			if(jp.getBorder() instanceof LineBorder) {
				LineBorder lb = (LineBorder) jp.getBorder();
				jp.setBorder(BorderFactory.createLineBorder(ColorUtils.swapColorChannels(lb.getLineColor(),cur,revert), lb.getThickness()));
			}
			if(jp.getVerticalScrollBar().getUI() instanceof CustomScrollbarUI) {
				CustomScrollbarUI csui = (CustomScrollbarUI) jp.getVerticalScrollBar().getUI();
				csui.setBlue(ColorUtils.swapColorChannels(csui.getBlue(),cur,revert));
				csui.setDarkerBlue(ColorUtils.swapColorChannels(csui.getDarkerBlue(),cur,revert));
				csui.setDark(ColorUtils.swapColorChannels(csui.getDark(),cur,revert));
			}
		} else if(c instanceof JSlider) {
			JSlider jp = (JSlider) c;
			jp.setForeground(ColorUtils.swapColorChannels(jp.getForeground(),cur,revert));
			jp.setBackground(ColorUtils.swapColorChannels(jp.getBackground(),cur,revert));
		} else if(c instanceof JProgressBar) {
			JProgressBar jp = (JProgressBar) c;
			jp.setForeground(ColorUtils.swapColorChannels(jp.getForeground(),cur,revert));
			jp.setBackground(ColorUtils.swapColorChannels(jp.getBackground(),cur,revert));
		}
	
	}
}
