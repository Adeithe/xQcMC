package gg.xqc.paper;

import gg.xqc.paper.event.FoodLevelChangeEventListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class PaperPlugin extends JavaPlugin {
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("xQc-Core") == null) {
			getLogger().severe("xQc Core is required for xQc Paper to run!");
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		if(!getDataFolder().exists())
			getDataFolder().mkdirs();
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			try(InputStream in = getResource("config.yml")) {
				Files.copy(in, config.toPath());
			} catch(NullPointerException | IOException e) {
				e.printStackTrace();
				getPluginLoader().disablePlugin(this);
				return;
			}
		}
		
		this.config = getConfig();
		
		getServer().getPluginManager().registerEvents(new FoodLevelChangeEventListener(this), this);
	}
	
	@Override
	public void onDisable() {
		getLogger().warning("xQc Paper is now disabled!");
	}
}
