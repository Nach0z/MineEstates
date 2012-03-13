package org.nach0z.mineestate;
import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.protection.flags.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class MineEstatePlugin extends JavaPlugin implements Listener{
    public Flag<Double> tester = new DoubleFlag("testing");
    public DoubleFlag tflag = DefaultFlag.PRICE;
    private WorldGuardPlugin worldguard_plugin;
    private ArrayList<String> available;
    public MySqlConnector db;
    public final WorldGuardPlugin WORLDGUARD = worldguard_plugin;
	public ProtectedRegion testreg;
	public void onDisable() {
        // TODO: Place any custom disable code here.
    }


    public void onEnable() {
//        getServer().getPluginManager().registerEvents(this, this);
//	System.out.println(getServer().getPluginManager().getPlugin("WorldGuard"));
	Plugin got_plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	if(got_plugin instanceof WorldGuardPlugin)
		worldguard_plugin = (WorldGuardPlugin) got_plugin;
	EstateCommandExecutor cmd = new EstateCommandExecutor(this);
	getCommand("estates").setExecutor(cmd);

	db = new MySqlConnector();
	available = db.getAvailable();
    }

}

