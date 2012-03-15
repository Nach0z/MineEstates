package org.nach0z.mineestate;
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
public class EstateCommandExecutor implements CommandExecutor {
	private RegionFlagManager regions = null;
	private MineEstatePlugin _plugin = null;
	private AccountHandler accounts = null;
	private String prefix = ChatColor.GREEN + "[Estates] " + ChatColor.GOLD;
	EstateCommandExecutor(MineEstatePlugin plugin) {
		_plugin=plugin;
		accounts = new AccountHandler(_plugin);
		regions = new RegionFlagManager(_plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		String owner = null;
		String price = null;
		String size = null;
		if(sender instanceof Player)
			player = (Player) sender;

		if(!command.getName().equalsIgnoreCase("estates"))
			return true;
		if(args[0].equalsIgnoreCase("search")) {
			for(int i = 0; i< args.length; i++) {
				if(args[i].equalsIgnoreCase("owner") && i < args.length-1)
					owner = args[i+1];
				if(args[i].equalsIgnoreCase("size") && i < args.length-1)
					size = args[i+1];
				if(args[i].equalsIgnoreCase("price") && i < args.length-1)
					price = args[i+1];

			}
		String message = getSales(owner, price, size);
		sender.sendMessage(prefix + message);
		return true;
		} else if (args[0].equalsIgnoreCase("buy")) {
			if(args[1] != null && regions.existsRegion(args[1])) {
				if(!accounts.hasFunds(player.getName(), regions.getRegionPrice(args[1])))
					sender.sendMessage(prefix + "You don't have enough funds to purchase this region!");
				else {
					regions.transferOwnership(args[1], player.getName());
				}
			}
		}


		return true;
	}

	public String getSales(String owner, String strPrice, String strSize) {
	//TODO: add in Owner/size/price search stuff.
	//BlockVector from ProtectedRegion.getMinimumPoint/getMaximumPoint has ints for min/max X, min/max Y
	return "lolzdebug OWNER = " + owner + "; PRICE = " + strPrice + "; SIZE = " + strSize;
	}

	

}
