package net.omniblock.shop.api.object;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;

/**
 * 
 * Esta clase abstracta es la base
 * para la creación de los distintos
 * tipos de tiendas tipo carteles.
 * 
 * @author zlToxicNetherlz
 *
 */
public abstract class AbstractShop {

	protected Sign sign;
	protected Chest chest;
	
	protected ShopType type;
	protected ShopActionType actionType = ShopActionType.BUY;
	
	protected int price = 1;
	
	protected String uniqueID;
	protected String playerNetworkID;
	
	public AbstractShop(Sign sign, Chest chest, ShopType type, int price, String playerNetworkID, String uniqueID) {
		
		this.sign = sign;
		this.chest = chest;
		this.type = type;
		this.price = price;
		
		this.uniqueID = uniqueID;
		this.playerNetworkID = playerNetworkID;
		return;
		
	}
	
	public void saveSign() {
		
		for(Map.Entry<String, Object> entry : getConfigData().entrySet())
			ConfigType.SHOPDATA.getConfig().set(entry.getKey(), entry.getValue());
		
		ConfigType.SHOPDATA.getConfigObject().save();
		return;
		
	}
	
	public abstract ShopLoadStatus loadSign(Player player);
	
	public abstract void destroySign();
	
	public abstract Map<String, Object> getConfigData();
	
	public abstract void clickEvent(PlayerInteractEvent e);
	
	public Sign getSign() {
		return sign;
	}
	
	public Block getBlock() {
		return sign.getBlock();
	}
	
	public ShopType getShopType() {
		return type;
	}
	
	public void setShopActionType(ShopActionType actionType) {
		this.actionType = actionType;
	}
	
	public ShopActionType getShopActionType() {
		return actionType;
	}
	
	public static enum ShopLoadStatus {
		
		CANNOT_LOAD,
		
		ERROR,
		LOADED,
		
		;
		
	}
	
}
