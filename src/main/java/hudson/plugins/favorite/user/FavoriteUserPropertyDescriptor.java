package hudson.plugins.favorite.user;

import hudson.model.AutoCompletionCandidates;
import hudson.model.Item;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import hudson.plugins.favorite.Messages;

public class FavoriteUserPropertyDescriptor extends UserPropertyDescriptor {

    public FavoriteUserPropertyDescriptor() {
        super(FavoriteUserProperty.class);
    }

    @Override
    public UserProperty newInstance(User user) {
        return new FavoriteUserProperty();
    }

    @Override
    public String getDisplayName() {
        return Messages.favoriteUserPropertyDescriptor();
    }

    @SuppressWarnings(value = "unused") // used by jelly
    public String toItemUrl(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return null;
        }

        Jenkins jenkins = Jenkins.get();
        Item item = jenkins.getItemByFullName(fullName);
        if (item == null) {
          return null;
        }

        return jenkins.getRootUrl() + item.getUrl();
    }

}
