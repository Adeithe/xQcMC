package gg.xqc.auth.event;

import com.google.gson.GsonBuilder;
import gg.xqc.auth.SpigotPlugin;
import gg.xqc.auth.obj.PlayerData;
import gg.xqc.auth.obj.TokenResponse;
import gg.xqc.core.http.HttpMethod;
import gg.xqc.core.http.HttpRequest;
import gg.xqc.core.http.HttpResponse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class PlayerPreLoginEventListener implements Listener {
	private SpigotPlugin Plugin;
	
	public PlayerPreLoginEventListener(SpigotPlugin plugin) { this.Plugin = plugin; }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		String reason = Plugin.config.getString("Authentication.Messages.unknown-error", "");
		if(reason == null || reason.length() <= 0) reason = "Unknown error.";
		
		try {
			PlayerData data = new PlayerData(event.getUniqueId(), event.getName());
			HttpRequest request = new HttpRequest(HttpMethod.POST, Plugin.getAPI().toParsedString(Plugin.config.getString("API.Endpoints.Authenticate"), data));
			try {
				ConfigurationSection headers = Plugin.config.getConfigurationSection("API.Headers");
				if(headers != null)
					for(String name : headers.getKeys(false))
						request.addHeader(name, Plugin.getAPI().toParsedString(headers.getString(name), data));
			} catch(NullPointerException e) {}
			request.setBody(data);
			HttpResponse response = request.execute();
			TokenResponse token = new GsonBuilder().create().fromJson(response.getBody(), TokenResponse.class);
			if(token.isSuccess) {
				reason = Plugin.config.getString("Authentication.Messages.code-generated", "");
				if(reason == null || reason.length() <= 0) reason = "${Token}";
				reason = reason.replaceAll("(?i)\\$\\{ID\\}", ""+ token.data.Id);
				reason = reason.replaceAll("(?i)\\$\\{TOKEN\\}", token.data.Code);
				reason = Plugin.getAPI().toParsedString(reason, data);
			} else
				reason = String.format("Error: %s (Code: %s)", token.error.message, token.error.status);
		} catch(MalformedURLException e) {
			e.printStackTrace();
			reason += " (MalformedURL)";
		} catch(IOException e) {
			e.printStackTrace();
			reason = Plugin.config.getString("Authentication.Messages.http-error", "");
			if(reason == null || reason.length() <= 0) reason = "Something went wrong while processing the request.";
		}
		reason = ChatColor.translateAlternateColorCodes('&', reason);
		
		event.setKickMessage(reason);
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		event.disallow(event.getLoginResult(), event.getKickMessage());
	}
}
