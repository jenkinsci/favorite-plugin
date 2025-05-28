package hudson.plugins.favorite.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.security.ACL;
import hudson.security.ACLContext;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@WithJenkins
class FavoriteFilterTest {

    private JenkinsRule rule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        this.rule = rule;

        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        MockAuthorizationStrategy authorizationStrategy = new MockAuthorizationStrategy();
        authorizationStrategy.grant(Jenkins.READ).onRoot().toEveryone();
        authorizationStrategy.grant(Item.READ).everywhere().toEveryone();
        rule.jenkins.setAuthorizationStrategy(authorizationStrategy);
    }

    @Test
    void filter() throws Exception {
        TopLevelItem cats = rule.createFreeStyleProject("cats");
        TopLevelItem dogs = rule.createFreeStyleProject("dogs");
        TopLevelItem fish = rule.createFreeStyleProject("fish");
        TopLevelItem pigs = rule.createFreeStyleProject("pigs");
        User bob = User.getById("bob", true);

        Favorites.addFavorite(bob, cats);
        Favorites.addFavorite(bob, pigs);

        Authentication bobAuth = new UsernamePasswordAuthenticationToken(bob.getId(), bob.getId(), new ArrayList<>());
        try (ACLContext ctx = ACL.as2(bobAuth)) {

            FavoriteFilter filter = new FavoriteFilter();

            List<TopLevelItem> added = new ArrayList<>();
            added.add(cats);
            added.add(dogs);
            added.add(fish);

            List<TopLevelItem> all = new ArrayList<>(added);
            all.add(pigs);

            List<TopLevelItem> filtered = filter.filter(added, all, null);
            assertThat(filtered, containsInAnyOrder(cats));
        }
    }

    @Test
    void filterAnonymous() throws Exception {
        TopLevelItem cats = rule.createFreeStyleProject("cats");
        TopLevelItem dogs = rule.createFreeStyleProject("dogs");
        TopLevelItem fish = rule.createFreeStyleProject("fish");
        TopLevelItem pigs = rule.createFreeStyleProject("pigs");

        try (ACLContext ctx = ACL.as2(Jenkins.ANONYMOUS2)) {

            FavoriteFilter filter = new FavoriteFilter();

            List<TopLevelItem> added = new ArrayList<>();
            added.add(cats);
            added.add(dogs);
            added.add(fish);

            List<TopLevelItem> all = new ArrayList<>(added);
            all.add(pigs);

            List<TopLevelItem> filtered = filter.filter(added, all, null);
            assertThat(filtered, empty());
        }
    }
}