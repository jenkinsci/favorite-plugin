package hudson.plugins.favorite.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import hudson.model.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FavoriteUserPropertyTest {

    @Rule
    public JenkinsRule rule = new JenkinsRule();

    User bob;

    FavoriteUserProperty property;

    @Before
    public void createUserAndProperty() throws IOException {
        bob = User.get("bob");
        property = new FavoriteUserProperty();
        bob.addProperty(property);
    }

    @Test
    public void testMigration() throws Exception {
        property.favorites.add("foo");
        property.favorites.add("bar");
        property.favorites.add("baz");

        assertFalse(property.hasFavorite("foo"));
        assertFalse(property.hasFavorite("bar"));
        assertFalse(property.hasFavorite("baz"));

        property.readResolve();

        assertTrue(property.hasFavorite("foo"));
        assertTrue(property.hasFavorite("bar"));
        assertTrue(property.hasFavorite("baz"));

        assertEquals(ImmutableList.copyOf(property.getAllFavorites()), ImmutableList.copyOf(property.getFavorites()));
    }

    @Test
    public void lazyDeletionGetAllFavorites() throws Exception {
        rule.createFreeStyleProject("d");

        property.addFavorite("a");
        property.addFavorite("b");
        property.addFavorite("c");
        property.addFavorite("d");

        // Current time + 1 year
        property.setClock(new Clock(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(265)));

        // Data should be retained
        assertEquals(Sets.newHashSet("a", "b", "c", "d"), property.getAllFavorites());

        // Set to one second after epoc
        property.setClock(new Clock(1L));

        // a,b,c do not exist and should be removed
        assertEquals(Sets.newHashSet("d"), property.getAllFavorites());
    }

    @Test
    public void lazyDeletionGetFavorites() throws Exception {
        rule.createFreeStyleProject("d");

        property.addFavorite("a");
        property.addFavorite("b");
        property.addFavorite("c");
        property.addFavorite("d");

        // Current time + 1 year
        property.setClock(new Clock(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(265)));

        // Data should be retained
        assertEquals(ImmutableSet.of("a", "b", "c", "d"), ImmutableSet.copyOf(property.getFavorites()));

        // Set to one second after epoc
        property.setClock(new Clock(1L));

        // a,b,c do not exist and should be removed
        assertEquals(ImmutableSet.of("d"), ImmutableSet.copyOf(property.getFavorites()));
    }
}
