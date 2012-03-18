package org.nach0z.mineestate;

import java.util.*;

public class Listing {
	public double price;
	public String name;
	public String size;
	public String type;
	public Listing(double price, String size, String name, String type) {
		this.price = price;
		this.size = size;
		this.type = type;
		this.name = name;
	}
}
