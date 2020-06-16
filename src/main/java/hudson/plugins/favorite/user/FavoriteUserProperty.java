package hudson.plugins.favorite.user;

import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Do not use directly.
 * Instead @Inject {@link hudson.plugins.favorite.Favorites}.
 */
@ExportedBean(defaultVisibility = 999)
public class FavoriteUserProperty extends UserProperty {

    private static final Logger LOGGER = Logger.getLogger(FavoriteUserProperty.class.getName());

    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new FavoriteUserPropertyDescriptor();

    @DataBoundConstructor
    public FavoriteUserProperty() {}

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    transient List<String> favorites = new ArrayList<>();

    private ConcurrentMap<String, Boolean> data = new ConcurrentHashMap();

    private transient long lastValidated = 0;

    /**
     * Use {#getAllFavorites()}
     * @return favorites
     */
    @Deprecated
    public List<String> getFavorites() {
        removeFavoritesWhichDoNotExist();
        return Collections.unmodifiableList( data.entrySet().stream().filter( input -> input != null && input.getValue()  )
                                                 .map( stringBooleanEntry -> stringBooleanEntry.getKey() )
        .collect( Collectors.toList()) );
    }

    public Set<String> getAllFavorites() {
        removeFavoritesWhichDoNotExist();
        return Maps.filterEntries( data, input -> input != null && input.getValue() ).keySet();
    }

    public void addFavorite(String job) throws IOException {
        data.put(job, true);
        user.save();
    }

    public void removeFavorite(String job) throws IOException {
        data.put(job, false);
        user.save();
    }

    public boolean toggleFavorite(String job) throws IOException {
        if (isJobFavorite(job)) {
            removeFavorite(job);
            return false;
        } else {
            addFavorite(job);
            return true;
        }
    }

    void deleteFavourite(String job) throws IOException {
        data.remove(job);
        user.save();
    }

    @Override
    public UserProperty reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
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
            data =  new ConcurrentHashMap();
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
            if (jenkins.getItemByFullName(fullName) == null) {
                try {
                    deleteFavourite(fullName);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Could not purge favorite '" + fullName + "'", e);
                }
            }
        }
    }

}
