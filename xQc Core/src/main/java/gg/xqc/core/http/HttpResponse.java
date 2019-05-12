package gg.xqc.core.http;

import lombok.Getter;

public class HttpResponse {
	@Getter private int statusCode;
	@Getter private String body;
	
	public HttpResponse(int code, String body) {
		this.statusCode = code;
		this.body = body;
	}
}
