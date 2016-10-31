package hudson.plugins.favorite.user;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FavoriteUserPropertyTest {
    @Test
    public void testMigration() throws Exception {
        FavoriteUserProperty property = new FavoriteUserProperty();
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
    }
}
