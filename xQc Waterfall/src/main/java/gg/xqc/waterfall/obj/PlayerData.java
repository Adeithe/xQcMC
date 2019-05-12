package gg.xqc.waterfall.obj;

import com.google.gson.annotations.SerializedName;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerData {
	@SerializedName("uuid") public String UniqueId;
	@SerializedName("name") public String Name;
	@SerializedName("has_forge") public boolean hasForge;
	
	public PlayerData() {}
	public PlayerData(ProxiedPlayer player) {
		this.UniqueId = player.getUniqueId().toString();
		this.Name = player.getName();
		this.hasForge = player.isForgeUser();
	}
}
