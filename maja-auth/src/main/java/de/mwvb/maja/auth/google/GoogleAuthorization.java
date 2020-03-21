package de.mwvb.maja.auth.google;

import de.mwvb.maja.auth.BaseAuthorization;

// The Google classes might not work any more.
public class GoogleAuthorization extends BaseAuthorization {

    @Override
    protected String getService() {
        return "Google";
    }
}
