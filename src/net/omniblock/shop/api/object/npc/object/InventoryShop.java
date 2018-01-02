package net.omniblock.shop.api.object.npc.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.object.npc.object.InventoryPaginator.PaginatorStyle;
import net.omniblock.shop.api.object.npc.object.InventorySlotter.SlotLocatorType;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.KindItem;

public class InventoryShop {

	public static List<InventoryShop> createdShops = new ArrayList<InventoryShop>();
	
	private String npcName;
	
	private KindItem kind;
	
	private InventoryPaginator paginator;
	private InventorySlotter slotter;
	
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
	public InventoryShop(KindItem kind, String npcName) {

		this.kind = kind;
		this.npcName = npcName;
		
		for(InventoryShop shop : createdShops)
			if(shop.getKind() == this.kind && shop.getNPCName().equals(this.npcName))
				throw new UnsupportedOperationException("Ya se ha inicializado una tienda del tipo " + kind.name() + " previamente.");
		
		this.paginator = new InventoryPaginator(PaginatorStyle.COLOURED_ARROWS);
		this.slotter = new InventorySlotter(SlotLocatorType.ROUND_SIX);

		InventoryBuilder cacheBuilder = new InventoryBuilder(TextUtil.format(this.npcName + inventoryBuy), 6 * 9, false);
		
		for(AdminShopItem item : AdminShopItem.values()) {
			
			if(item.getKind() != this.kind)
				continue;
			
			if(!slotter.hasNext()) {
				
				paginator.addPage(cacheBuilder);
				slotter.reset();
				
				cacheBuilder = new InventoryBuilder(TextUtil.format(this.npcName + inventoryBuy), 6 * 9, false);
				
			}
			
			cacheBuilder.addItem(new ItemBuilder(item.getMaterial()).data(item.getData()).amount(1)
					.lore("")
					.lore(itemLore)
					.lore("")
					.lore("&aCompralo en " + "&e&l$&r&e" + item.getPriceBuy())
					.lore("&9Vendelo en " + "&e&l$&r&e" + item.getPriceSell())
					.lore("")
					.build(), slotter.next(),
					new Action() {
				
						@Override
						public void click(ClickType click, Player player) {
							
							if(click == ClickType.LEFT) {
								makeBuy(item, player, item.getPriceBuy());
								return;
							}
							if(click == ClickType.RIGHT) {
								player.sendMessage("VENDISTE");
								return;
							}
						}

					});
			
			continue;
			
		}
		
		if(!paginator.contains(cacheBuilder))
			paginator.addPage(cacheBuilder);
		
		return;
		
	}

	/**
	 * Sistema de compra de ítems del NPC.
	 * 
	 */
	public void openShop(Player player) {
		
		paginator.openInventory(player);
		return;
		
	}

	/**
	 * Colocar más ítem para comprar o alguna opción extra.
	 * 
	 */
	private void makeBuy(AdminShopItem item, Player player, int price) {
		
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
	@SuppressWarnings("unused")
	private void makeSell() {
		
	}

	public String getNPCName() {
		return npcName;
	}
	
	public KindItem getKind() {
		return kind;
	}
	
	/**
	 * 
	 * Este metodo buscará una tienda registrada
	 * con las caracteristicas dadas, en caso
	 * de que no se logre encontrar dicha tienda
	 * simplemente se creará una nueva y posteriormente
	 * se registrará para futuras busquedas.
	 * 
	 * @param kind Tipo de tienda.
	 * @param npcname El nombre del npc que atienda
	 * dicha tienda.
	 * @return El objeto de la tienda habilitado para
	 * utilizar sus metodos.
	 */
	public static InventoryShop lookupShop(KindItem kind, String npcname) {
		
		for(InventoryShop shop : createdShops)
			if(shop.getKind() == kind && shop.getNPCName().equals(npcname))
				return shop;
		
		return new InventoryShop(kind, npcname);
		
	}
	
}
	