package hudson.plugins.favorite.user;

import com.google.common.collect.ImmutableList;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Favorites.FavoriteException;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.List;

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

    @Test
    public void testGetFavorites() throws Exception {
        User user = User.get("bob");
        Item project1 = rule.createFreeStyleProject("project1");
        assertFalse(Favorites.hasFavorite(user, project1));
        assertFalse(Favorites.isFavorite(user, project1));

        Item project2 = rule.createFreeStyleProject("project2");
        assertFalse(Favorites.hasFavorite(user, project2));
        assertFalse(Favorites.isFavorite(user, project2));

        Item project3 = rule.createFreeStyleProject("project3");
        assertFalse(Favorites.hasFavorite(user, project2));
        assertFalse(Favorites.isFavorite(user, project2));

        Favorites.addFavorite(user, project2);
        Favorites.addFavorite(user, project3);

        assertFalse(Favorites.isFavorite(user, project1));
        assertTrue(Favorites.isFavorite(user, project2));
        assertTrue(Favorites.isFavorite(user, project3));

        List<Item> favorites = ImmutableList.copyOf(Favorites.getFavorites(user));
        assertFalse(favorites.contains(project1));
        assertTrue(favorites.contains(project2));
        assertTrue(favorites.contains(project3));

        Favorites.removeFavorite(user, project3);

        favorites = ImmutableList.copyOf(Favorites.getFavorites(user));
        assertFalse(favorites.contains(project1));
        assertTrue(favorites.contains(project2));
        assertFalse(favorites.contains(project3));
    }
}
