package org.nach0z.mineestate;
import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import java.util.*;
import java.io.*;
import java.util.zip.*;
import java.util.jar.*;

public class MineEstatePlugin extends JavaPlugin implements Listener{
    private Economy econ = null;
    private Permission perms = null;
    private RegionFlagManager manager;
    private YAMLProcessor config;
    private YAMLProcessor wg_config;
    private WorldGuardPlugin worldguard_plugin;
	private RentCollector rentColl;
//    private ArrayList<String> available;
    public DBConnector db;
    public WorldGuardPlugin WORLDGUARD;
	public ProtectedRegion testreg;
	public void onDisable() {
        // TODO: Place any custom disable code here.
    }


    public void onEnable() {
	//        getServer().getPluginManager().registerEvents(this, this);
	//	System.out.println(getServer().getPluginManager().getPlugin("WorldGuard"));
		Plugin got_plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		manager = new RegionFlagManager(this);
		createDefaultConfig(new File(getDataFolder(), "config.yml"), "config.yml");

		if(got_plugin instanceof WorldGuardPlugin) {
			worldguard_plugin = (WorldGuardPlugin) got_plugin;
			WORLDGUARD = worldguard_plugin;
		}
		if(!setupEcon()) {
			System.out.println("[FATAL ERROR] MineEstates requires Vault and a Vault-compatible Economy plugin to be installed to interface with the economy! Please install Vault");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(!setupPerms()) {
			System.out.println("[FATAL ERROR] MineEstates requires a Vault-Compatible permissions plugin and Vault itself. Please install both of these. Check Vault's page to see if your permissions plugin works with it.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	/*	if(!WORLDGUARD.getConfig().getBoolean("regions.sql.use")) {
			System.out.println("[FATAL ERROR] MineEstates REQUIRES WorldGuard to be using the MySQL Database for functionality!");
			return;
		} else {
			System.out.println("[MineEstates] WorldGuard using MySQL Database version.");
		}
	*/
		wg_config = new YAMLProcessor(new File(WORLDGUARD.getDataFolder(), "config.yml"), true, YAMLFormat.EXTENDED);
		try {
			wg_config.load();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("[FATAL ERROR] MineEstates unable to read WorldGuard config file! Please install WorldGuard before using MineEstates.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!(config.getBoolean("storage.sql.use"))) {
			System.out.println("[FATAL ERROR] MineEstates currently requires the use of a MySQL database. Please configure your config.yml to use MySQL.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else if((config.getBoolean("storage.sql.use-worldguard") && !wg_config.getBoolean("regions.sql.use"))) {
			System.out.println("[FATAL ERROR] WorldGuard must be using a  MySQL database if storage.sql.use-worldguard is set to true in MineEstats' config.yml");
			getServer().getPluginManager().disablePlugin(this);
		} else {
			EstateCommandExecutor cmd = new EstateCommandExecutor(this);
			getCommand("estates").setExecutor(cmd);
			if(config.getBoolean("storage.sql.use-worldguard"))
				db = new MySqlConnector(this, wg_config, true);
			else
				db = new MySqlConnector(this, config, false);
			//available = db.getAvailable();
		}
		rentColl = new RentCollector(this);
    }

    public boolean setupEcon() {
	if(getServer().getPluginManager().getPlugin("Vault") == null)
		return false;
	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	if (rsp == null)
		return false;
	econ = rsp.getProvider();
	return econ != null;
    }

    public boolean setupPerms() {
	if(getServer().getPluginManager().getPlugin("Vault") == null)
		return false;
	RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	perms = rsp.getProvider();
	return perms != null;
    }

    public RegionFlagManager getRegionFlagManager() {
	return manager;
    }

    public DBConnector getDBConnector() {
	return db;
    }

    public Permission getPermissions() {
	return perms;
    }

    public void createDefaultConfig(File cfg, String defaultName) {
	File parent=cfg.getParentFile();
	if(!parent.exists()) {
		parent.mkdirs();
	}

	if(!cfg.exists()) {

		InputStream input = null;
		try {
			JarFile jar = new JarFile(getFile());
			ZipEntry orig = jar.getEntry(defaultName);
			input = jar.getInputStream(orig);
			config = new YAMLProcessor(cfg, true, YAMLFormat.EXTENDED);
		} catch (Exception e) {
			System.out.println(e);
		}
		if(input != null) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(cfg);
				byte[] buf = new byte[8192];
				int length = 0;
				while((length = input.read(buf)) > 0 ) {
					out.write(buf, 0, length);
				}
			} catch (Exception f) {
				System.out.println(f);
			} finally {
				try {
					if(input != null)
						input.close();
				} catch (Exception ignore) {
				}
				try {
					if(out != null)
						out.close();
				}catch (Exception ignore) {
				}
			}
		}
	}
	config = new YAMLProcessor(cfg, true, YAMLFormat.EXTENDED);
	try {
		config.load();
	} catch (Exception e) {
		System.out.println(e);
	}
    }

}

