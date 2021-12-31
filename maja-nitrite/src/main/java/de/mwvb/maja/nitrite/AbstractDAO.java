package de.mwvb.maja.nitrite;

import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;

import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

public abstract class AbstractDAO<E> {
	public static Database database;
	
	protected abstract Class<E> getEntityClass();

	@SuppressWarnings("unchecked")
	public void insert(E entity) {
		ds().insert(entity);
	}

	public void update(E entity) {
		ds().update(entity);
	}

	public void delete(E entity) {
		ds().remove(entity);
	}
	
	public void drop() {
		ds().drop();
	}

	/**
	 * @return all entities of the collection
	 */
	public List<E> list() {
		return ds().find().toList();
	}
	
	/**
	 * Find by id
	 * 
	 * @param id String
	 * @return null if not exists
	 */
	public E get(String id) {
		return ds().find(ObjectFilters.eq("id", id)).firstOrDefault();
	}

	public long size() {
		return ds().size();
	}
	
	protected final ObjectRepository<E> ds() {
		return database.getRepository(getEntityClass());
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
