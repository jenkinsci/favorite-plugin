package hudson.plugins.favorite.project;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.ArrayList;
import java.util.Collection;

@Extension
public class FavoriteProjectActionFactory extends TransientProjectActionFactory {
    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        ArrayList<FavoriteProjectAction> ta = new ArrayList<>();
		ta.add(new FavoriteProjectAction(target));
        return ta;
    }
}
