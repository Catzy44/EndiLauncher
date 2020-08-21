package me.catzy44.skin.skin2d;

import static me.catzy44.skin.skin2d.Constants.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class MinecraftSkin {
	
	public static final int SKIN_FRONT = 0;
	public static final int SKIN_BACK = 1;
	
    public static MinecraftSkin fetch(String username) throws IOException {
        return fromUrl(Constants.getSkinUrl(username));
    }

    public static MinecraftSkin fromUrl(URL url) throws IOException {
        return new MinecraftSkin(url.openStream());
    }

    private final BufferedImage skin;

    public MinecraftSkin(BufferedImage skin) {
        this.skin = skin;
        Util.correctHelmetTransparency(skin);
    }

    public MinecraftSkin(InputStream inputStream) throws IOException {
        this(ImageIO.read(inputStream));
    }

    public MinecraftSkin(File file) throws IOException {
        this(ImageIO.read(file));
    }

    protected void draw(Graphics2D g, int sx, int sy, int sw, int sh, int dx,
            int dy, int scale) {
        draw(g, sx, sy, sw, sh, dx, dy, scale, false, false);
    }

    protected void draw(Graphics2D g, int sx, int sy, int sw, int sh, int dx,
            int dy, int scale, boolean flip) {
        draw(g, sx, sy, sw, sh, dx, dy, scale, flip, false);
    }

    protected void draw(Graphics2D g, int sx, int sy, int sw, int sh, int dx,
            int dy, int scale, boolean flip, boolean transparent) {
        int dx1 = dx * scale;
        int dx2 = (dx + sw)  * scale;
        int dy1 = dy * scale;
        int dy2 = (dy + sh) * scale;

        if (flip) {
            int tmp = dx1;
            dx1 = dx2;
            dx2 = tmp;
        }

        if (transparent) {
            g.drawImage(skin, dx1, dy1, dx2, dy2,
                    sx, sy, sx + sw, sy + sh,
                    null);
        } else {
            g.drawImage(skin, dx1, dy1, dx2, dy2,
                    sx, sy, sx + sw, sy + sh,
                    Color.BLACK, null);
        }
    }

    protected BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getPreview(int scale) {
        return getPreview(scale, SKIN_FRONT);
    }

    public BufferedImage getPreview(int scale, int skinType) {
		BufferedImage img = createImage(PREVIEW_WIDTH * scale, PREVIEW_HEIGHT * scale);
		Graphics2D g = img.createGraphics();
		if (skinType == SKIN_FRONT) {

			draw(g, SRC_FRONT_HEAD_X, SRC_FRONT_HEAD_Y, SRC_FRONT_HEAD_W, SRC_FRONT_HEAD_H, PREVIEW_HEAD_X, PREVIEW_HEAD_Y, scale);
			/*
			 * draw(g, SRC_HELMET_X, SRC_HELMET_Y, SRC_HELMET_W, SRC_HELMET_H,
			 * PREVIEW_HELMET_X, PREVIEW_HELMET_Y, scale, false, true);
			 */
			draw(g, SRC_FRONT_BODY_X, SRC_FRONT_BODY_Y, SRC_FRONT_BODY_W, SRC_FRONT_BODY_H, PREVIEW_BODY_X, PREVIEW_BODY_Y, scale);
			draw(g, SRC_FRONT_ARM_X, SRC_FRONT_ARM_Y, SRC_FRONT_ARM_W, SRC_FRONT_ARM_H, PREVIEW_LEFT_ARM_X, PREVIEW_LEFT_ARM_Y, scale);
			draw(g, SRC_FRONT_ARM_X, SRC_FRONT_ARM_Y, SRC_FRONT_ARM_W, SRC_FRONT_ARM_H, PREVIEW_RIGHT_ARM_X, PREVIEW_RIGHT_ARM_Y, scale, true);
			draw(g, SRC_FRONT_LEG_X, SRC_FRONT_LEG_Y, SRC_FRONT_LEG_W, SRC_FRONT_LEG_H, PREVIEW_LEFT_LEG_X, PREVIEW_LEFT_LEG_Y, scale);
			draw(g, SRC_FRONT_LEG_X, SRC_FRONT_LEG_Y, SRC_FRONT_LEG_W, SRC_FRONT_LEG_H, PREVIEW_RIGHT_LEG_X, PREVIEW_RIGHT_LEG_Y, scale, true);
		} else {
			draw(g, SRC_BACK_HEAD_X, SRC_BACK_HEAD_Y, SRC_BACK_HEAD_W, SRC_BACK_HEAD_H, PREVIEW_HEAD_X, PREVIEW_HEAD_Y, scale);
			/*
			 * draw(g, SRC_HELMET_X, SRC_HELMET_Y, SRC_HELMET_W, SRC_HELMET_H,
			 * PREVIEW_HELMET_X, PREVIEW_HELMET_Y, scale, false, true);
			 */
			draw(g, SRC_BACK_BODY_X, SRC_BACK_BODY_Y, SRC_BACK_BODY_W, SRC_BACK_BODY_H, PREVIEW_BODY_X, PREVIEW_BODY_Y, scale);
			draw(g, SRC_BACK_ARM_X, SRC_BACK_ARM_Y, SRC_BACK_ARM_W, SRC_BACK_ARM_H, PREVIEW_LEFT_ARM_X, PREVIEW_LEFT_ARM_Y, scale);
			draw(g, SRC_BACK_ARM_X, SRC_BACK_ARM_Y, SRC_BACK_ARM_W, SRC_BACK_ARM_H, PREVIEW_RIGHT_ARM_X, PREVIEW_RIGHT_ARM_Y, scale, true);
			draw(g, SRC_BACK_LEG_X, SRC_BACK_LEG_Y, SRC_BACK_LEG_W, SRC_BACK_LEG_H, PREVIEW_LEFT_LEG_X, PREVIEW_LEFT_LEG_Y, scale);
			draw(g, SRC_BACK_LEG_X, SRC_BACK_LEG_Y, SRC_BACK_LEG_W, SRC_BACK_LEG_H, PREVIEW_RIGHT_LEG_X, PREVIEW_RIGHT_LEG_Y, scale, true);
		}

		g.dispose();
        return img;
    }

    public BufferedImage getHeadPreview() {
        return getHeadPreview(1);
    }

    public BufferedImage getHeadPreview(int scale) {
        BufferedImage img = createImage(HEAD_WIDTH * scale,
                HEAD_HEIGHT * scale);

        Graphics2D g = img.createGraphics();
        draw(g, SRC_FRONT_HEAD_X, SRC_FRONT_HEAD_Y, SRC_FRONT_HEAD_W, SRC_FRONT_HEAD_H,
                HEAD_HEAD_X, HEAD_HEAD_Y, scale);
        draw(g, SRC_FRONT_HELMET_X, SRC_FRONT_HELMET_Y, SRC_FRONT_HELMET_W, SRC_FRONT_HELMET_H,
                HEAD_HELMET_X, HEAD_HELMET_Y, scale, false, true);

        g.dispose();
        return img;
    }
}
