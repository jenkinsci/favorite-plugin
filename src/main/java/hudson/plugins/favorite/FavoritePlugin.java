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
    public void doToggleFavorite(StaplerRequest req, StaplerResponse resp, @QueryParameter String job, @QueryParameter String userName, @QueryParameter Boolean redirect) throws IOException {
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
        if(redirect) {
            if (!job.contains("/"))
            {
              resp.sendRedirect(resp.encodeRedirectURL(Hudson.getInstance().getRootUrl() + "job/" + job));
            }
            else
            {
              // The fullName is always using the pattern 'job/subJob', but Jenkins is not always using the 
              // same URL pattern. Folders are internally stored as child jobs and therefore using a '/job/'
              // between each name in the URL while matrix childs are directly appended. Therefore this 
              // approach is working for standard jobs, folder hierachies and matrix parent jobs, but not for
              // matrix child jobs.
              // TODO think about alternatives to support or to disable favorites for matrix childs
              // -->> getItemByFullName
              // default: rootUrl/job/jobName
              // folders: rootUrl/job/folder/job/subfolder/job/jobName
              // matrix : rootUrl/job/jobName/subJobName
              String[] folderHierarchy = job.split("/");
              StringBuilder urlPostfix = new StringBuilder("job/");
              for (int i = 0; i < folderHierarchy.length; i++)
              {
                urlPostfix.append(folderHierarchy[i]);
                if (i < folderHierarchy.length - 1)
                {
                  urlPostfix.append("/job/");
                }
              }
              resp.sendRedirect(resp.encodeRedirectURL(Hudson.getInstance().getRootUrl() + urlPostfix.toString()));
            }
        }
    }
}
