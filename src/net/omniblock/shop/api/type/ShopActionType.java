package net.omniblock.shop.api.type;

import net.omniblock.network.library.utils.TextUtil;

public enum ShopActionType {

	BUY("&a&lCOMPRAR &8(Click)"),
	SELL("&9&lVENDER &8(Click)"),
	
	;
	
	private String formattedAction;
	
	ShopActionType(String formattedAction){
		this.formattedAction = formattedAction;
	}
	
	public String getFormattedAction() {
		return TextUtil.format(formattedAction);
	}
	
}
