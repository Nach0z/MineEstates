package org.nach0z.mineestate;
import org.bukkit.*;
import org.bukkit.plugin.*;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public class AccountHandler {
	public static Economy econ = null;
	public Vault vault = null;
	public static Permission perms = null;
	private MineEstatePlugin _plugin;
	public AccountHandler(MineEstatePlugin plugin) {
		_plugin = plugin;
		if(_plugin.getServer().getPluginManager().getPlugin("Vault") instanceof Vault)
			vault = (Vault) _plugin.getServer().getPluginManager().getPlugin("Vault");
	}


	

}
