package net.omniblock.shop.utils;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 
 * Clase que proporciona utilidades
 * importantes para las entidades.
 * 
 * @author zlToxicNetherlz
 *
 */
public class EntityUtils {

	/**
	 * 
	 * Verificar si un jugador está observando
	 * una entidad.
	 * 
	 * @param player El jugador que observa.
	 * @param entity La entidad que es observada.
	 * @return <strong>true</strong> en caso de que
	 * el jugador sí está observando la entidad.
	 */
	public static boolean isLookingAtEntity(Player player, LivingEntity entity) {

		if (player != entity) {

			Location eye = player.getEyeLocation();
			Vector toEntity = entity.getEyeLocation().toVector().subtract(eye.toVector());
			double dot = toEntity.normalize().dot(eye.getDirection());

			return dot > 0.99D;

		}

		return false;

	}
	
	/**
	 * 
	 * Con este metodo se puede obtener el
	 * bloque que está viendo el usuario en base
	 * a una distancia definida.
	 * 
	 * @param player El jugador.
	 * @return El bloque que el jugador está viendo
	 * a esa distancia.
	 */
	public static Block getBlockAtLooking(Player player, int distance) {

		return player.getTargetBlock((Set<Material>) null, distance);

	}
	
}
