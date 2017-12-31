package net.omniblock.shop.api.object.npc.object;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.utils.TextUtil;

public abstract class InventoryShop {

	protected String npcName;
	protected String inventoryName;
	protected Player player;

	protected static final int emeraldSlot = 3;
	protected static final int optionSlot= 5;
	protected static final int paperSlot = 7;

	protected static final ItemStack buy = new ItemBuilder(Material.EMERALD).name(TextUtil.format("&2Comprar")).amount(1).build();
	protected static final ItemStack sell = new ItemBuilder(Material.PAPER).name(TextUtil.format("&cVender")).amount(1).build();

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
	public abstract void makeIventory();

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
	public abstract void moreOptions();
	
	/**
	 *	Hacer la compra de algún item
	 * 
	 * */
	protected void makeBuy(int price) {
		
		player.sendMessage(TextUtil.format("Gracias por su compra."));
		
	}
	
	/**
	 * Hacer la venta de algún item.
	 * 
	 * */
	protected void makeSell() {
		
	}
}
