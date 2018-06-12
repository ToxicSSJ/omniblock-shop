package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.network.library.helpers.inventory.InventoryBuilderListener;
import net.omniblock.network.library.utils.LocationUtils;
import net.omniblock.network.library.utils.NumberUtil;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.network.systems.CommandPatcher;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.object.npc.NPCShop;
import net.omniblock.shop.api.type.NPCShopType;
import net.omniblock.shop.utils.EntityUtils;
import net.omniblock.survival.platform.Platform;

public class ShopNPCManager {

	protected static List<NPCShop> registeredNPCs = new ArrayList<NPCShop>();
	protected static BukkitTask task;

	/**
	 *
	 * Se preparará toda la tienda de los NPCs.
	 * 
	 */
	public static void setup() {

		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new ShopNPCListener(),
				ShopPlugin.getInstance());

		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new InventoryBuilderListener(),
				ShopPlugin.getInstance());

		ShopPlugin.getInstance().getCommand("shopnpc").setExecutor(new ShopNPCExecutor());

		Platform.runLater(() -> {
			
			if (ConfigType.SHOP_NPC_DATA.getConfig().isSet("npcshop"))
				if (ConfigType.SHOP_NPC_DATA.getConfig().isConfigurationSection("npcshop"))
					for (String uniqueID : ConfigType.SHOP_NPC_DATA.getConfig().getConfigurationSection("npcshop")
							.getKeys(false)) {

						try {

							Location location = LocationUtils.deserializeLocation(ConfigType.SHOP_NPC_DATA.getConfig()
									.getString("npcshop." + uniqueID + ".location"));
							Location lookAt = LocationUtils.deserializeLocation(
									ConfigType.SHOP_NPC_DATA.getConfig().getString("npcshop." + uniqueID + ".lookAt"));

							NPCShopType type = NPCShopType.valueOf(
									ConfigType.SHOP_NPC_DATA.getConfig().getString("npcshop." + uniqueID + ".type"));

							NPCShop newShop = new NPCShop(type, uniqueID, location, lookAt);

							newShop.loadNPC();
							registerNPCShop(newShop);
							continue;

						} catch (Exception e) {

							ConfigType.SHOP_NPC_DATA.getConfig().set("npcshop." + uniqueID, null);
							ConfigType.SHOP_NPC_DATA.getConfigObject().save();
							continue;

						}

					}

			makeIA();

		}, 21);
	}

	/**
	 * Se registrara un NPC.
	 * 
	 * @param shop
	 *            Se crea un objeto nuevo para ser registrado.
	 * 
	 */
	public static void registerNPCShop(NPCShop shop) {

		if (!registeredNPCs.contains(shop))
			registeredNPCs.add(shop);

		return;

	}

	/**
	 * 
	 * Se elimina el NPC registrado.
	 * 
	 * @param shop
	 *            Se instancia el objeto para eliminarlo.
	 * 
	 */
	public static void removeNPCShop(NPCShop shop) {
		if (registeredNPCs.contains(shop))
			registeredNPCs.remove(shop);

		return;

	}

	/**
	 * 
	 * Verifica si existe una tienda registrada en el sistema en base al tipo de
	 * tienda.
	 * 
	 * @param type
	 *            El tipo de tienda que se desea comprobar.
	 * @return <strong>true</true> si existe una tienda registrada con el tipo
	 *         especificado.
	 */
	public static boolean existsNPCShop(NPCShopType type) {

		for (NPCShop shop : registeredNPCs)
			if (shop.getNpctype() == type)
				return true;

		return false;

	}

	/**
	 * 
	 * Este metodo devolverá la lista de los npc registrados y funcionales del
	 * sistema.
	 * 
	 * @return La lista de los npc registrados.
	 */
	public static List<NPCShop> getShops() {
		return registeredNPCs;
	}

	public static void makeIA() {

		task = Platform.runTimer(() -> {

			for (NPCShop npc : registeredNPCs) {

				String text = npc.getDialogs()[NumberUtil.getRandomInt(0, npc.getDialogs().length - 1)];

				if (text == null)
					continue;

				for (Entity entity : npc.getNpc().getEntity().getNearbyEntities(5, 5, 5)) {

					if (entity == null)
						continue;

					if (entity instanceof Player) {

						Player p = (Player) entity;
						LivingEntity npcEntity = (LivingEntity) npc.getNpc().getEntity();

						if (EntityUtils.isLookingAtEntity(p, npcEntity)) {

							p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 2, 2);
							p.sendMessage("");
							p.sendMessage(TextUtil.format(npc.getNpctype().getName() + "&b&l» " + "&7" + text));
							p.sendMessage("");

						}
					}
				}
			}
		}, 60);
	}

	/**
	 * Se registrara todos los eventos de los NPCs.
	 * 
	 */
	public static class ShopNPCListener implements Listener {

		@EventHandler
		public void onClick(PlayerInteractAtEntityEvent e) {

			Platform.runLater(() -> {

				if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked())) {

					NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());

					for (NPCShop shop : registeredNPCs) {

						if (shop.getNpc() != npc)
							continue;

						if (shop.getNpctype() != null) {

							if (shop.getNpctype().getAction() == null)
								continue;

							NPCShop.NPCAction action = shop.getNpctype().getAction();
							action.clickEvent(npc, e.getPlayer());

						}
					}
				}

			}, 10);
		}
	}

	public static class ShopNPCExecutor implements CommandExecutor {

		public static Map<Player, NPCShop> selectedNPC = new HashMap<Player, NPCShop>();

		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

			if (!cmd.getName().equalsIgnoreCase("shopnpc"))
				return false;

			if (!sender.hasPermission("shop.shopnpc.admin"))
				return false;

			if (!(sender instanceof Player))
				return false;

			Player player = (Player) sender;

			if (selectedNPC.containsKey(player)) {

				NPCShop shop = selectedNPC.get(player);

				if (shop == null || shop.isDestroyed()) {

					sender.sendMessage(TextUtil.format(
							"&8&lT&8iendas &b&l» &cLa tienda que intentas modificar ya ha sido destruida por otra persona, se ha deseleccionado."));
					selectedNPC.remove(player);
					return true;

				}

				if (args.length == 1)
					if (args[0].equalsIgnoreCase("deseleccionar")) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas deseleccionado la tienda &7'"
								+ shop.getNpctype().name() + "' &acorrectamente!"));
						selectedNPC.remove(player);
						return true;

					}

				if (args.length == 1)
					if (args[0].equalsIgnoreCase("destruir")) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas destruido la tienda &c'"
								+ shop.getNpctype().name() + "' &acorrectamente!"));

						shop.destroy();

						if (registeredNPCs.contains(shop))
							registeredNPCs.remove(shop);

						selectedNPC.remove(player);
						return true;

					}

				if (args.length == 1)
					if (args[0].equalsIgnoreCase("mover")) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas movido la tienda &7'"
								+ shop.getNpctype().name() + "' &aa tu posición correctamente!"));

						shop.move(player.getLocation(), EntityUtils.getBlockAtLooking(player, 10).getLocation());
						return true;

					}

				if (args.length == 2)
					if (args[0].equalsIgnoreCase("cambiar")) {

						NPCShopType type = NPCShopType.getTypeByName(args[1]);

						if (type == null) {

							sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl tipo de tienda &6'" + args[1]
									+ "' &ano es una tienda valida!"));
							return true;

						}

						if (type.equals(shop.getNpctype())) {

							sender.sendMessage(TextUtil
									.format("&8&lT&8iendas &b&l» &cYa la tienda es del tipo &7'" + args[1] + "'&c!"));
							return true;

						}

						if (existsNPCShop(type)) {

							sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cYa la tienda tipo &6'" + args[1]
									+ "' &cestá registrada por el sistema, no puedes duplicarla!"));
							return true;

						}

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas cambiado la tienda &8'"
								+ shop.getNpctype().name() + "' &aal tipo &b'" + args[1] + "'&a correctamente!"));

						shop.destroy();

						NPCShop newShop = new NPCShop(type, UUID.randomUUID().toString().substring(0, 10),
								shop.getLocation(), EntityUtils.getBlockAtLooking(player, 10).getLocation());

						newShop.loadNPC();
						registerNPCShop(newShop);

						selectedNPC.put(player, newShop);
						return true;

					}

				sender.sendMessage(CommandPatcher.BAR);
				sender.sendMessage(TextUtil.format(" &cERROR: &7Comando no valido."));
				sender.sendMessage(TextUtil.format(" &8» &7Tus comandos disponibles son:"));
				sender.sendMessage(
						TextUtil.format(" &b/shopnpc cambiar [tipo] &e- &7Cambia el tipo de la tienda seleccionada."));
				sender.sendMessage(TextUtil.format(" &b/shopnpc destruir &e- &7Destruye la tienda seleccionada."));
				sender.sendMessage(TextUtil.format(" &b/shopnpc mover &e- &7Mover el npc seleccionado a tu pocisión."));
				sender.sendMessage(TextUtil.format(" &b/shopnpc deseleccionar &e- &7Deselecciona la tienda actual."));
				sender.sendMessage(CommandPatcher.BAR);
				return true;

			}

			if (args.length == 2)
				if (args[0].equalsIgnoreCase("seleccionar")) {

					NPCShopType type = NPCShopType.getTypeByName(args[1]);
					NPCShop shop = null;

					if (type == null) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl tipo de tienda &6'" + args[1]
								+ "' &ano es una tienda valida!"));
						return true;

					}

					if (!existsNPCShop(type)) {

						sender.sendMessage(TextUtil.format(
								"&8&lT&8iendas &b&l» &cLa tienda tipo &6'" + args[1] + "' &c no ha sido creada!"));
						return true;

					}

					for (NPCShop cacheShop : registeredNPCs)
						if (cacheShop.getNpctype().equals(type))
							shop = cacheShop;

					sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas seleccionado la tienda &b'"
							+ shop.getNpctype().name() + "' &acorrectamente!"));
					selectedNPC.put(player, shop);
					return true;

				}

			if (args.length == 1)
				if (args[0].equalsIgnoreCase("seleccionar")) {

					Iterator<NPCShop> iterator = registeredNPCs.iterator();
					NPCShop cacheNPC = null;

					while (cacheNPC == null && iterator.hasNext()) {

						NPCShop cache = iterator.next();

						if (cache != null)
							if (cache.getNpc().isSpawned() && cache.getNpc().getEntity() != null)
								if (cache.getNpc().getEntity() instanceof LivingEntity)
									if (EntityUtils.isLookingAtEntity(player,
											(LivingEntity) cache.getNpc().getEntity()))
										if (player.hasLineOfSight(cache.getNpc().getEntity()))
											cacheNPC = cache;

					}

					if (cacheNPC == null) {

						sender.sendMessage(TextUtil.format(
								"&8&lT&8iendas &b&l» &cNo se ha detectado ninguna tienda en tu campo de visión!"));
						return true;

					}

					sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas seleccionado la tienda &b'"
							+ cacheNPC.getNpctype().name() + "' &acorrectamente!"));
					selectedNPC.put(player, cacheNPC);
					return true;

				}

			if (args.length == 2)
				if (args[0].equalsIgnoreCase("tp")) {

					NPCShopType type = NPCShopType.getTypeByName(args[1]);
					NPCShop shop = null;

					if (type == null) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl tipo de tienda &6'" + args[1]
								+ "' &ano es una tienda valida!"));
						return true;

					}

					if (!existsNPCShop(type)) {

						sender.sendMessage(TextUtil.format(
								"&8&lT&8iendas &b&l» &cLa tienda tipo &6'" + args[1] + "' &c no ha sido creada!"));
						return true;

					}

					for (NPCShop cacheShop : registeredNPCs)
						if (cacheShop.getNpctype().equals(type))
							shop = cacheShop;

					player.teleport(shop.getLocation());
					sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas sido teletransportado a la tienda &7'"
							+ args[1] + "' &acorrectamente!"));
					return true;

				}

			if (args.length == 1)
				if (args[0].equalsIgnoreCase("lista")) {

					sender.sendMessage(CommandPatcher.BAR);
					sender.sendMessage(TextUtil.format(""));
					sender.sendMessage(TextUtil.format(" &8» &7Variables importantes:"));
					sender.sendMessage(TextUtil.format(" &a&lVERDE &e- &7Tienda creada."));
					sender.sendMessage(TextUtil.format(" &c&lROJO &e- &7Tienda aún no creada."));
					sender.sendMessage(TextUtil.format(""));
					sender.sendMessage(TextUtil.format(" &8» &7Todas las tiendas se mostrarán a continuación:"));

					List<String> shops = Lists.newArrayList();

					for (NPCShopType shopType : NPCShopType.values())
						shops.add(existsNPCShop(shopType) ? "&a" + shopType.name() : "&c" + shopType.name());

					sender.sendMessage(" " + TextUtil.format(StringUtils.join(shops, "&8, &r")));

					sender.sendMessage(TextUtil.format(""));
					sender.sendMessage(CommandPatcher.BAR);
					return true;

				}

			if (args.length == 2)
				if (args[0].equalsIgnoreCase("crear")) {

					NPCShopType type = NPCShopType.getTypeByName(args[1]);

					if (type == null) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl tipo de tienda &6'" + args[1]
								+ "' &ano es una tienda valida!"));
						return true;

					}

					if (existsNPCShop(type)) {

						sender.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cYa la tienda tipo &6'" + args[1]
								+ "' &cestá registrada por el sistema, no puedes duplicarla!"));
						return true;

					}

					sender.sendMessage(
							TextUtil.format("&8&lT&8iendas &b&l» &aHas creado y &nseleccionado&r&a la tienda &b'"
									+ args[1] + "' &acorrectamente!"));

					NPCShop newShop = new NPCShop(type, UUID.randomUUID().toString().substring(0, 10),
							player.getLocation(), EntityUtils.getBlockAtLooking(player, 10).getLocation());

					newShop.loadNPC();
					registerNPCShop(newShop);

					selectedNPC.put(player, newShop);
					return true;

				}

			sender.sendMessage(CommandPatcher.BAR);
			sender.sendMessage(TextUtil.format(" &cERROR: &7Comando no valido."));
			sender.sendMessage(TextUtil.format(" &8» &7Tus comandos disponibles son:"));
			sender.sendMessage(
					TextUtil.format(" &b/shopnpc seleccionar &e- &7Selecciona la tienda que estás mirando."));
			sender.sendMessage(
					TextUtil.format(" &b/shopnpc seleccionar [tipo] &e- &7Selecciona la tienda especificada."));
			sender.sendMessage(TextUtil.format(
					" &b/shopnpc lista &e- &7Te mostrará la lista de todos las tiendas, en verde los ya creados, en rojo los aún faltantes."));
			sender.sendMessage(
					TextUtil.format(" &b/shopnpc crear [tipo] &e- &7Crea una tienda en el lugar donde estas."));
			sender.sendMessage(
					TextUtil.format(" &b/shopnpc tp [tipo] &e- &7Te teletransportará al tipo de NPC especificado."));
			sender.sendMessage(CommandPatcher.BAR);
			return true;

		}

	}

}
