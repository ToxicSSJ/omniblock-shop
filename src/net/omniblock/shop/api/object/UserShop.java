package net.omniblock.shop.api.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

import net.omniblock.network.handlers.base.bases.type.AccountBase;
import net.omniblock.network.handlers.base.sql.util.Resolver;
import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.utils.InventoryUtils;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.packets.util.Lists;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.ShopSignManager;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.config.variables.ItemsProtocol;
import net.omniblock.shop.api.exception.SignLoadException;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;
import net.omniblock.shop.utils.ItemNameUtils;
import net.omniblock.survival.base.SurvivalBankBase;

public class UserShop extends AbstractShop {
	
	public static List<Player> waitlistPlayers = Lists.newArrayList();
	
	protected ItemStack shopItem = new ItemStack(Material.ITEM_FRAME, 1);
	
	protected Hologram hologram;
	protected ItemLine itemLine;
	protected Player cachePlayer;
	
	protected UserShopStatus status = UserShopStatus.WAITING_ITEM;
	
	protected boolean destroyed = false;
	protected boolean savedShop = false;
	
	public UserShop(Sign sign, Chest chest, ShopActionType actionType, int price, String playerNetworkID, String uniqueID) {
		
		super(sign, chest, ShopType.PLAYER_SHOP, actionType, price, playerNetworkID, uniqueID);
		
		this.sign = sign;
		return;
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ShopLoadStatus loadSign(Player player) {
		
		if(ConfigType.SHOPDATA.getConfig().isSet("usershop." + uniqueID)) {
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("usershop." + uniqueID + ".shopItem"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el shopItem en la configuración.");
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("usershop." + uniqueID + ".actionType"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el actionType en la configuración.");
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("usershop." + uniqueID + ".status"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el status en la configuración.");
			
			shopItem = ConfigType.SHOPDATA.getConfig().getItemStack("usershop." + uniqueID + ".shopItem");
			actionType = ShopActionType.valueOf(ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".actionType"));
			status = UserShopStatus.valueOf(ConfigType.SHOPDATA.getConfig().getString("usershop." + uniqueID + ".status"));
			hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), chest.getLocation().clone().add(.5, 1.8, .5));
			itemLine = hologram.appendItemLine(shopItem);
			savedShop = true;
			
			if(status == UserShopStatus.WAITING_ITEM) {
				
				this.destroySign();
				return ShopLoadStatus.CANNOT_LOAD;
				
			}
			
			sign.setLine(0, actionType.getFormattedAction());
			sign.setLine(1, Resolver.getLastNameByNetworkID(playerNetworkID));
			sign.setLine(2, TextUtil.format("&n" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased()));
			sign.setLine(3, TextUtil.format("&l$&r" + price));
			sign.update(true);
			return ShopLoadStatus.LOADED;
			
		}
		
		if(player == null)
			return ShopLoadStatus.CANNOT_LOAD;
		
		if(!player.isOnline())
			return ShopLoadStatus.CANNOT_LOAD;
		
		if(waitlistPlayers.contains(player)) {
			this.sign.getBlock().breakNaturally();
			player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes terminar de crear la tienda que estabas haciendo antes de hacer otra.")); 
			return ShopLoadStatus.CANNOT_LOAD;
		}
		
		if(ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts." + playerNetworkID))
			if(ConfigType.SHOPDATA.getConfig().getInt("usershops-amounts." + playerNetworkID) + 1 > ShopSignManager.getMaxShopsByRank(player)) {
				
				this.sign.getBlock().breakNaturally();
				
				if(ShopSignManager.getMaxShopsByRank(player) >= 500) {
					
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLo sentimos, has alcanzado el limite maximo de tiendas permitidas!")); 
					return ShopLoadStatus.CANNOT_LOAD;
					
				}
					
				player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has alcanzado tu &climite maximo&7 de tiendas, Con un rango &6&lVIP &7podrás incrementar este limite!")); 
				return ShopLoadStatus.CANNOT_LOAD;
				
			}
				
		
		this.cachePlayer = player;
		this.shopItem = player.getItemInHand();
		this.savedShop = true;
		this.status = UserShopStatus.WAITING_ITEM;
		
		this.waitItem(player);
		this.saveSign();
		return ShopLoadStatus.LOADED;
		
	}

	@Override
	public void destroySign() {
		
		destroyed = true;
		ShopSignManager.removeShop(this);
		
		if(hologram != null)
			hologram.delete();
		
		if(ConfigType.SHOPDATA.getConfig().isSet("usershop." + uniqueID))
			ConfigType.SHOPDATA.getConfig().set("usershop." + uniqueID, null);
		
		ConfigType.SHOPDATA.getConfigObject().save();
		return;
		
	}
	
	public void waitItem(Player player) {
		
		waitlistPlayers.add(player);
		
		sign.setLine(1, TextUtil.format("Haz click con el"));
		sign.setLine(2, TextUtil.format("tipo de item que"));
		sign.setLine(3, TextUtil.format(actionType == ShopActionType.BUY ? "venderás" : "comprarás") + ".");
		
		player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Ahora debes hacer click derecho con el item que usarás en la tienda.")); 
		
		new BukkitRunnable() {
			
			private int seconds = 60;
			
			@Override
			public void run() {
				
				seconds--;
				
				if(status == UserShopStatus.CREATED || destroyed == true) {
					
					this.cancel();
					return;
					
				}
				
				if(!(sign.getBlock().getState() instanceof Sign)) {
					
					this.cancel();
					destroySign();
					return;
					
				}
					
				
				if(seconds - 1 == 0 || !player.isOnline()) {
					
					this.cancel();
					
					if(player.isOnline())
						player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cTu tienda se ha eliminado porque no has colocado el item que usarías en ella."));
					
					waitlistPlayers.remove(player);
					
					destroySign();
					sign.getBlock().breakNaturally();
					return;
					
				}
				
				sign.setLine(0, TextUtil.format("&8&lESPERANDO &c" + seconds));
				sign.update(true);
				return;
				
			}
			
		}.runTaskTimer(ShopPlugin.getInstance(), 0l, 20l);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void clickEvent(PlayerInteractEvent e) {
		
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if(status == UserShopStatus.WAITING_ITEM) {
			
			if(waitlistPlayers.contains(e.getPlayer()) && e.getPlayer().equals(cachePlayer)) {
				
				Player player = e.getPlayer();
				
				if(player.getItemInHand() == null) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item que usarás en la tienda puesto en tu mano.")); 
					return;
				}
				
				if(ItemsProtocol.isMaterialBlocked(player.getItemInHand().getType())) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl item que intentas usar en la tienda está bloqueado.")); 
					return;
				}
				
				shopItem = e.getPlayer().getItemInHand().clone();
				shopItem.setAmount(1);
				
				status = UserShopStatus.CREATED;
				
				hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), chest.getLocation().clone().add(.5, 1.8, .5));
				itemLine = hologram.appendItemLine(shopItem);
				
				sign.setLine(0, actionType.getFormattedAction());
				sign.setLine(1, Resolver.getLastNameByNetworkID(playerNetworkID));
				sign.setLine(2, TextUtil.format("&n" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased()));
				sign.setLine(3, TextUtil.format("&l$&r" + price));
				sign.update(true);
				this.saveSign();
				
				waitlistPlayers.remove(e.getPlayer());
				
				if(!ConfigType.SHOPDATA.getConfig().isSet("usershops-amounts"))
					ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + playerNetworkID, 0);
				
				ConfigType.SHOPDATA.getConfig().set("usershops-amounts." + playerNetworkID, ConfigType.SHOPDATA.getConfig().getInt("usershops-amounts." + playerNetworkID) + 1);
				ConfigType.SHOPDATA.getConfigObject().save();
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas creado una tienda correctamente!"));
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 10);
				return;
				
			}
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLa tienda a la que intentas acceder se encuentra en construcción...")); 
			return;
			
		}
		
		String playerLastName = Resolver.getLastNameByNetworkID(playerNetworkID);
		boolean isSneaking = e.getPlayer().isSneaking();
		
		if(cachePlayer == null)
			if(Bukkit.getPlayer(playerLastName) != null)
				cachePlayer = Bukkit.getPlayer(playerLastName);
		
		if(e.getPlayer().equals(cachePlayer) || Resolver.getNetworkIDByName(e.getPlayer().getName()).equals(this.getPlayerNetworkID())) {
			
			cachePlayer = e.getPlayer();
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7No puedes &cutilizar&7 tus propias tiendas.")); 
			return;
			
		}
		
		if(actionType == ShopActionType.BUY) {
			
			int money = SurvivalBankBase.getMoney(e.getPlayer());
			int avaiableAmount = InventoryUtils.countMatches(chest.getInventory(), shopItem);
			
			if(isSneaking) {
				
				int purchasableAmount = avaiableAmount > shopItem.getMaxStackSize() ? shopItem.getMaxStackSize() : avaiableAmount;
				int purchasablePrice = purchasableAmount * price;
				
				if(avaiableAmount == 0 || !chest.getInventory().containsAtLeast(shopItem, purchasableAmount) || purchasablePrice <= 0) {
					
					e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7La tienda se ha quedado sin &8stock&7 de este item!"));
					return;
					
				}
				
				if(money < purchasablePrice) {
					
					e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Te hacen falta &c$" + (purchasablePrice - money) + " &7para poder comprar &f&lx" + purchasableAmount + " &7" + (purchasableAmount > 1 ? "items" : "item") + " en esta tienda!")); 
					return;
				
				}
				
				if(!InventoryUtils.hasSpaceForStack(e.getPlayer().getInventory(), new ItemBuilder(shopItem).amount(purchasableAmount).build())) {
					
					e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo tienes suficiente espacio para almacenar los items!")); 
					return;
					
				}
				
				InventoryUtils.removeQuantity(chest.getInventory(), shopItem, purchasableAmount);
				
				SurvivalBankBase.addMoney(playerNetworkID, purchasablePrice, true);
				SurvivalBankBase.removeMoney(e.getPlayer(), purchasablePrice);
				
				e.getPlayer().getInventory().addItem(new ItemBuilder(shopItem).amount(purchasableAmount).build());
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Le has comprado &f&lx" + purchasableAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &a$" + purchasablePrice + " &7a &8" + playerLastName + "&7!"));
				
				if(cachePlayer != null)
					if(cachePlayer.isOnline())
						if(!AccountBase.getTags(cachePlayer).contains("silentsurvivalshop"))
							cachePlayer.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &8" + e.getPlayer().getName() + " &7te ha comprado &f&lx" + purchasableAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &a$" + purchasablePrice + "&7!"));
							
				return;
				
			}
			
			int purchasablePrice = price;
			
			if(avaiableAmount == 0 || !chest.getInventory().containsAtLeast(shopItem, 1)) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7La tienda se ha quedado sin &8stock&7 de este item!"));
				return;
				
			}
			
			if(money < purchasablePrice) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Te hacen falta &c$" + (purchasablePrice - money) + " &7para poder comprar &f&lx1 &7item en esta tienda!")); 
				return;
			
			}
			
			if(!InventoryUtils.hasSpaceForStack(e.getPlayer().getInventory(), new ItemBuilder(shopItem).amount(1).build())) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo tienes suficiente espacio para almacenar los items!")); 
				return;
				
			}
			
			InventoryUtils.removeQuantity(chest.getInventory(), shopItem, 1);
			
			SurvivalBankBase.addMoney(playerNetworkID, purchasablePrice, true);
			SurvivalBankBase.removeMoney(e.getPlayer(), purchasablePrice);
			
			e.getPlayer().getInventory().addItem(new ItemBuilder(shopItem).amount(1).build());
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Le has comprado &f&lx1 &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &a$" + purchasablePrice + " &7a &8" + playerLastName + "&7!"));
			
			if(cachePlayer != null)
				if(cachePlayer.isOnline())
					if(!AccountBase.getTags(cachePlayer).contains("silentsurvivalshop"))
						cachePlayer.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &8" + e.getPlayer().getName() + " &7te ha comprado &f&lx1" + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &a$" + purchasablePrice + "&7!"));
						
			return;
			
		}
		
		int money = SurvivalBankBase.getMoney(playerNetworkID, true);
		int avaiableAmount = InventoryUtils.countMatches(e.getPlayer().getInventory(), shopItem);
		
		if(isSneaking) {
			
			int forSaleAmount = avaiableAmount > shopItem.getMaxStackSize() ? shopItem.getMaxStackSize() : avaiableAmount;
			int forSalePrice = forSaleAmount * price;
			
			if(avaiableAmount == 0 || !e.getPlayer().getInventory().containsAtLeast(shopItem, forSaleAmount) || forSaleAmount <= 0) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item de la tienda en tu inventario para poder venderlo!"));
				return;
				
			}
			
			if(money < forSalePrice) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7El dueño de la tienda no tiene suficiente dinero para comprarte &f&lx" + forSaleAmount + " &7de ese item!")); 
				return;
			
			}
			
			if(!InventoryUtils.hasSpaceForStack(chest.getInventory(), new ItemBuilder(shopItem).amount(forSaleAmount).build())) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLa tienda a la cual le intentas vender el item se encuentra llena!")); 
				return;
				
			}
			
			InventoryUtils.removeQuantity(e.getPlayer().getInventory(), shopItem, forSaleAmount);
			
			SurvivalBankBase.removeMoney(playerNetworkID, forSalePrice, true);
			SurvivalBankBase.addMoney(e.getPlayer(), forSalePrice);
			
			chest.getInventory().addItem(new ItemBuilder(shopItem).amount(forSaleAmount).build());
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Le has vendido &f&lx" + forSaleAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + forSalePrice + " &7a &8" + playerLastName + "&7!"));
			
			if(cachePlayer != null)
				if(cachePlayer.isOnline())
					if(!AccountBase.getTags(cachePlayer).contains("silentsurvivalshop"))
						cachePlayer.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &8" + e.getPlayer().getName() + " &7te ha vendido &f&lx" + forSaleAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + forSalePrice + "&7!"));
						
			return;
			
		}
		
		int forSalePrice = price;
		
		if(avaiableAmount == 0 || !e.getPlayer().getInventory().containsAtLeast(shopItem, 1)) {
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item de la tienda en tu inventario para poder venderlo!"));
			return;
			
		}
		
		if(money < forSalePrice) {
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7El dueño de la tienda no tiene suficiente dinero para comprarte &f&lx1 &7de ese item!")); 
			return;
		
		}
		
		if(!InventoryUtils.hasSpaceForStack(chest.getInventory(), new ItemBuilder(shopItem).amount(1).build())) {
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLa tienda a la cual le intentas vender el item se encuentra llena!")); 
			return;
			
		}
		
		if(!InventoryUtils.hasSpaceForStack(chest.getInventory(), new ItemBuilder(shopItem).amount(1).build())) {
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo tienes suficiente espacio para almacenar los items!")); 
			return;
			
		}
		
		InventoryUtils.removeQuantity(e.getPlayer().getInventory(), shopItem, 1);
		
		SurvivalBankBase.removeMoney(playerNetworkID, forSalePrice, true);
		SurvivalBankBase.addMoney(e.getPlayer(), forSalePrice);
		
		chest.getInventory().addItem(new ItemBuilder(shopItem).amount(1).build());
		e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		
		e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Le has vendido &f&lx1 &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + forSalePrice + " &7a &8" + playerLastName + "&7!"));
		
		if(cachePlayer != null)
			if(cachePlayer.isOnline())
				if(!AccountBase.getTags(cachePlayer).contains("silentsurvivalshop"))
					cachePlayer.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &8" + e.getPlayer().getName() + " &7te ha vendido &f&lx1 &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + forSalePrice + "&7!"));
					
		return;
		
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, Object> getConfigData() {
		return new HashMap<String, Object>(){{
			
			put("usershop." + uniqueID + ".location", sign.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ());
			put("usershop." + uniqueID + ".playerNetworkID", playerNetworkID);
			put("usershop." + uniqueID + ".price", price);
			put("usershop." + uniqueID + ".shopItem", shopItem);
			put("usershop." + uniqueID + ".savedShop", savedShop);
			put("usershop." + uniqueID + ".status", status.name());
			put("usershop." + uniqueID + ".actionType", actionType.name());
			
		}};
	}

	public Player getCachePlayer() {
		return cachePlayer;
	}
	
	public void setCachePlayer(Player player) {
		this.cachePlayer = player;
		return;
	}
	
	public Hologram getHologram() {
		return hologram;
	}
	
	public static enum UserShopStatus {
		
		WAITING_ITEM,
		CREATED,
		
		;
		
	}
	
}
