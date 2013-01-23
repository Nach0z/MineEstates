package org.nach0z.mineestate;

import java.util.EnumSet;
public enum Commands {
	TP("TP", 1),
	GOTO("GOTO", 1),
	PAGE("PAGE", 1),
	USAGE("USAGE", 0),
	HELP("HELP", 0),
	SEARCH("SEARCH", 1),
	BUY("BUY", 1),
	SELL("SELL", 2),
	SELLPUBLIC("SELLPUBLIC", 2),
	LEASE("LEASE", 2),
	RENT("RENT", 2),
	CANCEL("CANCEL", 1);

	private int numArgs;

	private String text;

	Commands(String text, int numArgs) {
		this.numArgs = numArgs;
		this.text = text;
	}

	public static Commands fromString(String text) {
		if (text != null) {
	      for (Commands com : Commands.values()) {
	        if (text.equalsIgnoreCase(com.text)) {
	          return com;
	        }
	      }
	    }
	    return null;
	}

	public boolean satisfied(String[] args) {
		boolean ret = (args.length >= numArgs);
		if(args[0].equalsIgnoreCase("SEARCH")) {
			if(args.length%0 == 0)
				ret = false;
		}
		return ret;
	}

}
