package org.nach0z.mineestate;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.*;

import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class RentCollector implements Runnable {
	private MineEstatePlugin _plugin;
	private RegionFlagManager rfm;
	private AccountHandler accounts = new AccountHandler(_plugin);
	private BukkitScheduler sched = Bukkit.getScheduler();
	public RentCollector(MineEstatePlugin plugin) {
		_plugin = plugin;
		rfm = new RegionFlagManager(_plugin);
		sched.scheduleSyncRepeatingTask(_plugin,this,120,6000);
	}

	public void run() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + roundUpToNearestFive(cal.get(Calendar.MINUTE));
		System.out.println("COLLECTING RENT.");
		DBConnector dbc = _plugin.db;
		List<World> worlds = Bukkit.getWorlds();
		ArrayList<Listing> tenants = new ArrayList<Listing>();
/*		List<OfflinePlayer> players = new ArrayList<Player>();
		for(World w : worlds) {
			tenants.addAll(dbc.getTenants(w));
		}
*/
		for(World w : worlds) {
			for(Listing l : _plugin.db.getTenants(w)) {
				tenants.add(l);
			}
		}
		if (tenants.isEmpty())
			return;
		for(Listing plot : tenants) {
			if(plot.type.equals(time)) {
				if(!accounts.hasFunds(plot.owner, plot.price)) {
					//release plot from tenant's control
					System.out.println("TODO: Insufficient Funds");
					_plugin.db.removeTenant(plot.name, plot.world);
				} else if (dbc.daysRemaining(plot.name, plot.world) == 0) {
					//release plot from tenant's control
					System.out.println("TODO: Expired lease");
					_plugin.db.removeTenant(plot.name, plot.world);
				} else {
					accounts.chargeMoney(plot.owner, plot.price);
					accounts.addMoney(rfm.getOwnerName(plot.name, Bukkit.getWorld(plot.world.getName())), plot.price);
					dbc.subtractDay(plot.name, plot.world);
					System.out.println("Collected!");

				}
			}
		}
		System.out.println("Finished collecting rents.");
	}

	public int roundUpToNearestFive(int orig) {
		int ret = (orig + 4) / 5 * 5;
		return ret;
	}

}
