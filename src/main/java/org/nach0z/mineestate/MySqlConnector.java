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
					listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("listing_type"), regions.getOwnerName(name, world), world.getName());
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
                                        listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("listing_type"), regions.getOwnerName(name, world), world.getName());
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
			stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"' AND world LIKE '"+world.getName()+"'");
			stmt.executeUpdate("INSERT INTO estate_listings(region_name, listing_type, price, world) VALUES ('"+name+"', 'sale', "+price+", '"+world.getName()+"')");
			_plugin.getRegionFlagManager().setPriceFlag(name, price, world);
		} catch (Exception e) {
			System.out.println("Problem adding the region "+name+" to sales listings");
			System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean addForRent(String name, double price, World world) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"' AND world LIKE '"+world.getName()+"'");
            stmt.executeUpdate("INSERT INTO estate_listings(region_name, listing_type, price, world) VALUES ('"+name+"', 'rent', "+price+", '"+world.getName()+"')");
            _plugin.getRegionFlagManager().setPriceFlag(name, price, world);
        } catch (Exception e) {
            System.out.println("Problem adding the region "+name+" to rental listings");
            System.out.println(e);
            return false;
        }
        return true;
	}

	public boolean removeForSale(String name, World world) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"' AND world LIKE '"+world.getName()+"'");
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean removeForRent(String name, World world) {
		        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+name+"' AND world LIKE '"+world.getName()+"'");
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
	}

	public boolean isForSale(String name) {
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings WHERE region_name LIKE '"+name+"' AND listing_type LIKE 'sale'");
			return rs.next();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	public boolean isForRent(String name) {
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_listings WHERE region_name LIKE '"+name+"' AND listing_type LIKE 'rent'");
			return rs.next();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	public ArrayList<Listing> getTenants(String regionName, World world) {
		ArrayList<Listing> ret = new ArrayList<Listing>();
        try {
            Statement stmt = conn.createStatement();
            Listing listing;
            ResultSet rs = stmt.executeQuery("SELECT * FROM estate_tenants WHERE region_name LIKE '"+regionName+"' AND world LIKE '"+world.getName()+"'");
            while(rs.next()) {
                double price = rs.getDouble("price");
                String name = rs.getString("region_name");
                String size = regions.getRegionSize(name, world);
                //region_name, tenant, price, time_ordered
                listing = new Listing(price, regions.getRegionSize(name, world), name, rs.getString("time_ordered"), rs.getString("tenant"), rs.getString("world"));
                ret.add(listing);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
	}

	public boolean addTenant(String regionName, String tenantName, int regionPrice, int numDays, World world) {
        try {
			Date date = new Date();
       		Calendar cal = Calendar.getInstance();
       		cal.setTime(date);
        	String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + roundUpToNearestFive(cal.get(Calendar.MINUTE));
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM estate_listings WHERE region_name LIKE '"+regionName+"'");
            stmt.executeUpdate("INSERT INTO estate_tenants(region_name, tenant, price, time_ordered, days_remaining, world ) VALUES ('"+regionName+"', '"+tenantName+"', "+regionPrice+", "+time+"', '"+(numDays-1)+"', '" + world.getName()+"')");
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
	}

	public boolean subtractDay(String regionName, World world) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_tenants WHERE region_name LIKE '"+regionName+"' AND world LIKE '"+world.getName()+"'");
			int days = 0;
			while(rs.next()) {
				days = rs.getInt("days_remaining");
				stmt.executeUpdate("UPDATE estate_tenants SET days_remaining='"+(--days)+"' WHERE region_name='"+regionName+"' AND world='"+world.getName()+"'");
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public int daysRemaining(String regionName, World world) {
		int ret = -1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estate_tenants WHERE region_name LIKE '"+regionName+"' AND world LIKE '"+world.getName()+"'");
			while(rs.next()) {
				ret = rs.getInt("days_remaining");
			}
		} catch (Exception e) {
			System.out.println(e);
			return -1;
		}
		if(ret >=0)
			return ret;
		else
			return -1;
	}

    public boolean removeTenant(String regionName, World world) {
        try {
            Statement stmt = conn.createStatement();
			Listing listing;
            ResultSet rs = stmt.executeQuery("SELECT * FROM estate_tenants");
			double price = 0;
			String size;
            while(rs.next()) {
                price = rs.getDouble("price");
                size = regions.getRegionSize(regionName, world);
                //region_name, tenant, price, time_ordered
                listing = new Listing(price, regions.getRegionSize(regionName, world), regionName, rs.getString("time_ordered"), rs.getString("tenant"), world.getName());
			}
            stmt.executeUpdate("DELETE FROM estate_tenants WHERE region_name LIKE '"+regionName+"' AND world LIKE '"+world.getName()+"'");
            stmt.executeUpdate("INSERT INTO estate_listings(region_name, listing_type, price, world) VALUES ('"+regionName+"', 'rent', '"+price+"', '"+world.getName()+"')");
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

	public void createTables() {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS estate_listings ( region_name VARCHAR(64), listing_type VARCHAR(10), price DOUBLE(16,2), world VARCHAR(64))");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS estate_tenants ( region_name VARCHAR(64), tenant VARCHAR(32), price DOUBLE(16,2), time_ordered VARCHAR(12), world VARCHAR(64))");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

    public int roundUpToNearestFive(int orig) {
        int ret = (orig + 4) / 5 * 5;
        return ret;
    }
}
