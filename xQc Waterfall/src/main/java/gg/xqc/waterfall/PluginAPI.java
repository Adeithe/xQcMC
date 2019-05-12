package gg.xqc.waterfall;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginAPI {
	private BungeePlugin Plugin;
	
	public PluginAPI(BungeePlugin plugin) { this.Plugin = plugin; }
	
	public boolean isWhitelisted() { return false; }
	
	public String toParsedString(String s, ProxiedPlayer player) {
		if(s != null) {
			List<String> envars = new ArrayList<>();
			Matcher env = Pattern.compile("(?i)\\$\\{ENV\\.([0-9a-zA-Z_]+)\\}").matcher(s);
			while(env.find())
				envars.add(env.group());
			for(String envar : envars) {
				try {
					String var = envar.replaceAll("[\\$\\{\\}]", "");
					if(envar.contains(".")) {
						String _envar = System.getenv(var.split("\\.")[1]);
						if(_envar == null) _envar = "";
						s = s.replaceAll(Pattern.quote(envar), _envar);
					}
				} catch(Exception e) {}
			}
			
			if(player != null) {
				s = s.replaceAll("(?i)\\$\\{UUID\\}", player.getUniqueId().toString());
				s = s.replaceAll("(?i)\\$\\{NAME\\}", player.getName());
			}
		}
		return s;
	}
}
