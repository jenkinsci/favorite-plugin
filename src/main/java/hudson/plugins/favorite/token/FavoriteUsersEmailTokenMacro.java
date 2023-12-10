package hudson.plugins.favorite.token;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.tasks.Mailer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.tokenmacro.DataBoundTokenMacro;

@Extension
public class FavoriteUsersEmailTokenMacro extends DataBoundTokenMacro {
    @Override
    public String evaluate(AbstractBuild<?, ?> context, TaskListener listener, String macroName) {
        return evaluate(context, context.getWorkspace(), listener, macroName);
    }

    @Override
    public String evaluate(Run<?,?> run, FilePath workspace, TaskListener listener, String macroName) {
        List<String> users = new ArrayList<>();
        Job<?,?> project = run.getParent();
        for (User user : User.getAll()) {
            if (Favorites.isFavorite(user, project)) {
                // probably various ways to get this across various Jenkins installs.
                Mailer.UserProperty mail = user.getProperty(Mailer.UserProperty.class);
                users.add(mail.getAddress());
            }
        }
        return StringUtils.join(users, ",");
    }

    @Override
    public boolean acceptsMacroName(String macroName) {
        return macroName.equals("FAVORITE_USERS_EMAIL");
    }
}
