package net.omniblock.shop.api.type;

import net.omniblock.network.library.utils.TextUtil;

public enum ShopActionType {

	ADMIN("&c&lADMIN&eSHOP"),
	BUY("&a&lCOMPRAR"),
	SELL("&9&lVENDER"),
	
	;
	
	private String formattedAction;
	
	ShopActionType(String formattedAction){
		this.formattedAction = formattedAction;
	}
	
	public String getFormattedAction() {
		return TextUtil.format(formattedAction);
	}
	
}
