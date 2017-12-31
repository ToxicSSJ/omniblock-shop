package net.omniblock.shop.api.config;

import org.bukkit.configuration.file.FileConfiguration;

import net.omniblock.shop.ShopPlugin;
import net.omniblock.network.library.addons.configaddon.object.Config;

public enum ConfigType {

	SHOPDATA(new Config(ShopPlugin.getInstance(), "data/shopdata.yml")),
	SHOP_NPC_DATA(new Config(ShopPlugin.getInstance(), "data/shopnpc.yml")),

	;

	private Config config;

	ConfigType(Config config) {
		this.config = config;
	}

	public Config getConfigObject() {
		return config;
	}

	public FileConfiguration getConfig() {
		return config.getConfigFile();
	}

	public void setConfig(Config config) {
		this.config = config;
	}

}
