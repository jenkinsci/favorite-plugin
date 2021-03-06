package hudson.plugins.favorite.filter;


import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.plugins.favorite.Favorites;
import hudson.views.ViewJobFilter;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFilter extends ViewJobFilter {
    @DataBoundConstructor
    public FavoriteFilter() {

    }

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
        List<TopLevelItem> filtered = new ArrayList<>();

        Authentication authentication = Hudson.getAuthentication();

        String name = authentication.getName();
        if (name != null && authentication.isAuthenticated()) {
            Jenkins jenkins = Jenkins.get();
            if (jenkins == null) {
                throw new IllegalStateException("Jenkins not started");
            }
            User user = jenkins.getUser(name);
            if (user != null) {
                for (TopLevelItem item : added) {
                    if (Favorites.isFavorite(user, item)) {
                        filtered.add(item);
                    }
                }
            }
        }
        return filtered;
    }

}
