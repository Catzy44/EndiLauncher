package me.catzy44.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import me.catzy44.utils.Locker;

// This class downloads a file from a URL.
public class Download /*extends Observable */implements Runnable {

	// Max size of download buffer.
	private static final int MAX_BUFFER_SIZE = 1024;

	// These are the status names.
	public static final String STATUSES[] = { "Downloading", "Paused", "Complete", "Cancelled", "Error" };

	// These are the status codes.
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	private static NumberFormat nf;

	private URL url; // download URL
	private int size; // size of download in bytes
	private int downloaded; // number of bytes downloaded
	private int status; // current status of download
	private String savedirectory; // current save path
	private long timestamp; // download start timestamp
	private long bps = 0;
	
	private Download instance;

	// Constructor for Download.
	public Download(URL url, String dir) {
		instance = this;
		Download d = this;
		pcs.addPropertyChangeListener("status",new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				synchronized(d) {
					d.notifyAll();
				}
			}
		});
		
		this.url = url;
		this.savedirectory = dir;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		// Begin the download.
		download();
	}

	// Get this download's URL.
	public String getUrl() {
		return url.toString();
	}

	// Get this download's size.
	public int getSize() {
		return size;
	}

	public int getDownloaded() {
		return downloaded;
	}

	// Get this download's progress.
	public float getProgress() {
		return ((float) downloaded / size);
	}
	// Get this download's progress.

	public int getProgressInt() {
		return downloaded * 100 / size;
	}

	public float getProgressInPercent() {
		return ((float) downloaded / size) * 100;
	}

	// Get this download's status.
	public int getStatus() {
		return status;
	}

	// Get this download's local file path
	public String getSavedirectory() {
		return savedirectory;
	}

	public float getAverageBytesPerSec() {
		// AVOID DIVISION BY 0!!!
		long c2n0 = ((System.nanoTime() - timestamp) / 1000000000);
		if (c2n0 == 0) {
			c2n0 = 1;
		}
		return downloaded / c2n0;
	}

	public float getBytesPerSec() {
		return bps;
	}

	public float getKbPerSec() {
		return getBytesPerSec() / (1024);
	}

	public float getMbPerSec() {
		return getKbPerSec() / (1024);
	}

	public String getFormattedSomethingPerSecond() {
		float bps = getBytesPerSec();
		float kbps = bps / 1024;
		float mbps = kbps / 1024;

		if (mbps > 0.1) {
			return nf.format(mbps) + "MB/s";
		} else if (kbps > 1) {
			return nf.format(kbps) + "kB/s";
		} else {
			return nf.format(bps) + "B/s";
		}
	}

	public String getTimeLeft() {
		float bytespersec = getBytesPerSec();
		float seconds = size / bytespersec;

		if(seconds < 1) {
			return "0 sekund";
		} else if (seconds < 60) {
			return ((int)seconds) + " sekund";
		} else if (seconds == 60) {
			return "minuta";
		} else if ((int) seconds / 60 == 1) {
			return "minuta";
		} else if (seconds / 60 < 60) {
			return (int) seconds / 60 + " minut";
		} else if (seconds / 60 / 60 < 60) {
			return (int) seconds / 60 / 60 + " godzin";
		} else if (seconds / 60 / 60 / 24 < 30) {
			return (int) seconds / 60 / 60 / 24 + " dni";
		} else if (seconds / 60 / 60 / 24 / 30 < 12) {
			return (int) seconds / 60 / 60 / 24 / 30 + " miesięcy";
		} else if (seconds / 60 / 60 / 24 / 365 < 100) {
			return (int) seconds / 60 / 60 / 24 / 365 + " lat";
		} else if (seconds / 60 / 60 / 24 / 365 / 100 < 1000) {
			return (int) seconds / 60 / 60 / 24 / 365 / 100 + " stuleci";
		} else {
			return "10 minut";
		}
	}

	// Pause this download.
	public void pause() {
		pcs.firePropertyChange("status", status, PAUSED);
		status = PAUSED;
	}

	// Resume this download.
	public void resume() {
		pcs.firePropertyChange("status", status, DOWNLOADING);
		status = DOWNLOADING;
		download();
	}

	// Cancel this download.
	public void cancel() {
		pcs.firePropertyChange("status", status, CANCELLED);
		status = CANCELLED;
	}

	// Mark this download as having an error.
	private void error() {
		pcs.firePropertyChange("status", status, ERROR);
		status = ERROR;
	}

	// Start or resume downloading.
	private void download() {
		Thread thread = new Thread(this);
		thread.start();
	}

	// Get file name portion of URL.
	@SuppressWarnings("unused")
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}
	
	int down[] = new int[2];
	
	public Locker locker = new Locker();

	// Download file.
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

			// Connect to server.
			connection.connect();

			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2) {
				e = new Exception(getUrl()+" Response code "+connection.getResponseCode());
				error();
			}

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				e = new Exception(getUrl()+" Content length "+contentLength);
				error();
			}

			/*
			 * Set the size for this download if it hasn't been already set.
			 */
			if (size == -1) {
				size = contentLength;
				pcs.firePropertyChange("size", size, size);
			}

			// Open file and seek to the end of it.
			file = new RandomAccessFile(savedirectory /* + "/" + getFileName(url) */, "rw");
			file.seek(downloaded);

			timestamp = System.nanoTime();
			long LStimestamp = System.nanoTime();
			long LSdownloaded = 0;
			stream = connection.getInputStream();
			while (status == DOWNLOADING && !Thread.interrupted()) {
				if (((System.nanoTime() - LStimestamp) / 1000000000) > 1) {
					bps = LSdownloaded / ((System.nanoTime() - LStimestamp) / 1000000000);
					LStimestamp = System.nanoTime();
					LSdownloaded = 0;
				}
				/*
				 * Size buffer according to how much of the file is left to download.
				 */
				/** byte buffer[]; */
				byte buffer[] = new byte[MAX_BUFFER_SIZE];

				/*if (size - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[size - downloaded];
				}*/

				// Read from server into buffer.
				int read = stream.read(buffer);
				if (read == -1)
					break;

				// Write buffer to file.
				file.write(buffer, 0, read);

				downloaded += read;
				LSdownloaded += read;
				
				/*down[0] = downloaded;
				down[1] = size;*/
				pcs.firePropertyChange("downloaded", null, down);
			}
			/*
			 * Change status to complete if this point was reached because downloading has
			 * finished.
			 */
			if (status == DOWNLOADING) {
				pcs.firePropertyChange("status", DOWNLOADING, COMPLETE);
				status = COMPLETE;
			}
		} catch (Exception e) {
			this.e = e;
			error();
		} finally {
			// Close file.
			if (file != null) {
				try {
					file.close();
				} catch (Exception e) {
				}
			}

			// Close connection to server.
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
			locker.unlock();
		}
	}
	Exception e;
	
	public Exception getException() {
		return e;
	}
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "Download";
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
