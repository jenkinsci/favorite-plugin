package hudson.plugins.favorite;

import com.google.inject.Singleton;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.user.FavoriteUserProperty;

import javax.annotation.Nonnull;
import java.io.IOException;

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
    public static boolean toggleFavorite(@Nonnull User user, @Nonnull Item item) throws FavoriteException {
        try {
            FavoriteUserProperty property = getProperty(user);
            return property.toggleFavorite(item.getFullName());
        } catch (IOException e) {
            throw new FavoriteException("Could not determine Favorite state. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Check if the item is favorited
     * @param user to check
     * @param item to check
     * @return favorite state
     * @throws FavoriteException
     */
    public static boolean isFavorite(@Nonnull User user, @Nonnull Item item) throws FavoriteException {
        try {
            FavoriteUserProperty property = getProperty(user);
            return property.isJobFavorite(item.getFullName());
        } catch (IOException e) {
            throw new FavoriteException("Could not determine Favorite state. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Check if the item has a favorite entry regardless of its state
     * This is useful for checking if a favorite/unfavorite operation has ever been performed against this user
     * @param user to check
     * @param item to check
     * @return favorite state
     * @throws FavoriteException
     */
    public static boolean hasFavorite(@Nonnull User user, @Nonnull Item item) throws FavoriteException {
        try {
            FavoriteUserProperty property = getProperty(user);
            return property.hasFavorite(item.getFullName());
        } catch (IOException e) {
            throw new FavoriteException("Could not determine Favorite state. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Add an item as a favorite for a user
     * @param user to add the favorite to
     * @param item to favorite
     * @throws FavoriteException
     */
    public static void addFavorite(@Nonnull User user, @Nonnull Item item) throws FavoriteException {
        try {
            if (!isFavorite(user, item)) {
                FavoriteUserProperty property = getProperty(user);
                property.addFavorite(item.getFullName());
            } else {
                throw new FavoriteException("Favourite is already set for User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">");
            }
        } catch (IOException e) {
            throw new FavoriteException("Could not add Favorite. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     * Add an item as a favorite for a user
     * @param user to remove the favorite from
     * @param item to favorite
     * @throws FavoriteException
     */
    public static void removeFavorite(@Nonnull User user, @Nonnull Item item) throws FavoriteException {
        try {
            if (isFavorite(user, item)) {
                FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
                fup.removeFavorite(item.getFullName());
            } else {
                throw new FavoriteException("Favourite is already unset for User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">");
            }
        } catch (IOException e) {
            throw new FavoriteException("Could not remove Favorite. User: <" + user.getFullName() + "> Item: <" + item.getFullName() + ">", e);
        }
    }

    /**
     *
     * @param user to get the property from
     * @return favorite property
     * @throws IOException if there is a problem with the user property
     * @throws IllegalArgumentException when the user is anonymous
     */
    private static FavoriteUserProperty getProperty(@Nonnull User user) throws IOException {
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
