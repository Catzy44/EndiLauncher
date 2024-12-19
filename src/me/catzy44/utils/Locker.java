package me.catzy44.utils;

public class Locker {
	private boolean unlocked = false;
	private Object locker = new Object();
	
	public void lock() {
		unlocked = false;
	}
	
	public void waitForUnlock() {
		try {
			while (!unlocked) {
				synchronized (locker) {
					locker.wait(5000);
				}
			}
			//new Exception().printStackTrace();
		} catch (InterruptedException e) {
		}
	}
	public void waitForUnlock(int wait) {
		try {
			long time = System.currentTimeMillis();
			while (!unlocked && (System.currentTimeMillis()-time < wait)) {
				synchronized (locker) {
					locker.wait(wait);
				}
			}
		} catch (InterruptedException e) {
		}
	}
	public void unlock() {
		unlocked = true;
		synchronized(locker) {
			locker.notifyAll();
		}
	}
}
