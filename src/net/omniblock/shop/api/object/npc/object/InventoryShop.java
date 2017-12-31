package net.omniblock.shop.api.object.npc.object;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.AdminShopItem;

public abstract class InventoryShop {

	protected String npcName;
	protected String inventoryName;
	protected Player player;
	
	protected static final String inventoryBuy = " &7- " + "&8¿Que deseas compra?";
	protected static final String inventorySell = "&7-" + "&8¿Quieres vender algo?";
	
	protected static final String[] itemLore = 
			
			new String[] {
			TextUtil.format("&8- &7Sería una buena elección"),
			TextUtil.format("&7comprar un artículo como"),
			TextUtil.format("&7este, además que lo tengo"),
			TextUtil.format("&7a buen precio.")};
	
	protected static final int emeraldSlot = 2;
	protected static final int paperSlot = 6;
	protected static final int arrowSlot = 53;

	protected static final ItemStack buy = new ItemBuilder(Material.EMERALD).name(TextUtil.format("&2Comprar")).amount(1).build();
	protected static final ItemStack sell = new ItemBuilder(Material.PAPER).name(TextUtil.format("&cVender")).amount(1).build();
	protected static final ItemStack nextPage = new ItemBuilder(Material.ARROW).name(TextUtil.format("&2&lVer más…")).amount(1).build();

	
	
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
	public InventoryShop(String npcName, Player player, String inventoryName) {

		this.npcName = npcName;
		this.player = player;
		this.inventoryName = inventoryName;

	}
	
	/**
	 * Crear un inventario a un NPC.
	 * 
	 */
	public void makeIventory() {
		
		InventoryBuilder ib = new InventoryBuilder(TextUtil.format(npcName + " &8- " + "&7" + inventoryName), 1 * 9, true);
		ib.addItem(buy, emeraldSlot, new Action() {
			@Override
			public void click(ClickType click, Player player) {
				buy();
				return;
			}
		});
		ib.addItem(sell, paperSlot, new Action() {
			@Override
			public void click(ClickType click, Player player) {
				sell();
				return;
			}
		});
		ib.open(player);
	}

	/**
	 * Sistema de compra de ítems del NPC.
	 * 
	 */
	protected abstract void buy();

	/**
	 * Sistema de venta de items del NPC
	 * 
	 */
	protected abstract void sell();

	/**
	 * Colocar más ítem para comprar o alguna opción extra.
	 * 
	 */
	protected void makeBuy(AdminShopItem item, int price) {
		
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
	protected void makeSell() {
		
	}
}
	