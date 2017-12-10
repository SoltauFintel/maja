package de.mwvb.maja.auth;

import spark.Request;
import spark.Response;

public interface Authorization {

	boolean isRelevant(String service);
	
	/**
	 * Checks whether user is allowed to log in.
	 * 
	 * @param req
	 * @param res
	 * @param name user name, provided by login service
	 * @param foreignId user id, provided by login service
	 * @param service name of the login service
	 * @return null if user is authorized, otherwise error page HTML or Exception
	 */
	String check(Request req, Response res, String name, String foreignId, String service);
}
