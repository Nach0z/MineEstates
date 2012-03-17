package org.nach0z.mineestate;

import java.util.*;

public interface DBConnector {

	public ArrayList<String> getForSale();

	public ArrayList<String> getForRent();

	public boolean addForSale(String regionName, double price);

	public boolean addForRent(String regionName, double price);

	public boolean removeForSale(String regionName);

	public boolean removeForRent(String regionName);



}
