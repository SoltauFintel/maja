package de.mwvb.maja.rest;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RestCaller {

	/** Create */
	public String post(String url, String body) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(body));
		return request(url, post);
	}

	/** Create object */
	public String post(String url, Object object) throws IOException {
		String json = new Gson().toJson(object);
		return post(url, json);
	}

	/** Read JSON */
	public String get(String url) throws IOException {
		return request(url, new HttpGet(url));
	}

	/** Read object */
	public <T> T get(String url, Class<T> clazz) throws IOException {
		String json = get(url);
		return new Gson().fromJson(json, clazz);
	}

	/** Update */
	public String put(String url, String body) throws IOException {
		HttpPut put = new HttpPut(url);
		put.setEntity(new StringEntity(body));
		return request(url, put);
	}
	
	/** Update object */
	public String put(String url, Object object) throws IOException {
		String json = new Gson().toJson(object);
		return put(url, json);
	}

	/** Delete */
	public String delete(String url) throws IOException {
		return request(url, new HttpDelete(url));
	}
	
	private String request(String url, HttpRequest request) throws IOException {
		try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
			URI uri = URI.create(url);
			HttpResponse response = httpClient.execute(
					new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()),
					request,
					HttpClientContext.create());
			
			// Error handling
			final int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.INTERNAL_SERVER_ERROR_500) {
				String json = EntityUtils.toString(response.getEntity());
				try {
					ErrorMessage msg = new Gson().fromJson(json, ErrorMessage.class);
					throw new RestException(msg);
				} catch (JsonSyntaxException fallthru) {
				}
			}
			if (status != HttpStatus.OK_200) {
				throw new RestStatusException(status);
			}
			
			// Return response as String or JSON.
			return EntityUtils.toString(response.getEntity());
		}
	}
}
