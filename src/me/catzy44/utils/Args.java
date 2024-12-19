package me.catzy44.utils;

public class Args {
	private String[] args;
	public Args(String[] args) {
		this.args = args;
	}
	public int checkArg(String arg) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals(arg)) {
				return i;
			}
		}
		return -1;
	}
	public String get(int index) {
		return args[index];
	}
	public String[] getAll() {
		return args;
	}
}
