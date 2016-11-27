# Jenkins Favorite Plugin

See: https://wiki.jenkins-ci.org/display/JENKINS/Favorite+Plugin


## Theme Developer Notice

UI Files:

  * See JS: [javascript.jelly](./src/main/resources/hudson/plugins/favorite/assets/javascript.jelly)
  * See CSS: [css.jelly](./src/main/resources/hudson/plugins/favorite/assets/css.jelly)

You can subscribe to the fav-icon change event:

```
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

```
mvn clean package hpi:run -DskipTests -Djenkins.version=2.32
```

Now go to http://localhost:8080/jenkins and test the Plugin.
You might need to update plugins via GUI.

Remember to clean the `work` dir if something strange is happening, like plugin is not reloaded.

```
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
 
## License

to be done