package hudson.plugins.favorite.user;

import hudson.model.AutoCompletionCandidates;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.ItemGroup;
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

    public AutoCompletionCandidates doAutoCompleteJob(@QueryParameter String value) {
        AutoCompletionCandidates c = new AutoCompletionCandidates();
        for (String job : Hudson.getInstance().getJobNames()) {
            if (job.toLowerCase().startsWith(value.toLowerCase())) {
                c.add(job);
            }
        }
        return c;
    }

    public Item toItem(String fullName) {
        if (StringUtils.isEmpty(fullName)) return null;
        ItemGroup<? extends Item> container = Jenkins.getInstance();
        int start = 0;
        int index = fullName.indexOf('/', start);
        while (index != -1) {
            Item item = container.getItem(fullName.substring(start, index));
            if (item instanceof ItemGroup) {
                container = (ItemGroup<? extends Item>) item;
                start = index+1;
                index = fullName.indexOf('/', start);
            } else {
                index = fullName.indexOf('/', index + 1);
            }
        }
        return container.getItem(fullName.substring(start));
    }

}
