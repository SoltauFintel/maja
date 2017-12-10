package de.mwvb.maja.auth;

import java.util.ArrayList;
import java.util.List;

public class MultiAuthFeature implements AuthFeature {
	private final List<AuthFeature> features = new ArrayList<>();
	
	public MultiAuthFeature(AuthFeature ... theFeatures) {
		for (AuthFeature feature : theFeatures) {
			features.add(feature);
		}
	}
	
	@Override
	public void init(AuthPlugin owner) {
		features.forEach(feature -> feature.init(owner));
	}

	@Override
	public void routes() {
		features.forEach(feature -> feature.routes());
	}
}
