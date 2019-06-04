package gg.xqc.paper.cmd;

import gg.xqc.paper.PaperPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class Executable implements CommandExecutor, TabCompleter {
	private PaperPlugin Plugin;
	
	public Executable(PaperPlugin plugin) { this.Plugin = plugin; }
	
	public PaperPlugin getPlugin() { return this.Plugin; }
}
