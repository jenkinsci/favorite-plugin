function toggleFavorite(job, user, a) {
  new Ajax.Request(rootURL+'/plugin/favorite/toggleFavorite?job=' + job + "&userName=" + user);
  image = document.getElementById("fav_" + job);
  if (image.classList.contains("icon-fav-inactive")) {
    image.src = image.src.replace("star.png", "star-gold.png");
    image.classList.add("icon-fav-active");
    image.classList.remove("icon-fav-inactive");
    hoverNotification('Favori ajouté', a.parentNode);
  } else {
    image.src = image.src.replace("star-gold.png", "star.png");
    image.classList.add("icon-fav-inactive");
    image.classList.remove("icon-fav-active");
    hoverNotification('Favori supprimé', a.parentNode);
  }
  return false;
}
