package org.nach0z.mineestate;
import java.util.*;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.util.yaml.YAMLFormat;

public class MySqlConnector implements DBConnector {
	private RegionFlagManager regions;
	private MineEstatePlugin _plugin;
	private Connection conn;
	private Statement stmt;

	public MySqlConnector(MineEstatePlugin plugin, YAMLProcessor wg_config) {
		_plugin = plugin;
		try {
			conn= DriverManager.getConnection(wg_config.getString("regions.sql.dsn"), wg_config.getString("regions.sql.username"), wg_config.getString("regions.sql.password"));
			Statement stmt = conn.createStatement();
			createTables();
			regions = _plugin.getRegionFlagManager();

		} catch (Exception e) {
			System.out.println("[FATAL ERROR] MineEstates failed to connect to the WorldGuard MySQL database! MineEstates WILL NOT WORK");
			System.out.println(e);
			Bukkit.getServer().getPluginManager().disablePlugin(_plugin);
		}
	}

	public ArrayList<Listing> getForSale() {
		ArrayList<Listing> ret = new ArrayList<Listing>();
		try {
			Statement stmt = conn.createStatement();
			Listing listing;
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings");
			while(rs.next()) {
				if(rs.getString("listing_type").equalsIgnoreCase("sale")) {
					double price = rs.getDouble("price");
					String name = rs.getString("region_name");
					String size = regions.getRegionSize(name);
					//price, size, name, type
					listing = new Listing(price, regions.getRegionSize(name), name, rs.getString("listing_type"), regions.getOwnerName(name));
					ret.add(listing);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return ret;
	}

	public ArrayList<Listing> getForRent() {
                ArrayList<Listing> ret = new ArrayList<Listing>();
                try {
                        Statement stmt = conn.createStatement();
                        Listing listing;
                        ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings");
                        while(rs.next()) {
                                if(rs.getString("listing_type").equalsIgnoreCase("rent")) {
                                        double price = rs.getDouble("price");
                                        String name = rs.getString("region_name");
                                        String size = regions.getRegionSize(name);
                                        //price, size, name, type
                                        listing = new Listing(price, regions.getRegionSize(name), name, rs.getString("type"), regions.getOwnerName(name));
                                        ret.add(listing);
                                }
                        }
                } catch (Exception e) {
                        System.out.println(e);
                }
	        return ret;

	}

	public boolean addForSale(String name, double price) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"'");
			stmt.executeUpdate("INSERT INTO estate_listings(region_name, listing_type, price) VALUES ('"+name+"', 'sale', "+price+")");
		} catch (Exception e) {
			System.out.println("Problem adding the region "+name+" to sales listings");
			System.out.println(e);
			return false;
		}
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
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS estate_listings ( region_name VARCHAR(64), listing_type VARCHAR(10), price DOUBLE(16,2))");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
