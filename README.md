# Jenkins Favorite Plugin

This plugin allows you to mark a job a favorite.This is controlled via a
list view column you need to add to a view. You can then click on a star
to favorite or unfavorite a job. There is also a job filter to allow you
to create a view that only shows your favorites.

#### Precaution

Older versions of this plugin may not be safe to use. Please review the
following warnings before using an older version:

-   [Missing permission check allows anyone to change favorites for any
    other user](https://jenkins.io/security/advisory/2017-06-06/)
-   [CSRF vulnerability allows changing another user's
    favorites](https://jenkins.io/security/advisory/2017-06-06/)

## Configuration

It is recommended you change your default view to either one you create,
or follow the directions in [Editing or Replacing the All
View](https://wiki.jenkins.io/display/JENKINS/Editing+or+Replacing+the+All+View)
to allow the Favorite column to show up. Once you have the Favorite
column, it will show a star that logged in users will be able to click
to make a favorite. You can also add another view, and apply the
"Favorites Filter" to show favorites only in that view.

## Theme Developer Notice

UI Files:

  * See JS: [assets.js](./src/main/resources/hudson/plugins/favorite/assets.js)
  * See CSS: [assets.css](./src/main/resources/hudson/plugins/favorite/assets.css)

You can subscribe to the fav-icon change event:

```js
window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
  transition: "active-to-inactive",
  job: job
}));
```


## Development

### Prerequisites

You need the following tools installed on your system.

  * [Apache Maven](https://maven.apache.org/)

### Build

Build the `target/favorite.hpi` plugin file with the `mvn package` command.

### Run

Startup Jenkins with the Plugin.  

```shell
mvn clean package hpi:run -DskipTests -Djenkins.version=2.32
```

Now go to http://localhost:8080/jenkins and test the Plugin.
You might need to update plugins via GUI.

Remember to clean the `work` dir if something strange is happening, like plugin is not reloaded.

```shell
rm -rf work/*
```

### Pre Release Tests

 **Testcase 1: Favorite Column on Job List Page**

  * You are on the Job List Page and have created at least one Build Job.
  * On the right a column 'Fav' needs to show up and show a grey star for the job.
  * With a click on the grey star the star turns golden and the job is added as favorite.
  * Preview:
    * ![testcase-1-favorite-column](https://cloud.githubusercontent.com/assets/12599965/20640106/2d7b5094-b3d6-11e6-8623-180056acb82d.gif)


 **Testcase 2: Favorite Entries on User Config Page**

  * On the Jenkins Start Page click on the small arrow right to your username in the top right corner.
  * In the appearing dropdown click on configure.
  * When scrolling down to 'Favorites' section you will see all your favorites.
  * When clicking the golden star you can remove a job as favorite.
  * Preview:
    * ![testcase-2-favorite-config](https://cloud.githubusercontent.com/assets/12599965/20640200/f0c3b806-b3d7-11e6-9fd9-43a2676b0dc8.gif)

## Changelog
For recent versions, see [GitHub Releases](https://github.com/jenkinsci/favorite-plugin/releases)

For versions 2.3.2 and older, see [CHANGELOG.md](CHANGELOG.md)

## License

Licensed under [MIT License](./LICENSE.md)

## Author

Larry Shatzer
