package hudson.plugins.favorite;

import hudson.Plugin;
import hudson.model.User;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import org.acegisecurity.Authentication;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import jenkins.model.Jenkins;

public class FavoritePlugin extends Plugin {
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job, @QueryParameter String userName, @QueryParameter Boolean redirect) throws IOException {
        if ("".equals(userName) || userName == null) {
            Authentication authentication = Jenkins.getAuthentication();
            userName = authentication.getName();
        }
        if (!userName.equals("anonymous")) {
            User user = Jenkins.getInstance().getUser(userName);
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
        if(redirect) {
            if (!job.contains("/"))
            {
              // Works for default URL pattern: rootUrl/job/jobName
              resp.sendRedirect(resp.encodeRedirectURL(Jenkins.getInstance().getRootUrl() + "job/" + job));
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
              resp.sendRedirect(resp.encodeRedirectURL(Jenkins.getInstance().getRootUrl() + urlPostfix.toString()));
            }
        }
    }
}
