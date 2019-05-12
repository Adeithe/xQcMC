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
		public String createdAt;
		public String updatedAt;
	}
	
	public static class Error {
		public String code;
		public String message;
		public int status;
	}
}
