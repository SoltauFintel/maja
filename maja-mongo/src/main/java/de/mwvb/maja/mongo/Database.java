package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.logging.slf4j.SLF4JLoggerImplFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import de.mwvb.maja.web.AppConfig;

/**
 * Zugriff auf MongoDB
 */
public class Database {
	private MongoClient client;
	private Morphia morphia;
	private Datastore ds;
	private String name;
	private static String info = "--";
	
	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLoggerImplFactory.class);
	}
	
	public static void open(Class<?> ... entityClasses) {
		AppConfig config = new AppConfig();
		String dbname = config.get("dbname");
		if (dbname == null || dbname.isEmpty()) {
			throw new RuntimeException("Config parameter 'dbname' missing!");
		}
		String dbhost = config.get("dbhost", "localhost");
		String dbuser = config.get("dbuser");
		String dbpw = config.get("dbpw");
		AbstractDAO.database = new Database(dbhost, dbname, dbuser, dbpw, entityClasses);
		System.out.println("MongoDB database: " + dbname + "@" + dbhost
				+ (config.hasFilledKey("dbuser") ? (" with user " + dbuser + (config.hasFilledKey("dbpw")
						? " with password" : "")) : ""));
	}
	
	/**
	 * Öffnet Datenbank.
	 * 
	 * @param dbhost z.B. "localhost"
	 * @param name Name der MongoDB Datenbank
	 * @param user Datenbank Benutzername, null or leer wenn nicht erforderlich
	 * @param password Kennwort des Datenbank Benutzers, null or leer wenn nicht erforderlich
	 * @param entityClasses Für jedes Package eine Klasse, damit das Package registriert wird.
	 * Es muss also NICHT jede Entity Klasse angegeben werden!
	 */
	public Database(String dbhost, String name, String user, String password, Class<?> ... entityClasses) {
		this.name = name;
		List<MongoCredential> credentialsList = new ArrayList<>();
		if (user != null && !user.isEmpty()) {
			MongoCredential cred = MongoCredential.createCredential(user, name, password.toCharArray());
			credentialsList.add(cred);
		}
		client = new MongoClient(new ServerAddress(dbhost), credentialsList);
		morphia = new Morphia();
		ds = morphia.createDatastore(client, name);
		for (Class<?> entityClass : entityClasses) {
			morphia.mapPackageFromClass(entityClass);
		}
		ds.ensureIndexes();
		info = dbhost + "/" + name + (user == null || user.isEmpty() ? "" : "/" + user);
	}
	
	public Datastore ds() {
		return ds;
	}
	
	public void close() {
		ds = null;
		morphia = null;
		client.close();
		client = null;
	}
	
	/**
	 * @return Host/Name/User of last opened database
	 */
	public static String getInfo() {
		return info;
	}

	public GridFSDAO openGridFSDAO(String collection) {
		return new GridFSDAO(client.getDatabase(name), collection);
	}
}
