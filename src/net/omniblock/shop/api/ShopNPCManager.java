package net.omniblock.shop.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.omniblock.network.library.helpers.inventory.InventoryBuilderListener;
import net.omniblock.shop.ShopPlugin;
import net.omniblock.shop.api.object.npc.NPCShop;

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
		
		ShopPlugin.getInstance().getServer().getPluginManager().registerEvents(new InventoryBuilderListener(), 
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
		
		List<String> blacklist = Lists.newArrayList();

		@EventHandler
		public void onClick(PlayerInteractAtEntityEvent e) {
			
			if(!blacklist.contains(e.getPlayer().getName())) {
				
				blacklist.add(e.getPlayer().getName());
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						
						blacklist.remove(e.getPlayer().getName());
						return;
						
					}
					
				}.runTaskLater(ShopPlugin.getInstance(), 10L);

			if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked())) {

				NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());

				for (NPCShop shop : registeredNPCs) {
					
					if(shop.getNpc() != npc) continue;
					
					if (shop.getNpctype() != null) {
						
						if(shop.getNpctype().getAction() == null) continue;
						
						NPCShop.NPCAction action = shop.getNpctype().getAction();
						action.clickEvent(npc, e.getPlayer());
					
						}
					}
				}
			}
		}
	}
}
