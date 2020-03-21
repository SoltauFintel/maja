package de.mwvb.maja.rest;

/**
 * Opinionated REST service for CRUD methods
 */
public abstract class AbstractRestServiceCRUD<T> extends AbstractRestService<T> {

    public abstract Object list();

    public abstract T byId();

    /** POST */
    public abstract String create(T a);

    /** PUT */
    public abstract String update(T ba);

    /** DELETE */
    public abstract String delete();
}
