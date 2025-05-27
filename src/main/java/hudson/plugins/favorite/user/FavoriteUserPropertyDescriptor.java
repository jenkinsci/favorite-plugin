package hudson.plugins.favorite.user;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.Item;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.plugins.favorite.Messages;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

public class FavoriteUserPropertyDescriptor extends UserPropertyDescriptor {

    public FavoriteUserPropertyDescriptor() {
        super(FavoriteUserProperty.class);
    }

    @Override
    public UserProperty newInstance(User user) {
        return new FavoriteUserProperty();
    }

    /*
     * Intentionally set to false to hide it from the config screen.
     * @return false
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    @NonNull
    public String getDisplayName() {
        return Messages.favoriteUserPropertyDescriptor();
    }

    @SuppressWarnings(value = "unused") // used by jelly
    @Deprecated(forRemoval = true)
    public String toItemUrl(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return null;
        }

        Jenkins jenkins = Jenkins.get();
        Item item = jenkins.getItemByFullName(fullName);
        if (item == null) {
          return null;
        }

        return jenkins.getRootUrl() + item.getUrl();
    }

    /*
     * Used in Jelly only
     */
    @Restricted(NoExternalUse.class)
    public String toItemUrl(Item item) {

        Jenkins jenkins = Jenkins.get();
        return jenkins.getRootUrl() + item.getUrl();
    }

}
