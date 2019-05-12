package gg.xqc.core.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	@Getter private HttpMethod method;
	@Getter private URL url;
	@Getter private HashMap<String, String> headers = new HashMap<>();
	@Getter private HashMap<String, Object> params = new HashMap<>();
	private String body;
	
	public HttpRequest(HttpMethod method, String url) throws MalformedURLException {
		this.method = method;
		if(url != null) {
			if(!url.contains("?")) url += "?";
			this.url = new URL(url);
		}
	}
	
	public HttpRequest setMethod(HttpMethod method) {
		this.method = method;
		return this;
	}
	
	public HttpRequest setURL(URL url) {
		this.url = url;
		return this;
	}
	
	public HttpRequest setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
		return this;
	}
	
	public HttpRequest addHeader(String name, String value) {
		this.headers.put(name, value);
		return this;
	}
	
	public HttpRequest setParams(HashMap<String, Object> params) {
		this.params = params;
		return this;
	}
	
	public HttpRequest addParam(String name, Object value) {
		this.params.put(name, value);
		return this;
	}
	
	public HttpRequest setBody(Object body) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return this.setBody(gson.toJson(body));
	}
	
	public HttpRequest setBody(String body) {
		this.body = body;
		return this;
	}
	
	public HttpResponse execute() throws IOException {
		StringBuilder response = new StringBuilder();
		if(this.params != null && this.params.size() > 0) {
			StringBuilder params = new StringBuilder();
			for(Map.Entry<String, Object> param : this.params.entrySet()) {
				if(params.length() != 0) params.append("&");
				params.append(param.getKey());
				params.append("=");
				params.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			try {
				String url = this.url.toString();
				if(!url.endsWith("?")) url += "&";
				this.url = new URL(url + params.toString());
			} catch(MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
		HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
		con.setRequestMethod(this.method.toString());
		if(this.headers != null && this.headers.size() > 0)
			for(Map.Entry<String, String> header : this.headers.entrySet())
				con.setRequestProperty(header.getKey(), header.getValue());
		switch(this.method) {
			case POST:
				{
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Content-Length", Integer.toString(this.body.getBytes().length));
					con.setDoOutput(true);
					OutputStream os = con.getOutputStream();
					os.write(this.body.getBytes());
					os.flush();
					os.close();
				}
			break;
		}
		int code = con.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader((code < HttpURLConnection.HTTP_BAD_REQUEST)?con.getInputStream():con.getErrorStream()));
		String line;
		while((line = br.readLine()) != null)
			response.append(line);
		br.close();
		return new HttpResponse(code, response.toString());
	}
}
