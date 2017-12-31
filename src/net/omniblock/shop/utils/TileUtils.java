package net.omniblock.shop.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

/**
 * 
 * Clase encargada de ofrecer utilidades
 * para las TileEntities.
 * 
 * @author zlToxicNetherlz
 *
 */
public class TileUtils {

	/**
	 * 
	 * Buscar un cofre detrás de un cartel
	 * y devolverlo. En caso de que no exista
	 * este metodo devolverá null.
	 * 
	 * @param sign
	 * @return El Cofre detrás de un cartel con
	 * el tipo de objeto 'Chest'.
	 * @see Chest
	 */
	public static Chest getChestBehindSign(Sign sign) {
		
		org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) sign.getData();
		BlockFace attachedFace = signMaterial.getAttachedFace();
		
		Block chestBlock = sign.getBlock().getRelative(attachedFace);
		
		if(chestBlock.getType() == Material.CHEST)
			if(chestBlock.getState() instanceof Chest)
				return (Chest) chestBlock.getState();
		
		return null;
		
	}
	
}
