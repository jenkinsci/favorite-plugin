package hudson.plugins.favorite.filter;


import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.plugins.favorite.Favorites;
import hudson.views.ViewJobFilter;
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
        User user = User.current();
        if (user != null) {
            for (TopLevelItem item : added) {
                if (Favorites.isFavorite(user, item)) {
                    filtered.add(item);
                }
            }
        }

        return filtered;
    }

}
