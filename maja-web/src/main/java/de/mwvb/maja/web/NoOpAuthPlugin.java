package de.mwvb.maja.web;

public final class NoOpAuthPlugin implements AuthPlugin {

	@Override
	public void addNotProtected(String path) {
	}

	@Override
	public void routes() {
	}
}
