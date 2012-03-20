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
		if (message.size() == 0)
			return retList;
		for(int i = offset; i < offset + 8; i++) {
			if(i >= message.size())
				return retList;
			retList.add(message.get(i));
		}
		return retList;
	}

	public int getPages() {
		return (message.size() / 8) + 1;
	}
}
