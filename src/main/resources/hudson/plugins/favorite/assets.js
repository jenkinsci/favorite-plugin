Behaviour.specify("A.favorite-toggle", "favorite.toggle", 0, (a) => {
  a.onclick = (event) => {
    event.preventDefault();
    const job = a.getAttribute("data-fullName");
    fetch(rootURL + "/plugin/favorite/toggleFavorite?job=" + encodeURIComponent(job), {
      method: 'post',
      headers: crumb.wrap({}),
    });
    if (a.getAttribute("data-fav") === 'false') {
      a.setAttribute('data-fav', 'true');
      a.querySelector('.icon-fav-inactive').classList.add('jenkins-hidden');
      a.querySelector('.icon-fav-active').classList.remove('jenkins-hidden');
      hoverNotification("Favorite added", a.parentNode);
      window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
        transition: "active-to-inactive",
        job: job
      }));
    } else {
      a.setAttribute('data-fav', 'false');
      a.querySelector('.icon-fav-inactive').classList.remove('jenkins-hidden');
      a.querySelector('.icon-fav-active').classList.add('jenkins-hidden');
      hoverNotification("Favorite deleted", a.parentNode);
      window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
        transition: "inactive-to-active",
        job: job
      }));
    }
    return false;
  }
});
