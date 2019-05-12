package gg.xqc.auth;

import gg.xqc.auth.event.PlayerPreLoginEventListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SpigotPlugin extends JavaPlugin {
	public FileConfiguration config;
	
	private PluginAPI API;
	
	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("xQc-Core") == null) {
			getLogger().severe("xQc Core is required for xQc Waterfall to run!");
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
		this.API = new PluginAPI(this);
		
		getServer().getPluginManager().registerEvents(new PlayerPreLoginEventListener(this), this);
	}
	
	@Override
	public void onDisable() {
		getLogger().warning("xQc Auth is now disabled!");
	}
	
	public PluginAPI getAPI() { return this.API; }
}
