package com.github.civcraft.playaml.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData {

	private Map<String, ConfigurationSection> data;
	private boolean dirty;
	private UUID uuid;

	public PlayerData(UUID uuid, ConfigurationSection config) {
		this.uuid = uuid;
		this.data = new HashMap<String, ConfigurationSection>();
		for (String key : config.getKeys(false)) {
			this.data.put(key, config.getConfigurationSection(key));
		}
		this.dirty = false;
	}

	/**
	 * Retrieves a copy of the data stored for the plugin with the given name.
	 * Editing the given configuration section will not be saved unless you
	 * explicitly call the putData() method
	 * 
	 * @param pluginName
	 *            Name of the plugin for which you want to retrieve data
	 * @return ConfigurationSection containing the data stored for the given
	 *         plugin in this instance
	 */
	public ConfigurationSection getData(String pluginName) {
		ConfigurationSection config = data.get(pluginName);
		if (config == null) {
			config = new YamlConfiguration();
		}
		ConfigurationSection copy = new YamlConfiguration();
		for (String key : config.getKeys(false)) {
			copy.set(key, config.get(key));
		}
		return copy;
	}
	
	public String saveToString() {
		YamlConfiguration config = new YamlConfiguration();
		for(Entry <String, ConfigurationSection> entry : data.entrySet()) {
			config.set(entry.getKey(), entry.getValue());
		}
		return config.saveToString();
	}

	/**
	 * Stores the given data for the given plugin name and ensures that it will
	 * be persisted to the database eventually
	 * 
	 * @param pluginName
	 *            Name of the plugin to which this data belongs
	 * @param config
	 *            Data to save
	 */
	public void putData(String pluginName, ConfigurationSection config) {
		if (pluginName == null || config == null) {
			return;
		}
		dirty = true;
		data.put(pluginName, config);
	}

	/**
	 * Whether this data is dirty, meaning the cache has data which wasnt
	 * persisted to the database yet
	 * 
	 * @return Whether the data is dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty boolean for this data, if set to true the complete data
	 * will be written to the database eventually. You dont need to set dirty
	 * manually when putting data in
	 * 
	 * @param dirty
	 *            New dirty state
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * @return UUID of the player to which this data belongs
	 */
	public UUID getUUID() {
		return uuid;
	}

}
