package hudson.plugins.favorite.user;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.User;
import hudson.security.ACL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class FavoriteUserPropertyTest {

    private JenkinsRule rule;

    private User bob;

    private FavoriteUserProperty property;

    @BeforeEach
    void setUp(JenkinsRule rule) throws Exception {
        this.rule = rule;

        bob = User.getById("bob", true);
        property = new FavoriteUserProperty();
        bob.addProperty(property);
    }

    @Test
    void testMigration() {
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
    void lazyDeletionGetAllFavorites() throws Exception {
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
    void lazyDeletionGetFavorites() throws Exception {
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

    @Issue("JENKINS-76073")
    @Test
    void discoverOnlyPermissions() throws Exception {
        FreeStyleProject read = rule.createFreeStyleProject("read");
        FreeStyleProject discover = rule.createFreeStyleProject("discover");
        property.addFavorite("p");
        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        MockAuthorizationStrategy authorizationStrategy = new MockAuthorizationStrategy();
        authorizationStrategy.grant(Item.DISCOVER).onItems(discover).to("bob");
        authorizationStrategy.grant(Item.READ).onItems(read).to("bob");
        rule.jenkins.setAuthorizationStrategy(authorizationStrategy);

        try (var context = ACL.as(bob)) {
            property.addFavorite("discover");
            property.addFavorite("read");
            assertThat(property.isJobFavorite("discover"), is(true));
            assertThat(property.isJobFavorite("read"), is(true));
            assertEquals(ImmutableSet.of("read"), ImmutableSet.copyOf(property.getAllFavorites()));
        }
    }
}
