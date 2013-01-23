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
	private AccountHandler accounts = new AccountHandler(_plugin);
	private BukkitScheduler sched = Bukkit.getScheduler();
	public RentCollector() {
		sched.scheduleSyncRepeatingTask(_plugin,this,120,36000);
	}

	public void run() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + roundUpToNearestFive(cal.get(Calendar.MINUTE));
		DBConnector dbc = _plugin.db;
		List<World> worlds = Bukkit.getWorlds();
		ArrayList<Listing> tenants = new ArrayList<Listing>();
/*		List<OfflinePlayer> players = new ArrayList<Player>();
		for(World w : worlds) {
			tenants.addAll(dbc.getTenants(w));
		}
*/
		for(Listing plot : tenants) {
			//if(plot.type.equals(time)) {
				if(!accounts.hasFunds(plot.owner, plot.price)) {
					//release plot from tenant's control
				} else if (dbc.daysRemaining(plot.name, worlds.get(0)) == 0) {
					//release plot from tenant's control
				} else {
					accounts.chargeMoney(plot.owner, plot.price);
					dbc.subtractDay(plot.name, worlds.get(0));
				}
			//}
		}
	}

	public int roundUpToNearestFive(int orig) {
		int ret = (orig + 4) / 5 * 5;
		return ret;
	}

}
