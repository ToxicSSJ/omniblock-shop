package net.omniblock.shop.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
			
			if(cacheItem.isSimilar(item))
				count += cacheItem.getAmount();
			
		}
		
		return count;
		
	}

}
