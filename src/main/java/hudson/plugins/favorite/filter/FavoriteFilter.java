package hudson.plugins.favorite.filter;


import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.plugins.favorite.Favorites;
import hudson.views.ViewJobFilter;
import jenkins.model.Jenkins;
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

        User user = this.getUser();
        if (user != null) {
            for (TopLevelItem item : all) {
                if (Favorites.isFavorite(user, item)) {
                    filtered.add(item);
                }
            }
        }

        List<TopLevelItem> sorted = new ArrayList<TopLevelItem>(all);
        sorted.retainAll(filtered);
        return sorted;
    }

    private User getUser() {
        Jenkins jenkinsInstance = Jenkins.getInstance();

        if (jenkinsInstance == null) {
            throw new IllegalStateException("No jenkins instance found");
        }

        try {
            return jenkinsInstance.getMe();
        } catch (Exception e) {
            try {
                return jenkinsInstance.getUser(Hudson.ANONYMOUS.getName());
            } catch (Exception e2) {
                return null;
            }
        }
    }

}
