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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

		if(!command.getName().equalsIgnoreCase("estates"))
			return true;
		if(!(args.length > 1)) {
			sender.sendMessage(preferr + "Incorrect syntax: Usages include all of the following:");
/*			sender.sendMessage(prefix2 + "/estates sell <regionName> <price>");
			sender.sendMessage(prefix2 + "/estates buy <regionname>");
			sender.sendMessage(prefix2 + "/estates sellPublic <regionName> <price> (Requires specific permissions!)");
			sender.sendMessage(prefix2 + "/estates search [sales|rents] <params> <sort param>");
			sender.sendMessage(prefix2 + "/estates cancel <regionname>: Cancels the sale/lease of a region.");
			sender.sendMessage(prefix2 + "/estates page <pagenumber> (do this command to see more pages of results, if they exist)");
			sender.sendMessage(prefix2 + "Valid params: owner <ownername>, size <#x#> (ex. 10x10, or 14x7), price <maxprice>");
			sender.sendMessage(prefix2 + "Valid sort params: owner, name, size, price. To sort your results, add \" sort <sortparam> \" to your query.");
*/			sendUsage(sender);
			return true;
		}
		if(!(args[0].equalsIgnoreCase("page")))
			lookups.put(sender.getName(), null);
		if(args[1].equalsIgnoreCase("usage")) {
/*                        sender.sendMessage(prefix2 + "/estates sell <regionName> <price>");
                        sender.sendMessage(prefix2 + "/estates buy <regionname>");
                        sender.sendMessage(prefix2 + "/estates sellPublic <regionName> <price> (Requires specific permissions!)");
                        sender.sendMessage(prefix2 + "/estates search [sales|rents] <params> <sort param>");
			sender.sendMessage(prefix2 + "/estates cancel <regionname>: Cancels the sale/lease of a region.");
			sender.sendMessage(prefix2 + "/estates page <pagenumber> (do this command to see more pages of results, if they exist)");
                        sender.sendMessage(prefix2 + "Valid params: owner <ownername>, size <#x#> (ex. 10x10, or 14x7), price <maxprice>");
                        sender.sendMessage(prefix2 + "Valid sort params: owner, name, size, price. To sort your results, add \" sort <sortparam> \" to your query.");
*/			sendUsage(sender);
                        return true;
		}

		if(args[0].equalsIgnoreCase("search")) {
			if(args.length%2 != 0) {
				sender.sendMessage(preferr + "Wrong number of arguments. Usage: /estates search [sales|rents] <params> <sort param>");
				sender.sendMessage(preferr + "See /estates usage for a full description of params and sorting.");
			}
			for(int i = 0; i< args.length; i++) {
				if(args[i].equalsIgnoreCase("owner") && i < args.length-1 && !(owner.length() > 0) && i > sortIndex)
					owner = args[i+1];
				if(args[i].equalsIgnoreCase("size") && i < args.length-1 && !(size.length() > 0) && i > sortIndex)
					size = args[i+1];
				if(args[i].equalsIgnoreCase("price") && i < args.length-1 && !(price.length() > 0) && i > sortIndex)
					price = args[i+1];
				if(args[i].equalsIgnoreCase("sort") && i < args.length-1) {
					if(args[i+1].contains("owner")) {
						sort = "owner";
						sortIndex = i;
					}
					else if (args[i+1].contains("name")) {
						sort = "name";
						sortIndex = i;
					}
					else if (args[i+1].contains("size")) {
						sort = "size";
						sortIndex = i;
					}
					else if (args[i+1].contains("price")) {
						sort = "price";
						sortIndex = i;
					}
					else {
						sender.sendMessage(prefix + "Sort parameter must be one of: name, size, price, owner");
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
		String[] lines = message.split("\n");
		sender.sendMessage(prefix + lines[0]);
		LookupCache tmpCache = new LookupCache();
		for(int i = 1; i < lines.length; i++)
			tmpCache.addLine(lines[i]);
		lookups.put(sender.getName(), tmpCache);
		sender.sendMessage(prefpage + "--- Results page "+ChatColor.WHITE+" 1 "+ChatColor.GRAY+" of "+ChatColor.WHITE+tmpCache.getPages()+ChatColor.GRAY+" ---");
		for(String str : lookups.get(sender.getName()).getLines(0))
			sender.sendMessage(prefix2 + str);
		return true;

		} else if (args[0].equalsIgnoreCase("buy")) {
			if(!perms.has(player, "estates.plots.buy") && !player.isOp()) {
				sender.sendMessage(preferr + "You do not have permission to buy plots!");
				return true;
			}
			//@TODO add in confirmation/teleportation code
			if(args.length > 1 ) {
				if( regions.existsRegion(args[1]) && Double.compare(regions.getRegionPrice(args[1]), 0) > 0 ) {
					double regPrice = regions.getRegionPrice(args[1]);
					System.out.println(args[1] +" " + regions.getRegionPrice(args[1]));
					if(!accounts.hasFunds(player.getName(), regPrice)) {
						sender.sendMessage(prefix + "You don't have enough funds to purchase this region!");
					} else {
						if(regions.transferOwnership(args[1], player.getName())) {
							sender.sendMessage(prefix + "You have successfully purchased "+args[1]+" for " + regPrice +" "+ accounts.getUnitsPlural());
							regions.setPriceFlag(args[1], 0);
							_plugin.getDBConnector().removeForSale(args[1]);
							return true;
						} else {
							sender.sendMessage(prefix + "The purchase has failed. This may be because the region has multiple owners, or because of an internal error. Please talk to your server admin.");
						}
					}
				}
			} else {
				sender.sendMessage(preferr + "Wrong number of args: Usage is /estates buy <regionname>");
			}
		} else if (args[0].equalsIgnoreCase("sell")) {
			if(!perms.has(player, "estates.plots.sell") && !player.isOp()) {
				sender.sendMessage(preferr + "You do not have permission to sell plots!");
				return true;
			}
			if(args.length > 2 ) {
				if(regions.existsRegion(args[1])) {
					if( Double.compare(Double.parseDouble(args[2]), 0) > 0 && sender.getName().equalsIgnoreCase(regions.getOwnerName(args[1])) && regions.setPriceFlag(args[1], Double.parseDouble(args[2]))) {
						_plugin.getDBConnector().addForSale(args[1], Double.parseDouble(args[2]));
						sender.sendMessage(prefix + "Successfully added "+args[1]+" to the estate market for "+args[2]+"!");
					} else {
						sender.sendMessage(preferr + "Failed to add specified estate to the market: You are not the owner!");
					}
				} else {
					sender.sendMessage(preferr + "This region cannot be sold: It does not exist!");
				}
			} else {
				sender.sendMessage(preferr + "Incorrect syntax. Usage: /estates sell <regionname> <price>");
			}

		} else if (args[0].equalsIgnoreCase("sellPublic")) {
			if(!perms.has(player, "estates.plots.sellpublic") && !player.isOp()) {
				sender.sendMessage(preferr + "You do not have permission to sell public plots!");
				return true;
			}
			if(args.length > 2 && regions.setPriceFlag(args[1], Double.parseDouble(args[2]))) {
				_plugin.getDBConnector().addForSale(args[1], Double.parseDouble(args[2]));
				sender.sendMessage(prefix + "Successfully added "+args[1]+" to the estate market for "+args[2]+"!");
			} else {
				sender.sendMessage(preferr + "Incorrect syntax. Usage: /estates sellPublic <regionname> <price>");
			}
		} else if (args[0].equalsIgnoreCase("goto")) {
			if(!perms.has(player, "estates.plots.buy") && !player.isOp()) {
				sender.sendMessage(preferr + "You do not have permission to goto plots!");
				return true;
			}
			if(args.length > 1) {
				if(regions.existsRegion(args[1]) && _plugin.getDBConnector().isForSale(args[1])) {
					//World.getHighestBlockYAt(Location location)
					//player.teleport(Location location)
					Location loc = _plugin.getRegionFlagManager().getTPPos(args[1]);
					player.teleport(loc);
				} else {
					sender.sendMessage(prefix + "This plot is not available on the market right now.");
				}
			} else {
				sender.sendMessage(preferr + "Improper syntax. Usage: /estates goto <regionname>");
			}
		} else if (args[0].equalsIgnoreCase("page")) {
			if(lookups.get(sender.getName()) == null || lookups.get(sender.getName()).getLines(0).size() == 0) {
				sender.sendMessage(preferr + "You have not put in a search query yet! Use /estates search to look for available plots.");
				return true;
			}
			LookupCache tmpCache = lookups.get(sender.getName());
			int offset = 8*(Integer.parseInt(args[1])-1);
			if(tmpCache.getLines(offset).size() == 0)
				sender.sendMessage(prefix + "No results on this page.");
			else {
				sender.sendMessage(prefpage + "--- Results page "+ChatColor.WHITE+args[1]+ChatColor.GRAY+" of "+ChatColor.WHITE+tmpCache.getPages()+ChatColor.GRAY+" ---");
				for(String str : tmpCache.getLines(offset))
					sender.sendMessage(prefix2 + str);
			}
		} else if (args[0].equalsIgnoreCase("cancel")) {
			if(regions.existsRegion(args[1])) {
				if(sender.getName().equalsIgnoreCase(regions.getOwnerName(args[1])) || player.isOp() ||perms.has(player, "estates.plot.cancelOverride")) {
					if(!_plugin.getDBConnector().isForSale(args[1])) {
						sender.sendMessage(preferr + "This plot is not on the market right now!");
						return true;
					}
					_plugin.getDBConnector().removeForSale(args[1]);
					_plugin.getDBConnector().removeForRent(args[1]);
					_plugin.getRegionFlagManager().setPriceFlag(args[1], 0);
				} else {
					sender.sendMessage(preferr + "You are not allowed to cancel this listing!");
				}
			} else {
				sender.sendMessage(preferr + "This region does not exist!");
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
			if(!(strSize.equals("")) && !(isGreaterSize(listings.get(i).size, strSize))) {
				listings.remove(i);
				i--;
			}
			//select only regions with this owner name
			if(!(owner.equals("")) && !(listings.get(i).owner.equalsIgnoreCase(owner))) {
				listings.remove(i);
				i--;
			}
			//narrow by price (increasing)
			if(!(strPrice.equals("")) && !(Double.parseDouble(strPrice) >= listings.get(i).price)) {
				listings.remove(i);
				i--;
			}
			//
			if(!(sort.equals(""))) {
				Collections.sort(listings, new ListingComparator(sort, _plugin));
			}
		}
		if(listings.size() > 0) {
			String ret = "";
			ret += "Name:           Price:     Size:    Owner:";
			for(Listing lis : listings)
				ret+="\n"+lis.toString();
			return ret;
		} else
			return "No listings found for those parameters! Try broadening your search, or get an admin to put some plots on the market.";
	}

	public Boolean isGreaterSize(String test, String target) {
		String[] lis1pair = test.split("x");
                String[] lis2pair = target.split("x");
		return (Integer.parseInt(lis1pair[1]) >= Integer.parseInt(lis2pair[1]) && Integer.parseInt(lis1pair[0]) >= Integer.parseInt(lis2pair[0]));

	}

	public boolean sendUsage(CommandSender sender) {
	                sender.sendMessage(prefix2 + "/estates sell <regionName> <price>");
                        sender.sendMessage(prefix2 + "/estates buy <regionname>");
                        sender.sendMessage(prefix2 + "/estates sellPublic <regionName> <price> (Requires specific permissions!)");
                        sender.sendMessage(prefix2 + "/estates search [sales|rents] <params> <sort param>");
                        sender.sendMessage(prefix2 + "/estates cancel <regionname>: Cancels the sale/lease of a region.");
                        sender.sendMessage(prefix2 + "/estates page <pagenumber> (do this command to see more pages of results, if they exist)");
                        sender.sendMessage(prefix2 + "Valid params: owner <ownername>, size <#x#> (ex. 10x10, or 14x7), price <maxprice>");
                        sender.sendMessage(prefix2 + "Valid sort params: owner, name, size, price. To sort your results, add \" sort <sortparam> \" to your query.");
                        return true;
	}

/*	public boolean sendUsage(CommandSender sender, String command) {
		ArrayList<String> ret = new ArrayList<String>();
		command = command.toLowerCase();
		switch (command) {
			case "buy" :
				ret.add(prefix + "/estates buy <region name>");
				ret.add(prefix2 + "This command is used to buy a region off of the open market.");
				ret.add(prefix2 + "Regions that are not on the market cannot be bought using /estates buy.");
				ret.add(prefix2 + "Find a region using '/estates search sales' .");
				break;
			case "sell" :
				ret.add(prefix + "/estates sell <region name> <price>");
				ret.add(prefix2 + "This command is used by the owner of a region to put it up for sale on the open market.");
				ret.add(prefix2 + "Keep in mind that once someone has bought a region, it is THEIRS. You won't get a warning.");
				ret.add(prefix2 + "In other words, get everything you want off the lot before you put it on the market.");
				break;
			default:
				ret.add(preferr + "There is no help message for that command.");
				break;
		}
	}
*/
}
