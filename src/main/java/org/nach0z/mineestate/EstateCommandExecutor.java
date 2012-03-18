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

public class EstateCommandExecutor implements CommandExecutor {
	private RegionFlagManager regions = null;
	private MineEstatePlugin _plugin = null;
	private AccountHandler accounts = null;
	private String prefix = ChatColor.GREEN + "[Estates] " + ChatColor.GOLD;
	EstateCommandExecutor(MineEstatePlugin plugin) {
		_plugin=plugin;
		accounts = new AccountHandler(_plugin);
		regions =  _plugin.getRegionFlagManager();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		String owner = "";
		String price = "";
		String size = "";
		String sort = "";
		boolean rents = false;
		boolean sales = false;
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
				if(args[i].equalsIgnoreCase("sort") && i < args.length-1) {
					if(args[i+1].contains("owner"))
						sort = "owner";
					else if (args[i+1].contains("name"))
						sort = "name";
					else if (args[i+1].contains("size"))
						sort = "size";
					else if (args[i+1].contains("price"))
						sort = "price";
					else {
						sender.sendMessage(prefix + "Sort parameter must be one of: name, size, price, owner");
						return false;
					}
				}
				for(String str : args) {
					if(str.equalsIgnoreCase("sales"))
						sales = true;
					if(str.equalsIgnoreCase("rents"))
						rents = true;
				}

			}
		String message = getSales(owner, price, size, sales, rents, sort);
		sender.sendMessage(prefix + message);
		return true;
		} else if (args[0].equalsIgnoreCase("buy")) {
			if(args[1] != null && regions.existsRegion(args[1]) && Double.compare(regions.getRegionPrice(args[1]), 0) > 0 ) {
				double regPrice = regions.getRegionPrice(args[1]);
				if(!accounts.hasFunds(player.getName(), regPrice))
					sender.sendMessage(prefix + "You don't have enough funds to purchase this region!");
				else {
					if(regions.transferOwnership(args[1], player.getName())) {
						sender.sendMessage(prefix + "You have successfully purchased "+args[1]+" for " + price + accounts.getUnitsPlural());
						return true;
					} else {
						sender.sendMessage(prefix + "The purchase has failed. This may be because the region has multiple owners, or because of an internal error. Please talk to your server admin.");
						return false;
					}
				}
			}
		}


		return true;
	}

	public String getSales(String owner, String strPrice, String strSize, boolean sales, boolean rents, String sort) {
		//TODO: add in Owner/size/price search stuff.
		//BlockVector from ProtectedRegion.getMinimumPoint/getMaximumPoint has ints for min/max X, min/max Y
		ArrayList<Listing> listings = new ArrayList<Listing>();
		if(rents)
			for(Listing l : _plugin.getDBConnector().getForRent())
				listings.add(l);
		if(sales)
			for(Listing l : _plugin.getDBConnector().getForSale())
				listings.add(l);
		for(int i = 0; i < listings.size(); i++) {
			//narrow by size (increasing)
			if(!(strSize.equals("")) && !(isGreaterSize(listings.get(i).size, strSize)))
				listings.remove(i);
			//select only regions with this owner name
			if(!(owner.equals("")) && !(regions.getOwnerName(listings.get(i).name).equalsIgnoreCase(owner)))
				listings.remove(i);
			//narrow by price (increasing)
			if(!(strPrice.equals("")) && !(Double.parseDouble(strPrice) < listings.get(i).price))
				listings.remove(i);
			//
			if(!(sort.equals(""))) {
				Collections.sort(listings, new ListingComparator(sort, _plugin));
			}
		}
		return "lolzdebug OWNER = " + owner + "; PRICE = " + strPrice + "; SIZE = " + strSize + "; includeRents = " + rents + "; includeSales = " + sales;
	}

	public Boolean isGreaterSize(String test, String target) {
		String[] lis1pair = test.split("x");
                String[] lis2pair = target.split("x");
		return (Integer.parseInt(lis1pair[1]) >= Integer.parseInt(lis2pair[1]) && Integer.parseInt(lis1pair[0]) >= Integer.parseInt(lis2pair[0]));

	}


}
