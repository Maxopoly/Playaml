package com.github.civcraft.playaml;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.civcraft.playaml.commands.PlayamlCommandHandler;
import com.github.civcraft.playaml.data.PlayerDataManager;
import com.github.civcraft.playaml.database.PlayamlDAO;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class PlayamlPlugin extends ACivMod {

    private static PlayamlPlugin instance;
    private PlayamlDAO dao;
    private PlayerDataManager manager;

    public void onEnable() {
		super.onEnable();
		instance = this;
		handle = new PlayamlCommandHandler();
		handle.registerCommands();
		reloadConfig();
    }
    
    public void reloadConfig() {
    	saveDefaultConfig();
		reloadConfig();
		FileConfiguration config = getConfig();
		ManagedDatasource db = (ManagedDatasource) config.get("database");
		this.dao = new PlayamlDAO(db, getLogger());
		this.manager = new PlayerDataManager(this.dao.loadAllData());
    }

    public void onDisable() {
    	this.manager.saveAllData();
    }

    @Override
    protected String getPluginName() {
    	return "Playaml";
    }

    public static PlayamlPlugin getInstance() {
    	return instance;
    }
    
    public static PlayamlDAO getDAO() {
    	return getInstance().dao;
    }
    
    public static PlayerDataManager getManager() {
    	return getInstance().manager;
    }

}
