package net.omniblock.shop.api.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

import net.omniblock.network.handlers.base.sql.util.Resolver;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.packets.util.Lists;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.config.variables.ItemsProtocol;
import net.omniblock.shop.api.config.variables.LineRegex;
import net.omniblock.shop.api.exception.SignRegexException;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;
import net.omniblock.shop.utils.ItemNameUtils;

public class AdminShop extends AbstractShop {

	protected ItemStack shopItem;
	
	protected Hologram hologram;
	protected ItemLine itemLine;
	
	protected boolean destroyed = false;
	protected boolean savedShop = false;
	
	protected AdminShopStatus status = AdminShopStatus.WAITING_ITEM;
	
	public static List<Player> waitlistPlayers = Lists.newArrayList();
	
	public AdminShop(Sign sign, Chest chest, String uniqueID) {
		
		super(sign, chest, ShopType.ADMIN_SHOP, 0, "ADMIN", uniqueID);
		
		this.sign = sign;
		return;
		
	}

	@Override
	public void saveSign() {
		
		if(!this.sign.getLine(0).equalsIgnoreCase(LineRegex.CREATE_ADMIN_SHOP_UP))
			throw new SignRegexException(
					"El cartel ubicado en " +
					sign.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ() + " no " +
					"tiene el prefix de la tienda tipo administrador!");
		
		this.setShopActionType(ShopActionType.getByMiddleLine(this.sign.getLine(1)));
		this.savedShop = true;
		
		return;
		
	}

	@Override
	public void destroySign() {
		
	}
	
	public void waitItem(Player player){
		waitlistPlayers.add(player);
		
		sign.setLine(1, TextUtil.format("Has click con el"));
		sign.setLine(2, TextUtil.format("tipo de item que"));
		sign.setLine(3, TextUtil.format(actionType == ShopActionType.BUY ? "venderás" : "comprarás") + ".");
		
		player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &7Ahora debes hacer click derecho con el item que usarás en la tienda."));
		
		new BukkitRunnable() {
			
			private int seconds = 60;
			
			@Override
			public void run() {
				
				seconds--;
				
				if(status == AdminShopStatus.CREATED || destroyed == true) {
					
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
			
			if(waitlistPlayers.contains(e.getPlayer())){
				
				Player player = e.getPlayer();
				
				if(player.getItemInHand() == null) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cDebes tener el item que usarás en la tienda puesto en tu mano.")); 
					return;
				}
				
				if(ItemsProtocol.isMaterialBlocked(player.getItemInHand().getType())) {
					player.sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &cEl item que intentas usar en la tienda está bloqueado.")); 
					return;
				}
				
				shopItem = e.getPlayer().getItemInHand();
				status = AdminShopStatus.CREATED;
				
				hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), chest.getLocation().clone().add(.5, 1.8, .5));
				itemLine = hologram.appendItemLine(shopItem);
				
				String third ="";
				if(actionType == ShopActionType.BUY){
					price = AdminShopItem.getBuyPriceByMaterial(shopItem.getType());
					third = "&a&l$&a" + price;
				}else{
					AdminShopItem.getSellPriceByMaterial(shopItem.getType());
					third = "&6&l$&6" + price;
				}
				
				sign.setLine(0, actionType.getFormattedAction());
				sign.setLine(1, Resolver.getLastNameByNetworkID(playerNetworkID));
				sign.setLine(2, TextUtil.format("&8" + ItemNameUtils.getMaterialName(shopItem.getType())));
				sign.setLine(3, TextUtil.format(third));
				sign.update(true);
				this.saveSign();
				
				waitlistPlayers.remove(e.getPlayer());
				
				e.getPlayer().sendMessage(TextUtil.format("&8&lT&8iendas &b&l» &aHas creado una tienda correctamente!"));
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 10);
				return;
				
			}
			
			
		}else{
			
			
			
		}
		
		e.getPlayer().sendMessage("Esto es una complicacion :v");
	}

	@Override
	public ShopLoadStatus loadSign(Player player) {
		return ShopLoadStatus.CANNOT_LOAD;
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, Object> getConfigData() {
		return new HashMap<String, Object>(){{
			
			put("usershop." + uniqueID + ".location", sign.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ());
			put("usershop." + uniqueID + ".shopItem", shopItem);
			put("usershop." + uniqueID + ".savedShop", savedShop);
			put("usershop." + uniqueID + ".actionType", actionType);
			
		}};
	}
	
	public static enum AdminShopStatus {
		
		WAITING_ITEM,
		CREATED,
		
		;
		
	}
}
