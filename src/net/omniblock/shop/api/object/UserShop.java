package net.omniblock.shop.api.object;

import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

import net.omniblock.shop.api.config.LineRegex;
import net.omniblock.shop.api.exception.SignRegexException;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;

public class UserShop extends AbstractShop {

	protected Sign sign;
	
	protected String materialID = "0";
	protected String materialSubID = "0";
	
	protected boolean savedShop = false;
	
	public UserShop(Sign sign, String playerNetworkID, String uniqueID) {
		
		super(sign, ShopType.PLAYER_SHOP, playerNetworkID, uniqueID);
		
		this.sign = sign;
		return;
		
	}

	@Override
	public void saveSign() {
		
		if(!this.sign.getLine(0).equalsIgnoreCase(LineRegex.CREATE_USER_SHOP_UP))
			throw new SignRegexException(
					"El cartel ubicado en " +
					sign.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ() + " no " +
					"tiene el prefix de la tienda tipo usuario!");
		
		this.setShopActionType(ShopActionType.getByMiddleLine(this.sign.getLine(1)));
		this.savedShop = true;
		
		return;
		
	}

	@Override
	public void clickEvent(PlayerInteractEvent e) {
		
	}

}
