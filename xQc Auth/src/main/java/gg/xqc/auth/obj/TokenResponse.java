package gg.xqc.auth.obj;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
	@SerializedName("success") public boolean isSuccess;
	public Data data;
	public Error error;
	
	public static class Data {
		@SerializedName("id") public int Id;
		@SerializedName("uuid") public String UniqueId;
		@SerializedName("username") public String Name;
		@SerializedName("token") public String Code;
		public String createdAt;
		public String updatedAt;
	}
	
	public static class Error {
		public String code;
		public String message;
		public int status;
	}
}
