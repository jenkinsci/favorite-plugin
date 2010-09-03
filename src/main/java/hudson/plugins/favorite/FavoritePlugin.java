package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import org.acegisecurity.Authentication;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

public class FavoritePlugin extends Plugin {
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job, @QueryParameter String userName) {
        if ("".equals(userName) || userName == null) {
            Authentication authentication = Hudson.getAuthentication();
            userName = authentication.getName();
        }
        if (!userName.equals("anonymous")) {
            User user = Hudson.getInstance().getUser(userName);
            FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
            try {
                if (fup == null) {
                    user.addProperty(new FavoriteUserProperty());
                    fup = user.getProperty(FavoriteUserProperty.class);
                }
                fup.toggleFavorite(job);
                user.save();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
