package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public abstract class AbstractDAO<E> {
	public static Database database;
	
	protected abstract Class<E> getEntityClass();

	public void save(E entity) {
		ds().save(entity);
	}
	
	public void delete(E entity) {
		ds().delete(entity);
	}

	/**
	 * @return all entities of the collection
	 */
	public List<E> list() {
		return createQuery().asList();
	}
	
	/**
	 * Find by id
	 * 
	 * @param id String
	 * @return null if not exists
	 */
	public E get(String id) {
		return createQuery().field("id").equal(id).get();
	}

	public long size() {
		return createQuery().count();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> distinct(String fieldname) {
		return new ArrayList<String>(ds().getCollection(getEntityClass()).distinct(fieldname));
	}
	
	protected final Query<E> createQuery() {
		return database.ds().createQuery(getEntityClass());
	}

	protected final Datastore ds() {
		return database.ds();
	}

	public static String genId() {
    	return UUID.randomUUID().toString().replace("-", "");
    }

	public static String code6(String str) {
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
		return ret.substring(ret.length() - 6);
	}
	
	public static void checkId6(String id) {
		if (id == null || id.trim().length() != 6) {
			throw new RuntimeException("Illegal id");
		}
		for (int i = 0; i < id.length(); i++) {
			char c = id.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
				throw new RuntimeException("Illegal id");
			}
		}
	}
}
