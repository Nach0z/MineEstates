package org.nach0z.mineestate;

import java.io.*;
import java.util.*;
import org.bukkit.*;

import com.sk89q.util.yaml.*;

public class FileDBConnector implements DBConnector {
	private MineEstatePlugin _plugin = null;
	private File dbFile = null;
	private YAMLProcessor processor = null;
	//Going to use WorldEdit YAMLParser and whatnot for the DB. simple, pre-existing stuff. Has getStringList() for nodes.
	public FileDBConnector(MineEstatePlugin plugin) {
		_plugin = plugin;
		File file = new File(_plugin.getDataFolder().getName());
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		dbFile = new File(_plugin.getDataFolder().getName() + "/market.db");
		if(!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		processor = new YAMLProcessor(dbFile, false, YAMLFormat.COMPACT);
	}


	public ArrayList<Listing> getForSale(World world) {

		return new ArrayList<Listing>();
	}

	public ArrayList<Listing> getForRent(World world) {

		return new ArrayList<Listing>();
	}

	public ArrayList<Listing> getTenants(String regionName, World world) {
		return new ArrayList<Listing>();
	}

	public boolean removeTenant(String regionName, World world) {
		return true;
	}

	public boolean addTenant(String regionName, String tenantName, int regionPrice, int numDays, World world) {
		return true;
	}

	public boolean addForSale(String regionName, double price, World world) {

		return true;
	}

	public boolean addForRent(String regionName, double price, World world) {

		return true;
	}

	public boolean removeForSale(String regionName, World world) {

		return true;
	}

	public boolean removeForRent(String regionName, World world) {

		return true;
	}

	public boolean isForSale(String regionName) {
		return true;
	}

	public boolean isForRent(String regionName) {
		return true;
	}

	public boolean subtractDay(String regionName, World world) {
		return true;
	}

	public int daysRemaining(String regionName, World world) {
		return -1;
	}

}
