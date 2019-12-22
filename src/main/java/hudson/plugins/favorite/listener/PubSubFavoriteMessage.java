package hudson.plugins.favorite.listener;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.User;
import hudson.security.AccessControlled;
import javax.annotation.Nonnull;

import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pubsub.EventProps;
import org.jenkinsci.plugins.pubsub.AccessControlledMessage;
import org.jenkinsci.plugins.pubsub.Message;

@Extension(optional = true)
public class PubSubFavoriteMessage extends AccessControlledMessage<PubSubFavoriteMessage> {
    transient Item item;
    transient User user;

    /**
     * Create a plain message instance.
     */
    public PubSubFavoriteMessage() {
        super();
    }

    /**
     * Create a message instance associated with a Jenkins {@link Item}.
     * @param item The Jenkins {@link Item} with this message instance is to be associated.
     * @param user The Jenkins {@link User} with this message instance is to be associated.
     */
    public PubSubFavoriteMessage(@Nonnull final Item item, @Nonnull final User user) {
        super();
        this.item = item;
        this.user = user;
        set(EventProps.Job.job_name, item.getName());
        set(EventProps.Jenkins.jenkins_channel, "favorites");
        set(EventProps.Jenkins.jenkins_object_id, item.getName());
        set(EventProps.Jenkins.jenkins_object_url, item.getUrl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message clone() {
        final Message clone = new PubSubFavoriteMessage();
        clone.putAll(this);
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AccessControlled getAccessControlled() {
        return this.user;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Permission getRequiredPermission() {
        return Jenkins.READ;
    }
}