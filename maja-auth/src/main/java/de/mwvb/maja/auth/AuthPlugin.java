package de.mwvb.maja.auth;

import static spark.Spark.before;
import static spark.Spark.halt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.http.HttpStatus;
import org.pmw.tinylog.Logger;

import com.github.template72.data.DataMap;

import de.mwvb.maja.auth.facebook.FacebookAuthorization;
import de.mwvb.maja.auth.facebook.FacebookFeature;
import de.mwvb.maja.auth.google.GoogleAuthorization;
import de.mwvb.maja.auth.google.GoogleFeature;
import de.mwvb.maja.auth.rememberme.IKnownUser;
import de.mwvb.maja.auth.rememberme.NoOpRememberMeFeature;
import de.mwvb.maja.auth.rememberme.RememberMeFeature;
import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.maja.web.AppConfig;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Session;

/**
 * Web application plugin for authorization
 */
public class AuthPlugin implements de.mwvb.maja.web.AuthPlugin, Filter {
    public static final String USER_ATTR = "user";
    public static final String LOGGED_IN = "logged_in";
    public static final String LOGGED_IN_YES = "yes";
    public static final String USERID_ATTR = "user_id";
    protected final Set<String> notProtected = new HashSet<>();
    protected final Authorization authorization;
    protected final AuthFeature feature;
    protected final RememberMeFeature rememberMe;
    private boolean debugLogging = true;

    /** No remember-me constructor */
    public AuthPlugin() {
        this(new NoOpRememberMeFeature());
    }

    public AuthPlugin(RememberMeFeature rememberMe) {
        this.authorization = getAuthorization();
        this.feature = getFeature();
        this.rememberMe = rememberMe;
    }

    protected AuthFeature getFeature() {
        AuthFeature ret;
        if (hasGoogle() && hasFacebook()) {
            ret = new MultiAuthFeature(new GoogleFeature(), new FacebookFeature());
        } else if (hasGoogle()) {
            ret = new GoogleFeature();
        } else if (hasFacebook()) {
            ret = new FacebookFeature();
        } else {
            return null;
        }
        ret.init(this);
        return ret;
    }

    protected Authorization getAuthorization() {
        if (hasGoogle() && hasFacebook()) {
            return new AuthorizationDispatcher(new GoogleAuthorization(), new FacebookAuthorization());
        } else if (hasGoogle()) {
            return new GoogleAuthorization();
        } else if (hasFacebook()) {
            return new FacebookAuthorization();
        } else {
            return null;
        }
    }

    protected boolean hasGoogle() {
        return new AppConfig().hasFilledKey("google.key");
    }

    protected boolean hasFacebook() {
        return new AppConfig().hasFilledKey("facebook.key");
    }

    @Override
    public void addNotProtected(String path) {
        notProtected.add(path);
    }

    protected boolean isProtected(String uri) {
        for (String begin : notProtected) {
            if (uri.startsWith(begin)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void routes() {
        if (feature != null) {
            before(this);

            addNotProtected("/logout");
            Action.get("/logout", getLogoutAction(rememberMe));

            feature.routes();
        }
    }

    protected ActionBase getLogoutAction(RememberMeFeature rememberMe) {
        return new LogoutAction(rememberMe, isDebugLogging());
    }

    public static String getUser(Session session) {
        return session.attribute(USER_ATTR);
    }

    public static String getUserId(Session session) {
        return session.attribute(USERID_ATTR);
    }

    public static void setLoginData(boolean loggedIn, String name, String id, Session session) {
        session.attribute(LOGGED_IN, loggedIn ? LOGGED_IN_YES : null);
        session.attribute(USER_ATTR, name);
        session.attribute(USERID_ATTR, id);
    }

    @Override
    public void handle(Request req, Response res) throws Exception {
        String uri = req.uri();
        if (isProtected(uri) && !LOGGED_IN_YES.equals(req.session().attribute(LOGGED_IN))) {
            IKnownUser knownUser = rememberMe.getUserIfKnown(req, res);
            if (knownUser != null) {
                setLoginData(true, knownUser.getUser(), knownUser.getUserId(), req.session());
                return;
            }
            req.session().attribute("uri", uri); // Go back to this page after login
            halt(HttpStatus.UNAUTHORIZED_401, renderLoginPage());
        }
    }

    protected String renderLoginPage() {
        return Action.templates.render("login", new DataMap());
    }

    /**
     * Called by the callback action to login the user to the Maja system.
     * 
     * @param req        Request
     * @param res        Response
     * @param name       user name from the foreign auth service
     * @param foreignId  user id from the foreign auth service
     * @param service    id of the auth service
     * @param rememberMe true if the remember service shall store the login, false
     *                   if the remember service shall delete the login
     * @return usually "" because a redirect to another page will be executed
     */
    public String login(Request req, Response res, String name, String foreignId, String service,
            boolean rememberMeWanted) {
        String msg = authorization.check(req, res, name, foreignId, service);
        if (msg != null) {
            return msg;
        }

        String longId = getLongId(service, foreignId);
        setLoginData(true, name, longId, req.session());
        rememberMe.rememberMe(rememberMeWanted, res, name, longId);
        if (isDebugLogging()) {
            logLogin(name, longId);
        }

        // Redirect zur ursprünglich angewählten Seite
        String uri = req.session().attribute("uri");
        if (uri == null || uri.isEmpty()) {
            uri = "/";
        }
        req.session().removeAttribute("uri");
        res.redirect(uri);
        return "";
    }
    
    protected String getLongId(String service, String foreignId) {
        return service + "#" + foreignId;
    }

    protected void logLogin(String name, String userId) {
        Logger.debug("Login: " + name + " (" + userId + ")");
    }

    public boolean isDebugLogging() {
        return debugLogging;
    }

    public void setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }
}