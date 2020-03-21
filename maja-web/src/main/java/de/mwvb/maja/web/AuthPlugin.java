package de.mwvb.maja.web;

/**
 * AbstractWebApp auth plugin
 */
public interface AuthPlugin {

    /**
     * Adds a path that is not protected. That means everybody can access resources
     * on that path.
     * 
     * @param path or begin of path e.g. "/rest/_"
     */
    void addNotProtected(String path);

    /**
     * Setup routes.
     */
    void routes();
}
