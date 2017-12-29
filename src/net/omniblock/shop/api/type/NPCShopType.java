package net.omniblock.shop.api.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.omniblock.shop.api.object.NPCShop.NPCAction;


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
 * 
 */

public enum NPCShopType {
	
	SHOP_DEFAULT("Ciudadano", " ", new String[] {}, Material.EMERALD, null),

	SHOP_FOOD("Alex", "PANADERO", " ", new String[] {

			"¡Bienvenido! compre los más ricos panes de la ciudad!",
			"¡Panes frescos y crujientes solo en este local!" }, Material.BREAD, new NPCAction() {

				@Override
				public void clickEvent(NPC npc, Player player) {

				}
			}),

	SHOP_MATERIAL("Juan", "HERRERO", " ", new String[] {

			"¡Piedras preciosas a buen precio!", 
			"¡Acércate y mira nuestra galería de cosas únicas para ti!" },Material.DIAMOND_PICKAXE, new NPCAction() {

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

	NPCShopType(String name, String professionName, String skin, String[] npcDialogs, Material material, NPCAction action) {

		this.name = name;
		this.professionName = professionName;
		this.skin = skin;
		this.npcDialogs = npcDialogs;
		this.material = material;

		this.action = action;

	}
	
	NPCShopType(String name, String skin, String[] npcDialogs, Material material, NPCAction action) {

		this.name = name;
		this.skin = skin;
		this.npcDialogs = npcDialogs;
		this.material = material;
		
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
}
