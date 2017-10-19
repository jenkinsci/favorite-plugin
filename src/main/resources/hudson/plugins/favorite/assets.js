function toggleFavorite(job, a) {
  new Ajax.Request(rootURL + "/plugin/favorite/toggleFavorite?job=" + job, {method: 'POST'});
  var favIcon = document.getElementById("fav_" + job);
  if (favIcon.classList.contains("icon-fav-inactive")) {
    favIcon.classList.add("icon-fav-active");
    favIcon.classList.remove("icon-fav-inactive");
    hoverNotification("Favorite added", a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
      transition: "active-to-inactive",
      job: job
    }));
  } else {
    favIcon.classList.add("icon-fav-inactive");
    favIcon.classList.remove("icon-fav-active");
    hoverNotification("Favorite deleted", a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
      transition: "inactive-to-active",
      job: job
    }));
  }
  return false;
}
