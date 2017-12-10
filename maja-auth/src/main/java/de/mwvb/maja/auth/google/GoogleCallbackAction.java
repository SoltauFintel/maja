package de.mwvb.maja.auth.google;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import de.mwvb.maja.auth.facebook.FacebookHandle;
import de.mwvb.maja.web.ActionBase;
import spark.Request;

public class GoogleCallbackAction extends ActionBase {
	private static final String KEY = "state";

	@Override
	public String run() {
		String key = req.queryParams(KEY);
		FacebookHandle handle = GoogleLoginAction.pop(key);
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
        accessToken = oauth.refreshAccessToken(accessToken.getRefreshToken());
		OAuthRequest request = new OAuthRequest(Verb.GET, h.getUrl());
		oauth.signRequest(accessToken, request);
		com.github.scribejava.core.model.Response response = oauth.execute(request);
		if (response.getCode() == 200) {
			String body = response.getBody();
			GoogleLoginJSON reply = new ObjectMapper().readValue(body, GoogleLoginJSON.class);
			if (isValidReply(reply)) {
				// User is now authorized by foreign service. Now inform the master (AuthPlugin) about it to do the rest.
				return h.getAuthPlugin().login(req, res, reply.getDisplayName(), reply.getId(), "Google", h.isRememberMeWanted());
			} else {
				throw new RuntimeException("Google login response code is 200, but response is empty!\n" + body);
			}
		} else {
			throw new RuntimeException("Google login response code: " + response.getCode());
		}
	}

	private boolean isValidReply(GoogleLoginJSON reply) {
		return reply != null
				&& reply.getDisplayName() != null && !reply.getDisplayName().trim().isEmpty()
				&& reply.getId() != null && !reply.getId().trim().isEmpty();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class GoogleLoginJSON {
		private String displayName;
		private String id;
		// TODO Feld "emails" enthält Array aus Objekten, die wiederum ein Feld "value" haben. Darin steht die Emailadresse.

		GoogleLoginJSON() {
		}
		
		String getDisplayName() {
			return displayName;
		}

		void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		String getId() {
			return id;
		}

		void setId(String id) {
			this.id = id;
		}
	}
}
