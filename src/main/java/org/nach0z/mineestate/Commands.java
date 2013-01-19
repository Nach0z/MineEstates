package org.nach0z.mineestate;

import java.util.EnumSet;
public enum Commands {
	TP("TP", "tp", "Tp", "GOTO", "Goto", "goto", 1),
	PAGE("PAGE", "Page", "page", 1),
	USAGE("USAGE", "Usage", "usage", "HELP", "Help", "help", 0),
	SEARCH("SEARCH", "Search", "search", 1),
	BUY("BUY", "Buy", "buy", 1),
	SELL("SELL", "Sell", "sell", 2),
	SELLPUBLIC("SELLPUBLIC", "SellPublic", "sellpublic", 2),
	CANCEL("CANCEL", "Cancel", "cancel", 1);

	private int numArgs;

	Commands (String str1, String str2, String str3, int numArgs) {
		this.numArgs = numArgs;
	}

	Commands (String str1, String str2, String str3, String str4, int numArgs) {
		this.numArgs = numArgs;
	}

	Commands (String str1, String str2, String str3, String str4, String str5, String str6, int numArgs) {
		this.numArgs = numArgs;
	}

	public boolean satisfied(String[] args) {
		return (args.length >= numArgs);
	}

}
