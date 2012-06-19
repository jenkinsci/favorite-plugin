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
        List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);

        Authentication authentication = Hudson.getAuthentication();

        String name = authentication.getName();
        if (name != null && authentication.isAuthenticated()) {
            User user = Hudson.getInstance().getUser(name);
            if (user == null) {
                clearView(all, filtered);
            } else {
                FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
                for (TopLevelItem item : all) {
                    if (fup == null || !fup.isJobFavorite(item.getName())) {
                        filtered.remove(item);
                    }
                }
            }
        } else {
            clearView(all, filtered);
        }
        return filtered;
    }

    private void clearView(List<TopLevelItem> all, List<TopLevelItem> filtered) {
        for (TopLevelItem item : all) {
            filtered.remove(item);
        }
    }

}
