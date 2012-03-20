package org.nach0z.mineestate;
import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.domains.*;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldedit.BlockVector;

import java.util.Set;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionFlagManager {
	private MineEstatePlugin _plugin;
	AccountHandler accounts = null;
	public void onDisable() {
    }

	public RegionFlagManager(MineEstatePlugin plugin) {
		accounts = new AccountHandler(_plugin);
		_plugin=plugin;
	}

    public double getRegionPrice(String regionName) {
	DoubleFlag tflag = DefaultFlag.PRICE;
	World world = Bukkit.getServer().getWorld("world");
	ProtectedRegion testreg = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
	Double price = testreg.getFlag(tflag);
	if(price != null)
		return price;
	else
		return -1;
    }

    public String getOwnerName(String regionName) {
	World world = Bukkit.getServer().getWorld("world");
	ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
	DefaultDomain owners = target.getOwners();
	Set<String> ownerList = owners.getPlayers();
	if(ownerList.size() > 1)
		return "FAILED_MULTI";
	else if(ownerList.size() == 0)
		return "PUBLIC_DOMAIN";
	else {
		Object ob = ownerList.iterator().next();
		if(ob instanceof String)
			return (String) ob;
		return "";
	}
    }

    public boolean existsRegion(String regionName) {
	World world = Bukkit.getServer().getWorld("world");
	ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
	return (target != null);
    }

    public boolean transferOwnership(String regionName, String newOwner) {
	World world = Bukkit.getServer().getWorld("world");
	ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
	String currentOwner = getOwnerName(regionName);
	boolean ret;
	if(!_plugin.getDBConnector().isForSale(regionName)) {
		_plugin.getDBConnector().removeForSale(regionName);
		return false;
	}
	if(currentOwner.equals("FAILED_MULTI"))
		return false;
	if(accounts.chargeMoney(newOwner,getRegionPrice(regionName))) {
		DefaultDomain newOwners = new DefaultDomain();
		DefaultDomain curOwner = target.getOwners();
		newOwners.addPlayer(newOwner);
		target.setOwners(newOwners);
		if(!currentOwner.equals("PUBLIC_DOMAIN")) {
			if(accounts.addMoney(currentOwner, getRegionPrice(regionName))) {
				ret = true;
				target.setFlag(DefaultFlag.PRICE, null);
			} else {
				accounts.addMoney(newOwner,getRegionPrice(regionName));
				target.setOwners(curOwner);
				_plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
				_plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
				ret = false;
			}
		}
		target.setFlag(DefaultFlag.PRICE, null);
		_plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
		_plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
		try {
			_plugin.WORLDGUARD.getGlobalRegionManager().get(world).save();
		} catch (Exception e) {
			System.out.println(e);
		}

		ret = true;
		return ret;
	}
	return false;

    }

    public boolean setPriceFlag(String regionName, double price) {
	Double val;
	ProtectedRegion target;
	ProtectedRegion updated;
	World world = Bukkit.getServer().getWorld("world");
	if(!existsRegion(regionName))
		return false;
	if(Double.compare(price, 0.0) == 0)
		val = null;
	else
		val = price;
	target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
	target.setFlag(DefaultFlag.PRICE, val);
	_plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
	_plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
                try {
                        _plugin.WORLDGUARD.getGlobalRegionManager().get(world).save();
                } catch (Exception e) {
                        System.out.println(e);
                }
	return true;
    }

    public String getRegionSize(String regionName) {
	World world = Bukkit.getServer().getWorld("world");
	if(existsRegion(regionName) ){
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		BlockVector min = target.getMinimumPoint();
		BlockVector max = target.getMaximumPoint();
		int widthZ = (int)max.getZ() - (int)min.getZ();
		int widthX = (int)max.getX() - (int)min.getX();
		return widthX+"x"+widthZ;

	} else
		return "";
    }

    public Location getTPPos(String regionName) {
	Location loc = null;
	World world = Bukkit.getServer().getWorld("world");
        if(existsRegion(regionName) ){
                ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		BlockVector max = target.getMaximumPoint();
		loc = new Location(world, max.getX(), world.getHighestBlockYAt((int)max.getX(), (int)max.getZ()), max.getZ());
	}
	return loc;
    }

}

