package me.catzy44.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {
	public static final int PROCESSING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	private int entriesCount = 0;
	private int entriesSize = 0;

	private int processingEntry = 0;
	private int unpackedSize = 0;

	private int status = -1;

	public int getEntriesCount() {
		return entriesCount;
	}

	public int getEntriesSize() {
		return entriesSize;
	}

	public int getProcessingEntry() {
		return processingEntry;
	}

	public int getUnpackedSize() {
		return unpackedSize;
	}

	public float getSizeProgressPercent() {
		return (((float) unpackedSize / entriesSize) * 100f);
	}

	public float getCountProgressPercent() {
		return (((float) processingEntry / entriesCount) * 100f);
	}

	public void unzip(File zipFile, File destinationFolder) {
		unzip(zipFile, destinationFolder, "");
	}

	public int getStatus() {
		return status;
	}

	Pattern excludeRegex = null;
	
	public Unzip() {
		Unzip u = this;
		pcs.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				synchronized(u) {
					u.notifyAll();
				}
			}
		});
	}

	public void unzip(File zipFile, File directory, String exclude) {
		if (exclude != null) {
			excludeRegex = Pattern.compile(exclude);
		}
		status = PROCESSING;
		if (!directory.exists()) {
			directory.mkdirs();
		}
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fInput = new FileInputStream(zipFile);
			int[] statt = getZipInputStreamFileCountAndSize(fInput);
			entriesCount = statt[0];
			entriesSize = statt[1];

			fInput = new FileInputStream(zipFile);

			ZipInputStream zipInput = new ZipInputStream(fInput);
			ZipEntry entry = zipInput.getNextEntry();

			processingEntry = 0;
			unpackedSize = 0;

			b: while (entry != null) {
				String entryName = entry.getName();
				File file = new File(directory + File.separator + entryName);

				unpackedSize += entry.getSize();
				processingEntry++;

				if (excludeRegex != null) {
					Matcher m = excludeRegex.matcher(entryName);
					if (m.find()) {
						zipInput.closeEntry();
						entry = zipInput.getNextEntry();
						pcs.firePropertyChange("processedEntry", processingEntry - 1, processingEntry);
						pcs.firePropertyChange("processedData", unpackedSize - entry.getSize(), unpackedSize);
						continue b;
					}
				}

				if (entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if (!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if (!success) {
							System.out.println("Problem creating Folder");
						}
					}
				} else {
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				pcs.firePropertyChange("processedEntry", processingEntry - 1, processingEntry);
				pcs.firePropertyChange("processedData", unpackedSize - entry.getSize(), unpackedSize);
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
			zipInput.closeEntry();
			zipInput.close();
			fInput.close();

			pcs.firePropertyChange("status", status, COMPLETE);
			status = COMPLETE;
		} catch (IOException e) {
			e.printStackTrace();
		}
		status = ERROR;
	}

	public static int[] getZipInputStreamFileCountAndSize(FileInputStream fInput) throws IOException {
		int[] ret = new int[2];

		ZipInputStream zipInput = new ZipInputStream(fInput);
		ZipEntry entry = zipInput.getNextEntry();
		while (entry != null) {
			ret[1] += entry.getSize();
			ret[0]++;

			zipInput.closeEntry();
			entry = zipInput.getNextEntry();
		}
		zipInput.closeEntry();
		zipInput.close();

		return ret;
	}

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "Unzip";
	};
	
	public void waitt(int i) {
		synchronized(this) {
			try {
				this.wait(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
