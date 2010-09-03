package hudson.plugins.favorite.user;

import hudson.model.AutoCompletionCandidates;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import org.kohsuke.stapler.QueryParameter;

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
        return "Favorites";
    }

    public AutoCompletionCandidates doAutoCompleteJob(@QueryParameter String value) {
        AutoCompletionCandidates c = new AutoCompletionCandidates();
        for (String job : Hudson.getInstance().getJobNames()) {
            if (job.toLowerCase().startsWith(job.toLowerCase())) {
                c.add(job);
            }
        }
        return c;
    }

}
