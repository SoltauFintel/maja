package de.mwvb.maja.auth.google;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.auth.HandleStorage;
import de.mwvb.maja.auth.facebook.FacebookHandle;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.maja.web.AppConfig;

public class GoogleLoginAction extends ActionBase {
	private static final HandleStorage<FacebookHandle> handles = new HandleStorage<>();
	private final AuthPlugin authPlugin;
	private final String callback;
	
	public GoogleLoginAction(AuthPlugin authPlugin, String callback) {
		this.authPlugin = authPlugin;
		this.callback = callback;
	}

	@Override
	public String run() {
		boolean remember = !"0".equals(req.queryParams("remember"));
		AppConfig config = new AppConfig();
		String secretState = config.get("google.state") + new Random().nextInt(999999);
		String callback = config.get("host") + this.callback;
		OAuth20Service oauth = new ServiceBuilder(config.get("google.key"))
				.apiSecret(config.get("google.secret"))
				.scope("email")
				.state(secretState)
				.callback(callback)
				.build(GoogleApi20.instance());
        // pass access_type=offline to get refresh token
        // https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
        final Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        // force to reget refresh token (if usera are asked not the first time)
        additionalParams.put("prompt", "consent");
		String url = oauth.getAuthorizationUrl(additionalParams);
		String url2 = config.get("google.url");
		handles.push(secretState, new FacebookHandle(oauth, url2, authPlugin, remember));
		res.redirect(url);
		return "";
	}
	
	public static FacebookHandle pop(String key) {
		return handles.pop(key);
	}
}
