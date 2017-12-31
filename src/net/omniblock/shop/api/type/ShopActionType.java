package net.omniblock.shop.api.type;

import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.config.variables.LineRegex;

public enum ShopActionType {

	BUY("&a&lCOMPRAR &7(Click)"),
	SELL("&6&lVENDER &7(Click)"),
	
	;
	
	private String formattedAction;
	
	ShopActionType(String formattedAction){
		this.formattedAction = formattedAction;
	}
	
	public String getFormattedAction() {
		return TextUtil.format(formattedAction);
	}
	
	public static ShopActionType getByMiddleLine(String middle) {
		
		if(middle.equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE))
			return ShopActionType.BUY;
		
		return ShopActionType.SELL;
		
	}
	
}
