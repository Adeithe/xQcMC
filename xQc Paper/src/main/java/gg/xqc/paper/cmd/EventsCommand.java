package gg.xqc.paper.cmd;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.xqc.paper.PaperPlugin;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsCommand extends Executable {
	public EventsCommand(PaperPlugin plugin) { super(plugin); }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			if(!sender.isOp()) {
				this.sendMessage(sender, "&cYou must be a server operator to run this command!");
				return true;
			}
			
			try {
				BukkitPlayer player = new BukkitPlayer((Player) sender);
				if(args.length > 0) {
					switch(args[0]) {
						case "force":
							if(this.getPlugin().getEventManager() != null) {
								this.sendMessage(sender, "&aForcing an event to start...");
								this.getPlugin().getEventManager().next(true);
							} else this.sendMessage(sender, "&cUnable to host events! Please restart the server.");
						break;
						
						case "reload":
							this.getPlugin().reload();
						break;
						
						case "set":
							if(args.length > 1) {
								switch(args[1]) {
									case "floor":
										if(this.getPlugin().getEventManager() != null) {
											try {
												Region selection = this.getSelection(player);
												if(selection != null) {
													if(selection.getWorld() != null)
														this.getPlugin().getData().set("Data.Floor.World", selection.getWorld().getName());
													YamlConfiguration positions = new YamlConfiguration();
													positions.set("X1", selection.getMinimumPoint().getX());
													positions.set("Y1", selection.getMinimumPoint().getY());
													positions.set("Z1", selection.getMinimumPoint().getZ());
													positions.set("X2", selection.getMaximumPoint().getX());
													positions.set("Y2", selection.getMaximumPoint().getY());
													positions.set("Z2", selection.getMaximumPoint().getZ());
													this.getPlugin().getData().set("Data.Floor.Positions", positions);
													this.getPlugin().save();
													if(!this.getPlugin().getEventManager().backup()) {
														this.getPlugin().getLogger().severe("Configuration seems to be invalid! Skipping event...");
														return true;
													}
													this.sendMessage(sender, "&aFloor was set successfully!");
												} else this.sendMessage(sender, "&cYou must select a region first!");
											} catch(IncompleteRegionException e) {
												player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour region selection is invalid!"));
											}
										} else this.sendMessage(sender, "&cUnable to host events! Please restart the server.");
									break;
									
									case "arena":
										if(args.length > 2) {
											ProtectedRegion region = this.getPlugin().getEventManager().getRegionPlayerIsInByName(player, args[2]);
											if(region != null) {
												YamlConfiguration arena = new YamlConfiguration();
												arena.set("World", player.getWorld().getName());
												arena.set("ID", region.getId());
												this.getPlugin().getData().set("Data.Arena", arena);
												this.getPlugin().save();
												this.sendMessage(sender, "&aNow using region &l&c"+ region.getId() +"&r&a as the arena!");
											} else this.sendMessage(sender, "&cYou must be inside the region you want to use!");
										} else this.sendMessage(sender, "&cYou must specify a region!");
									break;
								}
							}
						break;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				this.sendMessage(sender, "&cSomething went wrong!");
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tabs = new ArrayList<>();
		if(sender instanceof Player) {
			if(args.length > 1) {
				BukkitPlayer player = new BukkitPlayer((Player) sender);
				switch(args[0]) {
					case "set":
						if(args.length > 2) {
							switch(args[1]) {
								case "arena":
									if(args.length > 3) {
										ApplicableRegionSet regionSet = this.getPlugin().getEventManager().getRegionsForPlayer(player);
										if(regionSet != null && regionSet.getRegions().size() > 0)
											for(ProtectedRegion region : regionSet.getRegions())
												tabs.add(region.getId());
									}
								break;
							}
						} else return Arrays.asList("arena", "floor");
					break;
				}
			} else return Arrays.asList("force", "reload", "set");
		}
		return tabs;
	}
	
	private Region getSelection(BukkitPlayer player) throws IncompleteRegionException {
		try {
			LocalSession session = this.getPlugin().getWorldEdit().getSessionManager().get(player);
			return session.getSelection(session.getSelectionWorld());
		} catch(NullPointerException e) {}
		return null;
	}
	
	private void sendMessage(CommandSender sender, String message) { sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)); }
	private void sendMessage(BukkitPlayer player, String message) { this.sendMessage(player.getPlayer(), message); }
	private void sendMessage(Player player, String message) { player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)); }
}
