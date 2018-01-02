package net.omniblock.shop.api.object.npc.object;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.network.library.helpers.ItemBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder;
import net.omniblock.network.library.helpers.inventory.InventoryBuilder.Action;

public class InventoryPaginator {

	protected List<InventoryBuilder> inventoryPages = Lists.newArrayList();
	
	protected PaginatorStyle style;
	
	public InventoryPaginator(PaginatorStyle style) {
		
		this.style = style;
		return;
		
	}
	
	public InventoryPaginator addPage(InventoryBuilder inventoryBuilder) {
		
		if(inventoryBuilder.getSize() < 6 * 9)
			throw new UnsupportedOperationException("El inventario debe tener un tamaño mayor o igual a 56.");
		
		if(inventoryBuilder.isDeleteOnClose())
			throw new UnsupportedOperationException("El inventario no debe ser borrado al cerrarse.");
		
		inventoryBuilder.addItem(style.getNext(true), 51);
		
		if(inventoryPages.size() != 0)
			getLastPage().addItem(style.getNext(false), 51, new Action() {

				@Override
				public void click(ClickType click, Player player) {
					
					inventoryBuilder.open(player);
					return;
					
				}
				
			});
		
		if(inventoryPages.size() == 0)
			inventoryBuilder.addItem(style.getBack(true), 47);
		
		if(inventoryPages.size() != 0)
			inventoryBuilder.addItem(style.getBack(false), 47, new Action() {

				InventoryBuilder backPage = getLastPage();
				
				@Override
				public void click(ClickType click, Player player) {
					
					backPage.open(player);
					return;
					
				}
				
			});
		
		
		
		inventoryPages.add(inventoryBuilder);
		return this;
		
	}
	
	public InventoryBuilder getLastPage() {
		
		if(inventoryPages.size() == 0)
			return null;
		
		return inventoryPages.get(inventoryPages.size() - 1);
		
	}
	
	public boolean contains(InventoryBuilder inventoryBuilder) {
		
		return inventoryPages.contains(inventoryBuilder);
			
		
	}
	
	public void openInventory(Player player) {
		
		if(inventoryPages.size() < 1)
			return;
		
		inventoryPages.get(0).open(player);
		return;
		
	}
	
	public PaginatorStyle getStyle() {
		return style;
	}
	
	public static enum PaginatorStyle {
		
		DEFAULT_ARROWS(
				new ItemBuilder(Material.ARROW)
					.name(TextUtil.format("&b« &7Volver"))
					.build(),
				new ItemBuilder(Material.ARROW)
					.name(TextUtil.format("&b» &7Siguiente"))
					.build(),
				new ItemBuilder(Material.ARROW)
					.name(TextUtil.format("&c✖ &8Volver"))
					.build(),
				new ItemBuilder(Material.ARROW)
					.name(TextUtil.format("&c✖ &8Siguiente"))
					.build()),
					
		COLOURED_ARROWS(
				new ItemBuilder(Material.TIPPED_ARROW)
					.setPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1, 1))
					.name(TextUtil.format("&b« &7Volver"))
					.hideAtributes()
					.build(),
				new ItemBuilder(Material.TIPPED_ARROW)
					.setPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1, 1))
					.name(TextUtil.format("&b» &7Siguiente"))
					.hideAtributes()
					.build(),
				new ItemBuilder(Material.TIPPED_ARROW)
					.setPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, 1))
					.name(TextUtil.format("&c✖ &8Volver"))
					.hideAtributes()
					.build(),
				new ItemBuilder(Material.TIPPED_ARROW)
					.setPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, 1))
					.name(TextUtil.format("&c✖ &8Siguiente"))
					.hideAtributes()
					.build()),
		
		
		;
		
		private ItemStack back, next, cantBack, cantNext;
		
		PaginatorStyle(ItemStack back, ItemStack next, ItemStack cantBack, ItemStack cantNext){
			
			this.back = back;
			this.next = next;
			
			this.cantBack = cantBack;
			this.cantNext = cantNext;
			
		}
		
		private ItemStack getBack(boolean cant) {
			return cant ? cantBack : back;
		}
		
		private ItemStack getNext(boolean cant) {
			return cant ? cantNext : next;
		}
		
	}
	
}
