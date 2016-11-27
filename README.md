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

## License

Licensed under [MIT License](./LICENSE.md)
