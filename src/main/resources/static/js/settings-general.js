Array.from(document.querySelectorAll('.general-settings-input'))
  .forEach(elem => {
    elem.addEventListener('change', () => {
      document.getElementById('general-settings-submit-btn').disabled = false;
    });
  });