package gg.xqc.paper.event;

import gg.xqc.paper.PaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeEventListener implements Listener {
	private PaperPlugin Plugin;
	
	public FoodLevelChangeEventListener(PaperPlugin plugin) { this.Plugin = plugin; }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(Plugin.config.getBoolean("disable-hunger", false)) {
			if(event.getFoodLevel() < 20)
				event.setFoodLevel(20);
			event.setCancelled(true);
		}
	}
}
