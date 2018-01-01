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
			
		
		for(ItemStack cacheItem : inventory.getContents()) {
			
			if(quantity <= 0)
				break;
			
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
		
		toRemoveStacks.forEach(itemStack -> inventory.setItem(getItemStackSlot(inventory, itemStack), null));
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
	
	/**
	 * 
	 * Con este metodo se puede obtener el
	 * slot donde se encuentra un itemstack
	 * dentro de un inventario.
	 * 
	 * @param inventory El inventario.
	 * @param item El stack.
	 * @return El slot donde se encuentra el
	 * item, en caso de que no se logre encontrar
	 * uno se devolverá -1.
	 */
	public static int getItemStackSlot(Inventory inventory, ItemStack item) {
		
		if(!inventory.contains(item))
			return -1;
		
		for(int i = 0; i <= inventory.getSize() - 1; i++)
			if(inventory.getItem(i) != null)
				if(inventory.getItem(i).equals(item))
					return i;
		
		return -1;
		
	}

	/**
	 * 
	 * Con este metodo se puede obtener la
	 * cantidad maxima de un item que puede
	 * almacenar un inventario.
	 * 
	 * @param inventory El inventario.
	 * @param item El item.
	 * @return La cantidad maxima de el tipo
	 * de item que puede almacenar el inventario.
	 */
	public static int getMaxStackSpaceQuantity(Inventory inventory, ItemStack item) {
		
		if(inventory.firstEmpty() != -1)
			return item.getMaxStackSize();
		
		for(int i = 0; i <= inventory.getSize() - 1; i++)
			if(inventory.getItem(i) != null)
				if(inventory.getItem(i).isSimilar(item))
					if(inventory.getItem(i).getAmount() != item.getMaxStackSize())
						return item.getMaxStackSize() - inventory.getItem(i).getAmount();
		
		return 0;
		
	}
	
}
