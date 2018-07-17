package net.omniblock.shop.api.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.omniblock.shop.api.object.npc.NPCShop.NPCAction;
import net.omniblock.shop.api.object.npc.object.InventoryShop;

public enum NPCShopType {

	DEFAULT("Ciudadano", "", new String[] {

			"Woow, que hermosas vistas tengo aquí, nunca me iráa de esta ciudad!",
			"Todos los días aumentas esos estúpidos impuestos, parece que quieren dejar al pueblo pobre!",
			"Está muy tranquila la ciudad hoy en día!" },

			Material.EMERALD, null),
	SHOP_FOOD("&aAlex", "&6&lALIMENTOS", "scarfguy", new String[] {

			"Bienvenido! compre los más ricos panes de la ciudad!",
			"Panes frescos y crujientes solo en este local!",
			"Recuerda tener una dieta equilibrada."},

			Material.BREAD, new NPCAction() {

				@Override
				public void clickEvent(NPC npc, Player player) {
				
					InventoryShop shop = InventoryShop.lookupShop(KindItem.FOODSTUFFS, NPCShopType.SHOP_FOOD.getName(), "&8�Busca algo en particular?");
					shop.openShop(player);
					
					return;
					
				}
			}),
	SHOP_MATERIALS("&aJuan", "&6&lMINERO", "qrj", new String[] {

			"Acercate y mira nuestra galeria de cosas unicas para ti!",
			"Los materiales son de primerísima calidad!",
			"Tu pon el dinero, yo te pongo el material que necesitas.",
			"Compra tus herramientas al mejor precio."},

			Material.DIAMOND_PICKAXE, new NPCAction() {

				@Override
				public void clickEvent(NPC npc, Player player) {

					InventoryShop shop = InventoryShop.lookupShop(KindItem.TOOLS, NPCShopType.SHOP_MATERIALS.getName(), "&8¿Quiere algo en concreto?");
					shop.openShop(player);

					return;
				}
				
			}),
	SHOP_BLOCKS("&aPedro", "&6&lCONSTUCTOR", "Korev", new String[] {

			"Todos los materiales que necesitas, ven y compra",
			"Todos los materiales que necesitas, ven y compra",
			"Los mejores bloques de todo Omniblock..."},

			Material.BRICK, new NPCAction() {

				@Override
				public void clickEvent(NPC npc, Player player) {
					
					InventoryShop shop = InventoryShop.lookupShop(KindItem.BUILDING_BLOCKS, NPCShopType.SHOP_BLOCKS.getName(), "&8¿Que desea?");
					shop.openShop(player);
					
					return;
					
				}
			}),
	SHOP_BLACKSMITH("&aRicardo", "&6&lHERRERO", "papand13", new String[] {

			"El único acero que puede traspasar mis armaduras.... es mi acero.",
			"" },

			Material.ANVIL, new NPCAction() {

				@Override
				public void clickEvent(NPC npc, Player player) {
					
					InventoryShop shop = InventoryShop.lookupShop(KindItem.ARMORS, NPCShopType.SHOP_BLACKSMITH.getName(), "&8¿Quiere algo en concreto?");
					shop.openShop(player);
					
				}
			}),
	
	;

	private String name;
	private String professionName;
	private String skin;
	private String[] npcDialogs;

	private Material material;
	private NPCAction action;

	/**
	 * NPCShopType > Tipos de NPC
	 * 
	 * @param name
	 *            Nombre que tendra el NPC.
	 * @param professionName
	 *            Nombre de la profesi�n que tendr� el NPC.
	 * @param skin
	 *            Nombre de la skin que se va a utilizar.
	 * @param npcDialogs
	 *            Dialogos de los NPCs.
	 * @param material
	 *            Material que representa su profesi�n.
	 * @param action
	 *            Acci�n que se realizara al hundir click al NPC.
	 * 
	 */
	NPCShopType(String name, String professionName, String skin, String[] npcDialogs, Material material,
			NPCAction action) {

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

	public static NPCShopType getTypeByName(String name) {
		
		try {
			
			NPCShopType type = valueOf(name);
			return type;
			
		} catch(Exception e) { return null; }
		
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
