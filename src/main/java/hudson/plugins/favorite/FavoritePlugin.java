package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites.FavoriteException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.annotation.Nonnull;
import java.io.IOException;

public class FavoritePlugin extends Plugin {
    // For stapler
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job, @QueryParameter Boolean redirect) throws IOException {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        User user = User.current();
        if (user != null && !isAnonymous(user)) {
            try {
                Favorites.toggleFavorite(user, getItem(job));
            } catch (FavoriteException e) {
                throw new IOException(e);
            }
        }
        if(redirect) {
            if (!job.contains("/"))
            {
              // Works for default URL pattern: rootUrl/job/jobName
              resp.sendRedirect(resp.encodeRedirectURL(jenkins.getRootUrl() + "job/" + job));
            }
            else
            {
              // Works for folder URL pattern:
              // rootUrl/job/folder/job/jobName
              // rootUrl/job/folder/job/subfolder/job/jobName etc.
              StringBuilder urlPostfix = new StringBuilder("job/");
              String[] itemNames = job.split("/");
              for (int i = 0; i < itemNames.length; i++)
              {
                urlPostfix.append(itemNames[i]);                  
                if (i < itemNames.length - 1)
                {
                  urlPostfix.append("/job/");
                }
              }
              resp.sendRedirect(resp.encodeRedirectURL(jenkins.getRootUrl() + urlPostfix.toString()));
            }
        }
    }

    static boolean isAnonymous(@Nonnull User user) {
        return user.getFullName().equalsIgnoreCase("anonymous");
    }

    public static Item getItem(String fullName) {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        Item item = jenkins.getItemByFullName(fullName);
        if (item == null) {
            throw new IllegalArgumentException("Item <" + fullName + "> does not exist");
        }
        return item;
    }
}
