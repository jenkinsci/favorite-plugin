package hudson.plugins.favorite.project;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.favorite.Messages;
import hudson.plugins.favorite.user.FavoriteUserProperty;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

public class FavoriteProjectAction implements Action {
    final private AbstractProject<?, ?> project;

    public FavoriteProjectAction(AbstractProject project) {
        this.project = project;
    }

    public String getProjectName() {
        return project.getName();
    }

    public String getIconFileName() {
        if (hasPermission()) {
            if (isFavorite()) {
                return "star-gold.png";
            } else {
                return "star.png";
            }
        }
        return null;
    }

    public String getDisplayName() {
        if (hasPermission()) {
            return Messages.favoriteColumn();
        }
        return null;
    }

    public String getUrlName() {
        if (hasPermission()) {
            try {
				return Hudson.getInstance().getRootUrl() + "plugin/favorite/toggleFavorite?job=" + URLEncoder.encode(getProjectName(),"UTF-8") + "&userName=" + URLEncoder.encode(getUserName(),"UTF-8") + "&redirect=true";
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
        if (!userName.equals("anonymous")) {
            return true;
        } else {
            return false;
        }
    }

    private String getUserName() {
        Authentication authentication = Hudson.getAuthentication();
        return authentication.getName();
    }

    private boolean isFavorite() {
        Authentication authentication = Hudson.getAuthentication();
        String userName = authentication.getName();
        if (!userName.equals("anonymous")) {
            User user = Hudson.getInstance().getUser(userName);
            FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
            return fup.isJobFavorite(project.getName());
        } else {
            return false;
        }
    }
}
