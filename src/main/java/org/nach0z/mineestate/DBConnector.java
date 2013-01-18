package org.nach0z.mineestate;

import java.util.*;
import org.bukkit.*;

public interface DBConnector {

	public ArrayList<Listing> getForSale(World world);

	public ArrayList<Listing> getForRent(World world);

	public ArrayList<Listing> getTenants(World world);

	public boolean addTenant(String regionName, String tenantName, int regionPrice);

	public boolean removeTenant(String regionName, World world);

	public boolean addForSale(String regionName, double price, World world);

	public boolean addForRent(String regionName, double price, World world);

	public boolean removeForSale(String regionName);

	public boolean removeForRent(String regionName);

	public boolean isForSale(String regionName);

}
