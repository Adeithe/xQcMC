package gg.xqc.paper.event;

import gg.xqc.paper.PaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerEventListener implements Listener {
	private PaperPlugin Plugin;
	
	public PlayerEventListener(PaperPlugin plugin) { this.Plugin = plugin; }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(!Plugin.config.getBoolean("Player.hunger", true)) {
			if(event.getFoodLevel() < 20)
				event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}
}
