package gg.xqc.paper.event;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import gg.xqc.paper.task.WorldEventManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class SpleefEvent extends IEvent {
	private BukkitWorld world;
	
	public SpleefEvent(WorldEventManager em) { super(em); }
	
	@Override
	public void onStart() {
		this.getWorldEventManager().broadcast("&aA Spleef event has started!");
		this.world = this.getWorldEventManager().getFloorWorld();
		CuboidRegion floor = this.getWorldEventManager().getFloor();
		if(floor == null) {
			this.getWorldEventManager().end();
			return;
		}
		floor.forEach(this::setSnow);
	}
	
	private void setSnow(BlockVector3 block) {
		if(this.world != null) {
			World world = this.world.getWorld();
			Block b = world.getBlockAt(new Location(world, block.getX(), block.getY(), block.getZ()));
			b.setType(Material.SNOW_BLOCK);
			b.getDrops().clear();
		}
	}
	
	@Override
	public boolean onPlayerBlockBreak(BlockBreakEvent e, BukkitPlayer player) {
		if(e.isCancelled() && e.getBlock().getType() == Material.SNOW_BLOCK) {
			e.setDropItems(false);
			return false;
		}
		return super.onPlayerBlockBreak(e, player);
	}
	
	@Override
	public boolean onPvP(EntityDamageByEntityEvent e, BukkitPlayer attacker, BukkitPlayer target) {
		return true;
	}
	
	@Override
	public void onFinish() {
		this.getWorldEventManager().broadcast("&cThe Spleef event has ended!");
	}
}
