package net.omniblock.shop.api.object.npc.object;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.KindItem;

public class InventoryShop {

	private String npcName;
	private Player player;
	private String inventoryName;
	
	private KindItem kind;
	
	private static final String inventoryBuy = " &7- " + "&8¿Que deseas compra?";
	
	private static final String[] itemLore = 
			
			new String[] {
			TextUtil.format("&8- &7Sería una buena elección"),
			TextUtil.format("&7comprar un artículo como"),
			TextUtil.format("&7este, además que lo tengo"),
			TextUtil.format("&7a buen precio.")};

	/**
	 * Este objeto se utiliza para crear un inventario.
	 * 
	 * @param npcName
	 *            El nombre que tiene el NPC.
	 * @param player
	 *            El jugador que abrió el inventario.
	 * @param inventoryName
	 *            Nombre del inventario.
	 * 
	 */
	public InventoryShop(KindItem kind, String npcName, Player player, String inventoryName) {

		this.kind = kind;
		
		this.npcName = npcName;
		this.player = player;
		this.inventoryName = inventoryName;

	}

	/**
	 * Sistema de compra de ítems del NPC.
	 * 
	 */
	public void buyAndSell() {

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
					.lore("&2Se vende en: " + "&a" + item.getPriceSell())
					.lore("")
					.build(), CURRENT_SLOT,
					new Action() {
						@Override
						public void click(ClickType click, Player player) {
							
							if(click == click.LEFT) {
								makeBuy(item, item.getPriceBuy());
								return;
							}
							if(click == click.RIGHT) {
								player.sendMessage("VENDISTE");
								return;
							}
						}

					});
			
			CURRENT_SLOT++;
			
			}

		ib.open(player);
		return;
	}

	/**
	 * Colocar más ítem para comprar o alguna opción extra.
	 * 
	 */
	private void makeBuy(AdminShopItem item, int price) {
		
		Material material = item.getMaterial();
		
		if(material == null) return;
		if(material != item.getMaterial()) return;
		
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 2, 2);
			player.sendMessage(TextUtil.format("Gracias por su compra."));
			player.sendMessage(TextUtil.format("El item comprado le costo: " +  price));
			player.getInventory().addItem(new ItemBuilder(material).amount(1).data(item.getData()).build());
			return;
			
	}
	
	/**
	 * Hacer la venta de algún item.
	 * 
	 * */
	private void makeSell() {
		
	}
}
	