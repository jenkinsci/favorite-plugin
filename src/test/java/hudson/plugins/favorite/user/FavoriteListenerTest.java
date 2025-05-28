package hudson.plugins.favorite.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.FavoritePlugin;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Favorites.FavoriteException;
import hudson.plugins.favorite.listener.FavoriteListener;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class FavoriteListenerTest {

    private JenkinsRule rule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        this.rule = rule;
    }

    @Test
    void testListener() throws IOException, FavoriteException {
        FreeStyleProject item = rule.createFreeStyleProject("My project");
        User user = User.getById("bob", true);

        FavoritePlugin plugin = new FavoritePlugin();
        TestFavoriteJobListener listener = (TestFavoriteJobListener) Iterables.getFirst(Iterables.filter(FavoriteListener.all(), Predicates.instanceOf(TestFavoriteJobListener.class)), null);
        assertNotNull(listener);

        // Ensure the listener is empty
        assertTrue(listener.addFavorites.isEmpty());
        assertTrue(listener.removeFavorites.isEmpty());

        // Toggle it on
        Favorites.addFavorite(user, item);
        assertEquals(item, listener.addFavorites.get(user));

        // Toggle it off
        Favorites.removeFavorite(user, item);
        assertEquals(item, listener.removeFavorites.get(user));
    }

    @Test
    void testToggleListener() throws IOException, FavoriteException {
        FreeStyleProject item = rule.createFreeStyleProject("My project");
        User user = User.getById("bob", true);

        FavoritePlugin plugin = new FavoritePlugin();
        TestFavoriteJobListener listener = (TestFavoriteJobListener) Iterables.getFirst(Iterables.filter(FavoriteListener.all(), Predicates.instanceOf(TestFavoriteJobListener.class)), null);
        assertNotNull(listener);

        // Ensure the listener is empty
        assertTrue(listener.addFavorites.isEmpty());
        assertTrue(listener.removeFavorites.isEmpty());

        // Toggle it on
        Favorites.toggleFavorite(user, item);
        assertEquals(item, listener.addFavorites.get(user));

        // Toggle it off
        Favorites.toggleFavorite(user, item);
        assertEquals(item, listener.removeFavorites.get(user));
    }

    @Test
    void testRenameNoFavorite() throws Exception {
        // GIVEN
        FreeStyleProject old = rule.createFreeStyleProject("Old project");
        User user = User.getById("bob", true);

        FavoriteUserProperty property = user.getProperty(FavoriteUserProperty.class);
        Set<String> favorites = property.getAllFavorites();

        FavoriteJobListener listener = new FavoriteJobListener();
        assertFalse(favorites.contains("New project"), "Should not contain 'New project'");
        assertFalse(favorites.contains("Old project"), "Should contain 'Old project'");

        // WHEN
        listener.onRenamed(old, "Old project", "New project");

        // THEN
        assertFalse(favorites.contains("New project"), "Should contain 'New project'");
        assertFalse(favorites.contains("Old project"), "Should not contain 'Old project'");
    }

    @Test
    void testRenameFavorite() throws Exception {
        // GIVEN
        FreeStyleProject old = rule.createFreeStyleProject("Old project");
        User user = User.getById("bob", true);

        FavoriteUserProperty property = user.getProperty(FavoriteUserProperty.class);
        Set<String> favorites = property.getAllFavorites();

        TestFavoriteJobListener listener = (TestFavoriteJobListener) Iterables.getFirst(Iterables.filter(FavoriteListener.all(), Predicates.instanceOf(TestFavoriteJobListener.class)), null);

        Favorites.addFavorite(user, old);

        FavoriteJobListener jobListener = new FavoriteJobListener();
        assertFalse(favorites.contains("New project"), "Should not contain 'New project'");
        assertTrue(favorites.contains("Old project"), "Should contain 'Old project'");

        // WHEN
        old.renameTo("New project");

        // THEN
        assertTrue(favorites.contains("New project"), "Should contain 'New project'");
        assertFalse(favorites.contains("Old project"), "Should not contain 'Old project'");
        assertEquals(old, listener.renameFavorites.get(user));
    }

    @Extension
    public static class TestFavoriteJobListener extends FavoriteListener {

        public final Map<User, Item> addFavorites = Maps.newHashMap();
        public final Map<User, Item> removeFavorites = Maps.newHashMap();

        public final Map<User, Item> renameFavorites = Maps.newHashMap();

        @Override
        public void onAddFavourite(Item item, User user) {
            addFavorites.put(user, item);
        }

        @Override
        public void onRemoveFavourite(Item item, User user) {
            removeFavorites.put(user, item);
        }

        @Override
        public void onLocationChangedFavorite(Item item, User user, String oldName, String newName) {
            renameFavorites.put(user, item);
        }
    }
}

