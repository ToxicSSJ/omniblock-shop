package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import net.omniblock.network.handlers.base.sql.util.Resolver;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.config.variables.LineRegex;
import net.omniblock.shop.api.object.AbstractShop;
import net.omniblock.shop.api.object.UserShop;
import net.omniblock.shop.api.object.AbstractShop.ShopLoadStatus;
import net.omniblock.shop.api.object.AdminShop;
import net.omniblock.shop.utils.LocationUtils;
import net.omniblock.shop.utils.TileUtils;

/**
 * 
 * Clase principal para el manejo de
 * carteles del sistema de tiendas.
 * 
 * @author zlToxicNetherlz & SoZyk
 * @see AbstractShop
 *
 */
public class ShopSignManager {

	protected static List<AbstractShop> registeredShops = new ArrayList<AbstractShop>();
	
	/**
	 * 
	 * Instalación del sistema de tiendas.
	 * Este metodo estatico debe ser ejecutado
	 * al iniciar el plugin.
	 * 
	 */
	public static void setup() {
		
		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new ShopSignListener(), ShopPlugin.getInstance());
		
		//
		// Se cargarán a continuación todos los
		// carteles registrados dentro de la
		// configuración.
		//
		
		//
		// Primero se cargan las tiendas de los
		// usuarios.
		//
		if(ConfigType.SHOPDATA.getConfig().isSet("usershop"))
			if(ConfigType.SHOPDATA.getConfig().isConfigurationSection("usershop"))
				for(String uniqueID : ConfigType.SHOPDATA.getConfig().getConfigurationSection("usershop").getKeys(false)) {
					
					try {
						
						Block block = LocationUtils.deserializeLocation(ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".location")).getBlock();
						
						Sign sign = (Sign) block.getState();
						Chest chest = TileUtils.getChestBehindSign(sign);
						
						UserShop userShop = new UserShop(
								sign,
								chest,
								ConfigType.SHOPDATA.getConfig().getInt("usershop." + uniqueID + ".price"),
								ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".playerNetworkID"),
								uniqueID);
						
						if(userShop.loadSign(null) == ShopLoadStatus.LOADED)
							addShop(userShop);
						
					} catch(Exception e) {
						
						ConfigType.SHOPDATA.getConfig().set("usershop." + uniqueID, null);
						ConfigType.SHOPDATA.getConfigObject().save();
						return;
						
					}
					
				}
		
		//
		// A continuación se cargarán las tiendas
		// tipo administrador.
		//
		if(ConfigType.SHOPDATA.getConfig().isSet("adminshop"))
			if(ConfigType.SHOPDATA.getConfig().isConfigurationSection("adminshop"))
				for(String uniqueID : ConfigType.SHOPDATA.getConfig().getConfigurationSection("adminshop").getKeys(false)) {
					
					try {
						
						Block block = LocationUtils.deserializeLocation(ConfigType.SHOPDATA.getConfig().getString("adminshop." + uniqueID + ".location")).getBlock();
						
						Sign sign = (Sign) block.getState();
						Chest chest = TileUtils.getChestBehindSign(sign);
						
						AdminShop userShop = new AdminShop(
								sign,
								chest,
								uniqueID);
						
						if(userShop.loadSign(null) == ShopLoadStatus.LOADED)
							addShop(userShop);
						
					} catch(Exception e) {
						
						ConfigType.SHOPDATA.getConfig().set("usershop." + uniqueID, null);
						ConfigType.SHOPDATA.getConfigObject().save();
						return;
						
					}
					
				}
		
		
	}
	
	/**
	 * 
	 * Añadir una tienda a la lista del
	 * registro.
	 * 
	 * @param shop Tienda que se desea añadir.
	 */
	public static void addShop(AbstractShop shop) {
		
		registeredShops.add(shop);
		return;
		
	}
	
	/**
	 * 
	 * Remover una tienda a la lista del
	 * registro.
	 * 
	 * @param shop Tienda que se desea remover.
	 */
	public static void removeShop(AbstractShop shop) {
		
		if(registeredShops.contains(shop))
			registeredShops.remove(shop);
		
		return;
		
	}
	
	/**
	 * 
	 * Esta clase es la encargada de
	 * ejecutar las acciones de los
	 * carteles registrados.
	 * 
	 * @author zlToxicNetherlz & SoZyk
	 *
	 */
	public static class ShopSignListener implements Listener {
		
		public static List<Player> blacklist = Lists.newArrayList();
		
		@EventHandler
		public void onClick(PlayerInteractEvent e) {
			
			if(e.getClickedBlock() != null) {
				
				if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
					return;
				
				if(e.getClickedBlock().getState() instanceof Sign) {
					
					if(blacklist.contains(e.getPlayer()))
						return;
					
					blacklist.add(e.getPlayer());
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							if(blacklist.contains(e.getPlayer()))
								blacklist.remove(e.getPlayer());
							
						}
						
					}.runTaskLater(ShopPlugin.getInstance(), 5L);
					
					Sign sign = (Sign) e.getClickedBlock().getState();
					
					for(AbstractShop shop : registeredShops) {
						
						Block shopBlock = shop.getBlock();
						Block block = sign.getBlock();
						
						if(shopBlock.equals(block)) {
							
							shop.clickEvent(e);
							return;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		@EventHandler
		public void onDestroy(BlockBreakEvent e) {
			
			if(		e.getBlock().getType() == Material.SIGN ||
					e.getBlock().getType() == Material.SIGN_POST ||
					e.getBlock().getType() == Material.WALL_SIGN) {
				
				for(AbstractShop shop : registeredShops) {
					
					Block shopBlock = shop.getBlock();
					Block block = e.getBlock();
					
					if(shopBlock.equals(block)) {
						
						if(e.getPlayer().isOp() || e.getPlayer().hasPermission("shop.usershop.adminbreak")) {
							
							shop.destroySign();
							return;
							
						}
						
						if(e.getPlayer().hasPermission("shop.usershop.break")) {
							
							
							
						}
						
						return;
						
					}
					
				}
				
			}
			
		}
		
		@SuppressWarnings("deprecation")
		@EventHandler
		public void onCreate(SignChangeEvent e) {
			
			//
			// Tomar el cofre detrás del cartel
			// en caso de que no exista dicho
			// cofre se retornará null.
			//
			Chest chest = TileUtils.getChestBehindSign((Sign) e.getBlock().getState());
			
			if(chest == null || e.getLines().length < 3)
				return;
			
			//
			// El cartel en caso de ser un Usuario funcionará de la siguiente
			// manera:
			// 
			// [TIENDA]             			| Prefijo tienda.
			// [COMPRAR / VENDER]   			| El tipo de tienda (compra o venta).
			// [PRECIO]							| El precio que se le dará a cada unidad.
			//
			if(e.getLine(0).equalsIgnoreCase(LineRegex.CREATE_USER_SHOP_UP)){

				if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE) || e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_SELL_SHOP_MIDDLE)) {
					
					if(!NumberUtils.isNumber(e.getLine(2)))
						return;
					
					UserShop shop = new UserShop(
							(Sign) e.getBlock().getState(),
							chest,
							NumberUtils.toInt(e.getLine(2)),
							Resolver.getNetworkIDByName(e.getPlayer().getName()),
							UUID.randomUUID().toString().substring(0, 4));
					
					if(shop.loadSign(e.getPlayer()) == ShopLoadStatus.LOADED)
						addShop(shop);
					
					return;
					
				}
				
			}
			
			//
			// El cartel en caso de ser tipo Admin funcionará de la siguiente
			// manera:
			// 
			// [TIENDA]             			| Prefijo tienda.
			// [COMPRAR / VENDER]   			| El tipo de tienda (compra o venta).
			//
			if(e.getPlayer().hasPermission("shop.shopadmin.admin") && e.getLine(0).equalsIgnoreCase(LineRegex.CREATE_ADMIN_SHOP_UP)){
				
				if(e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE) || e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_SELL_SHOP_MIDDLE)) {
					
					AdminShop shop = new AdminShop(
							(Sign) e.getBlock().getState(),
							chest,
							UUID.randomUUID().toString().substring(0, 4));
					
					if(shop.loadSign(e.getPlayer()) == ShopLoadStatus.LOADED)
						addShop(shop);
					
					return;
					
				}
				
			}
			
			
		}
		
	}
	
}
