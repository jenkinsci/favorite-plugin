package hudson.plugins.favorite.project;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Messages;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FavoriteProjectAction implements Action {
    final private AbstractProject<?, ?> project;

    public FavoriteProjectAction(AbstractProject project) {
        this.project = project;
    }

    public String getProjectName() {
        return project.getFullName();
    }

    public String getIconFileName() {
        if (hasPermission() && isSupportedJobType()) {
            return isFavorite() ? "star-large-gold.svg" : "star-large.svg";
        }
        return null;
    }

    public String getDisplayName() {
        if (hasPermission() && isSupportedJobType()) {
            return Messages.favoriteColumn();
        }
        return null;
    }

    public String getUrlName() {
        if (hasPermission() && isSupportedJobType()) {
            try {
                Jenkins jenkins = Jenkins.get();
                if (jenkins == null) {
                    throw new IllegalStateException("Jenkins not started");
                }
                return "plugin/favorite/toggleFavorite?job=" + URLEncoder.encode(getProjectName(), "UTF-8") + "&userName=" + URLEncoder.encode(getUserName(), "UTF-8") + "&redirect=true";
            } catch (UnsupportedEncodingException e) {
				
	            return null;
			}
        } else {
            return null;
        }
    }

    private boolean hasPermission() {
        Authentication authentication = Hudson.getAuthentication();
        String userName = authentication.getName();
        return !userName.equals( "anonymous" );
    }
    
    private boolean isSupportedJobType() {
      // MatrixProjects are supported, but the underlying MatrixConfigurations
      // cannot be referenced like other items because they are using the same
      // pattern like folders
        return !( project instanceof MatrixConfiguration );
    }

    private String getUserName() {
        Authentication authentication = getJenkins().getAuthentication();
        return authentication.getName();
    }

    private boolean isFavorite() {
        Authentication authentication = Hudson.getAuthentication();
        String userName = authentication.getName();
        if (!userName.equals("anonymous")) {
            User user = getJenkins().getUser(userName);
            return user != null && Favorites.isFavorite(user, project);
        }

        return false;
    }

    @NonNull
    Jenkins getJenkins() {
        Jenkins jenkins = Jenkins.get();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        return jenkins;
    }
}
