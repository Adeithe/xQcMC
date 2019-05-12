package gg.xqc.auth;

import gg.xqc.auth.obj.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginAPI {
	private SpigotPlugin Plugin;
	
	public PluginAPI(SpigotPlugin plugin) { this.Plugin = plugin; }
	
	public String toParsedString(String s, PlayerData data) {
		if(s != null) {
			List<String> envars = new ArrayList<>();
			Matcher env = Pattern.compile("(?i)\\$\\{ENV\\.([0-9a-zA-Z_]+)\\}").matcher(s);
			while(env.find())
				envars.add(env.group());
			for(String envar : envars) {
				String var = envar.replaceAll("[\\$\\{\\}]", "");
				if(envar.contains(".")) {
					String _envar = System.getenv(var.split("\\.")[1]);
					if(_envar == null) _envar = "";
					s = s.replaceAll(Pattern.quote(envar), _envar);
				}
			}
			
			s = s.replaceAll("(?i)\\$\\{UUID\\}", data.UniqueId);
			s = s.replaceAll("(?i)\\$\\{NAME\\}", data.Name);
		}
		return s;
	}
}
