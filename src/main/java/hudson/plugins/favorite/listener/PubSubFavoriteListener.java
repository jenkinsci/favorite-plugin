package hudson.plugins.favorite.listener;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.User;

import org.jenkinsci.plugins.pubsub.MessageException;
import org.jenkinsci.plugins.pubsub.PubsubBus;

@Extension(optional = true)
public class PubSubFavoriteListener extends FavoriteListener {
    @Override
    public void onAddFavourite(final Item item, final User user) {
        try {
            PubsubBus.getBus().publish(new PubSubFavoriteMessage(item, user).setEventName("favorite_add"));
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoveFavourite(final Item item, final User user) {
        try {
            PubsubBus.getBus().publish(new PubSubFavoriteMessage(item, user).setEventName("favorite_remove"));
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }
}