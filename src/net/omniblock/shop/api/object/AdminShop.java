package net.omniblock.shop.api.object;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.omniblock.shop.api.config.variables.LineRegex;
import net.omniblock.shop.api.exception.SignRegexException;
import net.omniblock.shop.api.type.ShopActionType;
import net.omniblock.shop.api.type.ShopType;

public class AdminShop extends AbstractShop {

	protected ItemStack shopItem;
	
	protected boolean savedShop = false;
	
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
	
	@Override
	public void clickEvent(PlayerInteractEvent e) {
		
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
	
}
