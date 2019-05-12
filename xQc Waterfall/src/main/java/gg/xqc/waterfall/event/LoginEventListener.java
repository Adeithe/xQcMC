package gg.xqc.waterfall.event;

import com.google.gson.GsonBuilder;
import gg.xqc.core.http.HttpMethod;
import gg.xqc.core.http.HttpRequest;
import gg.xqc.core.http.HttpResponse;
import gg.xqc.waterfall.BungeePlugin;
import gg.xqc.waterfall.obj.WhitelistResponse;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;

public class LoginEventListener implements Listener {
	private BungeePlugin Plugin;
	
	public LoginEventListener(BungeePlugin plugin) { this.Plugin = plugin; }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PostLoginEvent event) {
		if(!Plugin.config.getBoolean("Whitelist.Enabled")) return;
		try {
			String url = Plugin.getAPI().toParsedString(Plugin.config.getString("API.Endpoints.Validate", null), event.getPlayer());
			if(url == null) {
				event.getPlayer().disconnect(getReason());
				return;
			}
			HttpRequest request = new HttpRequest(HttpMethod.GET, url);
			Configuration headers = Plugin.config.getSection("API.Headers");
			String[] keys = headers.getKeys().toArray(new String[]{});
			for(int i = 0; i < keys.length; i++)
				request.addHeader(keys[i], Plugin.getAPI().toParsedString(headers.getString(keys[i], ""), event.getPlayer()));
			HttpResponse response = request.execute();
			if(response == null) {
				event.getPlayer().disconnect(getReason());
				return;
			}
			Plugin.getLogger().info(String.format("Retrieved whitelist status for %s (Status: %s)", event.getPlayer().getName(), response.getStatusCode()));
			Plugin.getLogger().info(response.getBody().replaceAll("\\s+", " "));
			WhitelistResponse whitelist = new GsonBuilder().create().fromJson(response.getBody(), WhitelistResponse.class);
			if(response.getStatusCode() == 401) {
				String reason = Plugin.config.getString("Whitelist.Messages.not-whitelisted", "");
				if(!whitelist.error.code.equalsIgnoreCase("user_not_whitelisted"))
					reason = whitelist.error.message;
				event.getPlayer().disconnect(getReason(reason));
			} else if(response.getStatusCode() != 200 || !whitelist.isSuccess)
				event.getPlayer().disconnect(getReason());
		} catch(IOException e) {
			e.printStackTrace();
			event.getPlayer().disconnect(getReason());
		}
	}
	
	private TextComponent getReason() { return getReason(Plugin.config.getString("Whitelist.Messages.http-error", "No reason")); }
	private TextComponent getReason(String reason) {
		if(reason == null) reason = "No reason provided.";
		reason = ChatColor.translateAlternateColorCodes('&', reason);
		return new TextComponent(reason);
	}
}
