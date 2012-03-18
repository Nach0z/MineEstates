package org.nach0z.mineestate;

import java.util.*;

public class Listing {
	public double price;
	public String name;
	public String size;
	public String type;
	public String owner;
	public Listing(double price, String size, String name, String type, String owner) {
		this.price = price;
		this.size = size;
		this.type = type;
		this.name = name;
		this.owner = owner;
	}

	public String toString() {
		String ret = "";
		ret += addSpaces(name, 15);
		ret += addSpaces(price + "", 10);
		ret += addSpaces(size, 8);
		ret += addSpaces(owner, 20);
//		return name + "\t" + price + "\t" + size + "\t" + owner;
		return ret;
	}

	public String addSpaces(String val, int len) {
		for(int i = val.length(); i < len; i++)
			val += " ";
		return val;
	}
}
