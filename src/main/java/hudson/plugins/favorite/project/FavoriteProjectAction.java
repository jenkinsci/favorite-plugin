package hudson.plugins.favorite.project;

import hudson.model.Action;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.Messages;
import org.jenkins.ui.icon.IconSpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FavoriteProjectAction implements Action, IconSpec {
    final private TopLevelItem topLevelItem;

    public FavoriteProjectAction(TopLevelItem topLevelItem) {
        this.topLevelItem = topLevelItem;
    }

    public String getItemName() {
        return topLevelItem.getFullName();
    }

    /**
     * Returns the items name
     * @return item name
     * @deprecated use {@link #getItemName()}
     */
    @Deprecated
    public String getProjectName() {
        return getItemName();
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getIconClassName() {
        if (hasPermission()) {
            return isFavorite()
                    ? "symbol-star plugin-ionicons-api"
                    : "symbol-star-outline plugin-ionicons-api";
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
            return "plugin/favorite/toggleFavorite?job=" + URLEncoder.encode(getItemName(), StandardCharsets.UTF_8) + "&redirect=true";
        } else {
            return null;
        }
    }

    private boolean hasPermission() {
        return User.current() != null;
    }

    /**
     * Check if associated project is marked as favorite.
     * @return {@code true} if project is marked as favorite
     */
    public boolean isFavorite() {
        User user = User.current();
        return user != null && Favorites.isFavorite(user, topLevelItem);
    }
}
