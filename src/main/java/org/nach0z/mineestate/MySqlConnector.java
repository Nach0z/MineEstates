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

	public MySqlConnector(MineEstatePlugin plugin, YAMLProcessor config, boolean useWGConf) {
		_plugin = plugin;
		try {
			if(useWGConf)
				conn= DriverManager.getConnection(config.getString("regions.sql.dsn"), config.getString("regions.sql.username"), config.getString("regions.sql.password"));
			else
				conn = DriverManager.getConnection(config.getString("storage.sql.dsn"), config.getString("storage.sql.username"), config.getString("storage.sql.password"));
			Statement stmt = conn.createStatement();
			createTables();
			regions = _plugin.getRegionFlagManager();

		} catch (Exception e) {
			System.out.println("[FATAL ERROR] MineEstates failed to connect to the WorldGuard MySQL database! MineEstates WILL NOT WORK");
			System.out.println(e);
			Bukkit.getServer().getPluginManager().disablePlugin(_plugin);
		}
	}

	public ArrayList<Listing> getForSale( World world) {
		ArrayList<Listing> ret = new ArrayList<Listing>();
		try {
			Statement stmt = conn.createStatement();
			Listing listing;
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings");
			while(rs.next()) {
				if(rs.getString("listing_type").equalsIgnoreCase("sale")) {
					double price = rs.getDouble("price");
					String name = rs.getString("region_name");
					String size = regions.getRegionSize(name, world);
					//price, size, name, type
					listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("listing_type"), regions.getOwnerName(name, world));
					ret.add(listing);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return ret;
	}

	public ArrayList<Listing> getForRent(World world) {
                ArrayList<Listing> ret = new ArrayList<Listing>();
                try {
                        Statement stmt = conn.createStatement();
                        Listing listing;
                        ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings");
                        while(rs.next()) {
                                if(rs.getString("listing_type").equalsIgnoreCase("rent")) {
                                        double price = rs.getDouble("price");
                                        String name = rs.getString("region_name");
                                        String size = regions.getRegionSize(name, world);
                                        //price, size, name, type
                                        listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("type"), regions.getOwnerName(name, world));
                                        ret.add(listing);
                                }
                        }
                } catch (Exception e) {
                        System.out.println(e);
                }
	        return ret;

	}

	public boolean addForSale(String name, double price, World world) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"'");
			stmt.executeUpdate("INSERT INTO estate_listings(region_name, listing_type, price) VALUES ('"+name+"', 'sale', "+price+")");
			_plugin.getRegionFlagManager().setPriceFlag(name, price, world);
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
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"'");
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean removeForRent(String name) {
		return true;
	}

	public boolean isForSale(String name) {
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings WHERE region_name LIKE '"+name+"'");
			return rs.next();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

	}

	public ArrayList<Listing> getTenants(World world) {
		ArrayList<Listing> ret = new ArrayList<Listing>();
        try {
            Statement stmt = conn.createStatement();
            Listing listing;
            ResultSet rs = stmt.executeQuery("SELECT * FROM estate_tenants");
            while(rs.next()) {
                double price = rs.getDouble("price");
                String name = rs.getString("region_name");
                String size = regions.getRegionSize(name, world);
                //region_name, tenant, price, time_ordered
                listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("time_ordered"), rs.getString("tenant"));
                ret.add(listing);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
	}

	public void createTables() {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS estate_listings ( region_name VARCHAR(64), listing_type VARCHAR(10), price DOUBLE(16,2))");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS estate_tenants ( region_name VARCHAR(64), tenant VARCHAR(32), price DOUBLE(16,2), time_ordered VARCHAR(12))");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
