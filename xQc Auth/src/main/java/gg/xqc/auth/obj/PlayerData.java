package gg.xqc.auth.obj;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class PlayerData {
	@SerializedName("uuid") public String UniqueId;
	@SerializedName("username") public String Name;
	
	public PlayerData() {}
	public PlayerData(UUID uuid, String name) {
		this.UniqueId = uuid.toString();
		this.Name = name;
	}
}
