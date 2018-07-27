package net.omniblock.shop;

import net.omniblock.network.library.utils.TextUtil;
import net.omniblock.network.systems.CommandPatcher;
import net.omniblock.shop.api.object.npc.object.InventoryShop;
import net.omniblock.shop.api.type.KindItem;
import net.omniblock.shop.api.type.NPCShopType;
import net.omniblock.survival.utils.HelpUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] argss) {

		if(sender instanceof Player) {

			Player player = ((Player) sender).getPlayer();

			if (cmd.getName().equalsIgnoreCase("shop") ||
					cmd.getName().equalsIgnoreCase("tienda")) {

				if(argss.length == 1)
					if(argss[0].equalsIgnoreCase("block") ||
							argss[0].equalsIgnoreCase("blocks") ||
							argss[0].equalsIgnoreCase("bloque") ||
							argss[0].equalsIgnoreCase("bloques")){

						InventoryShop shop = InventoryShop.lookupShop(KindItem.BUILDING_BLOCKS, NPCShopType.SHOP_BLOCKS.getName(), "&8¿Quiere algo en concreto?");
						shop.openShop(player);

						return true;

					}


				if(argss.length == 1)
					if(argss[0].equalsIgnoreCase("food") ||
							argss[0].equalsIgnoreCase("foods") ||
							argss[0].equalsIgnoreCase("comida") ||
							argss[0].equalsIgnoreCase("comidas")){

						InventoryShop shop = InventoryShop.lookupShop(KindItem.FOODSTUFFS, NPCShopType.SHOP_FOOD.getName(), "&8¿Quiere algo en concreto?");
						shop.openShop(player);

						return true;

					}


				if(argss.length == 1)
					if(argss[0].equalsIgnoreCase("tools") ||
							argss[0].equalsIgnoreCase("tool") ||
							argss[0].equalsIgnoreCase("armas") ||
							argss[0].equalsIgnoreCase("arma")){

						InventoryShop shop = InventoryShop.lookupShop(KindItem.TOOLS, NPCShopType.SHOP_MATERIALS.getName(), "&8¿Quiere algo en concreto?");
						shop.openShop(player);

						return true;

					}

				/*
				sender.sendMessage(CommandPatcher.BAR);
				sender.sendMessage(TextUtil.format(" &cERROR: &7Comando no valido."));
				sender.sendMessage(TextUtil.format(" &7Tus comandos disponibles son:"));
				sender.sendMessage(TextUtil.format(" &b/tienda <tipo> &e- &7Selecciona la tienda especificada."));
				sender.sendMessage(TextUtil.format(""));
				sender.sendMessage(TextUtil.format(" &7Las tiendas disponibles son:"));
				sender.sendMessage(TextUtil.format(" &e- &aBloques, comidas, armas."));
				sender.sendMessage(CommandPatcher.BAR);
				*/
				HelpUtil.cmdFormat(player, "/tienda <bloques | comida | armas>", "/tienda bloques");
				return true;

			}
		}
		return false;
	}
}
