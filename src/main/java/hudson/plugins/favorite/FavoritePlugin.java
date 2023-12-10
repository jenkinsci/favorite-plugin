package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites.FavoriteException;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;

@Restricted(NoExternalUse.class)
public class FavoritePlugin extends Plugin {

    @RequirePOST
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job, @QueryParameter Boolean redirect) throws IOException {
        User user = User.current();
        if (user != null) {
            Jenkins jenkins = Jenkins.get();
            Item item = jenkins.getItemByFullName(job);
            if (item == null) {
                throw HttpResponses.notFound();
            }

            try {
                Favorites.toggleFavorite(user, item);
            } catch (FavoriteException fe) {
                throw HttpResponses.error(fe);
            }

            if (redirect) {
                resp.sendRedirect(jenkins.getRootUrl() + item.getUrl());
            }
        } else {
            throw HttpResponses.forbidden();
        }
    }

}
