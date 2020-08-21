package me.catzy44.skin.skin3d;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JLabel;

import me.catzy44.skin.skin3d.gfx.ImageBitmap;
import me.catzy44.skin.skin3d.math.Matrix3;
import me.catzy44.skin.skin3d.math.Zombie;

public class ModelPreview extends JLabel implements Runnable {

	private static final long serialVersionUID = 1L;
	int WIDTH = 160;
	int HEIGHT = 160;
	public float scale = 1;
	private Thread thread;
	private volatile boolean running = false;
	ImageBitmap screenBitmap;
	public Zombie zombie;
	public boolean paused = false;
	private int xOld;
	private int yOld;
	long lastTime = System.nanoTime();
	public float time = 0.0F;
	public float yRot = 0.0F;
	public float xRot = 0.0F;
	// int color = 0xA0B0E0;
	int color = 0x00000000;

	public ModelPreview() {
		create();
	}

	public ModelPreview(int w, int h) {
		this.WIDTH = w;
		this.HEIGHT = h;
		create();
	}

	public ModelPreview(float s) {
		this.scale = s;
		create();
	}

	public ModelPreview(float s, int w, int h) {
		this.WIDTH = w;
		this.HEIGHT = h;
		this.scale = s;
		create();
	}
	
	private int fpsBoostInteger = 0;

	public void create() {
		Dimension size = new Dimension((int) (WIDTH * scale), (int) (HEIGHT * scale));
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);

		// Set up rendering stuff.
		screenBitmap = new ImageBitmap(WIDTH, HEIGHT);
		zombie = new Zombie(this);

		// Handle mouse input.
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				// Toggle pause state when clicked.
				paused = !paused;
			}

			public void mouseEntered(MouseEvent e) {
				if(FPS_SYNCHRO < 60) {FPS_SYNCHRO = 60;}
				fpsBoostInteger++;
			}

			public void mouseExited(MouseEvent e) {
				fpsBoostInteger--;
				if(fpsBoostInteger <= 0) {
					FPS_SYNCHRO = TARGET_FPS;
				}
			}

			public void mousePressed(MouseEvent e) {
				xOld = e.getX();
				yOld = e.getY();
				
				if(FPS_SYNCHRO < 60) {FPS_SYNCHRO = 60;}
				fpsBoostInteger++;
			}

			public void mouseReleased(MouseEvent e) {
				fpsBoostInteger--;
				if(fpsBoostInteger <= 0) {
					FPS_SYNCHRO = TARGET_FPS;
				}
			}
		});
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				if (notches < 0) {
					scale += 0.1;
				} else {
					scale -= 0.1;
				}
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				// Move the camera when the mouse is dragged.
				yRot -= (e.getX() - xOld) / 80.0f;
				xRot += (e.getY() - yOld) / 80.0f;
				xOld = e.getX();
				yOld = e.getY();

				float max = 1.5707964f;
				if (xRot < -max) {
					xRot = -max;
				}
				if (xRot > max) {
					xRot = max;
				}

				// System.out.println("xRot: " + String.valueOf(xRot) + " yRot: " +
				// String.valueOf(yRot));
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();

	}

	public synchronized void stop() {
		if (running) {
			running = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	int TARGET_FPS = 60;
	int FPS_SYNCHRO = 60;
	public void setTargetFps(int fps) {
		TARGET_FPS = fps;
		FPS_SYNCHRO = TARGET_FPS;
	}
	public void run() {
		/*
		 * while (running && !Thread.interrupted()) { repaint();
		 * 
		 * // Control frame rate try {Thread.sleep(35);} catch (InterruptedException ex)
		 * {} }
		 */
		long now;
		long updateTime;
		long wait;

		while (running && !Thread.interrupted()) {
			final long OPTIMAL_TIME = 1000000000 / FPS_SYNCHRO;
			
			if(paused) {
				try {Thread.sleep(50);} catch (InterruptedException e) {}
				continue;
			}
			now = System.nanoTime();

			repaint();

			updateTime = System.nanoTime() - now;
			wait = (OPTIMAL_TIME - updateTime) / 1000000;
			
			if(wait < 0) {
				try {Thread.sleep(50);} catch (InterruptedException e) {}
				continue;
			}

			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private double offX, offY = 0.0;
	private double offZ = 30;

	public void setOffset(double offX, double offY, double offZ) {
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		long now = System.nanoTime();
		if (!paused) {
			time -= (now - lastTime) / 1.0E9f;
		}
		lastTime = now;

		screenBitmap.clearZBuffer();
		screenBitmap.fill(0, 0, screenBitmap.width, screenBitmap.height, color);
		Matrix3 m = new Matrix3().translate(offX, offY, offZ).rotX(xRot).rotY(yRot).scale(scale, scale, scale);
		zombie.render(m, screenBitmap, time);

		// g2.drawImage(screenBitmap.getImage(), 0, 0, WIDTH * scale, HEIGHT * scale, 0,
		// 0, WIDTH, HEIGHT, null);
		g2.drawImage(screenBitmap.getImage(), 0, 0, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();
	}
}