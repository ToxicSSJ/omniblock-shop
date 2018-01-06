package net.omniblock.shop.api.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.utils.InventoryUtils;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.packets.util.Lists;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.ShopSignManager;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.config.variables.ItemsProtocol;
import net.omniblock.shop.api.exception.SignLoadException;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;
import net.omniblock.shop.utils.ItemNameUtils;
import net.omniblock.survival.base.SurvivalBankBase;

public class AdminShop extends AbstractShop {

	protected ItemStack shopItem;
	
	protected Hologram hologram;
	protected ItemLine itemLine;
	protected Player cachePlayer;
	
	protected boolean destroyed = false;
	protected boolean savedShop = false;
	
	protected AdminShopStatus status = AdminShopStatus.WAITING_ITEM;
	
	public static List<Player> waitlistPlayers = Lists.newArrayList();
	
	public AdminShop(Sign sign, Chest chest, ShopActionType actionType, String uniqueID) {
		
		super(sign, chest, ShopType.ADMIN_SHOP, actionType, 0, "ADMIN", uniqueID);
		
		this.sign = sign;
		return;
		
	}

	public void saveSign() {
		
		for(Map.Entry<String, Object> entry : getConfigData().entrySet())
			ConfigType.SHOPDATA.getConfig().set(entry.getKey(), entry.getValue());
		
		ConfigType.SHOPDATA.getConfigObject().save();
		return;
		
	}

	@Override
	public void destroySign() {
	
		destroyed = true;
		ShopSignManager.removeShop(this);
		
		if(hologram != null)
			hologram.delete();
		
		if(ConfigType.SHOPDATA.getConfig().isSet("adminshop." + uniqueID))
			ConfigType.SHOPDATA.getConfig().set("adminshop." + uniqueID, null);
		
		ConfigType.SHOPDATA.getConfigObject().save();
		return;
		
	}
	
	public void waitItem(Player player){
		waitlistPlayers.add(player);
		
		sign.setLine(1, TextUtil.format("Haz click con el"));
		sign.setLine(2, TextUtil.format("tipo de item que"));
		sign.setLine(3, TextUtil.format("negociaras."));
		
		player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Ahora debes hacer click derecho con el item que usarás en la tienda."));
		
		new BukkitRunnable() {
			
			private int seconds = 60;
			
			@Override
			public void run() {
				
				seconds--;
				
				
				if(status == AdminShopStatus.CREATED || destroyed == true) {
					waitlistPlayers.remove(player);
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
		
		if(status == AdminShopStatus.WAITING_ITEM){
			
			if(waitlistPlayers.contains(e.getPlayer()) && e.getPlayer().equals(cachePlayer)){
				
				Player player = e.getPlayer();
				
				if(player.getItemInHand().getType().equals(Material.AIR)) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item que usarás en la tienda puesto en tu mano.")); 
					return;
				}
				
				if(ItemsProtocol.isMaterialBlocked(player.getItemInHand().getType())) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl item que intentas usar en la tienda está bloqueado.")); 
					return;
				}
				
				shopItem = e.getPlayer().getItemInHand().clone();
				shopItem.setAmount(1);
				
				status = AdminShopStatus.CREATED;
				
				hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), chest.getLocation().clone().add(.5, 1.8, .5));
				itemLine = hologram.appendItemLine(shopItem);
				
				int buyPrice = AdminShopItem.getBuyPriceByMaterial(shopItem.getType());
				String buy = "&9C: &0&l$&0" + buyPrice;
				
				int sellPrice = AdminShopItem.getSellPriceByMaterial(shopItem.getType());
				String sell = "&aV: &0&l$&0" + sellPrice;
				
				sign.setLine(0, actionType.getFormattedAction());
				sign.setLine(1, TextUtil.format("&n" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased()));
				sign.setLine(2, TextUtil.format(buy));
				sign.setLine(3, TextUtil.format(sell));
				sign.update(true);
				this.saveSign();
				
				waitlistPlayers.remove(e.getPlayer());
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas creado una tienda correctamente!"));
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 10);
				return;
				
			}
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cLa tienda a la que intentas acceder se encuentra en construcción...")); 
			return;
			
		}
			
		boolean isSneaking = e.getPlayer().isSneaking();
		int money = SurvivalBankBase.getMoney(e.getPlayer());
		
		if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			
			int buyPrice = AdminShopItem.getBuyPriceByMaterial(shopItem.getType());
			int maxStackSpace = InventoryUtils.getMaxStackSpaceQuantity(e.getPlayer().getInventory(), shopItem);
			
			if(isSneaking) {
				
				buyPrice *= maxStackSpace;
				
				if(money < buyPrice) {
					
					e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Te hacen falta &c$" + (buyPrice - money) + " &7para poder comprar &f&lx" + maxStackSpace + " &7" + (maxStackSpace > 1 ? "items" : "item") + " en esta tienda!")); 
					return;
				
				}
				
				if(!InventoryUtils.hasSpaceForStack(e.getPlayer().getInventory(), new ItemBuilder(shopItem).amount(maxStackSpace).build())) {
					
					e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo tienes suficiente espacio para almacenar los items!")); 
					return;
					
				}
				
				SurvivalBankBase.removeMoney(e.getPlayer(), buyPrice);
				
				e.getPlayer().getInventory().addItem(new ItemBuilder(shopItem).amount(maxStackSpace).build());
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has comprado &f&lx" + maxStackSpace + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()) + " &7por &a$" + buyPrice + "."));
				return;
				
			}
			
			if(money < buyPrice) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Te hacen falta &c$" + (buyPrice - money) + " &7para poder comprar &f&lx" + shopItem.getMaxStackSize() + " &7" + (shopItem.getMaxStackSize() > 1 ? "items" : "item") + " en esta tienda!")); 
				return;
			
			}
			
			if(!InventoryUtils.hasSpaceForStack(e.getPlayer().getInventory(), new ItemBuilder(shopItem).amount(1).build())) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cNo tienes suficiente espacio para almacenar los items!")); 
				return;
				
			}
			
			SurvivalBankBase.removeMoney(e.getPlayer(), buyPrice);
			
			e.getPlayer().getInventory().addItem(new ItemBuilder(shopItem).amount(1).build());
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has comprado &f&lx1" + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()) + " &7por &a$" + buyPrice + "."));
			return;
			
		}
		
		isSneaking = e.getPlayer().isSneaking();
		
		int sellPrice = AdminShopItem.getSellPriceByMaterial(shopItem.getType());
		
		int avaiableAmount = InventoryUtils.countMatches(e.getPlayer().getInventory(), shopItem);
		int sellAmount = 1;
		
		if(isSneaking) {
			
			sellAmount = avaiableAmount > shopItem.getMaxStackSize() ? shopItem.getMaxStackSize() : avaiableAmount;
			sellPrice *= sellAmount;
			
			if(avaiableAmount == 0) {
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item de la tienda en tu inventario para poder venderlo!"));
				return;
				
			}
			
			InventoryUtils.removeQuantity(e.getPlayer().getInventory(), shopItem, sellAmount);
			SurvivalBankBase.addMoney(e.getPlayer(), sellPrice);
			
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has vendido &f&lx" + sellAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + sellPrice + "!"));
			return;
			
		}
		
		sellPrice *= sellAmount;
		
		if(avaiableAmount == 0) {
			
			e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item de la tienda en tu inventario para poder venderlo!"));
			return;
			
		}
		
		InventoryUtils.removeQuantity(e.getPlayer().getInventory(), shopItem, sellAmount);
		SurvivalBankBase.addMoney(e.getPlayer(), sellPrice);
		
		e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		
		e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Has vendido &f&lx" + sellAmount + " &7de &8" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased() + " &7al precio de &9$" + sellPrice + "!"));
		return;
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public ShopLoadStatus loadSign(Player player) {
		
		if(ConfigType.SHOPDATA.getConfig().isSet("adminshop." + uniqueID)) {
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("adminshop." + uniqueID + ".shopItem"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el shopItem en la configuración.");
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("adminshop." + uniqueID + ".actionType"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el actionType en la configuración.");
			
			if(!ConfigType.SHOPDATA.getConfig().isSet("adminshop." + uniqueID + ".status"))
				throw new SignLoadException("No se ha podido cargar el cartel '" + uniqueID + "' porque hace falta el status en la configuración.");
			
			shopItem = ConfigType.SHOPDATA.getConfig().getItemStack("adminshop." + uniqueID + ".shopItem");
			actionType = ShopActionType.valueOf(ConfigType.SHOPDATA.getConfig().getString("adminshop." + uniqueID + ".actionType"));
			status = AdminShopStatus.valueOf(ConfigType.SHOPDATA.getConfig().getString("adminshop." + uniqueID + ".status"));
			hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), chest.getLocation().clone().add(.5, 1.8, .5));
			itemLine = hologram.appendItemLine(shopItem);
			savedShop = true;
			
			if(status == AdminShopStatus.WAITING_ITEM) {
				
				this.destroySign();
				return ShopLoadStatus.CANNOT_LOAD;
				
			}
			
			int buyPrice = AdminShopItem.getBuyPriceByMaterial(shopItem.getType());
			String buy = "&9C: &0&l$&0" + buyPrice;
			
			int sellPrice = AdminShopItem.getSellPriceByMaterial(shopItem.getType());
			String sell = "&aV: &0&l$&0" + sellPrice;
			
			sign.setLine(0, actionType.getFormattedAction());
			sign.setLine(1, TextUtil.format("&n" + ItemNameUtils.getMaterialName(shopItem.getType()).firstAllUpperCased()));
			sign.setLine(2, TextUtil.format(buy));
			sign.setLine(3, TextUtil.format(sell));
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
		
		this.cachePlayer = player;
		this.shopItem = player.getItemInHand();
		this.savedShop = true;
		this.status = AdminShopStatus.WAITING_ITEM;
		
		this.waitItem(player);
		this.saveSign();
		return ShopLoadStatus.LOADED;
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, Object> getConfigData() {
		return new HashMap<String, Object>(){{
			
			put("adminshop." + uniqueID + ".location", sign.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ());
			put("adminshop." + uniqueID + ".shopItem", shopItem);
			put("adminshop." + uniqueID + ".savedShop", savedShop);
			put("adminshop." + uniqueID + ".status", status.name());
			put("adminshop." + uniqueID + ".actionType", actionType.name());
			
		}};
	}
	
	public static enum AdminShopStatus {
		
		WAITING_ITEM,
		CREATED,
		
		;
		
	}
}
