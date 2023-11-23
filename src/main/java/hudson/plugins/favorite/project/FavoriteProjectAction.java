package hudson.plugins.favorite.project;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Messages;
import org.jenkins.ui.icon.IconSpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FavoriteProjectAction implements Action, IconSpec {
    final private AbstractProject<?, ?> project;

    public FavoriteProjectAction(AbstractProject project) {
        this.project = project;
    }

    public String getProjectName() {
        return project.getFullName();
    }

    public String getIconFileName() {
        return null;
    }

    @Override
    public String getIconClassName() {
        if (hasPermission() && isSupportedJobType()) {
            return isFavorite()
                    ? "symbol-star plugin-ionicons-api"
                    : "symbol-star-outline plugin-ionicons-api";
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
            return "plugin/favorite/toggleFavorite?job=" + URLEncoder.encode(getProjectName(), StandardCharsets.UTF_8) + "&redirect=true";
        } else {
            return null;
        }
    }

    private boolean hasPermission() {
        return User.current() != null;
    }
    
    private boolean isSupportedJobType() {
      // MatrixProjects are supported, but the underlying MatrixConfigurations
      // cannot be referenced like other items because they are using the same
      // pattern like folders
        return !( project instanceof MatrixConfiguration );
    }

    private boolean isFavorite() {
        User user = User.current();
        return user != null && Favorites.isFavorite(user, project);
    }

}
