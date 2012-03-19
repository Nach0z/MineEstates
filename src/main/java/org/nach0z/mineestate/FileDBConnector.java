package org.nach0z.mineestate;

import java.io.*;
import java.util.*;

import com.sk89q.util.yaml.*;

public class FileDBConnector implements DBConnector {
	private MineEstatePlugin _plugin = null;
	private File dbFile = null;
	private YAMLProcessor processor = null;
	//Going to use WorldEdit YAMLParser and whatnot for the DB. simple, pre-existing stuff. Has getStringList() for nodes.
	public FileDBConnector(MineEstatePlugin plugin) {
		_plugin = plugin;
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

	public boolean isForSale(String regionName) {
		return true;
	}

}
