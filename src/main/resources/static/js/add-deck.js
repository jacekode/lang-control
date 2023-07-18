document.addEventListener('DOMContentLoaded', () => {
  checkLanguageSelectValidity();
});

document.getElementById('source-lang-select')
  .addEventListener('change', () => {
    checkLanguageSelectValidity();
  });
document.getElementById('target-lang-select')
  .addEventListener('change', () => {
    checkLanguageSelectValidity();
  });

function checkLanguageSelectValidity() {
  let sourceLangSelect = document.getElementById('source-lang-select');
  let targetLangSelect = document.getElementById('target-lang-select');
  if (sourceLangSelect.value == targetLangSelect.value) {
    sourceLangSelect.setCustomValidity('The languages can\'t be the same.');
  } else {
    sourceLangSelect.setCustomValidity('');
  }
}