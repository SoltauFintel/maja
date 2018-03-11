package de.mwvb.maja.web;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Maja-web comes with Spark, Actions, templating, configuration, banner, favicon,
 * Logging (tinylog) and Bootstrap.
 * 
 * <p>Static web resources must be in src/main/resources/web.
 * Place banner.txt and favicon.ico (16x16 PNG) in src/main/resources.
 * However, you web page should return a 192x192 PNG as browser icon:
 * &lt;link rel="icon" sizes="192x192" href="/favicon2.png"&gt;</p>
 */
public abstract class AbstractWebApp {
	private static final LocalDateTime boottime = LocalDateTime.now();
	protected Level level;
	protected AppConfig config;
	protected AuthPlugin auth;
	
	public void start(String appVersion) {
		initLogging();
		initConfig();
		int port = initPort();
		banner(port, appVersion);
    	initStaticFileLocation();
    	initDatabase();
    	init();
    	defaultRoutes();
    	routes();
    	ready();
	}

	protected void initLogging() {
		level = getDefaultLoggingLevel();
		String loglevel = System.getenv("LOGLEVEL");
		if ("TRACE".equalsIgnoreCase(loglevel)) {
			level = Level.TRACE;
		} else if ("DEBUG".equalsIgnoreCase(loglevel)) {
			level = Level.DEBUG;
		} else if ("INFO".equalsIgnoreCase(loglevel)) {
			level = Level.INFO;
		} else if ("WARNING".equalsIgnoreCase(loglevel) || "WARN".equalsIgnoreCase(loglevel)) {
			level = Level.WARNING;
		} else if ("ERROR".equalsIgnoreCase(loglevel)) {
			level = Level.ERROR;
		} else if ("OFF".equalsIgnoreCase(loglevel)) {
			level = Level.OFF;
		}
		Configurator.currentConfig()
			.writer(new ConsoleWriter())
			.formatPattern("{date}  {message}")
			.level(level)
			.activate();
	}

	protected Level getDefaultLoggingLevel() {
		return Level.WARNING;
	}

	protected void initConfig() {
		config = new AppConfig();
	}

	protected int initPort() {
		int port = Integer.parseInt(config.get("port"));
		port(port);
		return port;
	}

	protected void banner(int port, String version) {
		banner();
		System.out.println("v" + version + " ready on port " + port);
		System.out.println("Configuration file: " + config.getFilename()
				+ " | Log level: " + Logger.getLevel()
				+ " | Mode: " + (config.isDevelopment() ? "development" : "production"));
		
		String info = getTimeInfo();
		if (info != null) {
			System.out.println(info);
		}
	}

	protected void banner() {
		try (InputStream is = getClass().getResourceAsStream("/banner.txt")) {
			try (java.util.Scanner scanner = new java.util.Scanner(is)) {
				java.util.Scanner text = scanner.useDelimiter("\\A");
				if (text.hasNext()) {
					System.out.println(text.next());
				}
			}
		} catch (IOException ignore) {}
	}

	protected String getTimeInfo() {
		return "Date/time: " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now())
				+ ", timezone: " + ZoneId.systemDefault();
	}

	protected void initStaticFileLocation() {
		staticFileLocation("web");
    	if (config.isDevelopment()) {
    		externalStaticFileLocation("src/main/resources/web");
    	}
	}

	/**
	 * Open database (if database is needed)
	 */
	protected void initDatabase() {
	}
	
	/**
	 * init auth and app
	 */
	protected void init() {
		auth = new NoOpAuthPlugin();
	}
	
	protected void defaultRoutes() {
		setupExceptionHandler();
	
		get("/rest/_ping", (req, res) -> "pong");
		auth.addNotProtected("/rest/_");
	
		get("/favicon.ico", (req, res) -> getFavicon(req, res));
		auth.addNotProtected("/favicon.ico");
	
		auth.routes();
	}

	protected void setupExceptionHandler() {
		exception(RuntimeException.class, (exception, req, res) -> {
			ActionBase action = getErrorPage();
			action.init(req, res);
			if (action instanceof ErrorPage) {
				((ErrorPage) action).setException(exception);
			}
			if (action instanceof Action) {
				initAction(req, (Action) action);
			}
			String html = action.run();
			res.body(html);
		});
	}
	
	protected ActionBase getErrorPage() {
		return new GuruErrorPage();
	}

	protected String getFavicon(Request req, Response res) {
		try (InputStream is = getClass().getResourceAsStream("/favicon.ico")) {
			try (OutputStream os = res.raw().getOutputStream()) {
				byte[] buf = new byte[1200];
				for (int nChunk = is.read(buf); nChunk != -1; nChunk = is.read(buf)) {
					os.write(buf, 0, nChunk);
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		}
		return "";
	}

	protected abstract void routes();
	
	protected void ready() {
		System.out.println("App loaded");
	}
	
	protected void _get(String path, Class<? extends ActionBase> actionClass) {
		get(path, createRoute(actionClass));
	}

	protected void _get(String path, ActionBase action) {
		get(path, (req, res) -> {
			action.init(req, res);
			if (action instanceof Action) {
				initAction(req, (Action) action);
			}
			return action.run();
		});
	}

	protected void _post(String path, Class<? extends ActionBase> actionClass) {
		post(path, createRoute(actionClass));
	}

	protected void _put(String path, Class<? extends ActionBase> actionClass) {
		put(path, createRoute(actionClass));
	}
	
	protected void _delete(String path, Class<? extends ActionBase> actionClass) {
		delete(path, createRoute(actionClass));
	}

	protected Route createRoute(Class<? extends ActionBase> actionClass) {
		return (req, res) -> {
			ActionBase action = actionClass.newInstance();
			action.init(req, res);
			if (action instanceof Action) {
				initAction(req, (Action) action);
			}
			return action.run();
		};
	}

	protected void initAction(Request req, Action action) {
	}
	
	public static void GET(String path, Class<? extends ActionBase> actionClass) {
		get(path, (req, res) -> {
			ActionBase action = actionClass.newInstance();
			action.init(req, res);
			return action.run();
		});
	}

	public static void POST(String path, Class<? extends ActionBase> actionClass) {
		post(path, (req, res) -> {
			ActionBase action = actionClass.newInstance();
			action.init(req, res);
			return action.run();
		});
	}

	public static void PUT(String path, Class<? extends ActionBase> actionClass) {
		put(path, (req, res) -> {
			ActionBase action = actionClass.newInstance();
			action.init(req, res);
			return action.run();
		});
	}

	public static void DELETE(String path, Class<? extends ActionBase> actionClass) {
		delete(path, (req, res) -> {
			ActionBase action = actionClass.newInstance();
			action.init(req, res);
			return action.run();
		});
	}

	public void startForTest() {
		initConfig();
		initDatabase();
	}

	public static LocalDateTime getBoottime() {
		return boottime;
	}
}
