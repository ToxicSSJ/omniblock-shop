package net.omniblock.shop.api.config.variables;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public class ItemsProtocol {

	protected static List<Material> blockedMaterials = Arrays.asList(
			Material.AIR,
			Material.BEDROCK,
			Material.SPAWNER,
			Material.BARRIER,
			Material.COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK,
			Material.COMMAND_BLOCK_MINECART,
			Material.REPEATING_COMMAND_BLOCK);
	
	public static boolean isMaterialBlocked(Material material) {
		return blockedMaterials.contains(material);
	}
	
}
