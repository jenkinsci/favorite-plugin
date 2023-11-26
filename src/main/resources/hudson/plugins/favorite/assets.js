function toggleFavoriteOnClick(event, a) {
    event.preventDefault();
    return toggleFavorite(a.getAttribute("data-fullName"), a);
}

function toggleFavorite(job, a) {
  fetch(rootURL + "/plugin/favorite/toggleFavorite?job=" + encodeURIComponent(job), {
    method: 'post',
    headers: crumb.wrap({}),
  });
  var favIcon = document.getElementById("fav_" + job);
  if (favIcon.getAttribute("data-fav") === 'false') {
    favIcon.setAttribute('data-fav', 'true');
    favIcon.querySelector('.icon-fav-inactive').classList.add('jenkins-hidden');
    favIcon.querySelector('.icon-fav-active').classList.remove('jenkins-hidden');
    hoverNotification("Favorite added", a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
      transition: "active-to-inactive",
      job: job
    }));
  } else {
    favIcon.setAttribute('data-fav', 'false');
    favIcon.querySelector('.icon-fav-inactive').classList.remove('jenkins-hidden');
    favIcon.querySelector('.icon-fav-active').classList.add('jenkins-hidden');
    hoverNotification("Favorite deleted", a.parentNode);
    window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
      transition: "inactive-to-active",
      job: job
    }));
  }
  return false;
}

Behaviour.specify("A.favorite-toggle", "favorite.toggle", 0, (element) => {
  element.onclick = (event) => {
    toggleFavoriteOnClick(event, element);
  }
});
