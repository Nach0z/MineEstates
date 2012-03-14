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
	public static Permission perms = null;
	private MineEstatePlugin _plugin;
	public AccountHandler(MineEstatePlugin plugin) {
		_plugin = plugin;
	}


}
