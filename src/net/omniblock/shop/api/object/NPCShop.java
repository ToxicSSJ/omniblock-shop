package net.omniblock.shop.api.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.network.library.utils.TextUtil;

public class NPCShop {

	private NPCShopType npctype;
	private NPC npc;

	private BukkitTask task;

	/**
	 *
	 * Se creara un tipo de NPC
	 * 
	 * @param NPCShopType
	 *            Se define que tipo de NPC se creara o se utilizara.
	 * 
	 */

	public NPCShop(NPCShopType type) {

		// Se coloca el tipo de NPC.
		this.npctype = type;

		// Se crea el NPC, teniendo en cuenta el NPC ya registrado.
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npctype.getSkin());
		Location loc = npctype.location.clone();

		loc.setYaw(npctype.getYaw());
		loc.setPitch(npctype.getPitch());

		npc.spawn(loc);
		npc.setName(TextUtil.format(npctype.getName()));
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, npctype.getSkin());

		// Se inicia la IA del NPC. Ej: hablar.
		makeIA();

	}

	/**
	 * Con este método, se creará la inteligencia que tendrá el NPC.
	 * 
	 */

	public void makeIA() {

	}

	/**
	 * NPCShopType > Tipos de NPC
	 * 
	 * @param name
	 *            Nombre que tendra el NPC.
	 * @param professionName
	 *            Nombre de la profesión que tendrá el NPC.
	 * @param skin
	 *            Nombre de la skin que se va a utilizar.
	 * @param npcDialogs
	 *            Dialogos de los NPCs.
	 * @param material
	 *            Material que representa su profesión.
	 * @param action
	 *            Acción que se realizara al hundir click al NPC.
	 * @param Location
	 *            Localización donde se spawnea el NPC.
	 * @param yaw,
	 *            pitch Ubicación de la cabeza del NPC.
	 * 
	 */

	public enum NPCShopType {

		SHOP_FOOD("Alex", "PANADERO", " ", new String[] {

				"¡Bienvenido! compre los más ricos panes de la ciudad!",
				"¡Panes frescos y crujientes solo en este local!" }, Material.BREAD, null, 0, 0, new NPCAction() {

					@Override
					public void clickEvent(NPC npc, Player player) {

					}
				}),

		SHOP_MATERIAL("Juan", "HERRERO", " ", new String[] {

				"¡Piedras preciosas a buen precio!", "¡Acércate y mira nuestra galería de cosas únicas para ti!" },
				Material.DIAMOND_PICKAXE, null, 0, 0, new NPCAction() {

					@Override
					public void clickEvent(NPC npc, Player player) {

					}
				});

		;

		private String name;
		private String professionName;
		private String skin;
		private String[] npcDialogs;

		private Material material;
		private NPCAction action;

		private Location location;
		private float yaw, pitch;

		NPCShopType(String name, String professionName, String skin, String[] npcDialogs, Material material,
				Location location, float yaw, float pitch, NPCAction action) {

			this.name = name;
			this.professionName = professionName;
			this.skin = skin;
			this.npcDialogs = npcDialogs;
			this.material = material;

			this.location = location;
			this.yaw = yaw;
			this.pitch = pitch;

			this.action = action;

		}

		public String getName() {
			return name;
		}

		public String getProfessionName() {
			return professionName;
		}

		public String getSkin() {
			return skin;
		}

		public String[] getNpcDialogs() {
			return npcDialogs;
		}

		public Material getMaterial() {
			return material;
		}

		public NPCAction getAction() {
			return action;
		}

		public Location getLocation() {
			return location;
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}

	}

	public NPCShopType getNpctype() {
		return npctype;
	}

	public void setNpctype(NPCShopType npctype) {
		this.npctype = npctype;
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
