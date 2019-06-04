package gg.xqc.paper;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.WorldGuard;
import gg.xqc.paper.cmd.EventsCommand;
import gg.xqc.paper.event.spigot.PlayerEventListener;
import gg.xqc.paper.task.WorldEventManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class PaperPlugin extends JavaPlugin {
	public FileConfiguration config;
	private FileConfiguration data = new YamlConfiguration();
	
	private WorldEdit worldEdit;
	private WorldGuard worldGuard;
	private WorldEventManager eventManager;
	private int taskId = -1;
	
	@Override
	public void onEnable() {
		if(!require("xQc-Core")) return;
		if(!require("WorldEdit")) return;
		if(!require("WorldGuard")) return;
		
		this.reload();
		
		try {
			this.worldEdit = WorldEdit.getInstance();
			this.worldGuard = WorldGuard.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
			this.getLogger().severe("Failed to get WorldEdit/WorldGuard instances!");
			this.getPluginLoader().disablePlugin(this);
			return;
		}
		
		this.getCommand("events").setExecutor(new EventsCommand(this));
		
		this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
		
		this.eventManager = new WorldEventManager(this);
		this.eventManager.call(this.config.getInt("Event.duration", 600));
	}
	
	@Nullable public WorldEdit getWorldEdit() { return this.worldEdit; }
	@Nullable public WorldGuard getWorldGuard() { return this.worldGuard; }
	@Nullable public WorldEventManager getEventManager() { return this.eventManager; }
	public FileConfiguration getData() { return this.data; }
	
	public void reload() {
		if(!getDataFolder().exists())
			getDataFolder().mkdirs();
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			try(InputStream in = getResource("config.yml")) {
				Files.copy(in, config.toPath());
			} catch(NullPointerException | IOException e) {
				e.printStackTrace();
				this.getPluginLoader().disablePlugin(this);
				return;
			}
		}
		this.config = getConfig();
		this.loadData();
	}
	
	public void broadcast(String message) {
		for(Player player : this.getServer().getOnlinePlayers())
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	@Override
	public void onDisable() {
		if(this.getEventManager() != null && !this.getEventManager().isCancelled())
			this.getEventManager().cancel();
		this.getServer().getScheduler().cancelTasks(this);
		this.getLogger().warning("xQc Paper is now disabled!");
	}
	
	private boolean require(String pluginName) {
		if(this.getServer().getPluginManager().getPlugin(pluginName) == null) {
			this.getLogger().severe(pluginName +" is required for xQc Paper to run!");
			this.getPluginLoader().disablePlugin(this);
			return false;
		}
		return true;
	}
	
	private void loadData() {
		try {
			this.data.load(new File(getDataFolder(), "data.yml"));
		} catch(InvalidConfigurationException e) {
			e.printStackTrace();
		} catch(Exception e) {}
	}
	
	public void save() {
		try {
			this.data.save(new File(getDataFolder(), "data.yml"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
