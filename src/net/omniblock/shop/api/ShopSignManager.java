package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.omniblock.network.handlers.base.sql.util.Resolver;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.config.LineRegex;
import net.omniblock.shop.api.object.AbstractShop;
import net.omniblock.shop.api.object.AdminShop;
import net.omniblock.shop.api.object.UserShop;

public class ShopSignManager {

	protected static List<AbstractShop> registeredShops = new ArrayList<AbstractShop>();
	
	public static void setu() {
		
		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new ShopSignListener(), ShopPlugin.getInstance());
		
		//
		// TODO
		// CARGA DE CARTELES
		// EXISTENTES
		// EN CONFIGURACIÓN
		//
		
	}
	
	public static void addShop(AbstractShop shop) {
		
		registeredShops.add(shop);
		return;
		
	}
	
	public static void removeShop(AbstractShop shop) {
		
		if(registeredShops.contains(shop))
			registeredShops.remove(shop);
		
		return;
		
	}
	
	public static class ShopSignListener implements Listener {
		
		@EventHandler
		public void onClick(PlayerInteractEvent e) {
			
			if(e.getClickedBlock() != null) {
				
				if(e.getClickedBlock().getState() instanceof Sign) {
					
					Sign sign = (Sign) e.getClickedBlock().getState();
					
					for(AbstractShop shop : registeredShops) {
						
						if(shop.getSign() == sign) {
							
							shop.clickEvent(e);
							return;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		@EventHandler
		public void onCreate(SignChangeEvent e) {
			
			if(e.getLines().length >= 2) {
				
				/* El cartel esta pensado que funcione de la sig manera:
				 * 
				 * [TIENDA / ADMIN]
				 * [COMPRAR / VENDER]
				 * [ITEM]				//Se puede dejar en blanco (item en mano)
				 * [PRECIO x CANTIDAD]	//Si es admin y esta en blanco, poner solo la cantidad, el precio se pone automaticamente.
				 * 
				 */

				//Para carteles ADMIN;
				if(e.getPlayer().hasPermission("ShopSign.admin") && e.getLine(0).equalsIgnoreCase(LineRegex.CREATE_ADMIN_SHOP_UP)){
					
					//ADMINISTRADOR QUE COMPRA
					if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE)) {
						
						AdminShop shop = new AdminShop(
								(Sign) e.getBlock().getState(),
								Resolver.getNetworkIDByName(e.getPlayer().getName()),
								UUID.randomUUID().toString().substring(0, 4));
						
						shop.saveSign();
						addShop(shop);
						return;
						
					}
					//ADMINISTRADOR QUE VENDE
					else if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_SELL_SHOP_MIDDLE)) {
						
						AdminShop shop = new AdminShop(
								(Sign) e.getBlock().getState(),
								Resolver.getNetworkIDByName(e.getPlayer().getName()),
								UUID.randomUUID().toString().substring(0, 4));
						
						shop.saveSign();
						addShop(shop);
						return;
						
					}
				}
				
				//Para carteles TIENDA;
				if(e.getLine(0).equalsIgnoreCase(LineRegex.CREATE_USER_SHOP_UP)){
					//USER QUE COMPRA
					if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE)) {
						
						UserShop shop = new UserShop(
								(Sign) e.getBlock().getState(),
								Resolver.getNetworkIDByName(e.getPlayer().getName()),
								UUID.randomUUID().toString().substring(0, 4));
						
						shop.saveSign();
						addShop(shop);
						return;
						
					}
					//USER QUE VENDE
					else if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_SELL_SHOP_MIDDLE)) {
						
						UserShop shop = new UserShop(
								(Sign) e.getBlock().getState(),
								Resolver.getNetworkIDByName(e.getPlayer().getName()),
								UUID.randomUUID().toString().substring(0, 4));
						
						shop.saveSign();
						addShop(shop);
						return;
						
					}
				}
				
			}
			
		}
		
	}
	
}
