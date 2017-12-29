package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.object.NPCShop;

public class ShopNPCManager {

	protected static List<NPCShop> registeredNPCs = new ArrayList<NPCShop>();

	/**
	 *
	 * Se preparará toda la tienda de los NPCs.
	 * 
	 */

	public static void setup() {

		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new ShopNPCListener(),
				ShopPlugin.getInstance());

		return;

	}

	/**
	 * Se registrara un NPC.
	 * 
	 * @param shop
	 *            Se crea un objeto nuevo para ser registrado.
	 * 
	 */

	public static void registeredNPCShop(NPCShop shop) {
		registeredNPCs.add(shop);
		return;

	}

	/**
	 * 
	 * Se elimina el NPC registrado.
	 * 
	 * @param shop
	 *            Se instancia el objeto para eliminarlo.
	 * 
	 */

	public static void removeNPCShop(NPCShop shop) {
		if (registeredNPCs.contains(shop))
			registeredNPCs.remove(shop);

		return;

	}

	/**
	 * Se registrara todos los eventos de los NPCs.
	 * 
	 */

	public static class ShopNPCListener implements Listener {

		@EventHandler
		public void onClick(PlayerInteractAtEntityEvent e) {

			if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked())) {

				NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());

				for (NPCShop shop : registeredNPCs) {

					if (shop.getNpctype() != null) {

						NPCShop.NPCAction action = shop.getNpctype().getAction();
						action.clickEvent(npc, e.getPlayer());
					}
				}
			}
		}
	}
}
