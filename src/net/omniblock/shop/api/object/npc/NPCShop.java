package net.omniblock.shop.api.object.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.network.library.utils.LocationUtils;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.ShopNPCManager;
import net.omniblock.shop.api.config.ConfigType;
import net.omniblock.shop.api.type.NPCShopType;

public class NPCShop {

	private NPC npc;
	private NPCShopType type = NPCShopType.DEFAULT;
	
	private String uniqueID;
	private UUID npcUniqueID;
	
	private Hologram hologram;
	
	private Location location;
	private Location lookAt;
	
	private boolean loaded;
	private boolean destroyed;

	/**
	 *
	 * Constructor principal de la clase
	 * general NPCShop.
	 * 
	 * @param type Se define que tipo de NPC se creara o se utilizara.
	 * @param uniqueID Identidad unica de la tienda.
	 * @param location Localización donde se spawnea el NPC.
	 * @param lookAt Localización a la que mirará el NPC.
	 * 
	 */
	public NPCShop(NPCShopType type, String uniqueID, Location location, Location lookAt) {

		this.type = type;
		this.location = location;
		
		this.lookAt = lookAt;
		this.uniqueID = uniqueID;
		
	}

	/**
	 * 
	 * Cargar el NPC.
	 * 
	 */
	public void loadNPC() {
		
		if(loaded)
			return;
		
		if(!ShopNPCManager.getShops().contains(this))
			ShopNPCManager.registerNPCShop(this);
		
		loaded = true;
		
		hologram = HologramsAPI.createHologram(ShopPlugin.getInstance(), this.location.clone().add(0, 3.3, 0));
		
		hologram.appendItemLine(new ItemStack(this.type.getMaterial()));
		hologram.appendTextLine(TextUtil.format(this.type.getProfessionName()));
		
		if(ConfigType.SHOP_NPC_DATA.getConfig().isSet("npcshop." + uniqueID + ".npcUniqueID")) {
			
			this.npcUniqueID = UUID.fromString(ConfigType.SHOP_NPC_DATA.getConfig().getString("npcshop." + uniqueID + ".npcUniqueID"));
			NPC cacheNPC = CitizensAPI.getNPCRegistry().getByUniqueId(npcUniqueID);
			
			if(cacheNPC == null)
				cacheNPC = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(npcUniqueID);
			
			if(cacheNPC == null)
				craftCitizensNPC();
			
			npc = cacheNPC;
			
		} else { craftCitizensNPC(); }
		
		if(lookAt != null)
			npc.faceLocation(lookAt);
		
	}
	
	private void craftCitizensNPC() {
		
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, this.type.getSkin());
		npc.spawn(this.location);
		npc.setName(TextUtil.format(this.type.getName()));
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, this.type.getSkin());
		
		this.saveNPC();
		
	}
	
	/**
	 * 
	 * Metodo encargado de descargar
	 * el npc del sistema.
	 * 
	 */
	public void unloadNPC() {
		
		if(!loaded)
			return;
		
		if(npc != null)
			if(npc.isSpawned())
				npc.destroy();
		
		if(hologram != null)
			if(!hologram.isDeleted())
				hologram.delete();
		
	}
	
	/**
	 * 
	 * Metodo para destruir una tienda
	 * con todos sus elementos, también
	 * lo borrar de la configuracion.
	 * 
	 */
	public void destroy() {

		destroyed = true;
			
		if(npc != null)
			if(npc.isSpawned())
				npc.destroy();
		
		if(hologram != null)
			if(!hologram.isDeleted())
				hologram.delete();

		if(ConfigType.SHOP_NPC_DATA.getConfig().isSet("npcshop." + uniqueID)) {
			
			ConfigType.SHOP_NPC_DATA.getConfig().set("npcshop." + uniqueID, null);
			ConfigType.SHOP_NPC_DATA.getConfigObject().save();
			
		}
		
		return;
	}

	/**
	 * Con este método se obtiene los dialogos de los NPCs.
	 * 
	 */
	public String[] getDialogs() {
		return type.getNpcDialogs();
	}
	
	public void saveNPC() {
		
		for(Map.Entry<String, Object> entry : getConfigData().entrySet())
			ConfigType.SHOP_NPC_DATA.getConfig().set(entry.getKey(), entry.getValue());
		
		ConfigType.SHOP_NPC_DATA.getConfigObject().save();
		return;
		
	}
	
	@SuppressWarnings("serial")
	public Map<String, Object> getConfigData() {
		return new HashMap<String, Object>(){{
			
			put("npcshop." + uniqueID + ".location", LocationUtils.serializeLocation(location));
			put("npcshop." + uniqueID + ".lookAt", LocationUtils.serializeLocation(lookAt));
			put("npcshop." + uniqueID + ".npcID", npc.getUniqueId().toString());
			put("npcshop." + uniqueID + ".type", type.name());
			
		}};
	}
	
	public void setLocation(Location location) {
		this.location = location;
		return;
	}
	
	public void setLookAt(Location lookAt) {
		this.lookAt = lookAt;
		return;
	}
	
	public void setHologram(Hologram hologram) {
		this.hologram = hologram;
		return;
	}
	
	public void move(Location location, Location lookAt) {
		
		this.location = location;
		this.lookAt = lookAt;
		
		npc.teleport(location, TeleportCause.PLUGIN);
		npc.faceLocation(lookAt);
		
		if(hologram != null)
			hologram.teleport(location.clone().add(0, 3.3, 0));
		
		this.saveNPC();
		return;
		
	}
	
	public Location getLocation() {
		
		if(location != null)
			return location;
		
		if(npc != null)
			if(npc.getStoredLocation() != null)
				return npc.getStoredLocation();
		
		return null;
		
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public NPC getNpc() {
		return npc;
	}

	public NPCShopType getNpctype() {
		return type;
	}

	public Location getLookAt() {
		return lookAt;
	}

	public Hologram getHologram() {
		return hologram;
	}

	public String getUniqueID() {
		return uniqueID;
	}
	
	/**
	 * 
	 * Interface donde se realiza la acción del NPC.
	 * 
	 */
	public static interface NPCAction {
		public void clickEvent(NPC npc, Player player);
	}
	
}
