import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.protection.flags.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MineEstate extends JavaPlugin implements Listener {
    public Plugin got_plugin;
    public DoubleFlag tflag = new DoubleFlag("tflag");
    public WorldGuardPlugin worldguard_plugin;
	public ProtectedRegion testreg;
	public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
	System.out.println(getServer().getPluginManager().getPlugin("WorldGuard"));
	got_plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	if(got_plugin instanceof WorldGuardPlugin)
		worldguard_plugin = (WorldGuardPlugin) got_plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	World world = getServer().getWorld("world");
	testreg = worldguard_plugin.getGlobalRegionManager().get(world).getRegion("testRegion");
	Double price = testreg.getFlag(new DoubleFlag(""));
	//System.out.println(testreg);
	System.out.println(price + " = price");
    }
}

