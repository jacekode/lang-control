document.addEventListener('DOMContentLoaded', () => {
  showGeneratedSentences();
  fillTimeForecastIndicators();
});

let ratingFormElems = document.querySelectorAll('.rating-form');
for (let ratingFormElem of ratingFormElems) {
  ratingFormElem.addEventListener('submit', (event) => {
    event.preventDefault();
    postRating(ratingFormElem)
    .then(responseBody => handleRatingResponse(responseBody));
  });
}


function showDeckAnswer() {
  let cardBackElem = document.getElementById('card-back');
  cardBackElem.removeAttribute('hidden');
  let cardAnsBtnElem = document.getElementById('show-answer-btn');
  cardAnsBtnElem.setAttribute('hidden', true);
  let ratingContainerElem = document.getElementById('rating-container');
  ratingContainerElem.removeAttribute('hidden');
  document.getElementById('examples-container').removeAttribute('hidden');
}


function postRating(ratingFormElem) {
  let url = '/api/rating';
  let requestBody = convertFormToUrlEncodedString(ratingFormElem);

  let requestOptions = {
    method: "POST",
    headers: {
      'Content-Type': "application/x-www-form-urlencoded",
    },
    body: requestBody,
  };

  return fetch(url, requestOptions)
    .then(response => {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error("The server couldn't correctly process the rating!");
      }
    })
    .catch(error => console.log(error));
}


// function convertFormToJson(formElem) {
//   let formData = {};

//   for (let i = 0; i < formElem.elements.length; i++) {
//     let element = formElem.elements[i];
//     let name = element.name;
//     let value = element.value;

//     if (element.type === 'submit' || element.tagName === 'BUTTON') {
//       continue;
//     } else if (element.type === 'radio' && !element.checked) {
//       continue;
//     } else if (element.type === 'checkbox') {
//       if (!formData[name]) {
//         formData[name] = [];
//       }
//       if (element.checked) {
//         formData[name].push(value);
//       }
//     } else if (name && value) {
//       formData[name] = value;
//     } else if (name && !value) {
//       formData[name] = null;
//     }
//   }

//   let formJson = JSON.stringify(formData);
//   return formJson;
// }


function convertFormToUrlEncodedString(formElem) {
  let formDataUrlEncoded = new URLSearchParams(new FormData(formElem)).toString();
  return formDataUrlEncoded;
}


function handleRatingResponse(body) {
  let reviewContainerElem = document.querySelector('.review-container');
  reviewContainerElem.innerHTML = '';
  let msgElem = document.createElement('div');
  msgElem.textContent = 'Rating applied successfully!';
  msgElem.classList.add('mb-3');
  reviewContainerElem.append(msgElem);

  if (body.switchedToReviewMode) {
    let switchedToReviewModeDiv = document.createElement('div');
    switchedToReviewModeDiv.textContent = 'The card has been switched to Review Mode.';
    reviewContainerElem.append(switchedToReviewModeDiv);
  } else if (body.switchedToLearnMode) {
    let switchedToLearnModeDiv = document.createElement('div');
    switchedToLearnModeDiv.textContent = 'The card has been switched to Learn Mode.';
    reviewContainerElem.append(switchedToLearnModeDiv);
  }

  setTimeout(() => {
    window.location.replace('/review/next');
  }, 1000);
}


async function fillTimeForecastIndicators() {
  let cardId = document.getElementById('rating-container').dataset.cardId;
  let cardInLearnMode = document.getElementById('rating-container').dataset.cardInLearnMode;
  let url = `/api/cards/${cardId}/forecasts`;
  let responseBody = await performGetRequestBodyAsObject(url);
  if (cardInLearnMode) {
    let forecasts = responseBody.learnModeForecasts;
    document.querySelector('#learn-previous-time').textContent = forecasts.forPrevious;
    document.querySelector('#learn-normal-time').textContent = forecasts.forNormal;
    document.querySelector('#learn-next-time').textContent = forecasts.forNext;
    document.querySelector('#learn-to-review-mode-time').textContent = forecasts.forToReviewMode;
  } else {
    let forecasts = responseBody.reviewModeForecasts;
    document.querySelector('#review-cannot-solve-time').textContent = forecasts.forCannotSolve;
    document.querySelector('#review-difficult-time').textContent = forecasts.forDifficult;
    document.querySelector('#review-normal-time').textContent = forecasts.forNormal;
    document.querySelector('#review-easy-time').textContent = forecasts.forEasy;
  }
}


async function showGeneratedSentences() {
  let examplesContainer = document.getElementById('examples-container');
  let dynamicExamplesOn = examplesContainer.dataset.dynamicExamples === 'true';
  if (!dynamicExamplesOn) { 
    return;
  }

  let sentencesArray = await getGeneratedSentencesArray();
  let sentence = sentencesArray[0];
  let translation = await getSentenceTranslation(sentence);

  let sentenceDiv = document.createElement('div');
  sentenceDiv.textContent = sentence;
  sentenceDiv.classList.add('mb-3');
  examplesContainer.append(sentenceDiv);

  let translationDiv = document.createElement("div");
  translationDiv.textContent = translation;
  translationDiv.classList.add('mb-1');
  examplesContainer.append(translationDiv);
}


async function getGeneratedSentencesArray() {
  let examplesContainer = document.getElementById('examples-container');
  let keyword = examplesContainer.dataset.keyword;
  let targetLangCode = examplesContainer.dataset.targetLang;
  let partOfSpeech = examplesContainer.dataset.pos;
  let urlParams = new URLSearchParams();
  
  let sentencesUrl = '/api/sentences';
  urlParams.append('word', keyword);
  urlParams.append('lang', targetLangCode.toLowerCase());
  urlParams.append('pos', partOfSpeech.toLowerCase());
  urlParams.append('n', 1);

  let url = sentencesUrl + '?' + urlParams.toString();
  console.log('Sentence url: ' + url);
  let sentencesArray = await performGetRequestBodyAsObject(url);
  return sentencesArray;
}


async function getSentenceTranslation(sentence) {
  let langCode = document.getElementById('examples-container').dataset.nativeLang;

  let translationsUrl = '/api/translations';
  let urlParams = new URLSearchParams();
  urlParams.append('text', sentence);
  urlParams.append('lang', langCode.toLowerCase());

  let url = translationsUrl + '?' + urlParams.toString();
  console.log('Translation url: ' + url);
  let translatedSentence = await performGetRequestBodyAsText(url);
  return translatedSentence;
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