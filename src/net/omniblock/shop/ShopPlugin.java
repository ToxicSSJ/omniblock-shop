package net.omniblock.shop;

import org.bukkit.plugin.java.JavaPlugin;

import net.omniblock.network.handlers.Handlers;
import net.omniblock.network.handlers.network.NetworkManager;
import net.omniblock.packets.object.external.ServerType;
import net.omniblock.shop.api.ShopSignManager;

public class ShopPlugin extends JavaPlugin {

	private static ShopPlugin instance;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		if(NetworkManager.getServertype() != ServerType.SURVIVAL) {
			
			Handlers.LOGGER.sendModuleInfo("&7Se ha registrado Shop v" + this.getDescription().getVersion() + "!");
			Handlers.LOGGER.sendModuleMessage("Survival", "Se ha inicializado Shop en modo API!");
			return;
			
		}
		
		Handlers.LOGGER.sendModuleInfo("&7Se ha registrado Shop v" + this.getDescription().getVersion() + "!");
		Handlers.LOGGER.sendModuleMessage("Survival", "Se ha inicializado Shop correctamente!");
		
		ShopSignManager.setup();
		
	}
	
	public static ShopPlugin getInstance() {
		return instance;
	}
	
}
