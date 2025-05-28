package hudson.plugins.favorite.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.collect.ImmutableList;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Favorites.FavoriteException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class FavoritesTest {

    private JenkinsRule rule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        this.rule = rule;
    }

    @Test
    void testToggleFavorite() throws FavoriteException, IOException {
        User user = User.get("bob");
        Item item = rule.createFreeStyleProject("bob");

        assertFalse(Favorites.hasFavorite(user, item));
        assertFalse(Favorites.isFavorite(user, item));

        assertTrue(Favorites.toggleFavorite(user, item));
        assertTrue(Favorites.hasFavorite(user, item));
        assertTrue(Favorites.isFavorite(user, item));
    }

    @Test
    void testAddAndRemoveFavorite() throws FavoriteException, IOException {
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
    void testGetFavorites() throws Exception {
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
