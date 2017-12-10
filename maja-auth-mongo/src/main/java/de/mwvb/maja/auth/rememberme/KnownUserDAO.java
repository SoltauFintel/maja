package de.mwvb.maja.auth.rememberme;

import de.mwvb.maja.mongo.AbstractDAO;

public class KnownUserDAO extends AbstractDAO<KnownUser> {

	@Override
	protected Class<KnownUser> getEntityClass() {
		return KnownUser.class;
	}

	/**
	 * @param userId contains service name and foreign user id
	 */
	public void delete(String userId) {
		ds().delete(createQuery().field("userId").equal(userId));
	}
}
