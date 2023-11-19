package hudson.plugins.favorite.project;

import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Messages;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FavoriteProjectAction implements Action {
    final private TopLevelItem topLevelItem;

    public FavoriteProjectAction(TopLevelItem project) {
        this.topLevelItem = project;
    }

    public String getItemName() {
        return topLevelItem.getFullName();
    }

    @Override
    public String getIconFileName() {
        if (hasPermission()) {
            return isFavorite() ? "star-large-gold.svg" : "star-large.svg";
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        if (hasPermission()) {
            return Messages.favoriteColumn();
        }
        return null;
    }

    @Override
    public String getUrlName() {
        if (hasPermission()) {
            try {
                Jenkins jenkins = Jenkins.get();
                if (jenkins == null) {
                    throw new IllegalStateException("Jenkins not started");
                }
                return "plugin/favorite/toggleFavorite?job=" + URLEncoder.encode(getItemName(), "UTF-8") + "&userName=" + URLEncoder.encode(getUserName(), "UTF-8") + "&redirect=true";
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
    
    private String getUserName() {
        Authentication authentication = getJenkins().getAuthentication();
        return authentication.getName();
    }

    private boolean isFavorite() {
        Authentication authentication = Hudson.getAuthentication();
        String userName = authentication.getName();
        if (!userName.equals("anonymous")) {
            User user = getJenkins().getUser(userName);
            return user != null && Favorites.isFavorite(user, topLevelItem);
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
