package ch.hsr.traildevil.util;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpHandler {

	private HttpEntity entity;
	private HttpClient client;
	private HttpResponse response;
	private HttpGet httpGet;
	private InputStreamReader isr;

	public HttpHandler() {
		client = new DefaultHttpClient();
	}

	public void connectTo(String url) {
		try {
			httpGet = new HttpGet(url);
			httpGet.setHeader("Accept", "application/json");
			response = client.execute(httpGet);
			entity = response.getEntity();
			isr = new InputStreamReader(entity.getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputStreamReader getReader() {
		try {
			if(isr != null) return isr;
			if(entity != null){
				isr = new InputStreamReader(entity.getContent());
				return isr;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void resetStream() {
		if (isr != null) {
			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
