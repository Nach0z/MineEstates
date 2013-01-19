package org.nach0z.mineestate;

import java.util.EnumSet;
public enum Commands {
	TP("TP", "tp", "Tp", "GOTO", "Goto", "goto"),
	PAGE("PAGE", "Page", "page"),
	USAGE("USAGE", "Usage", "usage", "HELP", "Help", "help"),
	SEARCH("SEARCH", "Search", "search"),
	BUY("BUY", "Buy", "buy"),
	SELL("SELL", "Sell", "sell"),
	SELLPUBLIC("SELLPUBLIC", "SellPublic", "sellpublic"),
	CANCEL("CANCEL", "Cancel", "cancel");

	Commands (String str1, String str2, String str3) {
	}

	Commands (String str1, String str2, String str3, String str4) {
	}

	Commands (String str1, String str2, String str3, String str4, String str5, String str6) {
	}
}
