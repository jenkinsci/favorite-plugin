package hudson.plugins.favorite.listener;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.User;
import jenkins.model.Jenkins;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows plugins to listen to favoriting
 */
public abstract class FavoriteListener implements ExtensionPoint {

    private static final Logger LOGGER = Logger.getLogger(FavoriteListener.class.getName());

    public static Iterable<FavoriteListener> all() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        return jenkins.getExtensionList(FavoriteListener.class);
    }

    public static void fireOnAddFavourite(Item item, User user) {
        for (FavoriteListener listener : all()) {
            try {
                listener.onAddFavourite(item, user);
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "There was a problem firing listener " + listener.getClass().getName(), e);
            }
        }
    }

    public static void fireOnRemoveFavourite(Item item, User user) {
        for (FavoriteListener listener : all()) {
            try {
                listener.onRemoveFavourite(item, user);
            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "There was a problem firing listener " + listener.getClass().getName(), e);
            }
        }
    }

    /**
     * Fired when a favorite has been addedfor the user
     * @param item that was favourited
     * @param user that the favorite was recorded for
     */
    public abstract void onAddFavourite(Item item, User user);

    /**
     * Fired when a favorite has been removed for the user
     * @param item that was favourited
     * @param user that the favorite was recorded for
     */
    public abstract void onRemoveFavourite(Item item, User user);
}
