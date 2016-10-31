package hudson.plugins.favorite.column;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.favorite.Messages;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import hudson.views.ListViewColumn;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.acegisecurity.Authentication;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class FavoriteColumn extends ListViewColumn {

    @DataBoundConstructor
    public FavoriteColumn() {

    }

    @Extension
    public static final Descriptor<ListViewColumn> DESCRIPTOR = new DescriptorImpl();

    @Override
    public Descriptor<ListViewColumn> getDescriptor() {
        return DESCRIPTOR;
    }

    private static class DescriptorImpl extends Descriptor<ListViewColumn> {
        @Override
        public ListViewColumn newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new FavoriteColumn();
        }

        public String getDisplayName() {
            return Messages.favoriteColumn();
        }
    }

    public String getStar(String job) {
        FavoriteUserProperty fup = getFavoriteUserProperty();
        if (fup == null || !fup.isJobFavorite(job)) {
            return "star.png";
        } else {
            return "star-gold.png";
        }
    }

    private FavoriteUserProperty getFavoriteUserProperty() {
        Authentication authentication = Hudson.getAuthentication();
        String name = authentication.getName();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        User user = jenkins.getUser(name);
        if (user == null) {
            throw new IllegalStateException("Can't find user " + name);
        }
        return user.getProperty(FavoriteUserProperty.class);
    }

    public int getSortData(String job) {
        FavoriteUserProperty fup = getFavoriteUserProperty();
        if (fup == null || !fup.isJobFavorite(job)) {
            return 0;
        } else {
            return 1;
        }

    }

    public String getUserId() {
        Authentication authentication = Hudson.getAuthentication();
        return authentication.getName();
    }

    public boolean isLoggedIn() {
        Authentication authentication = Hudson.getAuthentication();
        if (authentication.getName().equals("anonymous")) {
            return false;
        } else {
            return true;
        }
    }
}
