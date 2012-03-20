package org.nach0z.mineestate;

import java.util.*;

public class LookupCache {
	public ArrayList<String> message;
	public LookupCache() {
		message = new ArrayList();
	}

	public void addLine(String str) {
		message.add(str);
	}

	public ArrayList<String> getLines(int offset) {
		ArrayList<String> retList = new ArrayList<String>();
		for(int i = offset; i < offset + 8; i++) {
			retList.add(message.get(i));
		}
		return retList;
	}
}
