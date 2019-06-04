package gg.xqc.paper.event;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import gg.xqc.paper.PaperPlugin;
import gg.xqc.paper.task.WorldEventManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class IEvent {
	private WorldEventManager eventManager;
	private boolean active;
	
	public IEvent(WorldEventManager em) {
		this.eventManager = em;
	}
	
	public final void start() { this.active = true; this.onStart(); }
	public final void finish() { this.active = false; this.onFinish(); }
	
	public abstract void onStart();
	public abstract void onFinish();
	
	public boolean onPlayerBlockPlace(BlockPlaceEvent e, BukkitPlayer player) { return e.isCancelled(); }
	public boolean onPlayerBlockBreak(BlockBreakEvent e, BukkitPlayer player) { return e.isCancelled(); }
	public boolean onPvP(EntityDamageByEntityEvent e, BukkitPlayer attacker, BukkitPlayer target) { return e.isCancelled(); }
	
	public PaperPlugin getPlugin() { return this.eventManager.getPlugin(); }
	public WorldEventManager getWorldEventManager() { return this.eventManager; }
	public boolean isActive() { return this.active; }
}
