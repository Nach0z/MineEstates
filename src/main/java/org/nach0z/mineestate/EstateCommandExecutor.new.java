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
	private World world;
    private RegionFlagManager regions = null;
    private MineEstatePlugin _plugin = null;
    private AccountHandler accounts = null;
    private Permission perms = null;
    private Map<String, LookupCache> lookups;
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
		world = player.getWorld;
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
		if(!command.satisfied(args)) {
			sendUsage(sender, args[1]);
			return true;
		}
		boolean ret = false;
		switch(command) {
			case PAGE : ret = page(args); break;
			case TP : ret = tp(args); break;
			case USAGE : ret = sendUsage(args); break;
			case SEARCH : ret = search(args); lookups.put(sender, null); break;
			case BUY : ret = buy(args); break;
			case SELL : ret = sell(args); break;
			case SELLPUBLIC : ret = sellpublic(args); break;
			case CANCEL : ret = cancel(args); break;
			default:
				break;
		}
		return ret;
	}

	private boolean page(String args[]) {
	}

	

}
