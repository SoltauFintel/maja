package de.mwvb.maja.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;

public class Database implements AutoCloseable {
	private Nitrite db;

	public Database() {
		this("test/test.db");
	}

	public Database(String dbname) {
		if (!dbname.endsWith(".db")) {
			throw new IllegalArgumentException("dbname must end with '.db'!");
		}
		db = Nitrite.builder().compressed().filePath(dbname).openOrCreate();
	}
	
	public <T> ObjectRepository<T> getRepository(Class<T> cls) {
		return db.getRepository(cls);
	}

	@Override
	public void close() throws Exception {
		db.close();
		db = null;
	}
}
