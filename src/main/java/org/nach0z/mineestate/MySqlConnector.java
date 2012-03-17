package org.nach0z.mineestate;
import java.util.*;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.util.yaml.YAMLFormat;

public class MySqlConnector implements DBConnector {
	private YAMLProcessor wg_config;
	private MineEstatePlugin _plugin;
	private Connection conn;
	private Statement stmt;

	public MySqlConnector(MineEstatePlugin plugin, YAMLProcessor _wg_config) {
		_plugin = plugin;
		wg_config = _wg_config;
		try {
			conn= DriverManager.getConnection(wg_config.getString("regions.sql.dsn"), wg_config.getString("regions.sql.username"), wg_config.getString("regions.sql.password"));
		} catch (Exception e) {
			System.out.println("[FATAL ERROR] MineEstates failed to connect to the WorldGuard MySQL database! MineEstates WILL NOT WORK");
		}
		createTables();
	}

	public ArrayList<String> getForSale() {

	return new ArrayList<String>();
	}

	public ArrayList<String> getForRent() {

	return new ArrayList<String>();
	}

	public boolean addForSale(String name, double price) {
		return true;
	}

	public boolean addForRent(String name, double price) {
		return true;
	}

	public boolean removeForSale(String name) {
		return true;
	}

	public boolean removeForRent(String name) {
		return true;
	}


	public void createTables() {
	}

}
