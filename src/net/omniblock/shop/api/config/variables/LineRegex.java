package net.omniblock.shop.api.config.variables;

import net.omniblock.network.library.utils.TextUtil;

public class LineRegex {

	public static final int USER_MAX_SHOPS = 80;
	
	public static final String CREATE_ADMIN_SHOP_UP = "[admin]";
	public static final String CREATE_USER_SHOP_UP = "[tienda]";
	
	public static final String CREATE_BUY_SHOP_MIDDLE = "[comprar]";
	public static final String CREATE_SELL_SHOP_MIDDLE = "[vender]";
	
	public static final String USE_BUY_SHOP_UP = TextUtil.format("&a&lCOMPRAR");
	public static final String USE_SELL_SHOP_UP = TextUtil.format("&c&lVENDER");
	
}
