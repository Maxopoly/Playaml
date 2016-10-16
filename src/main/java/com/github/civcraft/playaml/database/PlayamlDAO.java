package com.github.civcraft.playaml.database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.civcraft.playaml.PlayamlPlugin;

import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class PlayamlDAO {
	
	private ManagedDatasource db;
	private Logger logger;
	
	private static final String updateData = "insert into playamlData (uuid, data) values(?,?) on duplicate key update data = values(data);";
	private static final String loadAllData = "select uuid, data from playamlData;";
	
	public PlayamlDAO(ManagedDatasource db, Logger logger) {
		this.db = db;
		this.logger = logger;
		registerMigrations();
		db.updateDatabase();
	}
	
	private void registerMigrations() {
		db.registerMigration(0, false, 
				"create table if not exists playamlData (uuid varchar(36) not null, data blob not null, unique(uuid));"
		);
	}
	
	public void updateDataAsync(UUID uuid, byte [] data) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				updateData(uuid, data);				
			}
		}.runTaskAsynchronously(PlayamlPlugin.getInstance());
	}
	
	public void updateData(UUID uuid, byte [] data) {
		try (Connection connection = db.getConnection();
				PreparedStatement updateStatement = connection.prepareStatement(updateData)) {
			updateStatement.setString(1, uuid.toString());
			updateStatement.setBlob(2, new SerialBlob(data));
			updateStatement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to update data for " + uuid.toString(), e);
		}
	}
	
	public Map <UUID, byte []> loadAllData() {
		Map <UUID, byte []> data = new HashMap<UUID, byte[]>();
		try (Connection connection = db.getConnection();
				PreparedStatement loadStatement = connection.prepareStatement(loadAllData);
					ResultSet allData = loadStatement.executeQuery()) {
			while (allData.next()) {
				UUID uuid = UUID.fromString(allData.getString(1));
				Blob blob = allData.getBlob(2);
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				data.put(uuid, blobAsBytes);
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to load all data", e);
		}
		return data;
	}
}
