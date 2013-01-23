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
	private Player player;
    private RegionFlagManager regions = null;
    private MineEstatePlugin _plugin = null;
    private AccountHandler accounts = null;
    private Permission perms = null;
    private Map<String, LookupCache> lookups;
    private String prefix = ChatColor.GREEN + "[Estates] " + ChatColor.GOLD;
    private String preferr = ChatColor.GREEN + "[Estates] "+ChatColor.RED;
    private String prefix2 = ChatColor.GREEN + "[Estates] "+ChatColor.YELLOW;
    private String prefpage = ChatColor.GREEN + "[Estates] "+ChatColor.GRAY;
	private CommandSender sender;
    EstateCommandExecutor(MineEstatePlugin plugin) {
        _plugin=plugin;
        accounts = new AccountHandler(_plugin);
        regions =  _plugin.getRegionFlagManager();
        perms = _plugin.getPermissions();
        lookups = new HashMap<String, LookupCache>();
    }

    public boolean onCommand(CommandSender sender, Command commandStr, String label, String[] args) {
        Player player = null;
		this.sender = sender;
		world = player.getWorld();
        if(sender instanceof Player) {
            player = (Player) sender;
			this.player = player;
		}

        if(!commandStr.getName().equalsIgnoreCase("estates"))
            return true;
        if(!(args.length > 1)) {
            sendUsage(sender);
            return true;
        }
		//Sets up a command enum for switch cases. Much faster than the old method.
		Commands command = Commands.fromString(args[0]);
		if(!command.satisfied(args)) {
			sendUsage(sender, args[1]);
			return true;
		}
		boolean ret = false;
		switch(command) {
			case PAGE : ret = page(args); break;
			case GOTO :
			case TP : ret = tp(args); break;
			case HELP :
			case USAGE : ret = usage(args); break;
			case SEARCH : ret = search(args); lookups.put(sender.getName(), null); break;
			case BUY : ret = buy(args); break;
			case SELL : ret = sell(args); break;
			case SELLPUBLIC : ret = sellpublic(args); break;
			case CANCEL : ret = cancel(args); break;
			case LEASE : ret = lease(args); break;
			case RENT : ret = rent(args); break;
			default:
				break;
		}
		return ret;
	}

	private boolean page(String args[]) {
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
		return true;
	}

	private boolean tp(String args[]) {
        if((!perms.has(player, "estates.plots.goto")) && !player.isOp()) {
            sender.sendMessage(preferr + "You do not have permission to goto plots!");
            return true;
        }
        if(args.length > 1) {
            if(regions.existsRegion(args[1],world) && _plugin.getDBConnector().isForSale(args[1])) {
                //World.getHighestBlockYAt(Location location)
                //player.teleport(Location location)
                Location loc = _plugin.getRegionFlagManager().getTPPos(args[1], world);
                if(loc != null)
                    player.teleport(loc);
                else
                    sender.sendMessage(preferr + "There was no safe place to teleport to inside the region.");
            } else {
                sender.sendMessage(prefix + "This plot is not available on the market right now.");
            }
        } else {
            sender.sendMessage(preferr + "Improper syntax. Usage: /estates goto <regionname>");
			return false;
        }
		return true;
	}

	private boolean usage(String args[]) {
		if(args.length == 0)
			sendUsage(sender);
		else
			sendUsage(sender, args[1]);
		return true;
	}

	 private boolean sendUsage(CommandSender sender) {
                    sender.sendMessage(prefix2 + "/estates sell <regionName> <price>");
                        sender.sendMessage(prefix2 + "/estates buy <regionname>");
                        sender.sendMessage(prefix2 + "/estates sellPublic <regionName> <price>");
                        sender.sendMessage(prefix2 + "/estates search [sales|rents] <params> <sortparam>");
                        sender.sendMessage(prefix2 + "/estates cancel <regionname>");
                        sender.sendMessage(prefix2 + "/estates page <pagenumber>");
                        return true;
    }

    private boolean sendUsage(CommandSender sender, String command) {
        ArrayList<String> ret = new ArrayList<String>();
        command = command.toLowerCase();
        if(command.equalsIgnoreCase("buy")) {
            ret.add(prefix + "/estates buy <region name>");
            ret.add(prefix2 + "This command is used to buy a region off of the open market.");
            ret.add(prefix2 + "Regions that are not on the market cannot be bought using /estates buy.");
            ret.add(prefix2 + "Find a region using '/estates search sales' .");
        } else if (command.equalsIgnoreCase("sell")) {
            ret.add(prefix + "/estates sell <region name> <price>");
            ret.add(prefix2 + "This command is used by the owner of a region to put it up for sale on the open market.");
            ret.add(prefix2 + "Keep in mind that once someone has bought a region, it is THEIRS. You won't get a warning.");
            ret.add(prefix2 + "In other words, get everything you want off the lot before you put it on the market.");
        } else if (command.equalsIgnoreCase("search")) {
            ret.add(prefix + "/estates search [type] <param> <sortparam>");
            ret.add(prefix2 + "This command is used to search the open market for regions.");
            ret.add(prefix2 + "[type] MUST be specified, and must be either \"sales\" or \"rents\" ");
            ret.add(prefix2 + "Params are: size, owner, price");
            ret.add(prefix2 + "Sort params are: owner, name, size, price");
            ret.add(prefix2 + "See "+ChatColor.RED+"/estates help flags "+ChatColor.YELLOW+"for more information.");
        } else if (command.equalsIgnoreCase("sellpublic")) {
            ret.add(prefix + "/estates sellpublic <regionname> <price>");
            ret.add(prefix2 + "Use this command to put a region on the market without a previous owner.");
            ret.add(prefix2 + "This command is for admin use, to create new regions and put them on the market without");
            ret.add(prefix2 + "the money being given to them for the purchase.");
            ret.add(prefix2 + "Use "+ChatColor.RED+"/estates sell"+ChatColor.YELLOW+" to sell your own plots.");
        } else if (command.equalsIgnoreCase("help")) {
            ret.add(prefix + "/estates help <help topic>");
            ret.add(prefix2 + "Use this to see the usage of each command");
            ret.add(prefix2 + "Help topics are: sell, sellpublic, search, buy, and flags");
        } else if (command.equalsIgnoreCase("flags")) {
            ret.add(prefix + "/estates search flag detail");
            ret.add(prefix2 + "owner <ownername> : Shows only results owned by <ownername>");
            ret.add(prefix2 + "size LxW : shows only sizes longer than L and wider than W. try 10x10.");
            ret.add(prefix2 + "price <number> : shows only plots less expensive than <number>");
            ret.add(prefix2 + "sort <flag> : sorts by either name, size, or price.");
        } else {
                ret.add(preferr + "There is no help message for that command.");
        }
        for(String str : ret)
            sender.sendMessage(str);
        return true;
	}

	private boolean search(String[] args) {
        String owner = "";
        String price = "";
        String size = "";
        String sort = "";
        int sortIndex = -1;
        boolean rents = false;
        boolean sales = false;
        if(args.length%2 != 0) {
            sender.sendMessage(preferr + "Wrong number of arguments. Usage: /estates search [sales|rents] <params> <sort param>");
            sender.sendMessage(preferr + "See /estates usage for a full description of params and sorting.");
        }
        for(int i = 0; i< args.length; i++) {
            if(args[i].equalsIgnoreCase("owner") && i < args.length-1 && !args[i-1].equalsIgnoreCase("sort"))
                owner = args[i+1];
            if(args[i].equalsIgnoreCase("size") && i < args.length-1 && !args[i-1].equalsIgnoreCase("sort"))
                size = args[i+1];
            if(args[i].equalsIgnoreCase("price") && i < args.length-1 && !args[i-1].equalsIgnoreCase("sort"))
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
        String message = getSales(owner, price, size, sales, rents, sort, world);
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
	}

	private boolean buy(String[] args) {
		if(!perms.has(player, "estates.plots.buy") && !player.isOp()) {
            sender.sendMessage(preferr + "You do not have permission to buy plots!");
            return true;
        }
        //@TODO add in confirmation/teleportation code
        if( regions.existsRegion(args[1], world) && Double.compare(regions.getRegionPrice(args[1], world), 0) > 0 ) {
	        double regPrice = regions.getRegionPrice(args[1], world);
            System.out.println(args[1] +" " + regions.getRegionPrice(args[1], world));
            if(!accounts.hasFunds(player.getName(), regPrice)) {
                sender.sendMessage(prefix + "You don't have enough funds to purchase this region!");
				return false;
            } else {
                if(regions.transferOwnership(args[1], player.getName(), world)) {
                    sender.sendMessage(prefix + "You have successfully purchased "+args[1]+" for " + regPrice +" "+ accounts.getUnitsPlural());
                    regions.setPriceFlag(args[1], 0, world);
                    _plugin.getDBConnector().removeForSale(args[1]);
                    return true;
                } else {
                    sender.sendMessage(prefix + "The purchase has failed. This may be because the region has multiple owners, or because of an internal error. Please talk to your server admin.");
					return false;
                }
            }
        }
		return false;
	}

	private boolean sell(String[] args) {
	    if(!perms.has(player, "estates.plots.sell") && !player.isOp()) {
            sender.sendMessage(preferr + "You do not have permission to sell plots!");
            return true;
        }
		if(regions.existsRegion(args[1],world)) {
            if( Double.compare(Double.parseDouble(args[2]), 0) > 0 && sender.getName().equalsIgnoreCase(regions.getOwnerName(args[1], world)) && regions.setPriceFlag(args[1], Double.parseDouble(args[2]), world)) {
                _plugin.getDBConnector().addForSale(args[1], Double.parseDouble(args[2]), world);
                sender.sendMessage(prefix + "Successfully added "+args[1]+" to the estate market for "+args[2]+"!");
				return true;
            } else {
                sender.sendMessage(preferr + "Failed to add specified estate to the market: You are not the owner!");
				return false;
            }
        } else {
            sender.sendMessage(preferr + "This region cannot be sold: It does not exist!");
        }
		return false;
	}

	private boolean sellpublic(String[] args) {
		if(!perms.has(player, "estates.plots.sellpublic") && !player.isOp()) {
            sender.sendMessage(preferr + "You do not have permission to sell public plots!");
            return true;
        }
		if(args.length > 2 && regions.setPriceFlag(args[1], Double.parseDouble(args[2]), world)) {
            _plugin.getDBConnector().addForSale(args[1], Double.parseDouble(args[2]), world);
            sender.sendMessage(prefix + "Successfully added "+args[1]+" to the estate market for "+args[2]+"!");
			return true;
        } else {
            sender.sendMessage(preferr + "Incorrect syntax. Usage: /estates sellPublic <regionname> <price>");
			return false;
        }
	}

	private boolean cancel(String[] args) {
		if(regions.existsRegion(args[1],world)) {
            if(sender.getName().equalsIgnoreCase(regions.getOwnerName(args[1], world)) || player.isOp() ||perms.has(player, "estates.plot.cancelOverride")) {
                if(!_plugin.getDBConnector().isForSale(args[1])) {
                    sender.sendMessage(preferr + "This plot is not on the market right now!");
                    return true;
                }
                _plugin.getDBConnector().removeForSale(args[1]);
                _plugin.getDBConnector().removeForRent(args[1]);
                _plugin.getRegionFlagManager().setPriceFlag(args[1], 0, world);
				return true;
            } else {
                sender.sendMessage(preferr + "You are not allowed to cancel this listing!");
				return false;
            }
        } else {
            sender.sendMessage(preferr + "This region does not exist!");
			return false;
        }
	}

	 public String getSales(String owner, String strPrice, String strSize, boolean sales, boolean rents, String sort, World world) {
        //TODO: add in Owner/size/price search stuff.
        //BlockVector from ProtectedRegion.getMinimumPoint/getMaximumPoint has ints for min/max X, min/max Y
        ArrayList<Listing> listings = new ArrayList<Listing>();
        if(rents)
            for(Listing l : _plugin.getDBConnector().getForRent(world))
                listings.add(l);
        if(sales)
            for(Listing l : _plugin.getDBConnector().getForSale(world))
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
                Collections.sort(listings, new ListingComparator(sort, _plugin, world));
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

    public boolean isGreaterSize(String test, String target) {
        String[] lis1pair = test.split("x");
                String[] lis2pair = target.split("x");
        return (Integer.parseInt(lis1pair[1]) >= Integer.parseInt(lis2pair[1]) && Integer.parseInt(lis1pair[0]) >= Integer.parseInt(lis2pair[0]));

    }

	private boolean lease(String[] args) {
        if(!perms.has(player, "estates.plots.lease") && !player.isOp()) {
            sender.sendMessage(preferr + "You do not have permission to sell plots!");
            return true;
        }
        if(regions.existsRegion(args[1],world)) {
            if( Double.compare(Double.parseDouble(args[2]), 0) > 0 && sender.getName().equalsIgnoreCase(regions.getOwnerName(args[1], world)) && regions.setPriceFlag(args[1], Double.parseDouble(args[2]), world)) {
                _plugin.getDBConnector().addForRent(args[1], Double.parseDouble(args[2]), world);
                sender.sendMessage(prefix + "Successfully added "+args[1]+" to the renal market for "+args[2]+"!");
				return true;
            } else {
                sender.sendMessage(preferr + "Failed to add specified estate to the market: You are not the owner!");
				return false;
            }
        } else {
            sender.sendMessage(preferr + "This region cannot be leased: It does not exist!");
			return false;
        }
	}

	private boolean rent(String[] args) {
		//stub
		return true;
	}

}
