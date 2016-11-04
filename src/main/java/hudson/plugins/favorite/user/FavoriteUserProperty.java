package hudson.plugins.favorite.user;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Do not use directly.
 * Instead @Inject {@link hudson.plugins.favorite.Favorites}.
 */
@ExportedBean(defaultVisibility = 999)
public class FavoriteUserProperty extends UserProperty {

    @Extension
    public static final UserPropertyDescriptor DESCRIPTOR = new FavoriteUserPropertyDescriptor();

    @DataBoundConstructor
    public FavoriteUserProperty() {}

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    transient List<String> favorites = new ArrayList<String>();

    private ConcurrentMap<String, Boolean> data = Maps.newConcurrentMap();

    public Set<String> getFavorites() {
        return Maps.filterEntries(data, new Predicate<Entry<String, Boolean>>() {
            @Override
            public boolean apply(@Nullable Entry<String, Boolean> input) {
                return input != null && input.getValue();
            }
        }).keySet();
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
            data = Maps.newConcurrentMap();
            for (String job : favorites) {
                data.put(job, true);
            }
            favorites = null;
        }
        return this;
    }
}