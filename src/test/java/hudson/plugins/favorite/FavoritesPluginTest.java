package hudson.plugins.favorite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.listener.FavoriteListener;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import jenkins.model.Jenkins;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class FavoritesPluginTest {

    private static final List<String> events = new CopyOnWriteArrayList<>();

    private JenkinsRule rule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        this.rule = rule;

        events.clear();
        rule.jenkins.setSecurityRealm(rule.createDummySecurityRealm());
        MockAuthorizationStrategy authorizationStrategy = new MockAuthorizationStrategy();
        authorizationStrategy.grant(Jenkins.READ).onRoot().toEveryone();
        authorizationStrategy.grant(Item.READ).everywhere().toEveryone();
        rule.jenkins.setAuthorizationStrategy(authorizationStrategy);
    }

    @Test
    void testToggleFavoriteAnonymous() throws Exception {
        rule.createFreeStyleProject("cats");
        try (JenkinsRule.WebClient client = rule.createWebClient()) {
            WebResponse response = requestToggleFavorite(client, "cats", false);
            assertEquals(403, response.getStatusCode());

            // verify favorite column not visible
            HtmlPage root = client.goTo("");
            assertEquals(0, root.querySelectorAll("svg.icon-fav-active").size());
            assertEquals(0, root.querySelectorAll("svg.icon-fav-inactive").size());

            // verify favorite action not available
            HtmlPage cats = client.goTo("job/cats/");
            assertFalse(hasFavoriteAction(cats));
        }
    }

    @Test
    void testToggleFavorite() throws Exception {
        rule.createFreeStyleProject("dogs");
        Item cats = rule.createFreeStyleProject("cats");
        User bob = User.getById("bob", true);
        assertFalse(Favorites.isFavorite(bob, cats));
        try (JenkinsRule.WebClient client = rule.createWebClient()) {
            client.login("bob", "bob");
            // verify favorite action available
            HtmlPage catsPage = client.goTo("job/cats/");
            assertTrue(hasFavoriteAction(catsPage));

            // add favorite
            WebResponse res = requestToggleFavorite(client, "cats", false);
            assertEquals(200, res.getStatusCode());
            assertTrue(Favorites.isFavorite(bob, cats));

            // verify table view
            HtmlPage root = client.goTo("");
            assertFalse(root.querySelectorAll("svg.icon-fav-active").isEmpty());
            assertFavoriteColumStatus(root, "cats", true);
            assertFavoriteColumStatus(root, "dogs", false);

            // remove favorite
            res = requestToggleFavorite(client, "cats", false);
            assertEquals(200, res.getStatusCode());
            assertFalse(Favorites.isFavorite(bob, cats));
        }

        assertEquals(Arrays.asList("add:bob:cats", "remove:bob:cats"), events);
    }

    @Test
    void testToggleFavoriteJobDoesNotExist() throws Exception {
        try (JenkinsRule.WebClient client = rule.createWebClient()) {
            client.login("bob", "bob");
            WebResponse res = requestToggleFavorite(client, "foo/bar", false);
            assertEquals(404, res.getStatusCode());
        }

        assertEquals(0, events.size());
    }

    @Test
    void testToggleFavoriteRedirect() throws Exception {
        rule.createFreeStyleProject("cats");
        try (JenkinsRule.WebClient client = rule.createWebClient()) {
            client.login("bob");
            WebResponse res = requestToggleFavorite(client, "cats", true);
            assertEquals(200, res.getStatusCode());
            assertEquals("/jenkins/job/cats/", res.getWebRequest().getUrl().getPath());
        }
    }

    private void assertFavoriteColumStatus(HtmlPage page, String job, boolean isFavorite) {
        DomElement row = page.getElementById("job_" + job);
        DomElement activeFavIcon = row.querySelector("svg.icon-fav-active");
        DomElement inactiveFavIcon = row.querySelector("svg.icon-fav-inactive");
        assertNotNull(activeFavIcon);
        assertNotNull(inactiveFavIcon);
        if (isFavorite) {
            assertFalse(activeFavIcon.getAttribute("class").contains("jenkins-hidden"));
            assertTrue(inactiveFavIcon.getAttribute("class").contains("jenkins-hidden"));
        } else {
            assertTrue(activeFavIcon.getAttribute("class").contains("jenkins-hidden"));
            assertFalse(inactiveFavIcon.getAttribute("class").contains("jenkins-hidden"));
        }
    }

    private WebResponse requestToggleFavorite(JenkinsRule.WebClient client, String job, boolean redirect) throws Exception {
        String path = "/plugin/favorite/toggleFavorite?job=" + job;
        if (redirect) {
            path += "&redirect=true";
        }

        URL url = client.createCrumbedUrl(path);
        WebRequest req = new WebRequest(url, HttpMethod.POST);
        return client.loadWebResponse(req);
    }

    private boolean hasFavoriteAction(HtmlPage page) {
        return page.querySelectorAll("span.task-link-text").stream()
                .anyMatch(e -> "Favorite".equals(e.getTextContent()));
    }

    @Extension
    public static class TestFavoriteLister extends FavoriteListener {

        @Override
        public void onAddFavourite(Item item, User user) {
            events.add("add:" + user.getFullName() + ":" + item.getName());
        }

        @Override
        public void onRemoveFavourite(Item item, User user) {
            events.add("remove:" + user.getFullName() + ":" + item.getName());
        }
    }

}
