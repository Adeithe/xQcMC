package gg.xqc.paper.event;

import gg.xqc.paper.PaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobEventListener implements Listener {
	private PaperPlugin Plugin;
	
	public MobEventListener(PaperPlugin plugin) { this.Plugin = plugin; }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if(Plugin.config.getBoolean("Creatures.require-spawners", false))
			if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER)
				event.setCancelled(true);
		event.getEntity().setAI(Plugin.config.getBoolean("Creatures.allow-ai", true));
	}
}
