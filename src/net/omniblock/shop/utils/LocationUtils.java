package net.omniblock.shop.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * 
 * Clase de utilidad para la serialización
 * de localizaciones.
 * 
 * @author zlToxicNetherlz
 *
 */
public class LocationUtils {

	/**
	 * 
	 * Este metodo serializará
	 * una localización convirtiendola
	 * en un String que luego podrá
	 * ser deserializado.
	 * 
	 * @param location La localización que se
	 * serializará.
	 * @return La localización serializada en
	 * formato String.
	 */
	public static String serializeLocation(Location location) {
		
		if(location == null)
			return null;
		
		return new String(location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
		
	}
	
	public static Location deserializeLocation(String location) throws Exception {
		
		if(location == null)
			return null;
		
		if(StringUtils.countMatches(location, ",") < 3)
			throw new UnsupportedOperationException("El formato de la localización no es valido.");
		
		String[] splittedLocation = location.split(",");
		
		if(Bukkit.getWorld(splittedLocation[0]) == null)
			throw new UnsupportedOperationException("El mundo '" + splittedLocation[0] + " no existe.");
		
		return new Location(
				Bukkit.getWorld(splittedLocation[0]), 
				Double.valueOf(splittedLocation[1]), 
				Double.valueOf(splittedLocation[2]),
				Double.valueOf(splittedLocation[3]));
		
	}
	
}
