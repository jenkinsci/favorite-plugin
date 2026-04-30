Behaviour.specify("A.favorite-toggle, button[data-favorite-toggle]", "favorite.toggle", 0, (a) => {
  const iconFav = `<svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" viewBox="0 0 512 512" class="jenkins-!-color-yellow"><title></title><path d="M394 480a16 16 0 01-9.39-3L256 383.76 127.39 477a16 16 0 01-24.55-18.08L153 310.35 23 221.2a16 16 0 019-29.2h160.38l48.4-148.95a16 16 0 0130.44 0l48.4 149H480a16 16 0 019.05 29.2L359 310.35l50.13 148.53A16 16 0 01394 480z" fill="currentColor"></path></svg>`;
  const iconNoFav = `<svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" viewBox="0 0 512 512"><title></title><path d="M480 208H308L256 48l-52 160H32l140 96-54 160 138-100 138 100-54-160z" fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="32"></path></svg>`;

  const fav = a.dataset.fav === 'true';
  if (a.nodeName === "BUTTON") {
      if (fav) {
        a.querySelector('svg').classList.add('jenkins-!-color-yellow');
      }
  }

  a.onclick = (event) => {
    event.preventDefault();
    const job = a.dataset.fullName;
    fetch(rootURL + "/plugin/favorite/toggleFavorite?job=" + encodeURIComponent(job), {
      method: 'post',
      headers: crumb.wrap({}),
    }).then((response) => {
      if (response.ok) {
        if (a.dataset.fav === 'false') {
          a.dataset.fav = 'true';
          if (a.nodeName === "BUTTON") {
            a.querySelector('.jenkins-dropdown__item__icon').innerHTML = iconFav;
          } else {
            a.querySelector('.icon-fav-inactive').classList.add('jenkins-hidden');
            a.querySelector('.icon-fav-active').classList.remove('jenkins-hidden');
          }
          hoverNotification("Favorite added", a.parentNode);
          window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
            transition: "active-to-inactive",
            job: job
          }));
        } else {
          a.dataset.fav = 'false';
          if (a.nodeName === "BUTTON") {
            a.querySelector('.jenkins-dropdown__item__icon').innerHTML = iconNoFav;
          } else {
            a.querySelector('.icon-fav-inactive').classList.remove('jenkins-hidden');
            a.querySelector('.icon-fav-active').classList.add('jenkins-hidden');
          }
          hoverNotification("Favorite deleted", a.parentNode);
          window.dispatchEvent(new CustomEvent('favorite-plugin-icon-change', {
            transition: "inactive-to-active",
            job: job
          }));
        }
      }
    });
    return false;
  }
});
