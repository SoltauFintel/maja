package de.mwvb.maja.auth.rememberme;

import spark.Request;
import spark.Response;

/**
 * Remember me feature:
 * User stays logged on during browser restart or web app restart.
 * User must log in only if he logged off or after many (e.g. 30) days.
 */
public interface RememberMeFeature {

	/**
	 * Remembers user. Used for login.
	 * 
	 * @param rememberMeWanted
	 * @param res
	 * @param user
	 * @param userId
	 */
	void rememberMe(boolean rememberMeWanted, Response res, String user, String userId);
	
	/**
	 * Used for auto-login.
	 * 
	 * @param req
	 * @param res
	 * @return null if there's no known user
	 */
	IKnownUser getUserIfKnown(Request req, Response res);

	/**
	 * Forgets user. Used for logout.
	 * 
	 * @param res
	 * @param userId contains service name and foreign user id
	 */
	void forget(Response res, String userId);
}
