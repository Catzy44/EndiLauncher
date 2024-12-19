package me.catzy44.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	private String algorithm = "SHA1";
	
	byte[] data;
	String hashed;

	public Hash(String s) {
		data = s.getBytes(StandardCharsets.UTF_8);
		hashed = hash(data,algorithm);
	}
	
	public Hash(String s, String alg) {
		algorithm = alg;
		data = s.getBytes(StandardCharsets.UTF_8);
		hashed = hash(data,algorithm);
	}
	
	public Hash(byte[] s) {
		data = s;
		hashed = hash(data,algorithm);
	}
	
	public Hash(byte[] s, String alg) {
		algorithm = alg;
		data = s;
		hashed = hash(data,algorithm);
	}
	
	public Hash(File s) {
		this(s,"SHA1");
		/*try {
			data = Files.readAllBytes(s.toPath());
			hashed = hash(data,algorithm);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public Hash(File s, String alg) {
		algorithm = alg;
		try {
			MessageDigest digest = MessageDigest.getInstance(alg);
			
			InputStream fis =  new FileInputStream(s);
			byte[] buffer = new byte[1024];
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					digest.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			
			byte[] encodedhash = digest.digest();
			hashed = bytesToHex(encodedhash);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private String hash(byte[] s, String alg) {
		try {
			MessageDigest digest = MessageDigest.getInstance(alg);
			byte[] encodedhash = digest.digest(s);
			return bytesToHex(encodedhash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	@Override
	public String toString() {
		return hashed;
	}
	
	@Override
	public boolean equals(Object s) {
		return hashed.equals(s);
	}
	
	public boolean equals(String s) {
		return hashed.equals(s);
	}
	
	public boolean verifyAgainst(String s) {
		return hashed.equals(hash(s.getBytes(StandardCharsets.UTF_8),algorithm));
	}
	
	public boolean verifyAgainst(String s, String alg) {
		return hashed.equals(hash(s.getBytes(StandardCharsets.UTF_8),alg));
	}
	
	public boolean verifyAgainst(byte[] s) {
		return hashed.equals(hash(s,algorithm));
	}
	
	public boolean verifyAgainst(byte[] s, String alg) {
		return hashed.equals(hash(s,alg));
	}
	
	public boolean verifyAgainst(File s) {
		try {
			return hashed.equals(hash(Files.readAllBytes(s.toPath()),algorithm));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean verifyAgainst(File s, String alg) {
		try {
			return hashed.equals(hash(Files.readAllBytes(s.toPath()),alg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
