package org.nach0z.mineestate;

import java.util.*;

public class ListingComparator implements Comparator {
	private String compareMethod;
	private RegionFlagManager regions;
	public ListingComparator(String method, MineEstatePlugin plugin) {
		compareMethod = method;
		regions = plugin.getRegionFlagManager();
	}

	public int compare(Object ob1, Object ob2) {
		Listing lis1;
		Listing lis2;
		if(!(ob1 instanceof Listing && ob2 instanceof Listing)) {
			return 0;
		} else {
			lis2 = (Listing) ob2;
			lis1 = (Listing) ob1;
		}
		if(compareMethod.equalsIgnoreCase("owner")) {
			String name1 = regions.getOwnerName(lis1.name);
			String name2 = regions.getOwnerName(lis2.name);
			return name1.compareTo(name2);
		} else if (compareMethod.equalsIgnoreCase("name")) {
			return lis1.name.compareTo(lis2.name);
		} else if (compareMethod.equalsIgnoreCase("size")) {
			String[] lis1pair = lis1.size.split("x");
			String[] lis2pair = lis2.size.split("x");
			if(lis1pair[0].equals(lis2pair[0]))
				return lis1pair[1].compareTo(lis2pair[1]);
			else
				return lis1pair[0].compareTo(lis2pair[0]);
		} else {
			return Double.compare(new Double(lis1.price), new Double(lis2.price));
		}
	}

}
