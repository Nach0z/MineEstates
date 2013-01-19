package org.nach0z.mineestate;

import java.util.*;

import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.protection.flags.*;

import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;

public class EstateCommandExecutor implements CommandExecutor {
    private RegionFlagManager regions = null;
    private MineEstatePlugin _plugin = null;
    private AccountHandler accounts = null;
    private Permission perms = null;
    private Map<String, LookupCache> lookups;;
    private String prefix = ChatColor.GREEN + "[Estates] " + ChatColor.GOLD;
    private String preferr = ChatColor.GREEN + "[Estates] "+ChatColor.RED;
    private String prefix2 = ChatColor.GREEN + "[Estates] "+ChatColor.YELLOW;
    private String prefpage = ChatColor.GREEN + "[Estates] "+ChatColor.GRAY;
    EstateCommandExecutor(MineEstatePlugin plugin) {
        _plugin=plugin;
        accounts = new AccountHandler(_plugin);
        regions =  _plugin.getRegionFlagManager();
        perms = _plugin.getPermissions();
        lookups = new HashMap<String, LookupCache>();
    }

    public boolean onCommand(CommandSender sender, Command commandStr, String label, String[] args) {
        Player player = null;
        String owner = "";
        String price = "";
        String size = "";
        String sort = "";
        int sortIndex = -1;
        boolean rents = false;
        boolean sales = false;
        if(sender instanceof Player)
            player = (Player) sender;

        if(!commandStr.getName().equalsIgnoreCase("estates"))
            return true;
        if(!(args.length > 1)) {
            sendUsage(sender);
            return true;
        }
		//Sets up a command enum for switch cases. Much faster than the old method.
		Command command = Command.fromString(commandStr);
	}
}
