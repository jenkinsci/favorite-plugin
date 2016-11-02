package hudson.plugins.favorite.user;

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
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FavoriteListenerTest {
    @Rule
    public final JenkinsRule rule = new JenkinsRule();

    @Test
    public void testListener() throws IOException, FavoriteException {
        FreeStyleProject item = rule.createFreeStyleProject("My project");
        User user = User.get("bob");

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

    @Extension
    public static class TestFavoriteJobListener extends FavoriteListener {

        public final Map<User, Item> addFavorites = Maps.newHashMap();
        public final Map<User, Item> removeFavorites = Maps.newHashMap();

        @Override
        public void onAddFavourite(Item item, User user) {
            addFavorites.put(user, item);
        }

        @Override
        public void onRemoveFavourite(Item item, User user) {
            removeFavorites.put(user, item);
        }
    }
}

