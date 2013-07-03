package hudson.plugins.favorite.filter;


import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import hudson.views.ViewJobFilter;
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
        List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();

        Authentication authentication = Hudson.getAuthentication();

        String name = authentication.getName();
        if (name != null && authentication.isAuthenticated()) {
            User user = Hudson.getInstance().getUser(name);
            if (user != null) {
                FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
                if (fup != null) {
                    for (TopLevelItem item : added) {
                        if (fup.isJobFavorite(item.getFullName())) {
                            filtered.add(item);
                        }
                    }
                }
            }
        }
        return filtered;
    }

}
