function toggleFavorite(job, user, a) {
  new Ajax.Request(rootURL+'/plugin/favorite/toggleFavorite?job=' + job + "&userName=" + user);
  image = document.getElementById("fav_" + job);
  if (image.classList.contains("icon-fav-inactive")) {
    if (image.hasAttribute("src")) {
      image.src = image.src.replace("star.png", "star-gold.png");
    }
    image.classList.add("icon-fav-active");
    image.classList.remove("icon-fav-inactive");
    hoverNotification('お気に入りに追加', a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', { transition: "active-to-inactive" }));
  } else {
    if (image.hasAttribute("src")) {
      image.src = image.src.replace("star-gold.png", "star.png");
    }
    image.classList.add("icon-fav-inactive");
    image.classList.remove("icon-fav-active");
    hoverNotification('お気に入りから削除', a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', { transition: "inactive-to-active" }));
  }
  return false;
}
