package net.omniblock.shop.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

/**
 * 
 * Esta clase proporcionará utilidades
 * para el manejo eficiente de inventarios.
 * 
 * @author zlToxicNetherlz
 *
 */
public class InventoryUtils {

	/**
	 * 
	 * Con este metodo se podrá contar
	 * la cantidad de cierto item
	 * dentro de un inventario.
	 * 
	 * @param inventory El inventario.
	 * @param item El item del cual se
	 * contará su cantidad.
	 * @return La cantidad de objetos de
	 * ese item.
	 */
	public static int countMatches(Inventory inventory, ItemStack item) {
		
		int count = 0;
		
		for(ItemStack cacheItem : inventory.getContents()) {
			
			if(cacheItem == null)
				continue;
			
			if(cacheItem.isSimilar(item))
				count += cacheItem.getAmount();
			
		}
		
		return count;
		
	}
	
	/**
	 * 
	 * Con este metodo se podrá eliminar
	 * la cantidad de un item en un
	 * inventario.
	 * 
	 * @param inventory El inventario.
	 * @param item El item que se removerá.
	 * @param quantity La cantidad de items que removerás.
	 */
	public static void removeQuantity(Inventory inventory, ItemStack item, int quantity) {
		
		if(quantity <= 0)
			return;
		
		List<ItemStack> toRemoveStacks = Lists.newArrayList();
		Map<ItemStack, Integer> toSetStacks = new HashMap<ItemStack, Integer>();
		
		if(quantity <= item.getMaxStackSize())
			if(inventory.contains(item)) {
				inventory.setItem(inventory.first(item), null);
				return;
			}
			
		
		for(ItemStack cacheItem : inventory.getContents()) {
			
			if(quantity <= 0)
				return;
			
			if(cacheItem == null)
				continue;
			
			if(cacheItem.isSimilar(item)) {
				
				if(quantity >= cacheItem.getAmount()) {
					
					toRemoveStacks.add(cacheItem);
					quantity -= cacheItem.getAmount();
					continue;
					
				}
				
				toSetStacks.put(cacheItem, cacheItem.getAmount() - quantity);
				quantity = 0;
				break;
				
			}
			
		}
		
		toRemoveStacks.forEach(itemStack -> inventory.setItem(inventory.first(itemStack), null));
		toSetStacks.entrySet().forEach(entry -> entry.getKey().setAmount(entry.getValue()));
		return;
		
	}

	/**
	 * 
	 * Con este metodo se puede comprobar
	 * si un inventario tiene suficiente
	 * espacio para recibir cierto stack.
	 * 
	 * @param inventory El inventario.
	 * @param item El stack.
	 * @return <strong>true</strong> si tiene espacio suficiente.
	 */
	public static boolean hasSpaceForStack(Inventory inventory, ItemStack item) {
		
		int quantity = item.getAmount();
		
		if(inventory.firstEmpty() != -1)
			return true;
		
		for(ItemStack cacheItem : inventory.getContents()) {
			
			if(quantity <= 0)
				return true;
			
			if(cacheItem == null)
				continue;
			
			if(cacheItem.isSimilar(item))
				if(cacheItem.getAmount() < cacheItem.getMaxStackSize())
					quantity -= cacheItem.getMaxStackSize() - cacheItem.getAmount();
			
		}
		
		if(quantity <= 0)
			return true;
		
		return false;
		
	}
	
}
