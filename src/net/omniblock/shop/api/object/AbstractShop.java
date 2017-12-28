package net.omniblock.shop.api.object;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;

public abstract class AbstractShop {

	protected Sign sign;
	
	protected ShopType type;
	protected ShopActionType actiontype = ShopActionType.BUY;
	
	protected String uniqueID;
	protected String playerNetworkID;
	
	public AbstractShop(Sign sign, ShopType type, String playerNetworkID, String uniqueID) {
		
		this.sign = sign;
		this.type = type;
		
		this.uniqueID = uniqueID;
		this.playerNetworkID = playerNetworkID;
		
	}
	
	public abstract void saveSign();
	
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
	
	public void setShopActionType(ShopActionType actiontype) {
		this.actiontype = actiontype;
	}
	
	public ShopActionType getShopActionType() {
		return actiontype;
	}
	
}
