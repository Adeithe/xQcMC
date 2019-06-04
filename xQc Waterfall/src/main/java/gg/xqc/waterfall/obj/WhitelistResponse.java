package gg.xqc.waterfall.obj;

import com.google.gson.annotations.SerializedName;

public class WhitelistResponse {
	@SerializedName("success") public boolean isSuccess;
	public Data data;
	public Error error;
	
	public static class Data {
		public int id;
		public String twitchId;
		public String minecraftUuid;
		public String minecraftUsername;
		public boolean isSubbed;
		@SerializedName("typeUpper") public Type type;
		public String createdAt;
		public String updatedAt;
		
		public enum Type {
			OWNER(500),
			ADMIN(400),
			MOD(300),
			STREAMER(200),
			VIP(100),
			USER(0);
			
			private int level;
			Type(int level) { this.level = level; }
			
			public int getLevel() { return this.level; }
		}
	}
	
	public static class Error {
		public String code;
		public String message;
		public int status;
	}
}
