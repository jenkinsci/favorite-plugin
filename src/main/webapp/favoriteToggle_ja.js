function toggleFavorite(job, user, a) {
  new Ajax.Request(rootURL+'/plugin/favorite/toggleFavorite?job=' + job + "&userName=" + user);
  image = document.getElementById("fav_" + job);
  if(image.src.match(/star.png$/)) {
    image.src = image.src.replace("star.png", "star-gold.png");
    hoverNotification('お気に入りに追加', a.parentNode);
  } else {
    image.src = image.src.replace("star-gold.png", "star.png");
    hoverNotification('お気に入りから削除', a.parentNode);
  }
  return false;
}
