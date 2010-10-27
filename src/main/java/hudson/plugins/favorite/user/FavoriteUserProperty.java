package hudson.plugins.favorite.user;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExportedBean(defaultVisibility = 999)
public class FavoriteUserProperty extends UserProperty {
    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new FavoriteUserPropertyDescriptor();

    @DataBoundConstructor
    public FavoriteUserProperty() {
        this.getDescriptor();
    }

    private List<String> favorites = new ArrayList<String>();

    public List<String> getFavorites() {
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

    @Override
    public UserProperty reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
        return this;
    }

    public boolean isJobFavorite(String job) {
        return favorites.contains(job);
    }

    @Override
    public UserPropertyDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

}