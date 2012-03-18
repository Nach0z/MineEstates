package org.nach0z.mineestate;

import java.io.*;
import java.util.*;

public class FileDBConnector implements DBConnector {
	private MineEstatePlugin _plugin = null;
	private File dbFile = null;
	//Going to use WorldEdit YAMLParser and whatnot for the DB. simple, pre-existing stuff. Has getStringList() for nodes.
	public FileDBConnector(MineEstatePlugin plugin) {
		_plugin = plugin;
		dbFile = new File(_plugin.getDataFolder().getName() + "/market.db");
	}


	public ArrayList<Listing> getForSale() {

		return new ArrayList<Listing>();
	}

	public ArrayList<Listing> getForRent() {

		return new ArrayList<Listing>();
	}

	public boolean addForSale(String regionName, double price) {

		return true;
	}

	public boolean addForRent(String regionName, double price) {

		return true;
	}

	public boolean removeForSale(String regionName) {

		return true;
	}

	public boolean removeForRent(String regionName) {

		return true;
	}


}
