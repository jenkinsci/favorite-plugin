package hudson.plugins.favorite.user;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.User;
import hudson.model.listeners.ItemListener;
import hudson.plugins.favorite.FavoritePlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class FavoriteJobListener extends ItemListener {

  private final static Logger LOGGER = Logger.getLogger(FavoriteJobListener.class.getName());

  @Override
  public void onRenamed(Item item, String oldName, String newName) {
    if (item instanceof AbstractProject<?, ?>) {
      for (User user : User.getAll()) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        try {
          if (fup != null) {
            fup.deleteFavourite(oldName);
            fup.addFavorite(newName);
          }
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Could not migrate favourites from <" + oldName + "> to <" + newName + ">. Favourites have been lost for this item.", e);
        }
      }
    }
  }

  @Override
  public void onDeleted(Item item) {
    if (item instanceof AbstractProject<?, ?>) {
      for (User user : User.getAll()) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        try {
          if (fup != null) {
            fup.deleteFavourite(item.getFullName());
          }
        } catch (IOException e) {
          LOGGER.log(Level.WARNING, "Remove favourites deleted item <" + item.getFullName() + ">.", e);
        }
      }
    }
  }
}
