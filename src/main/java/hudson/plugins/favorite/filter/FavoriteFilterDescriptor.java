package hudson.plugins.favorite.filter;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.favorite.Messages;
import hudson.views.ViewJobFilter;

@Extension
public class FavoriteFilterDescriptor extends Descriptor<ViewJobFilter> {
    public FavoriteFilterDescriptor() {
        super(FavoriteFilter.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.favoritesFilter();
    }
}