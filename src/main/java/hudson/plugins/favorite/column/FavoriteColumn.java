package hudson.plugins.favorite.column;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.User;
import hudson.plugins.favorite.Messages;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
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

    private static class DescriptorImpl extends ListViewColumnDescriptor {
        @Override
        public ListViewColumn newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new FavoriteColumn();
        }

        @Override
        @NonNull
        public String getDisplayName() {
            return Messages.favoriteColumn();
        }

        @Override
        public boolean shownByDefault() {
            // only show the column for authenticated users
            return User.current() != null;
        }
    }

    @SuppressWarnings(value = "unused") // used by stapler
    public boolean isFavorite(String job) {
        FavoriteUserProperty fup = getFavoriteUserProperty();
        return fup != null && fup.isJobFavorite(job);
    }

    private FavoriteUserProperty getFavoriteUserProperty() {
        User user = User.current();
        if (user == null) {
            return null;
        }

        return user.getProperty(FavoriteUserProperty.class);
    }

    @SuppressWarnings(value = "unused") // used by stapler
    public int getSortData(String job) {
        FavoriteUserProperty fup = getFavoriteUserProperty();
        if (fup == null || !fup.isJobFavorite(job)) {
            return 0;
        } else {
            return 1;
        }
    }

}
