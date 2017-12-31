package net.omniblock.shop.api.object.npc.object;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.KindItem;
import net.omniblock.shop.api.type.NPCShopType;

public class FoodShop extends InventoryShop {

	protected final KindItem kind = KindItem.FOODSTUFFS;

	public FoodShop(Player player, String inventoryName) {
		super(NPCShopType.SHOP_FOOD.getName(), player, inventoryName);
	}

	@Override
	public void buy() {

		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + inventoryBuy), 6 * 9, true);

		int CURRENT_SLOT = 0;
		int MAX_SLOT = (6 * 9) - 1;

		for (AdminShopItem item : AdminShopItem.values()) {

			if (item.getKind() != kind)
				continue;
			if (CURRENT_SLOT == MAX_SLOT)
				break;

			ib.addItem(new ItemBuilder(item.getMaterial()).data(item.getData()).amount(1)
					.lore("")
					.lore(itemLore)
					.lore("")
					.lore("&6Precio del artículo: " + "&e" + item.getPriceBuy())
					.lore("")
					.build(), CURRENT_SLOT,
					new Action() {
						@Override
						public void click(ClickType click, Player player) {
							makeBuy(item, item.getPriceBuy());
							return;
						}

					});
			
			CURRENT_SLOT++;
			
			}

		ib.open(player);
		return;

		
	}

	@Override
	public void sell() {

		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + inventorySell), 6 * 9,
				true);
		ib.open(player);

	}
}
