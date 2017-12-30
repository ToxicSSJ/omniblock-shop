package net.omniblock.shop.api.object.npc.object;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.NPCShopType;

public class FoodShop extends InventoryShop {

	public FoodShop(Player player, String inventoryName) {
		super(NPCShopType.SHOP_FOOD.getName(), player, inventoryName);
	}

	@Override
	public void makeIventory() {

		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + " - " + inventoryName), 1 * 9, true);

		ib.addItem(buy, emeraldSlot, new Action() {

			@Override
			public void click(ClickType click, Player player) {

				buy();
				return;

			}});

		ib.addItem(sell, paperSlot, new Action() {

			@Override
			public void click(ClickType click, Player player) {
				
				sell();
				return;
				
			}});

		ib.open(player);
	}

	@Override
	public void buy() {
		
		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + "-" + "¡Bienvenido! ¿que desea compra?"), 6 * 9, true);
		ib.open(player);
	}

	@Override
	public void sell() {

		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + "-" + "¿Quieres vender algo?"), 6 * 9, true);
		ib.open(player);
		
	}

	@Override
	public void moreOptions() {

	}

}
