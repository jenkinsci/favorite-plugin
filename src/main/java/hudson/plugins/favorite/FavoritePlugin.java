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
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job) {
        Authentication authentication = Hudson.getAuthentication();
        String name = authentication.getName();
        System.out.println("NAME: " + name);
        if (authentication.isAuthenticated()) {
            User user = Hudson.getInstance().getUser(name);
            FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
            try {
                if (fup == null) {
                    user.addProperty(new FavoriteUserProperty());
                }
                fup.toggleFavorite(job);
                user.save();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
