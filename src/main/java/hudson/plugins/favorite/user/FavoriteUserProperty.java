package hudson.plugins.favorite.user;

import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.export.ExportedBean;
import org.springframework.security.access.AccessDeniedException;

/**
 * Do not use directly.
 * Instead @Inject {@link hudson.plugins.favorite.Favorites}.
 */
@ExportedBean(defaultVisibility = 999)
public class FavoriteUserProperty extends UserProperty implements Action {

    private static final Logger LOGGER = Logger.getLogger(FavoriteUserProperty.class.getName());

    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new FavoriteUserPropertyDescriptor();

    @DataBoundConstructor
    public FavoriteUserProperty() {
    }

    public User getUser() {
        return user;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    transient List<String> favorites = new ArrayList<>();

    private ConcurrentMap<String, Boolean> data = new ConcurrentHashMap<>();

    private transient long lastValidated = 0;

    /**
     * Use {#getAllFavorites()}
     * @return unmodifiable favorites job set
     */
    @Deprecated
    public List<String> getFavorites() {
        removeFavoritesWhichDoNotExist();
        return data.entrySet().stream()
                .filter(input -> input != null && input.getValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    // Jelly usage only
    @Restricted(NoExternalUse.class)
    public List<Item> getFavoritesSortedByDisplayName() {
        final Jenkins jenkins = Jenkins.get();
        return getAllFavorites().stream().map(jenkins::getItemByFullName).filter(Objects::nonNull)
                .sorted(Comparator.comparing(Item::getFullDisplayName, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    /**
     * Get all favorite jobs.
     * @return unmodifiable favorites job set
     */
    public Set<String> getAllFavorites() {
        removeFavoritesWhichDoNotExist();
        return Maps.filterEntries( data, input -> input != null && input.getValue() ).keySet();
    }

    /**
     * Add favorite job.
     * @param job job full name
     * @throws IOException if error saving status
     */
    public void addFavorite(@NonNull String job) throws IOException {
        data.put(job, true);
        user.save();
    }

    /**
     * Set un-favorite a job.
     * @param job full job name
     * @throws IOException if error saving status
     */
    public void removeFavorite(@NonNull String job) throws IOException {
        data.put(job, false);
        user.save();
    }

    /**
     * If job is favorited, then un-favorite it, else favorite it.
     * @param job full job name
     * @return true if the new status is favorite, false if new status is not favorite.
     * @throws IOException if error saving status
     */
    public boolean toggleFavorite(@NonNull String job) throws IOException {
        if (isJobFavorite(job)) {
            removeFavorite(job);
            return false;
        } else {
            addFavorite(job);
            return true;
        }
    }

    void deleteFavourite(@NonNull String job) throws IOException {
        data.remove(job);
        user.save();
    }

    @Override
    public UserProperty reconfigure(StaplerRequest2 req, JSONObject form) throws Descriptor.FormException {
        return this;
    }

    public boolean isJobFavorite(String job) {
        Boolean favorite;
        return ((favorite = data.get(job)) != null) ? favorite : false;
    }

    /**
     * Checks if the user has a favorite entry for this job
     * e.g. this is true when the user has favoriteted or unfavoriteted a job
     * but not true for when a job has not been favorited by this user
     * @param job path
     * @return favorite
     */
    public boolean hasFavorite(String job) {
        return data.containsKey(job);
    }

    @Override
    public UserPropertyDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Migrates this properties storage from favourite's list to a map of booleans
     * @return this
     */
    Object readResolve() {
        if (favorites != null) {
            data =  new ConcurrentHashMap<>();
            for (String job : favorites) {
                data.put(job, true);
            }
            favorites = null;
        }
        return this;
    }

    private void removeFavoritesWhichDoNotExist() {
        Jenkins jenkins = Jenkins.get();
        for (String fullName : Collections.unmodifiableSet(data.keySet())) {
            try {
                if (jenkins.getItemByFullName(fullName) == null) {
                    try {
                        deleteFavourite(fullName);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Could not purge favorite '" + fullName + "'", e);
                    }
                }
            } catch (AccessDeniedException ade) {
                // user has probably only discover permission, but not read permission
                // we cannot check if the job still exists, so delete from favorites
                LOGGER.log(Level.FINE, "Cannot check existence of favorite '" + fullName + "'", ade);
                try {
                    deleteFavourite(fullName);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Could not purge favorite '" + fullName + "'", e);
                }
            }
        }
    }

    @Override
    public String getIconFileName() {
        return "symbol-star plugin-ionicons-api";
    }

    @Override
    public String getDisplayName() {
        return "Favorites";
    }

    @Override
    public String getUrlName() {
        return "favorites";
    }
}
