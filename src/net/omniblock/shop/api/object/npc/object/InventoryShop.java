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

	public static final int emeraldSlot = 4;
	public static final int paperSlot = 6;

	public static final ItemStack buy = new ItemBuilder(Material.EMERALD).name(TextUtil.format("&2Comprar")).amount(1).build();
	public static final ItemStack sell = new ItemBuilder(Material.PAPER).name(TextUtil.format("&cVender")).amount(1).build();

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

	public abstract void buy();

	/**
	 * Sistema de venta de items del NPC
	 * 
	 */

	public abstract void sell();

	/**
	 * Colocar más ítem para comprar o alguna opción extra.
	 * 
	 */

	public abstract void moreOptions();
}
