package hudson.plugins.favorite.user;

import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Favorites.FavoriteException;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertFalse;

public class FavoritesTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void testToggleFavorite() throws FavoriteException, IOException {
        User user = User.get("bob");
        Item item = rule.createFreeStyleProject("bob");

        assertFalse(Favorites.hasFavorite(user, item));
        assertFalse(Favorites.isFavorite(user, item));

        assertTrue(Favorites.toggleFavorite(user, item));
        assertTrue(Favorites.hasFavorite(user, item));
        assertTrue(Favorites.isFavorite(user, item));
    }

    @Test
    public void testAddAndRemoveFavorite() throws FavoriteException, IOException {
        User user = User.get("bob");
        Item item = rule.createFreeStyleProject("bob");

        assertFalse(Favorites.hasFavorite(user, item));
        assertFalse(Favorites.isFavorite(user, item));

        try {
            Favorites.removeFavorite(user, item);
            fail("Exception not thrown");
        } catch (FavoriteException e) {}

        assertFalse(Favorites.hasFavorite(user, item));
        assertFalse(Favorites.isFavorite(user, item));

        Favorites.addFavorite(user, item);
        assertTrue(Favorites.hasFavorite(user, item));
        assertTrue(Favorites.isFavorite(user, item));

        try {
            Favorites.addFavorite(user, item);
            fail("Exception not thrown");
        } catch (FavoriteException e) {}

        Favorites.removeFavorite(user, item);
        assertTrue(Favorites.hasFavorite(user, item));
        assertFalse(Favorites.isFavorite(user, item));
    }
}
