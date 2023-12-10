package hudson.plugins.favorite.project;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TopLevelItem;
import java.util.ArrayList;
import java.util.Collection;
import jenkins.model.TransientActionFactory;

@Extension
public class FavoriteProjectActionFactory extends TransientActionFactory<TopLevelItem> {
    @Override
    public Collection<? extends Action> createFor(TopLevelItem target) {
        ArrayList<FavoriteProjectAction> ta = new ArrayList<>();
        ta.add(new FavoriteProjectAction(target));
        return ta;
    }

    @Override
    public Class<TopLevelItem> type() {
        return TopLevelItem.class;
    }
}
