package net.omniblock.shop.api.config.variables;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public class ItemsProtocol {

	protected static List<Material> blockedMaterials = Arrays.asList(
			Material.AIR,
			Material.BEDROCK,
			Material.MOB_SPAWNER,
			Material.BARRIER,
			Material.COMMAND,
			Material.COMMAND_CHAIN,
			Material.COMMAND_MINECART,
			Material.COMMAND_REPEATING);
	
	public static boolean isMaterialBlocked(Material material) {
		return blockedMaterials.contains(material);
	}
	
}
