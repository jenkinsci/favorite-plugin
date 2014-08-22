function toggleFavorite(job, user, a) {
  new Ajax.Request(rootURL+'/plugin/favorite/toggleFavorite?job=' + job + "&userName=" + user);
  image = document.getElementById("fav_" + job);
  if(image.src.match(/star.png$/)) {
    image.src = image.src.replace("star.png", "star-gold.png");
    hoverNotification('Favori ajouté', a.parentNode);
  } else {
    image.src = image.src.replace("star-gold.png", "star.png");
    hoverNotification('Favori supprimé', a.parentNode);
  }
  return false;
}
