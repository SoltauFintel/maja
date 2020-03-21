package de.mwvb.maja.auth;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.auth.rememberme.RememberMeFeature;
import de.mwvb.maja.web.ActionBase;
import spark.Session;

public class LogoutAction extends ActionBase {
    private final RememberMeFeature rememberMe;
    private final boolean isDebugLogging;

    public LogoutAction(RememberMeFeature rememberMe, boolean isDebugLogging) {
        this.rememberMe = rememberMe;
        this.isDebugLogging = isDebugLogging;
    }

    @Override
    protected void execute() {
        Session session = req.session();
        String userId = AuthPlugin.getUserId(session);
        if (userId != null && isDebugLogging) {
            logLogout(AuthPlugin.getUser(session), userId);
        }
        rememberMe.forget(res, userId);
        AuthPlugin.setLoginData(false, null, null, session);

        res.redirect("/");
    }

    protected void logLogout(String user, String userId) {
        Logger.debug("Logout: " + user + " (" + userId + ")");
    }
}
