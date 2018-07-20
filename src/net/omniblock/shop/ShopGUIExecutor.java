package net.omniblock.shop;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.object.npc.object.InventoryShop;
import net.omniblock.shop.api.type.KindItem;
import net.omniblock.shop.api.type.NPCShopType;
import net.omniblock.survival.systems.commands.gui.GuiExecutor;
import net.omniblock.survival.systems.commands.gui.ItemGUI;
import org.bukkit.Material;

public class ShopGUIExecutor implements GuiExecutor {

	@Override
	public ItemGUI[] onCreate() {
		return new ItemGUI[]{
				new ItemGUI(new ItemBuilder(Material.BRICK)
						.name(TextUtil.format("&2Tienda de bloques"))
						.lore("")
						.lore(TextUtil.format("&8&m-&r &7Utiliza este comando"))
						.lore(TextUtil.format("&7Para ver la tienda de bloques."))
						.lore(TextUtil.format(""))
						.lore(TextUtil.format("&7úsalo así: "))
						.lore(TextUtil.format(" &7&e- &a/tienda bloque"))
						.lore(TextUtil.format(" &7&e- &a/tienda bloques"))
						.lore(TextUtil.format(" &7&e- &a/tienda block"))
						.lore(TextUtil.format(" &7&e- &a/tienda blocks"))
						.build(), (Click, player) -> {

					InventoryShop shop = InventoryShop.lookupShop(KindItem.BUILDING_BLOCKS, NPCShopType.SHOP_BLOCKS.getName(), "&8¿Quiere algo en concreto?");
					shop.openShop(player);
					return;
				}),
				new ItemGUI(new ItemBuilder(Material.COOKIE)
						.name(TextUtil.format("&2Tienda de comida"))
						.lore("")
						.lore(TextUtil.format("&8&m-&r &7Utiliza este comando"))
						.lore(TextUtil.format("&7Para ver la tienda de alimentos."))
						.lore(TextUtil.format(""))
						.lore(TextUtil.format("&7úsalo así: "))
						.lore(TextUtil.format(" &7&e- &a/tienda comida"))
						.lore(TextUtil.format(" &7&e- &a/tienda comidas"))
						.lore(TextUtil.format(" &7&e- &a/tienda food"))
						.lore(TextUtil.format(" &7&e- &a/tienda foods"))
						.build(), (Click, player) -> {

					InventoryShop shop = InventoryShop.lookupShop(KindItem.FOODSTUFFS, NPCShopType.SHOP_FOOD.getName(), "&8¿Quiere algo en concreto?");
					shop.openShop(player);
					return;

				}),
				new ItemGUI(new ItemBuilder(Material.DIAMOND_PICKAXE)
						.name(TextUtil.format("&2Tienda de armas"))
						.lore("")
						.lore(TextUtil.format("&8&m-&r &7Utiliza este comando"))
						.lore(TextUtil.format("&7para ver las tiendas de armas"))
						.lore(TextUtil.format(""))
						.lore(TextUtil.format("&7úsalo así: "))
						.lore(TextUtil.format(" &7&e- &a/tienda arma"))
						.lore(TextUtil.format(" &7&e- &a/tienda armas"))
						.lore(TextUtil.format(" &7&e- &a/tienda tool"))
						.lore(TextUtil.format(" &7&e- &a/tienda tools"))
						.build(), (Click, player) -> {

					InventoryShop shop = InventoryShop.lookupShop(KindItem.TOOLS, NPCShopType.SHOP_MATERIALS.getName(), "&8¿Quiere algo en concreto?");
					shop.openShop(player);
					return;

				}),
		};

	}
}
