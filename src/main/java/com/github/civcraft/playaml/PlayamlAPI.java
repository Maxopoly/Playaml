package com.github.civcraft.playaml;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayamlAPI {

	/**
	 * Retrives a configuration section which will contain any data previously
	 * stored by this plugin for the given plugin. Direct changes to this config
	 * section will not be saved and not be reflected in the data structure of
	 * this plugin, unless explicitly saved with the appropriate API method.
	 * 
	 * @param uuid
	 *            UUID of the player for whom data should be retrieved
	 * @param plugin
	 *            Plugin requesting data
	 * @return ConfigurationSection containing any previously saved data, this
	 *         may be empty if no data was saved previously
	 */
	public static ConfigurationSection getData(UUID uuid, JavaPlugin plugin) {
		if (uuid == null || plugin == null) {
			throw new IllegalArgumentException("UUID and plugin may not be null, parameters given were: uuid:" + uuid
					+ ", plugin:" + plugin);
		}
		return PlayamlPlugin.getManager().getData(uuid).getData(plugin.getName());
	}

	/**
	 * Saves the given data for the given player and plugin, which means it will
	 * be persisted in the database eventually and any further calls to get data
	 * for the same plugin/player combo will retrieve a copy of this
	 * configsection until another save is issued
	 * 
	 * @param uuid UUID of the player to whom the data belongs
	 * @param plugin Plugin requesting the save
	 * @param data Data to save
	 */
	public static void saveData(UUID uuid, JavaPlugin plugin, ConfigurationSection data) {
		if (uuid == null || plugin == null || data == null) {
			throw new IllegalArgumentException("Arguments may not be null, parameters given were: uuid:" + uuid
					+ ", plugin:" + plugin + ", configsection: " + data);
		}
		PlayamlPlugin.getManager().getData(uuid).putData(plugin.getName(), data);
	}

}
