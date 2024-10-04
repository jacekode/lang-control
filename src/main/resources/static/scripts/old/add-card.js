let cardInputElems = document.querySelectorAll('#card-front-input, #card-back-input');
for (let inputElem of cardInputElems) {
  inputElem.addEventListener('input', () => {
    toggleDisableTranslateBtn();
    toggleDisableGenerateExampleBtn();
  });
}

document.getElementById('dynamic-examples-checkbox')
  .addEventListener('click', () => {
    toggleDisableGenerateExampleBtn();
    toggleDisableExampleTextareas();
  });

let posBtns = document.querySelectorAll('.pos-radio-btn');
for (let posBtn of posBtns) {
  posBtn.addEventListener('click', () => {
    toggleDisableTranslateBtn();
    toggleDisableGenerateExampleBtn();
  });
}

document.addEventListener('DOMContentLoaded', () => {
  toggleDisableTranslateBtn();
  toggleDisableGenerateExampleBtn();
  toggleDisableExampleTextareas();
});


document.getElementById('translate-words-btn')
  .addEventListener('click', function() {
    translateFrontOrBack();
  });
document.getElementById('generate-example-btn')
  .addEventListener('click', function(event) {
    fillSentences(event.currentTarget.dataset.targetLang, 
      event.currentTarget.dataset.sourceLang);
  });


  function translateFrontOrBack() {
    let frontInputElem = document.getElementById('card-front-input');
    let backInputElem = document.getElementById('card-back-input');
    if (frontInputElem.value == '' && backInputElem.value == '') {
      return;
    } else if (backInputElem.value == '') {
      translateWords(frontInputElem.dataset.langFrom, frontInputElem.dataset.langTo, false);
    } else {
      translateWords(backInputElem.dataset.langFrom, backInputElem.dataset.langTo, true);
    }
  }


async function translateWords(langCodeFrom, langCodeTo, fromBackToFront) {
  console.log('Method translateBackOrFrontSide() invoked!');

  // clean data from previous request

  cleanContainerData();

  // prepare the request

  let translateFrom = langCodeFrom;
  let translateTo = langCodeTo;
  let wordToTranslate = null;
  let sourceWordInputElem;
  if (fromBackToFront) {
    sourceWordInputElem = document.getElementById('card-back-input');
  } else {
    sourceWordInputElem= document.getElementById('card-front-input');
  }

  wordToTranslate = sourceWordInputElem.value;
  console.log('wordToTranslate: ' + wordToTranslate);

  let baseUrl = 'http://localhost:8080';
  let url = '/api/dictionary?word=' + encodeURIComponent(wordToTranslate) + 
    '&from=' + encodeURIComponent(translateFrom) + 
    '&to=' + encodeURIComponent(translateTo) + '&pos=' + 
    encodeURIComponent(getSelectedPartOfSpeechValue());
  
  // perform the request and get the response

  let translationsArray = await performGetRequestBodyAsObject(baseUrl + url);

  // display the data

  if (translationsArray.length === 0) {
    document.getElementById('no-translations-info-msg').hidden = false;
  }
  for (let translationWord of translationsArray) {
    let wordCheckboxElem = document.createElement('input');
    wordCheckboxElem.id = translationWord + '-word-selector';
    wordCheckboxElem.classList.add('btn-check');
    wordCheckboxElem.name = 'translationSelector';
    if (fromBackToFront) {
      wordCheckboxElem.type = "checkbox";
      wordCheckboxElem.addEventListener('change', function() {
        updateWordInputValue(translationWord, fromBackToFront);
      });
    } else {
      wordCheckboxElem.type = "radio";
      wordCheckboxElem.addEventListener('change', function() {
        updateWordInputValue(translationWord, fromBackToFront);
      });
    }

    let wordCheckboxLabelElem = document.createElement('label');
    wordCheckboxLabelElem.classList.add('btn', 'btn-outline-secondary', 'rounded-pill', 'mb-1', 'me-1');
    wordCheckboxLabelElem.setAttribute('for', translationWord + '-word-selector');
    wordCheckboxLabelElem.textContent = translationWord;

    // let wordSelector = document.createElement('div');
    // wordSelector.append(wordCheckboxElem, wordCheckboxLabelElem);

    let translationSelectorContainerElem;
    if (fromBackToFront) {
      translationSelectorContainerElem = document.getElementById('front-translation-selector-container');
    } else {
      translationSelectorContainerElem = document.getElementById('back-translation-selector-container');
    }
    translationSelectorContainerElem.append(wordCheckboxElem, wordCheckboxLabelElem);
  }
}


function cleanContainerData() {
  let translationSelectorContainers = document.querySelectorAll('.translation-selector-container');
  for (let container of translationSelectorContainers) {
    container.innerHTML = '';
  }

  let noTranslationsMsgElem = document.getElementById('no-translations-info-msg');
  noTranslationsMsgElem.hidden = true;
}


function performGetRequestBodyAsObject(url) {
  return fetch(url)
    .then(response => {
      if (response.status >= 200 && response.status <= 299) {
        return response.json();
      } else {
        console.log(`Received the response status code ${response.status} from the server.`);
      }
    })
    .catch(error => {
      console.log(`An error has occured while trying to perform the request: ${error}`);
    });
}


function performGetRequestBodyAsText(url) {
  return fetch(url)
    .then(response => {
      if (response.status >= 200 && response.status <= 299) {
        return response.text();
      } else {
        throw new Error('Couldn\'t fetch translations!');
      }
    })
    .catch(error => {
      console.log(`An error has occured while trying to perform the request: ${error}`);
    });
}


function updateWordInputValue(wordToAddOrRemove, fromBackToFront) {
  let wordSelector = document.getElementById(wordToAddOrRemove + '-word-selector');
  let inputElem;

  if (fromBackToFront) {
    inputElem = document.getElementById('card-front-input');
    if (wordSelector.checked) {
      if (!inputElem.value) {
        inputElem.value = wordToAddOrRemove;
      } else {
        let existingValue = inputElem.value;
        inputElem.value = existingValue + '; ' + wordToAddOrRemove;
      }
    } else {
      let inputFieldValue = inputElem.value;
      let updatedInputFieldValue = inputFieldValue.replace(wordToAddOrRemove, '');
      updatedInputFieldValue = updatedInputFieldValue.replace('; ; ', '; ');
      if (updatedInputFieldValue.endsWith('; ')) {
        updatedInputFieldValue = updatedInputFieldValue.slice(0, -2);
      }
      if (updatedInputFieldValue.startsWith('; ')) {
        updatedInputFieldValue = updatedInputFieldValue.substring(2, updatedInputFieldValue.length);
      }
      inputElem.value = updatedInputFieldValue;
    }
  } else {
    inputElem = document.getElementById('card-back-input');
    if (wordSelector.checked) {
      inputElem.value = wordToAddOrRemove;
    } else {
      inputElem.value = '';
    }
  }
  let inputEvent = new Event('input', { bubbles: true });
  inputElem.dispatchEvent(inputEvent);
}


async function fillSentences(keywordLangCode, translationLangCode) {
  await fillExampleSentence(keywordLangCode);
  await fillTranslatedSentence(translationLangCode);
}


async function fillExampleSentence(keywordLangCode) {
  console.log('fillExampleSentence() method invoked');
  let noSentencesMsgElem = document.getElementById('no-sentences-info-msg');
  noSentencesMsgElem.hidden = true;

  let keyword = document.getElementById('card-back-input').value;
  let partOfSpeech = getSelectedPartOfSpeechValue();
  let url = '/api/sentences?word=' + encodeURIComponent(keyword) +
    '&lang=' + encodeURIComponent(keywordLangCode) +
    '&pos=' + encodeURIComponent(partOfSpeech) + '&n=1';

  let sentencesArray = await performGetRequestBodyAsObject(url);

  if (sentencesArray.length === 0) {
    noSentencesMsgElem.hidden = false;
  } else {
    let exampleTextareaElem = document.getElementById('card-example-textarea');
    exampleTextareaElem.value = sentencesArray[0];
  }
}


async function fillTranslatedSentence(langCodeTo) {
  let textToTranslate = document.getElementById('card-example-textarea').value;

  let url = '/api/translations?text=' + encodeURIComponent(textToTranslate) + 
    '&lang=' + encodeURIComponent(langCodeTo);

  let translatedSentence = await performGetRequestBodyAsText(url);
  if (translatedSentence) {
    document.getElementById('card-translated-example-textarea').value = translatedSentence;
  }
}


function getSelectedPartOfSpeechValue() {
  let posRadioBtns = document.querySelectorAll('.pos-radio-btn');
  for (let i = 0; i < posRadioBtns.length; i++) {
    if (posRadioBtns[i].checked) {
      return posRadioBtns[i].value;
    }
  }
  return null;
}


function toggleDisableTranslateBtn() {
  let translateWordsBtn = document.getElementById('translate-words-btn');
  let cardFrontInputElem = document.getElementById('card-front-input');
  let cardBackInputElem = document.getElementById('card-back-input');
  if ((cardFrontInputElem.value && cardBackInputElem.value) || 
      (!cardFrontInputElem.value && !cardBackInputElem.value) || 
      !getSelectedPartOfSpeechValue()) {
    translateWordsBtn.setAttribute('disabled', true);
    translateWordsBtn.classList.add('btn-outline-secondary');
    translateWordsBtn.classList.remove('btn-outline-warning');
  } else {
    translateWordsBtn.removeAttribute('disabled');
    translateWordsBtn.classList.add('btn-outline-warning');
    translateWordsBtn.classList.remove('btn-outline-secondary');

  }
}


function toggleDisableGenerateExampleBtn() {
  let generateExampleBtn = document.getElementById('generate-example-btn');
  let cardFrontInputElem = document.getElementById('card-front-input');
  let cardBackInputElem = document.getElementById('card-back-input');
  let dynamicExamplesOn = document.getElementById('dynamic-examples-checkbox').checked;
  if (!(getSelectedPartOfSpeechValue() && cardBackInputElem.value) || 
      dynamicExamplesOn) {
    generateExampleBtn.setAttribute('disabled', true);
    generateExampleBtn.classList.add('btn-outline-secondary');
    generateExampleBtn.classList.remove('btn-outline-warning');
  } else {
    generateExampleBtn.removeAttribute('disabled');
    generateExampleBtn.classList.add('btn-outline-warning');
    generateExampleBtn.classList.remove('btn-outline-secondary');

  }
}


function toggleDisableExampleTextareas() {
  let dynExamplesCheckbox = document.getElementById('dynamic-examples-checkbox');
  let isChecked = dynExamplesCheckbox.checked;
  document.getElementById('card-example-textarea').disabled = isChecked;
  document.getElementById('card-translated-example-textarea').disabled = isChecked;
  // let generateBtn = document.getElementById('generate-example-btn');
  // generateBtn.disabled = isChecked;

  // if (isChecked) {
  //   generateBtn.classList.add('btn-outline-secondary');
  //   generateBtn.classList.remove('btn-outline-warning');
  // } else {
  //   generateBtn.classList.add('btn-outline-warning');
  //   generateBtn.classList.remove('btn-outline-secondary');
  // }
}


// function addGenerateInProgressSpinner() {
//   let generateExampleBtnElem = document.getElementById('generate-example-btn');
//   generateExampleBtnElem.setAttribute("disabled", true);
//   generateExampleBtnElem.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
//   Loading...`;
// }


// function removeGenerateInProgressSpinner() {
//   let generateExampleBtnElem = document.getElementById('generate-example-btn');
//   generateExampleBtnElem.removeAttribute('disabled');
//   generateExampleBtnElem.innerHTML = 'Generate example';
// }