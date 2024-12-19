package me.catzy44.math;

public class VanillaMath {
	private static final float[] SIN_TABLE = new float[65536];
	static {
		for (int i = 0; i < 65536; i++) {
			SIN_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
		}
	}

	public static float sin(float value) {
		return SIN_TABLE[(int) (value * 10430.378F) & 0xFFFF];
	}

	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 0xFFFF];
	}
	
	public static float sin(double value) {
		return SIN_TABLE[(int) (value * 10430.378F) & 0xFFFF];
	}

	public static float cos(double value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 0xFFFF];
	}
}
