function toggleFavorite(job, a) {
  new Ajax.Request('/plugin/favorite/toggleFavorite?job=' + job);
  image = document.getElementById("fav_" + job);
  if(image.src.match(/star.gif$/)) {
    image.src = image.src.replace("star.gif", "star-gold.gif");
    hoverNotification('Favorite added', a.parentNode);
  } else {
    image.src = image.src.replace("star-gold.gif", "star.gif");
    hoverNotification('Favorite deleted', a.parentNode);
  }
  return false;
}