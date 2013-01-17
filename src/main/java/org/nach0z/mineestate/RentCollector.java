import org.bukkit.*;
import org.bukkit.scheduler.*;

public class RentCollector implements Runnable {

	private BukkitScheduler sched = Bukkit.getScheduler();
	public RentCollector() {
		sched.scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard"),this,120,36000);
	}

	public void run() {
		
	}

}
