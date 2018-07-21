package net.omniblock.shop.api.object.npc.object;

import java.util.ArrayList;
import java.util.List;

import net.omniblock.network.library.helpers.inventory.paginator.PaginatorStyle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;
import net.omniblock.network.library.helpers.inventory.paginator.InventoryPaginator;
import net.omniblock.network.library.helpers.inventory.paginator.InventorySlotter;
import net.omniblock.network.library.helpers.inventory.paginator.InventorySlotter.SlotLocatorType;
import net.omniblock.network.library.utils.InventoryUtils;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.AdminShopItem;
import net.omniblock.shop.api.type.KindItem;
import net.omniblock.shop.utils.ItemNameUtils;
import net.omniblock.survival.base.SurvivalBankBase;

public class InventoryShop {

	public static List<InventoryShop> createdShops = new ArrayList<InventoryShop>();
	
	private String npcName;
	
	private KindItem kind;
	
	private InventoryPaginator paginator;
	private InventorySlotter slotter;
	
	private String inventoryName;
	
	private static final String[] itemLore = 
			
			new String[] {
			TextUtil.format("&8- &7Es una gran elecci�n,"),
			TextUtil.format("&7adem�s; lo tengo"),
			TextUtil.format("&7al mejor precio.")};

	/**
	 * Este objeto se utiliza para crear un inventario.
	 * 
	 * @param npcName
	 *            El nombre que tiene el NPC.
	 * @param player
	 *            El jugador que abri� el inventario.
	 * @param inventoryName
	 *            Nombre del inventario.
	 * 
	 */
	public InventoryShop(KindItem kind, String npcName, String inventoryName) {

		this.kind = kind;
		this.npcName = npcName;
		this.inventoryName = inventoryName;
		
		for(InventoryShop shop : createdShops)
			if(shop.getKind() == this.kind && shop.getNPCName().equals(this.npcName))
				throw new UnsupportedOperationException("Ya se ha inicializado una tienda del tipo " + kind.name() + " previamente.");
		
		this.paginator = new InventoryPaginator(PaginatorStyle.COLOURED_ARROWS);
		this.slotter = new InventorySlotter(SlotLocatorType.ROUND_SIX);

		InventoryBuilder cacheBuilder = new InventoryBuilder(TextUtil.format(this.inventoryName), 6 * 9, false);
		
		for(AdminShopItem item : AdminShopItem.values()) {
			
			if(item.getKind() != this.kind)
				continue;
			
			if(!slotter.hasNext()) {
				
				paginator.addPage(cacheBuilder);
				slotter.reset();
				
				cacheBuilder = new InventoryBuilder(TextUtil.format(this.inventoryName), 6 * 9, false);
				
			}
			
			cacheBuilder.addItem(new ItemBuilder(item.getMaterial()).data(item.getData()).amount(1)
					.lore("")
					.lore(itemLore)
					.lore("")
					.lore("&aCompralo en " + "&e&l$&r&e" + item.getPriceBuy() + " &8&l(Click izquierdo)")
					.lore("")
					.lore("&9Vendelo en " + "&e&l$&r&e" + item.getPriceSell() + " &8&l(Click derecho)")
					.lore("")
					.build(), slotter.next(),
					new Action() {
				
						@Override
						public void click(ClickType click, Player player) {
							makeSale(item, player, item.getPriceBuy(), item.getPriceSell(), getClick(click));
							return;
						}});
			
			continue;
			
		}
		
		if(!paginator.contains(cacheBuilder))
			paginator.addPage(cacheBuilder);
		
		createdShops.add(this);
		return;
		
	}

	/**
	 * Sistema de compra de �tems del NPC.
	 * 
	 */
	public void openShop(Player player) {
		
		paginator.openInventory(player);
		return;
		
	}


	/**
	 * Hacer la venta y compra de alg�n item.
	 * 
	 * @param item Tipo de item seleccionado.
	 * @param player Jugador que interact�a con la GUI. 
	 * @param priceBuy Precio de compra del ITEM.
	 * @param priceSell Precio de venta del ITEM.
	 * @param click Tipo de click que se ejecuto en la GUI.
	 * 
	 * */
	private void makeSale(AdminShopItem item, Player player, int priceBuy, int priceSell, ClickType click) {
		
		ItemStack  itemShop = new ItemBuilder(item.getMaterial()).amount(1).data(item.getData()).build();
		
		int maxStackSpace = InventoryUtils.getMaxStackSpaceQuantity(player.getInventory(), itemShop);
		int avaiableAmount = InventoryUtils.countMatches(player.getInventory(), itemShop) + 1;
		
		int sellAmount = 1;
		
		int money = SurvivalBankBase.getMoney(player);
		
		if(item.getMaterial() == null) return;
		if(!player.isOnline()) return;
		
		if(click == ClickType.LEFT) {
			
			if(money < priceBuy) {
				
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 2);
				player.sendMessage(TextUtil.format(this.npcName + "&b&l� &7Te hacen falta &c$" + (priceBuy - money) + " &7para poder comprar &f&l" + itemShop + " &7 en esta tienda!")); 
				return;
			
			}
			
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 2, 2);
			player.getInventory().addItem(new ItemBuilder(item.getMaterial()).amount(1).data(item.getData()).build());
			player.sendMessage(TextUtil.format(this.npcName + "&b&l� &7Has comprado &f&lx" + avaiableAmount + " &7de &8" + ItemNameUtils.getMaterialName(item.getMaterial()) + " &7por &a$" + priceBuy + "."));

			SurvivalBankBase.removeMoney(player, priceBuy);
			return;
			
		}
		
		if(click == ClickType.SHIFT_LEFT) {

			
			int priceMaxAmount = maxStackSpace * priceBuy;
			
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 2, 2);
			player.getInventory().addItem(new ItemBuilder(item.getMaterial()).amount(maxStackSpace).data(item.getData()).build());
			player.sendMessage(TextUtil.format(this.npcName + "&b&l� &7Has comprado &f&lx" +  maxStackSpace  + " &8" + ItemNameUtils.getMaterialName(item.getMaterial()) + " &7al precio de &9$" + priceMaxAmount + "!"));
			SurvivalBankBase.removeMoney(player, priceMaxAmount);
			
			return;
			
		}
		
		if(click == ClickType.RIGHT) {
			
			if(avaiableAmount - 1 <= 0) {
				
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 2);
				player.sendMessage(TextUtil.format(this.npcName + "&b&l� &cNo tienes " + ItemNameUtils.getMaterialName(item.getMaterial()) + "&c en el inventario.")); 
				return;
			
			}
			
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 2, 2);
			InventoryUtils.removeQuantity(player.getInventory(), new ItemBuilder(item.getMaterial()).amount(1).data(item.getData()).build(), sellAmount);
			player.sendMessage(TextUtil.format(this.npcName + "&b&l� &7Has vendido "  + "&8" + ItemNameUtils.getMaterialName(item.getMaterial()) + " &7al precio de &9$" + priceSell + "!"));
			SurvivalBankBase.addMoney(player, priceSell);
			return;
		}
		
		if(click == ClickType.SHIFT_RIGHT) {
			
			int amount = avaiableAmount > itemShop.getMaxStackSize() ? itemShop.getMaxStackSize() : avaiableAmount - 1;
			int sellItem = amount * item.getPriceSell();
			
			if(amount <= 0) return;
			
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 2, 2);
			InventoryUtils.removeQuantity(player.getInventory(), new ItemBuilder(item.getMaterial()).amount(1).data(item.getData()).build(), amount);
			player.sendMessage(TextUtil.format(this.npcName + " &b&l� &7Has vendido &f&lx" + amount  + " &8" + ItemNameUtils.getMaterialName(item.getMaterial()) + " &7al precio de &9$" + sellItem + "!"));
			SurvivalBankBase.addMoney(player, sellItem);
			return;
		}
	}

	public String getNPCName() {
		return npcName;
	}
	
	public KindItem getKind() {
		return kind;
	}
	
	public ClickType getClick(ClickType click) {
		return click;
	}
	
	/**
	 * 
	 * Este metodo buscar� una tienda registrada
	 * con las caracteristicas dadas, en caso
	 * de que no se logre encontrar dicha tienda
	 * simplemente se crear� una nueva y posteriormente
	 * se registrar� para futuras busquedas.
	 * 
	 * @param kind Tipo de tienda.
	 * @param npcname El nombre del npc que atienda
	 * dicha tienda.
	 * @return El objeto de la tienda habilitado para
	 * utilizar sus metodos.
	 */
	public static InventoryShop lookupShop(KindItem kind, String npcname, String inventoryName) {
		
		for(InventoryShop shop : createdShops)
			if(shop.getKind() == kind && shop.getNPCName().equals(npcname))
				return shop;
		
		return new InventoryShop(kind, npcname, inventoryName);
		
	}
}
	