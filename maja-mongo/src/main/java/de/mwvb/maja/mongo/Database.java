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
 * MongoDB access
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

    public static void open(Class<?>... entityClasses) {
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
                + (config.hasFilledKey("dbuser")
                        ? (" with user " + dbuser + (config.hasFilledKey("dbpw") ? " with password" : ""))
                        : ""));
    }

    public Database(String dbhost, String name, String user, String password, Class<?>... entityClasses) {
        this(dbhost, name, user, password, false, entityClasses);
    }

    /**
     * Opens database
     * 
     * @param dbhost        e.g. "localhost"
     * @param name          name of MongoDB database
     * @param user          database user name, null or empty if not necessary
     * @param password      password of the database user, null or empty if not necessary
     * @param ensureCaps    initialize capped collections
     * @param entityClasses for each package one class for registering that package. So it's not necessary to specify all classes.
     */
    public Database(String dbhost, String name, String user, String password, boolean ensureCaps,
            Class<?>... entityClasses) {
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
        if (ensureCaps) {
            ds.ensureCaps();
        }
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
     * @return Host/name/user of last opened database
     */
    public static String getInfo() {
        return info;
    }

    public GridFSDAO openGridFSDAO(String collection) {
        return new GridFSDAO(client.getDatabase(name), collection);
    }
}
