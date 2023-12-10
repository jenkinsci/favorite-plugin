package hudson.plugins.favorite;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.listener.FavoriteListener;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import jenkins.model.Jenkins;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Public API for Favorites
 */
public final class Favorites {
    /**
     * Toggles the favorite for a job
     * @param user that the favorite belongs to
     * @param item to favorite
     * @throws FavoriteException
     * @return favorite state
     */
    public static boolean toggleFavorite(@NonNull User user, @NonNull Item item) throws FavoriteException {
        try {
            FavoriteUserProperty property = getProperty(user);
            boolean result = property.toggleFavorite(item.getFullName());
            if (result) {
                FavoriteListener.fireOnAddFavourite(item, user);
            } else {
                FavoriteListener.fireOnRemoveFavourite(item, user);
            }
            return result;
        } catch (IOException e) {
            throw new FavoriteException("Could not determine Favorite state. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Check if the item is favorited
     * @param user to check
     * @param item to check
     * @return favorite state
     */
    public static boolean isFavorite(@NonNull User user, @NonNull Item item) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        return fup != null && fup.isJobFavorite(item.getFullName());
    }

    /**
     * Check if the item has a favorite entry regardless of its state
     * This is useful for checking if a favorite/unfavorite operation has ever been performed against this user
     * @param user to check
     * @param item to check
     * @return favorite state
     * @throws FavoriteException
     */
    public static boolean hasFavorite(@NonNull User user, @NonNull Item item) throws FavoriteException {
        try {
            FavoriteUserProperty property = getProperty(user);
            return property.hasFavorite(item.getFullName());
        } catch (IOException e) {
            throw new FavoriteException("Could not determine Favorite state. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Add an item as a favorite for a user
     * Fires {@link FavoriteListener#fireOnAddFavourite(Item, User)}
     * @param user to add the favorite to
     * @param item to favorite
     * @throws FavoriteException
     */
    public static void addFavorite(@NonNull User user, @NonNull Item item) throws FavoriteException {
        try {
            if (!isFavorite(user, item)) {
                FavoriteUserProperty property = getProperty(user);
                property.addFavorite(item.getFullName());
                FavoriteListener.fireOnAddFavourite(item, user);
            } else {
                throw new FavoriteException("Favourite is already set for User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">");
            }
        } catch (IOException e) {
            throw new FavoriteException("Could not add Favorite. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Remove an item as a favorite for a user
     * Fires {@link FavoriteListener#fireOnRemoveFavourite(Item, User)}
     * @param user to remove the favorite from
     * @param item to favorite
     * @throws FavoriteException
     */
    public static void removeFavorite(@NonNull User user, @NonNull Item item) throws FavoriteException {
        try {
            if (isFavorite(user, item)) {
                FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
                fup.removeFavorite(item.getFullName());
                FavoriteListener.fireOnRemoveFavourite(item, user);
            } else {
                throw new FavoriteException("Favourite is already unset for User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">");
            }
        } catch (IOException e) {
            throw new FavoriteException("Could not remove Favorite. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Get all items that the provided user has favorited
     * @param user to lookup favorites for
     * @return favorite items
     */
    public static Iterable<Item> getFavorites(@NonNull User user) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        if (fup == null) {
            return Collections.emptyList();
        }
        Set<String> favorites = fup.getAllFavorites();
        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }
        final Iterator<String> iterator = favorites.iterator();
        final Jenkins jenkins = Jenkins.get();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        return () -> Iterators.filter( new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Item next() {
                return jenkins.getItemByFullName(iterator.next());
            }

            /* Fix build. */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }, Predicates.notNull());
    }

    /**
     *
     * @param user to get the property from
     * @return favorite property
     * @throws IOException if there is a problem with the user property
     * @throws IllegalArgumentException when the user is anonymous
     */
    private static FavoriteUserProperty getProperty(@NonNull User user) throws IOException {
        if (FavoritePlugin.isAnonymous(user)) {
            throw new IllegalArgumentException("user cannot be anonymous");
        }
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        if (fup == null) {
            user.addProperty(new FavoriteUserProperty());
            fup = user.getProperty(FavoriteUserProperty.class);
        }
        return fup;
    }

    /** Exception for Favorite operations */
    public static class FavoriteException extends Exception {
        public FavoriteException(String message, Throwable cause) {
            super(message, cause);
        }

        public FavoriteException(String message) {
            super(message);
        }
    }
}
