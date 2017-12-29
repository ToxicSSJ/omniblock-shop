package net.omniblock.shop.api.object;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.shop.api.type.NPCShopType;

public class NPCShop {

	private NPC npc;
	private NPCShopType type = NPCShopType.SHOP_DEFAULT;

	private Location location;

	private BukkitTask task;

	/**
	 *
	 * Se creara un tipo de NPC
	 * 
	 * @param NPCShopType
	 *            Se define que tipo de NPC se creara o se utilizara.
	 * @param Location
	 *            Localización donde se spawnea el NPC.
	 * @param yaw-pitch
	 *            Ubicación de la cabeza del NPC.
	 * 
	 */

	public NPCShop(NPCShopType type, Location location, float yaw, float pitch) {

		this.type = type;
		this.location = location;
		
		this.location.setYaw(yaw);
		this.location.setPitch(pitch);

		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, type.getSkin());
		npc.spawn(this.location);
		npc.setName(TextUtil.format(type.getName()));
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, type.getSkin());

		makeIA();

	}

	/**
	 * 
	 * Elimina un NPC ya creado.
	 * 
	 */

	public NPCShop destroy() {

		if (npc != null)
			if (npc.isSpawned())
				npc.destroy();

		task.cancel();

		return this;
	}

	/**
	 * Con este método, creará la inteligencia que tendrá el NPC.
	 * 
	 */

	public void makeIA() {

	}

	/**
	 * 
	 * Interface donde se realiza la acción del NPC.
	 * 
	 */

	public static interface NPCAction {
		public void clickEvent(NPC npc, Player player);
	}

	public NPCShopType getNpctype() {
		return type;
	}

	public void setNpctype(NPCShopType npctype) {
		this.type = npctype;
	}
}
