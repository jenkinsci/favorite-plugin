package hudson.plugins.favorite.user;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.listeners.ItemListener;

import hudson.plugins.favorite.listener.FavoriteListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class FavoriteJobListener extends ItemListener {

  private final static Logger LOGGER = Logger.getLogger(FavoriteJobListener.class.getName());

  @Override
  public void onLocationChanged(Item item, String oldName, String newName) {
    if (item instanceof TopLevelItem) {
      for (User user : User.getAll()) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        try {
          if (fup != null && fup.isJobFavorite(oldName)) {
            fup.deleteFavourite(oldName);
            fup.addFavorite(newName);
            FavoriteListener.fireOnLocationChangedFavorite(item, user, oldName, newName);
          }
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Could not migrate favourites from <" + oldName + "> to <" + newName + ">. Favourites have been lost for this item.", e);
        }
      }
    }
  }
}
