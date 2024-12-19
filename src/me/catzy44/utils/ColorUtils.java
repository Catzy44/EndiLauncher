package me.catzy44.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

import me.catzy44.tools.Themes.Theme;

public class ColorUtils {
	public static BufferedImage swapImageChannels(BufferedImage source, Theme mode, boolean revert) {
		int w = source.getWidth();
		int h = source.getHeight();
		BufferedImage ret = new BufferedImage(w, h, source.getType());
		for (int i = 0; i < w; i++) {
			for (int k = 0; k < h; k++) {
				int pixel = source.getRGB(i, k);

				int alpha = (pixel >> 24) & 0xff;
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				if (revert) {
					switch (mode) {
					case FUTURISTIC:
						ret.setRGB(i, k, new Color(red, green, blue, alpha).getRGB());
						break;
					case HOLO:
						ret.setRGB(i, k, new Color(red, blue, green, alpha).getRGB());
						break;
					case ENDER:
						ret.setRGB(i, k, new Color(green, red, blue, alpha).getRGB());
						break;
					case LIME:
						ret.setRGB(i, k, new Color(blue, red, green, alpha).getRGB());
						break;
					case SWEET:
						ret.setRGB(i, k, new Color(green, blue, red, alpha).getRGB());
						break;
					case PHOTON:
						ret.setRGB(i, k, new Color(blue, green, red, alpha).getRGB());
						break;
					}
				} else {
					switch (mode) {
					case FUTURISTIC:
						ret.setRGB(i, k, new Color(red, green, blue, alpha).getRGB());
						break;
					case HOLO:
						ret.setRGB(i, k, new Color(red, blue, green, alpha).getRGB());
						break;
					case ENDER:
						ret.setRGB(i, k, new Color(green, red, blue, alpha).getRGB());
						break;
					case LIME:
						ret.setRGB(i, k, new Color(green, blue, red, alpha).getRGB());
						break;
					case SWEET:
						ret.setRGB(i, k, new Color(blue, red, green, alpha).getRGB());
						break;
					case PHOTON:
						ret.setRGB(i, k, new Color(blue, green, red, alpha).getRGB());
						break;
					}
				}
			}
		}
		return ret;
	}

	public static Color swapColorChannels(Color col, Theme mode, boolean revert) {
		if (revert) {
			switch (mode) {
			case FUTURISTIC:
				return new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
			case HOLO:
				return new Color(col.getRed(), col.getBlue(), col.getGreen(), col.getAlpha());
			case ENDER:
				return new Color(col.getGreen(), col.getRed(), col.getBlue(), col.getAlpha());
			case LIME:
				return new Color(col.getBlue(), col.getRed(), col.getGreen(), col.getAlpha());
			case SWEET:
				return new Color(col.getGreen(), col.getBlue(), col.getRed(), col.getAlpha());
			case PHOTON:
				return new Color(col.getBlue(), col.getGreen(), col.getRed(), col.getAlpha());
			}
		} else {
			switch (mode) {
			case FUTURISTIC:
				return new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
			case HOLO:
				return new Color(col.getRed(), col.getBlue(), col.getGreen(), col.getAlpha());
			case ENDER:
				return new Color(col.getGreen(), col.getRed(), col.getBlue(), col.getAlpha());
			case LIME:
				return new Color(col.getGreen(), col.getBlue(), col.getRed(), col.getAlpha());
			case SWEET:
				return new Color(col.getBlue(), col.getRed(), col.getGreen(), col.getAlpha());
			case PHOTON:
				return new Color(col.getBlue(), col.getGreen(), col.getRed(), col.getAlpha());
			}
		}
		return null;
	}
}
