function toggleFavorite(job, user, a) {
  new Ajax.Request(rootURL+'/plugin/favorite/toggleFavorite?job=' + job + "&userName=" + user);
  image = document.getElementById("fav_" + job);
  if(image.src.match(/star.gif$/)) {
    image.src = image.src.replace("star.gif", "star-gold.gif");
    hoverNotification('お気に入りに追加', a.parentNode);
  } else {
    image.src = image.src.replace("star-gold.gif", "star.gif");
    hoverNotification('お気に入りから削除', a.parentNode);
  }
  return false;
}
