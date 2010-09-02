package hudson.plugins.favorite.user;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.ArrayList;

@ExportedBean(defaultVisibility = 999)
public class FavoriteUserProperty extends UserProperty {
    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new DescriptorImpl();

    @DataBoundConstructor
    public FavoriteUserProperty() {
        this.getDescriptor();
    }

    private ArrayList<String> favorites = new ArrayList<String>();

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void addFavorite(String job) throws IOException {
        favorites.add(job);
        user.save();
    }

    public void removeFavorite(String job) throws IOException {
        favorites.remove(job);
        user.save();
    }

    public void toggleFavorite(String job) throws IOException {
        if (isJobFavorite(job)) {
            removeFavorite(job);
        } else {
            addFavorite(job);
        }
    }

    public boolean isJobFavorite(String job) {
        return favorites.contains(job);
    }

    @Override
    public UserPropertyDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    private static class DescriptorImpl extends UserPropertyDescriptor {

        public DescriptorImpl() {
            super(FavoriteUserProperty.class);
        }

        @Override
        public UserProperty newInstance(User user) {
            return new FavoriteUserProperty();
        }

        @Override
        public String getDisplayName() {
            return "Favorites";
        }
    }

}
