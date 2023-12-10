package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites.FavoriteException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

@Restricted(NoExternalUse.class)
public class FavoritePlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(FavoritePlugin.class.getName());
    @RequirePOST
    public void doToggleFavorite(StaplerResponse resp, @QueryParameter String job, @QueryParameter Boolean redirect) throws IOException {
        Jenkins jenkins = Jenkins.get();
        User user = User.current();
        if (user != null) {
            try {
                Favorites.toggleFavorite(user, getItem(job));
            } catch (FavoriteException e) {
                throw new IOException(e);
            } catch (IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job with name \"" + job + "\" not found.");
            }
        }
        if (redirect) {
            String url = "job/" + String.join("/job/", job.split("/"));
            resp.sendRedirect(resp.encodeRedirectURL(jenkins.getRootUrl() + url));
        }
    }

    public static Item getItem(String fullName) {
        Jenkins jenkins = Jenkins.get();
        Item item = jenkins.getItemByFullName(fullName);
        if (item == null) {
            throw new IllegalArgumentException("Item <" + fullName + "> does not exist");
        }
        return item;
    }

}
