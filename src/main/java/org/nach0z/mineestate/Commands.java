package org.nach0z.mineestate;

import java.util.EnumSet;
public enum Commands {
//	TP("TP", "tp", "Tp", "GOTO", "Goto", "goto", 1),
	TP("TP", 1),
	GOTO("GOTO", 1),
//	PAGE("PAGE", "Page", "page", 1),
	PAGE("PAGE", 1),
//	USAGE("USAGE", "Usage", "usage", "HELP", "Help", "help", 0),
	USAGE("USAGE", 0),
	HELP("HELP", 0),
//	SEARCH("SEARCH", "Search", "search", 1),
	SEARCH("SEARCH", 1),
//	BUY("BUY", "Buy", "buy", 1),
	BUY("BUY", 1),
//	SELL("SELL", "Sell", "sell", 2),
	SELL("SELL", 2),
//	SELLPUBLIC("SELLPUBLIC", "SellPublic", "sellpublic", 2),
	SELLPUBLIC("SELLPUBLIC", 2),
//	CANCEL("CANCEL", "Cancel", "cancel", 1);
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
