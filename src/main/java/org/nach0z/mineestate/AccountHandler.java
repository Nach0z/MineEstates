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
		Plugin tmpvlt = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		if(tmpvlt instanceof Vault)
			vault = (Vault) Bukkit.getServer().getPluginManager().getPlugin("Vault");
		econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();

	}


	public boolean hasFunds(String playerName, double amount) {
	if(!econ.hasAccount(playerName))
		econ.createBank(playerName,playerName);

	double bal = econ.getBalance(playerName);
		return (Double.compare(bal, amount) >= 0);

	}

	public boolean chargeMoney(String playerName, double amount) {
		return econ.withdrawPlayer(playerName, amount).transactionSuccess();
	

	}

	public boolean addMoney(String playerName, double amount) {
		return econ.depositPlayer(playerName, amount).transactionSuccess();
	}

}
