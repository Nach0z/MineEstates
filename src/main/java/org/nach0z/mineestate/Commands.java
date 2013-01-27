package org.nach0z.mineestate;

import java.util.EnumSet;
public enum Commands {
	TP("TP", 2),
	GOTO("GOTO", 2),
	PAGE("PAGE", 2),
	USAGE("USAGE", 1),
	HELP("HELP", 1),
	SEARCH("SEARCH", 2),
	BUY("BUY", 2),
	SELL("SELL", 3),
	SELLPUBLIC("SELLPUBLIC", 3),
	LEASE("LEASE", 3),
	RENT("RENT", 2),
	EVICT("EVICT", 2),
	LEAVE("LEAVE", 2),
	CANCEL("CANCEL", 2);

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
			if(!(args.length%2 == 0))
				ret = false;
		}
		return ret;
	}

}
