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

public class MineEstatePlugin extends JavaPlugin implements Listener{
    private Economy econ = null;
    private YAMLProcessor wg_config;
    private WorldGuardPlugin worldguard_plugin;
    private ArrayList<String> available;
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
	if(got_plugin instanceof WorldGuardPlugin) {
		worldguard_plugin = (WorldGuardPlugin) got_plugin;
		WORLDGUARD = worldguard_plugin;
		System.out.println("WorldGuardPlugin!");
		System.out.println(worldguard_plugin.getDataFolder());
	}
	else
		System.out.println("Not a WorldGuardPlugin!");
	if(!setupEcon()) {
		System.out.println("[FATAL ERROR] MineEdit requires Vault and a Vault-compatible Economy plugin to be installed to interface with the economy! Please install Vault");
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

	System.out.println(WORLDGUARD.getDataFolder());
	wg_config = new YAMLProcessor(new File(WORLDGUARD.getDataFolder(), "config.yml"), true, YAMLFormat.EXTENDED);
	try {
		wg_config.load();
	} catch (Exception e) {
		System.out.println(e);
		System.out.println("[FATAL ERROR] MineEstates unable to read WorldGuard config file! Please install WorldGuard before using MineEstates.");
		getServer().getPluginManager().disablePlugin(this);
		return;
	}

	if (!wg_config.getBoolean("regions.sql.use")) {
		System.out.println("[FATAL ERROR] MineEstates requires WorldGuard to be using the MySQL Database method currently. Later releases may allow non-MySQL databases.");
		getServer().getPluginManager().disablePlugin(this);
		return;
	 } else {

		EstateCommandExecutor cmd = new EstateCommandExecutor(this);
		getCommand("estates").setExecutor(cmd);

		db = new MySqlConnector(this, wg_config);
		available = db.getAvailable();
	}
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

}

