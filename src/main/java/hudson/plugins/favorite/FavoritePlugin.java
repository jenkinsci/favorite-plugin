package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites.FavoriteException;
import java.io.IOException;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.interceptor.RequirePOST;

@Restricted(NoExternalUse.class)
public class FavoritePlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(FavoritePlugin.class.getName());
    @RequirePOST
    public void doToggleFavorite(StaplerResponse2 resp, @QueryParameter String job, @QueryParameter Boolean redirect) throws IOException {
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
                resp.sendRedirect2(jenkins.getRootUrl() + item.getUrl());
            }
        } else {
            throw HttpResponses.forbidden();
        }
    }
}
