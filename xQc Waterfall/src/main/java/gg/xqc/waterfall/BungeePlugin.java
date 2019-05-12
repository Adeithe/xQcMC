package gg.xqc.waterfall;

import gg.xqc.waterfall.event.LoginEventListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeePlugin extends Plugin {
	public Configuration config;
	
	private PluginAPI API;
	
	@Override
	public void onEnable() {
		if(getProxy().getPluginManager().getPlugin("xQc-Core") == null) {
			getLogger().severe("xQc Core is required for xQc Waterfall to run!");
			onDisable();
			return;
		}
		
		if(!getDataFolder().exists())
			getDataFolder().mkdirs();
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			try(InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, config.toPath());
			} catch(IOException e) {
				e.printStackTrace();
				onDisable();
				return;
			}
		}
		
		if(!loadConfig()) {
			onDisable();
			return;
		}
		
		this.API = new PluginAPI(this);
		
		getProxy().getPluginManager().registerListener(this, new LoginEventListener(this));
		getLogger().info("xQc Waterfall is now enabled!");
	}
	
	@Override
	public void onDisable() {
		this.config = null;
		getProxy().getPluginManager().unregisterCommands(this);
		getProxy().getPluginManager().unregisterListeners(this);
		getLogger().warning("xQc Waterfall is now disabled!");
	}
	
	public PluginAPI getAPI() { return this.API; }
	
	public boolean loadConfig() {
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
			return true;
		} catch(IOException e) { e.printStackTrace(); }
		return false;
	}
	
	public boolean saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
			return true;
		} catch(IOException e) { e.printStackTrace(); }
		return false;
	}
}
