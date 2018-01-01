package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import net.omniblock.network.handlers.base.bases.type.RankBase;
import net.omniblock.network.handlers.base.sql.util.Resolver;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.network.systems.rank.type.RankType;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.config.variables.LineRegex;
import net.omniblock.shop.api.object.AbstractShop;
import net.omniblock.shop.api.object.UserShop;
import net.omniblock.shop.api.object.AbstractShop.ShopLoadStatus;
import net.omniblock.shop.api.type.ShopActionType;
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
								ShopActionType.valueOf(ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".actionType")),
								ConfigType.SHOPDATA.getConfig().getInt("usershop." + uniqueID + ".price"),
								ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".playerNetworkID"),
								uniqueID);
						
						if(userShop.loadSign(null) == ShopLoadStatus.LOADED) {
							
							addShop(userShop);
							
							if(!ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts")) {
								
								ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + userShop.getPlayerNetworkID(), 1);
								ConfigType.SHOPDATA.getConfigObject().save();
								
							}
							
						}
						
					} catch(Exception e) {
						
						String playerNetworkID = ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".playerNetworkID");
						
						if(ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts"))
							ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + playerNetworkID, ConfigType.SHOPDATA.getConfig().getInt("usershops-amounts." + playerNetworkID) - 1);
						
						ConfigType.SHOPDATA.getConfig().set("usershop." + uniqueID, null);
						ConfigType.SHOPDATA.getConfigObject().save();
						continue;
						
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
						
						Location location = LocationUtils.deserializeLocation(ConfigType.SHOPDATA.getConfig().getString("adminshop." + uniqueID + ".location"));
						
						Sign sign = (Sign) location.getBlock().getState();
						Chest chest = TileUtils.getChestBehindSign(sign);
						
						AdminShop adminShop = new AdminShop(
								sign,
								chest,
								ShopActionType.ADMIN,
								uniqueID);
						
						if(adminShop.loadSign(null) == ShopLoadStatus.LOADED)
							addShop(adminShop);
						
					} catch(Exception e) {
						
						e.printStackTrace();
						
						ConfigType.SHOPDATA.getConfig().set("adminshop." + uniqueID, null);
						ConfigType.SHOPDATA.getConfigObject().save();
						continue;
						
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
	 * Recibir todas las tiendas registradas
	 * por el sistema.
	 * 
	 * @return Una lista con todas las tiendas
	 * que han sido registradas.
	 */
	public static List<AbstractShop> getShops() {
		
		return registeredShops;
		
	}
	
	/**
	 * 
	 * Con este metodo podrás recibir el numero
	 * maximo de tiendas que puede tener un usuario
	 * en base a su rango.
	 * 
	 * @return El numero maximo de tiendas que puede
	 * crear.
	 */
	public static int getMaxShopsByRank(Player player) {
		
		RankType rank = RankBase.getRank(player);
		
		switch(rank) {
		
		case ADMIN:
			return 1000;
		case BNF:
			return 500;
		case CEO:
			return 1000;
		case GM:
			return 500;
		case HELPER:
			return 500;
		case MOD:
			return 500;
		case TITAN:
			return 500;
		case GOLEM:
			return 145;
		case USER:
			return 45;
		default:
			return 45;
		
		}
		
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
				
				//
				// Con el fin de unicamente registrar
				// los eventos tipo click sobre un
				// bloque.
				//
				if(		e.getAction() == Action.RIGHT_CLICK_AIR ||
						e.getAction() == Action.LEFT_CLICK_AIR ||
						e.getAction() == Action.PHYSICAL)
					return;
				
				
				
				//
				// Para prevenir que los usuarios puedan abrir
				// los cofres 
				//
				if(e.getClickedBlock().getState() instanceof Chest) {
					
					//
					// Verificar que el cofre que se está
					// abriendo pertenezca a una tienda.
					//
					for(AbstractShop shop : registeredShops) {
						
						Block chestBlock = shop.getChest().getBlock();
						
						//
						// En caso de que pertenezca a una tienda verificar
						// si quien está abriendo el cofre es un usuario
						// y si lo es, verificar si es el propietario.
						//
						if(chestBlock.equals(e.getClickedBlock())) {
							
							if(shop instanceof UserShop) {
								
								if(((UserShop) shop).getCachePlayer() == null)
									if(Bukkit.getPlayer(Resolver.getLastNameByNetworkID(shop.getPlayerNetworkID())) != null)
										((UserShop) shop).setCachePlayer(Bukkit.getPlayer(Resolver.getLastNameByNetworkID((shop.getPlayerNetworkID()))));
								
								Player shopOwner = ((UserShop) shop).getCachePlayer();
								
								if(e.getPlayer().equals(shopOwner))
									return;
								
								e.setCancelled(true);
								e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLa tienda que intentas abrir no es de tu propiedad!"));
								return;
							}
							
							e.setCancelled(true);
							return;
							
						}
						
					}
					
				}
				
				if(e.getClickedBlock().getState() instanceof Sign) {
					
					//
					// La blacklist es un sistema
					// para evitar que se repita muchas
					// veces el mismo evento ya que
					// PlayerInteractEvent se puede llamar
					// hasta 5 veces en 1 solo click.
					// 
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
					
					//
					// Iterar todas las tiendas que
					// ya están registradas.
					//
					for(AbstractShop shop : registeredShops) {
						
						Block shopBlock = shop.getBlock();
						Block block = sign.getBlock();
						
						if(shopBlock.equals(block)) {
							
							//
							// Ejecutar el metodo de click en caso
							// de que el bloque clickleado sea
							// perteneciente a la tienda que se
							// está iterando.
							//
							shop.clickEvent(e);
							return;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		@EventHandler
		public void onDestroy(BlockBreakEvent e) {
			
			//
			// Verificar si el bloque es un valido
			// componente de una tienda.
			//
			if(		e.getBlock().getType() == Material.SIGN ||
					e.getBlock().getType() == Material.SIGN_POST ||
					e.getBlock().getType() == Material.WALL_SIGN ||
					e.getBlock().getType() == Material.CHEST ||
					e.getBlock().getType() == Material.TRAPPED_CHEST) {
				
				//
				// Iterar todas las tiendas para buscar
				// si el bloque destruído pertenece
				// a alguna de ellas.
				//
				for(AbstractShop shop : registeredShops) {
					
					Block chestBlock = shop.getChest().getBlock();
					Block shopBlock = shop.getBlock();
					
					Block block = e.getBlock();
					
					if(shopBlock.equals(block) || chestBlock.equals(block)) {
						
						//
						// Acciones a tomar en el caso
						// de que la tienda haya sido
						// tipo Jugador. (UserShop)
						//
						if(shop instanceof UserShop) {
							
							//
							// Si al jugador que intenta destruír el cartel/cofre
							// no le pertenece la tienda se cancelará el evento.
							//
							if(Resolver.getLastNameByNetworkID(shop.getPlayerNetworkID()) == e.getPlayer().getName()) {
								
								if(ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts"))
									ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + shop.getPlayerNetworkID(), ConfigType.SHOPDATA.getConfig().getInt("usershops-amounts." + shop.getPlayerNetworkID()) - 1);
								
								ConfigType.SHOPDATA.getConfigObject().save();
								
								e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has &8destruido &7tu tienda correctamente!"));
								return;
								
							}
							
							//
							// Si quien destruyó la tienda fue un
							// miembro del Staff y este es Moderador/Ayudante
							// se ejecutará la acción y se enviará
							// el registro al Discord.
							// TODO
							//
							if(e.getPlayer().isOp() || e.getPlayer().hasPermission("shop.usershop.adminbreak")) {
								
								if(ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts"))
									ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + shop.getPlayerNetworkID(), ConfigType.SHOPDATA.getConfig().getInt("usershops-amounts." + shop.getPlayerNetworkID()) - 1);
								
								ConfigType.SHOPDATA.getConfigObject().save();
								
								e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has forzado la destrucción de la tienda &c'" + shop.getUniqueID() + "'&7 de &8" + Resolver.getLastNameByNetworkID(shop.getPlayerNetworkID()) + "&7 correctamente!"));
								
								shop.destroySign();
								return;
								
							}
								
							e.setCancelled(true);
							return;
							
						}
						
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
					
					//
					// Verificar que la linea 3 se está
					// utilizando para el precio.
					//
					if(!NumberUtils.isNumber(e.getLine(2)))
						return;
					
					//
					// Verificar que el cofre con el cual
					// se está creando la tienda no esté
					// siendo utilizando por otra.
					//
					for(AbstractShop shop : registeredShops) {
						
						Block chestBlock = shop.getChest().getBlock();
					
						if(chestBlock.equals(chest.getBlock())) {
							
							e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cYa se ha creado una tienda en este lugar!"));
							
							e.setCancelled(true);
							e.getBlock().breakNaturally();
							return;
							
						}
					
					}
					
					UserShop shop = new UserShop(
							(Sign) e.getBlock().getState(),
							chest,
							e.getLine(1).equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE) ? ShopActionType.BUY : ShopActionType.SELL,
							NumberUtils.toInt(e.getLine(2)),
							Resolver.getNetworkIDByName(e.getPlayer().getName()),
							UUID.randomUUID().toString().substring(0, 10));
					
					if(shop.loadSign(e.getPlayer()) == ShopLoadStatus.LOADED)
						addShop(shop);
					
					return;
					
				}
				
			}
			
			//
			// El cartel en caso de ser tipo Admin funcionará de la siguiente
			// manera:
			// 
			// [ADMIN]             			| Prefijo tienda.
			//
			if(e.getLine(0).equalsIgnoreCase(LineRegex.CREATE_ADMIN_SHOP_UP)){
					
					AdminShop shop = new AdminShop(
							(Sign) e.getBlock().getState(),
							chest,
							ShopActionType.ADMIN,
							UUID.randomUUID().toString().substring(0, 4));
					
					if(shop.loadSign(e.getPlayer()) == ShopLoadStatus.LOADED)
						addShop(shop);
					
					return;
					
			}
			
		}
		
		@EventHandler
		public void onPlace(BlockPlaceEvent e) {
			
			//
			// Verificar si el bloque que se ha puesto
			// se trata de un hopper y si encima del mismo
			// hay un cofre.
			//
			if(e.getBlock().getType() == Material.HOPPER) {
				
				Chest chest = 
						e.getBlock().getRelative(BlockFace.UP).getType() != Material.CHEST ?
								TileUtils.getChestByHopper((Hopper) e.getBlock().getState()) :
								(Chest) e.getBlock().getRelative(BlockFace.UP).getState();
				
				//
				// Iterar todas las tiendas para buscar
				// si el cofre que es relativo al hopper
				// pertenece a alguna de ellas.
				//
				if(chest != null)
					for(AbstractShop shop : registeredShops) {
					
						Block chestBlock = shop.getChest().getBlock();
					
						if(chestBlock.equals(chest.getBlock()))
							if(!Resolver.getLastNameByNetworkID(shop.getPlayerNetworkID()).equals(e.getPlayer().getName())) {
								
								//
								// En caso de que el usuario no sea el dueño
								// de la tienda cancelar el evento y enviarle
								// un mensaje.
								//
								
								e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo puedes colocar tolvas alrededor de las tiendas de los demás usuarios!"));
								e.setBuild(false);
								e.setCancelled(true);
								return;
								
							} else { return; }
					
					}
				
			}
			
		}
		
		
		
	}
	
}
