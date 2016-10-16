package com.github.civcraft.playaml.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.civcraft.playaml.PlayamlPlugin;
import com.github.civcraft.playaml.database.PlayamlDAO;

public class PlayerDataManager {

	private Map<UUID, PlayerData> datas;

	public PlayerDataManager(Map<UUID, byte[]> data) {
		for (Entry<UUID, byte[]> entry : data.entrySet()) {
			UUID uuid = entry.getKey();
			YamlConfiguration yaml = new YamlConfiguration();
			try {
				yaml.loadFromString(decompress(entry.getValue()));
			} catch (InvalidConfigurationException e) {
				PlayamlPlugin.getInstance().warning("Failed to load data for " + uuid.toString(), e);
			}
			datas.put(uuid, new PlayerData(uuid, yaml));
		}
	}

	/**
	 * Gets the player data for a specific player
	 * 
	 * @param player
	 *            Player to get data for
	 * @return Data stored for the given uuid if any exists or a blank set of
	 *         data if no previous record exists. If the given uuid is null,
	 *         null will also be returned
	 */
	public PlayerData getData(UUID player) {
		if (player == null) {
			return null;
		}
		PlayerData data = datas.get(player);
		if (data == null) {
			data = new PlayerData(player, new YamlConfiguration());
			datas.put(player, data);
		}
		return data;
	}

	/**
	 * Saves all data marked as dirty to the database
	 */
	public void saveAllData() {
		PlayamlDAO dao = PlayamlPlugin.getDAO();
		for (Entry<UUID, PlayerData> entry : datas.entrySet()) {
			PlayerData data = entry.getValue();
			if (data.isDirty()) {
				dao.updateData(entry.getKey(), compress(data.saveToString()));
				data.setDirty(false);
			}
		}
	}

	/**
	 * Compresses a given String to a byte array using gzip
	 * 
	 * @param str
	 *            String to compress
	 * @return Compressed string
	 */
	public static byte[] compress(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length());
		byte[] compressed = null;
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(str.getBytes());
			gzip.close();
			compressed = bos.toByteArray();
			bos.close();
		} catch (IOException e) {
			PlayamlPlugin.getInstance().warning("Failed to compact string " + str + " ; " + e);
		}
		return compressed;
	}

	/**
	 * Decompresses a given byte array to a string using gzip
	 * 
	 * @param data
	 *            Data to decompress
	 * @return Resulting string
	 */
	public static String decompress(byte[] data) {
		StringBuilder sb = new StringBuilder();
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
			GZIPInputStream gis = new GZIPInputStream(bis);
			BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			gis.close();
			bis.close();
		} catch (IOException e) {
			PlayamlPlugin.getInstance().warning("Failed to decompact data ; " + e);
		}
		return sb.toString();
	}
}
