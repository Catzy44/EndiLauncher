package me.catzy44.struct;

public class ForgePromotedVersion {
	private String recommended;
	private String latest;
	
	public ForgePromotedVersion(String ver, String type) {
		set(ver,type);
	}
	
	public String getLatest() {
		return latest;
	}
	
	public String getRecommended() {
		return recommended == null ? latest : recommended;
	}
	
	public void set(String ver, String type) {
		if(type.equals("latest")) {
			latest = ver;
		} else if(type.equals("recommended")) {
			recommended = ver;
		}
	}
	
}