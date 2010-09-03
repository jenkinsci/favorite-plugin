package hudson.plugins.favorite.column;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import hudson.views.ListViewColumn;
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
        public ListViewColumn newInstance(StaplerRequest req,
                                          JSONObject formData) throws FormException {
            return new FavoriteColumn();
        }

        public String getDisplayName() {
            return "Favorite";
        }
    }

    public String getStar(String job) {
        Authentication authentication = Hudson.getAuthentication();
        String name = authentication.getName();
        User user = Hudson.getInstance().getUser(name);
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        if (fup == null || !fup.isJobFavorite(job)) {
            return "star.gif";
        } else {
            return "star-gold.gif";
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
