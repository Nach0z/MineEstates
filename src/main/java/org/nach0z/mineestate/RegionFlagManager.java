package org.nach0z.mineestate;

import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.domains.*;
import com.sk89q.worldguard.protection.flags.*;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.Vector;

import java.util.Set;
import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;

public class RegionFlagManager {
	private MineEstatePlugin _plugin;
	AccountHandler accounts = null;
	public void onDisable() {
    }

	public RegionFlagManager(MineEstatePlugin plugin) {
		accounts = new AccountHandler(_plugin);
		_plugin=plugin;
	}

    public double getRegionPrice(String regionName, World world) {
		DoubleFlag tflag = DefaultFlag.PRICE;
//		World world = Bukkit.getServer().getWorld("world");
		ProtectedRegion testreg = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		Double price = testreg.getFlag(tflag);
		if(price != null)
			return price;
		else
			return -1;
    	}

    public Location getTeleLocation(String regionName, World world) {
		LocationFlag lFlag = DefaultFlag.TELE_LOC;
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		com.sk89q.worldedit.Location loc = target.getFlag(lFlag);
		if(loc == null)
			return null;
		com.sk89q.worldedit.Vector vec = loc.getPosition();
		Location loc2;
		String worldName = loc.getWorld().getName();
		double x = vec.getX();
		double y = vec.getY();
		double z = vec.getZ();
		World wrld = Bukkit.getServer().getWorld(worldName);
		loc2 = new Location(wrld, x, y, z);

		return loc2;
    }

    public String getOwnerName(String regionName, World world) {
	//	World world = Bukkit.getServer().getWorld("world");
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
    
    public String getMemberNames(String regionName, World world) {
    	ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
    	DefaultDomain members = target.getMembers();
    	Set<String> memberList = members.getPlayers();
    	if(memberList.size() > 1)
    		return "FAILED_MULTI";
    	else if (memberList.size() == 0)
    		return "EMPTY";
    	else {
    		Object ob = memberList.iterator().next();
    		if(ob instanceof String)
    			return (String) ob;
    		return "";
    	}
    }

    public boolean existsRegion(String regionName, World world) {
//		World world = Bukkit.getServer().getWorld("world");
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		return (target != null);
    }

    public boolean removeMember(String regionName, String member, World world) {
    	ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
    	String currentOwner = getOwnerName(regionName, world);
    	boolean ret;
    	if(currentOwner.equals("FAILED_MULTI"))
    		return false;
    	DefaultDomain newMembers = target.getMembers();
    	newMembers.removePlayer(member);
    	target.setMembers(newMembers);
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
    
	public boolean addMember(String regionName, String member, World world) {
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		String currentOwner = getOwnerName(regionName, world);
		boolean ret;
		if(!_plugin.getDBConnector().isForRent(regionName)) {
			_plugin.getDBConnector().removeForRent(regionName, world);
			return false;
		}
		if(currentOwner.equals("FAILED_MULTI"))
			return false;
		if(accounts.chargeMoney(member, getRegionPrice(regionName, world))) {
			DefaultDomain newMembers = target.getMembers();
			newMembers.addPlayer(member);
			target.setMembers(newMembers);
			if(accounts.addMoney(currentOwner, getRegionPrice(regionName, world))) {
				ret = true;
			} else {
				newMembers.removePlayer(member);
				target.setMembers(newMembers);
				accounts.addMoney(member, getRegionPrice(regionName, world));
                _plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
                _plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
				ret = false;
			}
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

    public boolean transferOwnership(String regionName, String newOwner, World world) {
//		World world = Bukkit.getServer().getWorld("world");
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
		String currentOwner = getOwnerName(regionName, world);
		boolean ret;
		if(!_plugin.getDBConnector().isForSale(regionName)) {
			_plugin.getDBConnector().removeForSale(regionName, world);
			return false;
		}
		if(currentOwner.equals("FAILED_MULTI"))
			return false;
		if(accounts.chargeMoney(newOwner,getRegionPrice(regionName, world))) {
			DefaultDomain newOwners = new DefaultDomain();
			DefaultDomain curOwner = target.getOwners();
			newOwners.addPlayer(newOwner);
			target.setOwners(newOwners);
			if(!currentOwner.equals("PUBLIC_DOMAIN")) {
				if(accounts.addMoney(currentOwner, getRegionPrice(regionName, world))) {
					ret = true;
					target.setFlag(DefaultFlag.PRICE, null);
				} else {
					accounts.addMoney(newOwner,getRegionPrice(regionName, world));
					target.setOwners(curOwner);
					_plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
					_plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
					ret = false;
				}
			}
			target.setFlag(DefaultFlag.PRICE, null);
			_plugin.WORLDGUARD.getGlobalRegionManager().get(world).removeRegion(regionName);
			_plugin.WORLDGUARD.getGlobalRegionManager().get(world).addRegion(target);
			updateLockette(regionName, newOwner, world);
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

    public boolean setPriceFlag(String regionName, double price, World world) {
		Double val;
		ProtectedRegion target;
		ProtectedRegion updated;
//		World world = Bukkit.getServer().getWorld("world");
		if(!existsRegion(regionName, world))
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

    public String getRegionSize(String regionName, World world) {
//		World world = Bukkit.getServer().getWorld("world");
		if(existsRegion(regionName, world) ){
			ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
			BlockVector min = target.getMinimumPoint();
			BlockVector max = target.getMaximumPoint();
			int widthZ = (int)max.getZ() - (int)min.getZ();
			int widthX = (int)max.getX() - (int)min.getX();
			return widthX+"x"+widthZ;

		} else
			return "";
	    }

    public Location getTPPos(String regionName, World world) {
		Location loc = null;
		int blockIndex = -1;
//		World world = Bukkit.getServer().getWorld("world");
   	    if(existsRegion(regionName, world) ){
   	        ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regionName);
			if(!(getTeleLocation(regionName, world) == null)) {
				loc = getTeleLocation(regionName, world);
			} else {
				BlockVector min = target.getMinimumPoint();
				BlockVector max = target.getMaximumPoint();
				int maxX = (int) max.getX();
				int maxZ = (int) max.getZ();
				int x = (int) min.getX();
				int z = (int) min.getZ();
				int i = x;
				int j = z;
				for(x = (int) min.getX(); x < maxX; x++) {
					for(z = (int) min.getZ(); z < maxZ; z++) {
						if(blockIndex == -1) {
							blockIndex = getSafeBlock(x,z,world,target);
							i = x;
							j = z;
						}
						System.out.println(x+" "+blockIndex+" "+z);
					}
				}

				loc = new Location(world, new Double(i) + 0.5, blockIndex, new Double(j) + 0.5);
			}
		}
		if(blockIndex != -1 || loc != null)
			return loc;
		else
			return null;
    }

    public int getSafeBlock(int x, int z, World world, ProtectedRegion target) {
		int blockIndex = -1;
		for(int i = 1; i < 256 ; i++) {
            Block bl = world.getBlockAt(x,i,z);
            Vector blvec = new Vector(bl.getX(), bl.getY(), bl.getZ());
            boolean b1 = (world.getBlockAt(x,i,z).getTypeId() == 0);
            boolean b2 = (world.getBlockAt(x,i+1,z).getTypeId() == 0);
			int floorBlockId = world.getBlockAt(x,i-1,z).getTypeId();
            boolean b3 = (floorBlockId == 10);
            boolean b4 = (floorBlockId == 11);
            boolean b5 = (floorBlockId == 0);
			boolean b6 = (floorBlockId == 81);
			boolean b7 = (floorBlockId == 51);
			boolean b8 = (floorBlockId == 70);
			boolean b9 = (floorBlockId == 72);
			boolean b10 = (floorBlockId == 77);
			boolean b11 = (floorBlockId == 50);
			boolean b12 = (floorBlockId == 75);
			boolean b13 = (floorBlockId == 76);

            if(b1 && b2 && !(b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11 || b12 || b13) && target.contains(blvec))
                blockIndex = i;
        }
		return blockIndex;

    }



    public void updateLockette(String regName, String newOwner, World world) {
		ProtectedRegion target = _plugin.WORLDGUARD.getGlobalRegionManager().get(world).getRegion(regName);
		CuboidRegion cuboid = new CuboidRegion(target.getMinimumPoint(), target.getMaximumPoint());
		ArrayList<ThreadedChunkEditor> list = new ArrayList<ThreadedChunkEditor>();
		for(Vector2D vec : cuboid.getChunks()) {
			System.out.println(vec);
			Chunk chunk = world.getChunkAt((int)vec.getX(),(int)vec.getZ());
			ThreadedChunkEditor tce = new ThreadedChunkEditor(cuboid, chunk, newOwner);
			tce.start();
		}

		for(ThreadedChunkEditor tce : list) {
			try {
				tce.join();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		System.out.println("Finished threaded processes!");
    }

}

