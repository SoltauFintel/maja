package de.mwvb.maja.auth.facebook;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.codehaus.jackson.map.ObjectMapper;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import de.mwvb.maja.web.ActionBase;
import spark.Request;

/**
 * Facebook replies to this action/route.
 */
public class FacebookCallbackAction extends ActionBase {
	private static final String KEY = "state";

	@Override
	public String run() {
		String key = req.queryParams(KEY);
		FacebookHandle handle = FacebookLoginAction.pop(key);
		if (handle == null) {
			throw new RuntimeException("Login tooks too long!");
			// host= Eintrag in AppConfig.properties könnte falsch sein.
		}
		try {
			return login(req, res, handle);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String login(Request req, spark.Response res, FacebookHandle h) throws IOException, InterruptedException, ExecutionException {
		String code = req.queryParams("code");
		OAuth20Service oauth = h.getOauth();
		OAuth2AccessToken accessToken = oauth.getAccessToken(code);
		OAuthRequest request = new OAuthRequest(Verb.GET, h.getFacebookUrl());
		oauth.signRequest(accessToken, request);
		com.github.scribejava.core.model.Response response = oauth.execute(request);
		if (response.getCode() == 200) {
			String body = response.getBody();
			FacebookLoginJSON reply = new ObjectMapper().readValue(body, FacebookLoginJSON.class);
			if (isValidReply(reply)) {
				// User is now authorized by foreign service. Now inform the master (AuthPlugin) about it to do the rest.
				return h.getAuthPlugin().login(req, res, reply.getName(), reply.getId(), "Facebook", h.isRememberMeWanted());
			} else {
				throw new RuntimeException("Facebook login response code is 200, but response is empty!\n" + body);
			}
		} else {
			throw new RuntimeException("Facebook login response code: " + response.getCode());
		}
	}

	private boolean isValidReply(FacebookLoginJSON reply) {
		return reply != null
				&& reply.getName() != null && !reply.getName().trim().isEmpty()
				&& reply.getId() != null && !reply.getId().trim().isEmpty();
	}

	static class FacebookLoginJSON {
		private String name;
		private String id;

		FacebookLoginJSON() {
		}
		
		String getName() {
			return name;
		}

		void setName(String name) {
			this.name = name;
		}

		String getId() {
			return id;
		}

		void setId(String id) {
			this.id = id;
		}
	}
}
