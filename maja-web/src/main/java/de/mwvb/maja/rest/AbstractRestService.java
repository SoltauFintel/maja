package de.mwvb.maja.rest;

public abstract class AbstractRestService<T> extends AbstractNoEntityRestService {

    public abstract Class<T> getEntityClass();
}
