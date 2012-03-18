package org.nach0z.mineestate;

import java.util.*;

public class ListingComparator implements Comparator {
	//name, price, size, owner
	private int[] order;
	private int[] sortOwner = {4, 1, 2, 3};
	private int[] sortSize = {3, 1, 2, 4};
	private int[] sortPrice = {2, 1, 3, 4};
	private int[] sortNormal = {1, 2, 3, 4};
	private String method;
	private RegionFlagManager regions;
	public ListingComparator(String method, MineEstatePlugin plugin) {
		this.method = method;
		regions = plugin.getRegionFlagManager();
	}

	public int compare(Object ob1, Object ob2) {
		boolean equal = true;
		order = sortNormal;
		if(method.equals("owner"))
			order = sortOwner;
		if(method.equals("size"))
			order = sortSize;
		if(method.equals("price"))
			order = sortPrice;
		Listing lis1;
		Listing lis2;
		if(!(ob1 instanceof Listing && ob2 instanceof Listing)) {
			return 0;
		} else {
			lis2 = (Listing) ob2;
			lis1 = (Listing) ob1;
		}
		int val = 0;
		for(int i : order) {
			if(val == 0)
				equal = true;
			else
				equal = false;
			if(equal) {
				switch (i) {
					case 1: val = compareName(lis1, lis2);
						break;
					case 2: val = comparePrice(lis1, lis2);
						break;
					case 3: val = compareSize(lis1, lis2);
						break;
					case 4: val = compareOwner(lis1, lis2);
						break;
				}
			}
		}
		return val;
	}

	public int compareOwner(Listing lis1, Listing lis2) {
		String name1 = regions.getOwnerName(lis1.name);
                String name2 = regions.getOwnerName(lis2.name);
                return name1.compareTo(name2);
	}

	public int compareName(Listing lis1, Listing lis2) {
		return lis1.name.compareTo(lis2.name);
	}

	public int compareSize(Listing lis1, Listing lis2) {
		String[] lis1pair = lis1.size.split("x");
                String[] lis2pair = lis2.size.split("x");
                if(lis1pair[0].equals(lis2pair[0]))
                        return lis1pair[1].compareTo(lis2pair[1]);
                else
                        return lis1pair[0].compareTo(lis2pair[0]);
	}

	public int comparePrice(Listing lis1, Listing lis2) {
		return Double.compare(new Double(lis1.price), new Double(lis2.price));
	}
}
