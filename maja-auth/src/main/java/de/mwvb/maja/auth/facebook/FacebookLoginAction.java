package de.mwvb.maja.auth.facebook;

import java.util.Random;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.auth.HandleStorage;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.maja.web.AppConfig;

/**
 * Asks Facebook for authorization
 * 
 * <p>Can have URL argument "remember=0" if it's not wanted that the login is remembered.</p>
 */
public class FacebookLoginAction extends ActionBase {
	private static final HandleStorage<FacebookHandle> handles = new HandleStorage<>();
	private final AuthPlugin authPlugin;
	private final String callback;
	
	public FacebookLoginAction(AuthPlugin authPlugin, String callback) {
		this.authPlugin = authPlugin;
		this.callback = callback;
	}

	@Override
	public String run() {
		boolean remember = !"0".equals(req.queryParams("remember"));
		AppConfig config = new AppConfig();
		String secretState = config.get("facebook.state") + new Random().nextInt(999999);
		String callback = config.get("host") + this.callback;
		OAuth20Service oauth = new ServiceBuilder(config.get("facebook.key"))
				//v4: .apiKey(config.get("facebook.key"))
				.apiSecret(config.get("facebook.secret"))
				.state(secretState)
				.callback(callback)
				.build(FacebookApi.instance());
		String facebookUrl = config.get("facebook.url");
		String url = oauth.getAuthorizationUrl();
		handles.push(secretState, new FacebookHandle(oauth, facebookUrl, authPlugin, remember));
		res.redirect(url);
		return "";
	}
	
	public static FacebookHandle pop(String key) {
		return handles.pop(key);
	}
}
