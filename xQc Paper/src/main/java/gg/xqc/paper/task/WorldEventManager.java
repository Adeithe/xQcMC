package gg.xqc.paper.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.blacklist.event.BlockBreakBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemUseBlacklistEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import gg.xqc.paper.event.SpleefEvent;
import jdk.nashorn.internal.ir.Flags;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.jetbrains.annotations.Nullable;
import gg.xqc.paper.PaperPlugin;
import gg.xqc.paper.event.IEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

public class WorldEventManager implements Runnable, Listener {
	private PaperPlugin Plugin;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private BukkitTask task;
	private ProtectedRegion arena;
	private IEvent event;
	private boolean forced;
	
	private List<IEvent> events = new ArrayList<>();
	
	public WorldEventManager(PaperPlugin plugin) {
		this.Plugin = plugin;
		this.getPlugin().getServer().getPluginManager().registerEvents(this, this.getPlugin());
		this.restore();
		this.reloadArena();
		
		this.events.add(new SpleefEvent(this));
	}
	
	public PaperPlugin getPlugin() { return this.Plugin; }
	public RegionContainer getRegionContainer() { return this.getPlugin().getWorldGuard().getPlatform().getRegionContainer(); }
	@Nullable public IEvent getActiveEvent() { return this.event; }
	@Nullable public ProtectedRegion getArena() { return this.arena; }
	public boolean isForced() { return this.forced; }
	
	public void broadcast(String message) { if(message != null) this.getPlugin().broadcast("&a&l[Event]&r "+ message); }
	
	@Override
	public void run() {
		if(this.getPlugin() == null) return;
		if(this.event == null) {
			this.reloadArena();
			this.cancel();
			double p = Math.random() * 100;
			if(this.isForced() || p < this.getPlugin().config.getDouble("Event.chance-in-percent", 15)) {
				this.getPlugin().getLogger().info("Starting a new random event...");
				this.event = this.events.get(new Random().nextInt(this.events.size()));
				this.event.start();
				this.call(this.getPlugin().config.getInt("Event.duration", 600));
				return;
			}
		}
		this.next();
	}
	
	public void next() { next(false); }
	public void next(boolean force) {
		this.forced = force;
		if(this.event != null) {
			this.getPlugin().getLogger().info("Ending the current random event...");
			this.event.finish();
			this.forced = false;
			this.event = null;
			this.restore();
		} else this.cancel();
		this.call(force?1:(this.getPlugin().config.getInt("Event.interval", 900)));
	}
	
	public void end() { if(this.event != null) this.next(true); }
	
	public void call(long delay) {
		if(this.task != null) this.cancel();
		if(delay <= 0) delay = 1;
		this.task = this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), this, delay * 20);
	}
	
	public boolean isCancelled() {
		if(this.task == null) return true;
		return this.task.isCancelled();
	}
	
	public void cancel() {
		if(this.task == null) return;
		this.getPlugin().getServer().getScheduler().cancelTask(this.task.getTaskId());
		this.task = null;
	}
	
	public BukkitWorld getFloorWorld() {
		FileConfiguration data = this.getPlugin().getData();
		String worldName = data.getString("Data.Floor.World", null);
		if(worldName == null) worldName = this.getPlugin().getServer().getWorlds().get(0).getName();
		return new BukkitWorld(this.getPlugin().getServer().getWorld(worldName));
	}
	
	@Nullable
	public CuboidRegion getFloor() {
		FileConfiguration data = this.getPlugin().getData();
		BukkitWorld world = this.getFloorWorld();
		BlockVector3 pos1 = BlockVector3.at(data.getInt("Data.Floor.Positions.X1", 0), data.getInt("Data.Floor.Positions.Y1", 0), data.getInt("Data.Floor.Positions.Z1", 0));
		BlockVector3 pos2 = BlockVector3.at(data.getInt("Data.Floor.Positions.X2", 0), data.getInt("Data.Floor.Positions.Y2", 0), data.getInt("Data.Floor.Positions.Z2", 0));
		if(pos1.distance(pos2) == 0 || pos1.distance(pos2) > this.getPlugin().config.getDouble("max-distance", 250.0)) return null;
		return new CuboidRegion(world, pos1, pos2);
	}
	
	public void reloadArena() {
		String arenaWorld = this.getPlugin().getData().getString("Data.Arena.World");
		String arenaId = this.getPlugin().getData().getString("Data.Arena.ID");
		if(arenaWorld != null && arenaId != null) {
			RegionManager manager = this.getRegionContainer().get(new BukkitWorld(this.getPlugin().getServer().getWorld(arenaWorld)));
			if(manager != null)
				this.arena = manager.getRegion(arenaId);
		}
	}
	
	public boolean backup() {
		CuboidRegion region = this.getFloor();
		if(region == null) return false;
		List<BlockData> blocks = new ArrayList<>();
		World world = this.getFloorWorld().getWorld();
		region.forEach((block) -> blocks.add(new BlockData(world.getBlockAt(new Location(world, block.getX(), block.getY(), block.getZ())))));
		try {
			FileWriter writer = new FileWriter(new File(this.getPlugin().getDataFolder(), "floor.json"));
			gson.toJson(blocks, writer);
			writer.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			this.getPlugin().getLogger().severe("Failed to save backup data!");
		}
		return false;
	}
	
	public void restore() {
		try {
			File file = new File(this.getPlugin().getDataFolder(), "floor.json");
			List<BlockData> blocks = gson.fromJson(new FileReader(file), new TypeToken<List<BlockData>>(){}.getType());
			BukkitWorld world = this.getFloorWorld();
			for(BlockData block : blocks) {
				Block b = world.getWorld().getBlockAt(new Location(world.getWorld(), block.x, block.y, block.z));
				b.setType(block.type);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Nullable public ApplicableRegionSet getRegionsForPlayer(Player player) { return this.getRegionsForPlayer(new BukkitPlayer(player)); }
	@Nullable public ApplicableRegionSet getRegionsForPlayer(BukkitPlayer player) {
		RegionManager manager = this.getRegionContainer().get(player.getWorld());
		if(manager == null) return null;
		return manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
	}
	
	public ProtectedRegion getRegionPlayerIsInByName(BukkitPlayer player, String name) {
		ApplicableRegionSet regionSet = this.getRegionsForPlayer(player);
		if(regionSet != null) {
			Set<ProtectedRegion> regions = regionSet.getRegions();
			if(regions.size() > 0) {
				ProtectedRegion region = null;
				for(ProtectedRegion r : regions)
					if(r.getId().equalsIgnoreCase(name)) region = r;
				if(region != null)
					return region;
			}
		}
		return null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		if(this.getActiveEvent() == null) return;
		BukkitPlayer player = new BukkitPlayer(e.getPlayer());
		ApplicableRegionSet regions = this.getRegionsForPlayer(player);
		if(regions != null) {
			for(ProtectedRegion region : regions.getRegions()) {
				if(this.getArena() != null && region.getId().equalsIgnoreCase(this.getArena().getId())) {
					if(region.contains(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))
						e.setCancelled(this.getActiveEvent().onPlayerBlockPlace(e, player));
					break;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		if(this.getActiveEvent() == null) return;
		BukkitPlayer player = new BukkitPlayer(e.getPlayer());
		ApplicableRegionSet regions = this.getRegionsForPlayer(player);
		if(regions != null) {
			for(ProtectedRegion region : regions.getRegions()) {
				if(this.getArena() != null && region.getId().equalsIgnoreCase(this.getArena().getId())) {
					if(region.contains(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))
						e.setCancelled(this.getActiveEvent().onPlayerBlockBreak(e, player));
					break;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPvP(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
		BukkitPlayer attacker = new BukkitPlayer((Player) e.getDamager());
		BukkitPlayer target = new BukkitPlayer((Player) e.getEntity());
		ApplicableRegionSet attackerRegions = this.getRegionsForPlayer(attacker);
		ApplicableRegionSet targetRegions = this.getRegionsForPlayer(target);
		boolean targetIsInArena = false;
		if(targetRegions != null) {
			for(ProtectedRegion region : targetRegions.getRegions()) {
				if(this.getArena() != null && region.getId().equalsIgnoreCase(this.getArena().getId())) {
					targetIsInArena = true;
					break;
				}
			}
		}
		if(attackerRegions != null) {
			for(ProtectedRegion region : attackerRegions.getRegions()) {
				if(targetIsInArena && this.getArena() != null && region.getId().equalsIgnoreCase(this.getArena().getId())) {
					this.getPlugin().getLogger().info("Found region "+ region.getId());
					if(e.isCancelled()) {
						if(this.getActiveEvent() != null) e.setCancelled(this.getActiveEvent().onPvP(e, attacker, target));
						else e.setCancelled(false);
					}
					break;
				}
			}
		}
	}
	
	public static class BlockData {
		public double x;
		public double y;
		public double z;
		public Material type;
		
		public BlockData() {}
		public BlockData(Block block) {
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
			this.type = block.getType();
		}
	}
}
