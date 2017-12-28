package net.omniblock.shop.api.type;

import net.omniblock.shop.api.config.LineRegex;

public enum ShopActionType {

	BUY,
	SELL,
	
	;
	
	public static ShopActionType getByMiddleLine(String middle) {
		
		if(	   middle.equalsIgnoreCase(LineRegex.CREATE_BUY_SHOP_MIDDLE) 
			|| middle.equalsIgnoreCase(LineRegex.USE_BUY_SHOP_UP))
			return ShopActionType.BUY;
		
		return ShopActionType.SELL;
		
	}
	
}
