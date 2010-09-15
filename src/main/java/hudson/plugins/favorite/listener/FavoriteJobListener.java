package hudson.plugins.favorite.listener;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.User;
import hudson.model.listeners.ItemListener;
import hudson.plugins.favorite.user.FavoriteUserProperty;

import java.io.IOException;

@Extension
public class FavoriteJobListener extends ItemListener {

  @Override
  public void onRenamed(Item item, String oldName, String newName) {
    if (item instanceof AbstractProject<?, ?>) {
      for (User user : User.getAll()) {
        FavoriteUserProperty fup = user.getProperty(FavoriteUserProperty.class);
        try {
          if (fup != null) {
            if (fup.isJobFavorite(oldName)) {
              fup.removeFavorite(oldName);
              fup.addFavorite(newName);
              user.save();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
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
            if (fup.isJobFavorite(item.getName())) {
              fup.removeFavorite(item.getName());
              user.save();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
