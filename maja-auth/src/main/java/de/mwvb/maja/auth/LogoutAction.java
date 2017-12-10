package de.mwvb.maja.auth;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.auth.rememberme.RememberMeFeature;
import de.mwvb.maja.web.ActionBase;
import spark.Session;

public class LogoutAction extends ActionBase {
	private final RememberMeFeature rememberMe;

	public LogoutAction(RememberMeFeature rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	@Override
	public String run() {
		Session session = req.session();
		String userId = AuthPlugin.getUserId(session);
		if (userId != null) {
			Logger.debug("Logout: " + AuthPlugin.getUser(session) + " (" + userId + ")");
		}
		rememberMe.forget(res, userId);
		AuthPlugin.setLoginData(false, null, null, session);
		
		res.redirect("/");
		return "";
	}
}
