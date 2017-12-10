package de.mwvb.maja.auth.rememberme;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.web.AppConfig;
import spark.Request;
import spark.Response;

public class RememberMeInMongoDB implements RememberMeFeature {
	// https://stackoverflow.com/a/5083809/3478021
	
	private final KnownUserDAO dao;
	private final Cookie cookie;
	
	public RememberMeInMongoDB() {
		dao = new KnownUserDAO();
		String appName = new AppConfig().get("app.name");
		cookie = new Cookie("KNOWNUSERID" + appName);
	}
	
	@Override
	public void rememberMe(boolean rememberMeWanted, Response res, String user, String userId) {
		if (rememberMeWanted) {
			KnownUser knownUser = new KnownUser();
			knownUser.setCreatedAt(new java.util.Date(System.currentTimeMillis()));
			knownUser.setId(AbstractDAO.genId());
			knownUser.setUser(user);
			knownUser.setUserId(userId);
			dao.save(knownUser);
			
			cookie.set(knownUser.getId(), res, "remember-me");
		} else {
			cookie.remove(res);
			dao.delete(userId);
		}
	}
	
	@Override
	public void forget(Response res, String userId) {
		cookie.remove(res);
		if (userId != null) {
			dao.delete(userId);
		}
	}

	@Override
	public IKnownUser getUserIfKnown(Request req, Response res) {
		String id = cookie.get(req);
		if (id == null) {
			return null;
		}
		KnownUser ku = dao.get(id);
		if (ku == null) {
			cookie.remove(res);
		} else {
			Logger.debug("Remembered user: " + ku.getUser() + " (" + ku.getUserId() + ")");
			cookie.extendLifeTime(ku.getId(), res);
		}
		return ku;
	}
}
