package org.nach0z.mineestate;

import java.util.*;
import org.bukkit.*;

public interface DBConnector {

	public ArrayList<Listing> getForSale(World world);

	public ArrayList<Listing> getForRent(World world);

	public ArrayList<Listing> getTenants(String regionName, World world);

	public boolean addTenant(String regionName, String tenantName, int regionPrice, int numDays, World world);

	public boolean removeTenant(String regionName, World world);

	public boolean subtractDay(String regionName, World world);

	public boolean addForSale(String regionName, double price, World world);

	public boolean addForRent(String regionName, double price, World world);

	public boolean removeForSale(String regionName, World world);

	public boolean removeForRent(String regionName, World world);

	public boolean isForSale(String regionName);

	public boolean isForRent(String regionName);

	public int daysRemaining(String regionName, World world);

}
