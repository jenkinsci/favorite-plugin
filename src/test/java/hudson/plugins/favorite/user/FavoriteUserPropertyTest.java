package hudson.plugins.favorite.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import hudson.model.FreeStyleProject;
import hudson.model.User;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

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
        rule.createFolder("f").createProject(FreeStyleProject.class, "z");

        rule.createFreeStyleProject("d");

        property.addFavorite("a");
        property.addFavorite("b");
        property.addFavorite("c");
        property.addFavorite("d");
        property.addFavorite("f/z");

        // a,b,c do not exist and should be removed
        assertEquals(Sets.newHashSet("d", "f/z"), property.getAllFavorites());
    }

    @Test
    public void lazyDeletionGetFavorites() throws Exception {
        rule.createFolder("f").createProject(FreeStyleProject.class, "z");
        rule.createFreeStyleProject("d");

        property.addFavorite("a");
        property.addFavorite("b");
        property.addFavorite("c");
        property.addFavorite("d");
        property.addFavorite("f/z");

        // a,b,c do not exist and should be removed
        assertEquals(ImmutableSet.of("d", "f/z"), ImmutableSet.copyOf(property.getFavorites()));
    }
}
